package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.*
import com.example.data.repository.InvitationSaaSRepository
import com.example.service.ChatMessage
import com.example.service.GeminiApiService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppRole(val label: String, val badgeColorHex: String) {
    CUSTOMER("Customer (Mempelai)", "#871A5B"),
    RESELLER("Reseller (Mitra Agen)", "#1E88E5"),
    SUPER_ADMIN("Super Admin (Platform Owner)", "#2E7D32"),
    PUBLIC_GUEST("Tamu Undangan (Public /i/{slug})", "#E65100")
}

sealed class AppScreen {
    data object ThemeCatalog : AppScreen()
    data class InvitationViewer(val slug: String, val guestName: String = "Tamu Terhormat") : AppScreen()
    data object CustomerDashboard : AppScreen()
    data class CustomerCheckout(val themeId: Long) : AppScreen()
    data class CustomerInvitationEditor(val orderId: Long) : AppScreen()
    data class CustomerGuestManager(val invitationId: Long) : AppScreen()
    data object ResellerDashboard : AppScreen()
    data object AdminDashboard : AppScreen()
    data object AiStudio : AppScreen()
    data object WebsitePortal : AppScreen()
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    val repository = InvitationSaaSRepository(database)
    val geminiService = GeminiApiService()

    // Navigation & Role State
    private val _currentRole = MutableStateFlow(AppRole.CUSTOMER)
    val currentRole: StateFlow<AppRole> = _currentRole.asStateFlow()

    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.CustomerDashboard)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _screenBackStack = mutableListOf<AppScreen>()

    // Current Customer / Reseller identity
    private val _currentUserId = MutableStateFlow<Long>(3) // Default to Fajar Pratama (Customer)
    val currentUserId: StateFlow<Long> = _currentUserId.asStateFlow()

    // UI feedback
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _isGeneratingAi = MutableStateFlow(false)
    val isGeneratingAi: StateFlow<Boolean> = _isGeneratingAi.asStateFlow()

    // AI Chat history
    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                role = "model",
                content = "Halo! Selamat datang di Wedding AI Assistant. Saya siap membantu menyusun narasi Love Story romantis, ayat/kutipan pernikahan, konsep tema, atau panduan etika undangan. Apa yang ingin Anda diskusikan?"
            )
        )
    )
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory.asStateFlow()

    // Data streams
    val allThemes: StateFlow<List<ThemeEntity>> = repository.allThemes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeThemes: StateFlow<List<ThemeEntity>> = repository.activeThemes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val paymentEvents: StateFlow<List<PaymentEventEntity>> = repository.allPaymentEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customerOrders: StateFlow<List<OrderEntity>> = _currentUserId.flatMapLatest { id ->
        repository.getOrdersByCustomer(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val resellerOrders: StateFlow<List<OrderEntity>> = repository.getOrdersByReseller(2)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val resellerCustomers: StateFlow<List<UserEntity>> = repository.getCustomersByReseller(2)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun navigateTo(screen: AppScreen) {
        _screenBackStack.add(_currentScreen.value)
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (_screenBackStack.isNotEmpty()) {
            _currentScreen.value = _screenBackStack.removeAt(_screenBackStack.size - 1)
            return true
        }
        return false
    }

    fun switchRole(role: AppRole) {
        _currentRole.value = role
        when (role) {
            AppRole.CUSTOMER -> {
                _currentUserId.value = 3 // Fajar Pratama
                navigateTo(AppScreen.CustomerDashboard)
            }
            AppRole.RESELLER -> {
                _currentUserId.value = 2 // Reseller
                navigateTo(AppScreen.ResellerDashboard)
            }
            AppRole.SUPER_ADMIN -> {
                _currentUserId.value = 1 // Admin
                navigateTo(AppScreen.AdminDashboard)
            }
            AppRole.PUBLIC_GUEST -> {
                navigateTo(AppScreen.InvitationViewer("fajar-dina", "Tamu Terhormat"))
            }
        }
        showSnackbar("Beralih ke mode: ${role.label}")
    }

    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    // Checkout & Order creation
    fun createOrder(
        themeId: Long,
        groomName: String,
        brideName: String,
        eventAt: Long,
        venue: String,
        address: String?,
        mapsUrl: String?,
        customSlug: String?,
        forCustomerId: Long? = null,
        onSuccess: (OrderEntity) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val customerId = forCustomerId ?: if (_currentRole.value == AppRole.RESELLER) 4L else _currentUserId.value
                val resellerId = if (_currentRole.value == AppRole.RESELLER) 2L else 2L // Referred by reseller 2

                val order = repository.createOrderAndDraftInvitation(
                    customerId = customerId,
                    resellerId = resellerId,
                    themeId = themeId,
                    groomName = groomName,
                    brideName = brideName,
                    eventAt = eventAt,
                    venue = venue,
                    address = address,
                    mapsUrl = mapsUrl,
                    customSlug = customSlug
                )
                showSnackbar("Order ${order.orderNumber} berhasil dibuat! Menunggu pembayaran.")
                onSuccess(order)
            } catch (e: Exception) {
                showSnackbar("Gagal membuat order: ${e.localizedMessage}")
            }
        }
    }

    // Simulate Webhook Payment
    fun simulatePaymentWebhook(orderNumber: String, isSuccess: Boolean = true) {
        viewModelScope.launch {
            val result = repository.processWebhookPayment(orderNumber, isSuccess)
            if (result) {
                showSnackbar("✅ Webhook Midtrans terverifikasi: Pembayaran Order $orderNumber LUNAS! Undangan otomatis aktif.")
            } else {
                showSnackbar("❌ Pembayaran Order $orderNumber ditandai gagal atau dibatalkan.")
            }
        }
    }

    // Update Invitation
    fun saveInvitation(invitation: InvitationEntity) {
        viewModelScope.launch {
            repository.updateInvitation(invitation)
            showSnackbar("Pengaturan undangan berhasil disimpan!")
        }
    }

    // Add Guest
    fun addGuest(invitationId: Long, name: String, phone: String?) {
        viewModelScope.launch {
            repository.addGuest(invitationId, name, phone)
            showSnackbar("Tamu '$name' berhasil ditambahkan.")
        }
    }

    // Delete Guest
    fun deleteGuest(guest: GuestEntity) {
        viewModelScope.launch {
            repository.deleteGuest(guest)
            showSnackbar("Tamu '${guest.name}' telah dihapus.")
        }
    }

    // Submit Public RSVP
    fun submitRsvp(
        invitationId: Long,
        name: String,
        status: String,
        count: Int,
        message: String?,
        token: String?,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            repository.submitRsvp(invitationId, name, status, count, message, token)
            showSnackbar("Terima kasih, konfirmasi kehadiran Anda telah tersimpan!")
            onDone()
        }
    }

    // Admin theme management
    fun toggleThemeStatus(theme: ThemeEntity) {
        viewModelScope.launch {
            repository.toggleThemeActive(theme)
            showSnackbar("Status tema '${theme.name}' berhasil diubah.")
        }
    }

    fun updateThemePrice(theme: ThemeEntity, newPrice: Long) {
        viewModelScope.launch {
            repository.updateThemePrice(theme, newPrice)
            showSnackbar("Harga tema '${theme.name}' berhasil diperbarui ke Rp ${String.format("%,d", newPrice)}.")
        }
    }

    fun addNewTheme(theme: ThemeEntity) {
        viewModelScope.launch {
            repository.insertTheme(theme)
            showSnackbar("Tema '${theme.name}' berhasil ditambahkan!")
        }
    }

    // AI Features
    fun generateLoveStory(
        groom: String,
        bride: String,
        howMet: String,
        proposal: String,
        vision: String,
        onResult: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isGeneratingAi.value = true
            val story = geminiService.generateDeepLoveStory(groom, bride, howMet, proposal, vision)
            _isGeneratingAi.value = false
            onResult(story)
        }
    }

    fun generateRomanticQuote(
        category: String,
        tone: String,
        onResult: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isGeneratingAi.value = true
            val quote = geminiService.generateQuotesAndCopy(category, tone)
            _isGeneratingAi.value = false
            onResult(quote)
        }
    }

    fun quickPolishWish(
        rawWish: String,
        relationship: String,
        onResult: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isGeneratingAi.value = true
            val polished = geminiService.quickPolishRsvpWish(rawWish, relationship)
            _isGeneratingAi.value = false
            onResult(polished)
        }
    }

    fun searchVenueAdvice(
        query: String,
        onResult: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isGeneratingAi.value = true
            val advice = geminiService.searchVenueAndTraditions(query)
            _isGeneratingAi.value = false
            onResult(advice)
        }
    }

    fun analyzePreweddingPhoto(
        bitmap: Bitmap,
        onResult: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isGeneratingAi.value = true
            val analysis = geminiService.analyzeWeddingPhoto(bitmap)
            _isGeneratingAi.value = false
            onResult(analysis)
        }
    }

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(role = "user", content = text)
        val currentList = _chatHistory.value.toMutableList().apply { add(userMsg) }
        _chatHistory.value = currentList

        viewModelScope.launch {
            _isGeneratingAi.value = true
            val reply = geminiService.chatWithWeddingPlanner(_chatHistory.value, text)
            _isGeneratingAi.value = false
            _chatHistory.value = _chatHistory.value.toMutableList().apply {
                add(ChatMessage(role = "model", content = reply))
            }
        }
    }
}
