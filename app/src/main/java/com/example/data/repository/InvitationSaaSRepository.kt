package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

data class PaymentLinkResult(
    val provider: String,
    val reference: String,
    val paymentUrl: String,
    val virtualAccount: String,
    val qrisPayload: String
)

class InvitationSaaSRepository(private val database: AppDatabase) {
    private val userDao = database.userDao()
    private val themeDao = database.themeDao()
    private val orderDao = database.orderDao()
    private val invitationDao = database.invitationDao()
    private val guestDao = database.guestDao()
    private val greetingDao = database.greetingDao()
    private val paymentEventDao = database.paymentEventDao()

    val allThemes: Flow<List<ThemeEntity>> = themeDao.getAllThemes()
    val activeThemes: Flow<List<ThemeEntity>> = themeDao.getActiveThemes()
    val allOrders: Flow<List<OrderEntity>> = orderDao.getAllOrders()
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()
    val allPaymentEvents: Flow<List<PaymentEventEntity>> = paymentEventDao.getAllEvents()

    fun getOrdersByCustomer(userId: Long): Flow<List<OrderEntity>> = orderDao.getOrdersByCustomer(userId)
    fun getOrdersByReseller(resellerId: Long): Flow<List<OrderEntity>> = orderDao.getOrdersByReseller(resellerId)
    fun getCustomersByReseller(resellerId: Long): Flow<List<UserEntity>> = userDao.getCustomersByReseller(resellerId)
    fun getGuestsByInvitation(invitationId: Long): Flow<List<GuestEntity>> = guestDao.getGuestsByInvitation(invitationId)
    fun getGreetingsByInvitation(invitationId: Long): Flow<List<GreetingEntity>> = greetingDao.getGreetingsByInvitation(invitationId)
    fun observeInvitationBySlug(slug: String): Flow<InvitationEntity?> = invitationDao.observeInvitationBySlug(slug)

    suspend fun getInvitationByOrderId(orderId: Long): InvitationEntity? = invitationDao.getInvitationByOrderId(orderId)
    suspend fun getInvitationBySlug(slug: String): InvitationEntity? = invitationDao.getInvitationBySlug(slug)
    suspend fun getThemeById(id: Long): ThemeEntity? = themeDao.getThemeById(id)
    suspend fun getUserById(userId: Long): UserEntity? = userDao.getUserById(userId)
    suspend fun getOrderById(orderId: Long): OrderEntity? = orderDao.getOrderById(orderId)

    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        val existingUsers = userDao.getUserById(1)
        if (existingUsers != null) return@withContext

        // 1. Seed Users
        val admin = UserEntity(id = 1, name = "Budi Hartono (Super Admin)", email = "admin@nikahsaas.com", role = "super_admin", phone = "+6281234567890")
        val reseller = UserEntity(id = 2, name = "Mitra Wedding Bahagia (Reseller)", email = "reseller@mitranikah.id", role = "reseller", phone = "+6281987654321")
        val customer1 = UserEntity(id = 3, name = "Fajar Pratama (Customer)", email = "fajar.pratama@gmail.com", role = "customer", referredByResellerId = 2, phone = "+6285711223344")
        val customer2 = UserEntity(id = 4, name = "Bagus Santoso (Customer)", email = "bagus.santoso@gmail.com", role = "customer", referredByResellerId = 2, phone = "+6285799887766")
        userDao.insertUsers(listOf(admin, reseller, customer1, customer2))

        // 2. Seed Themes
        val themes = listOf(
            ThemeEntity(
                id = 1,
                name = "Royal Javanese Luxury",
                slug = "royal-javanese",
                description = "Nuansa Kraton Yogyakarta dengan ornamen gunungan emas klasik, motif batik prada, dan musik gending gamelan sakral.",
                previewImage = "theme_jawa",
                price = 149000,
                category = "Tradisional",
                isActive = true,
                accentColorHex = "#9C6B28",
                features = "Gamelan Player, Gunungan Emas, Buku Tamu, Amplop Digital, QRIS"
            ),
            ThemeEntity(
                id = 2,
                name = "Modern Minimalist Chic",
                slug = "modern-minimalist",
                description = "Desain elegan modern dengan tipografi serif bersih, warna warm ivory dan rose mauve kontemporer.",
                previewImage = "theme_minimalist",
                price = 99000,
                category = "Modern",
                isActive = true,
                accentColorHex = "#871A5B",
                features = "Minimalist Grid, Live Countdown, Direct WhatsApp RSVP, Google Maps"
            ),
            ThemeEntity(
                id = 3,
                name = "Rustic Botanical Garden",
                slug = "rustic-botanical",
                description = "Estetika boho alami dengan daun eucalyptus, bunga kering pampas, dan palet warna terracotta hangat.",
                previewImage = "theme_rustic",
                price = 129000,
                category = "Rustic",
                isActive = true,
                accentColorHex = "#A0522D",
                features = "Botanical Border, Acoustic Romance Music, Interactive Gallery"
            ),
            ThemeEntity(
                id = 4,
                name = "Islamic Grace & Barakah",
                slug = "islamic-grace",
                description = "Sentuhan ornamen arabesque islami bernuansa emerald dan emas dengan doa mahabbah dan kutipan hadits suci.",
                previewImage = "theme_islamic",
                price = 119000,
                category = "Islami",
                isActive = true,
                accentColorHex = "#1B4D3E",
                features = "Kaligrafi Bismillah, Doa Walimah, Countdown Akad, Amplop Syariah"
            ),
            ThemeEntity(
                id = 5,
                name = "Luxury Midnight Gold",
                slug = "midnight-gold",
                description = "Kemewahan bintang malam dengan aksen navy tua dan debu emas berkilau untuk pesta resepsi megah.",
                previewImage = "theme_luxury",
                price = 199000,
                category = "Luxury",
                isActive = true,
                accentColorHex = "#1A2A3A",
                features = "Parallax Star Effect, Orchestral Audio, Multi-Event Akad & Resepsi"
            )
        )
        themeDao.insertThemes(themes)

        // 3. Seed Initial Order & Invitation for customer 1 (Fajar & Dina)
        val eventCalendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 21) // 3 weeks from now
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
        }
        val eventTime = eventCalendar.timeInMillis

        val orderNumber = "ORD-202610-001"
        val order1 = OrderEntity(
            id = 1,
            orderNumber = orderNumber,
            userId = customer1.id,
            resellerId = reseller.id,
            themeId = 1,
            amount = 149000,
            currency = "IDR",
            status = "completed",
            paymentStatus = "paid",
            paymentProvider = "Midtrans",
            paymentReference = "MDT-" + UUID.randomUUID().toString().substring(0, 8).uppercase(Locale.ROOT),
            paymentUrl = "https://app.midtrans.com/snap/v2/vtweb/simulated-order-1",
            paidAt = System.currentTimeMillis() - 86400000L,
            createdAt = System.currentTimeMillis() - 90000000L
        )
        orderDao.insertOrder(order1)

        val invitation1 = InvitationEntity(
            id = 1,
            orderId = 1,
            slug = "fajar-dina",
            groomName = "Fajar Pratama, S.Kom.",
            brideName = "Dina Lestari, S.E.",
            groomBio = "Putra kedua dari Bpk. Ir. H. Bambang Sujiwo & Ibu Hj. Siti Aminah",
            brideBio = "Putri bungsu dari Bpk. Drs. H. Hendra Wicaksono & Ibu Hj. Ratna Dewi",
            eventAt = eventTime,
            venue = "Grand Ballroom Hotel Sahid Jaya",
            address = "Jl. Jenderal Sudirman No. 86, Karet Tengsin, Jakarta Pusat",
            mapsUrl = "https://maps.google.com/?q=Hotel+Sahid+Jaya+Jakarta",
            story = "Kisah kami berawal di perpustakaan kampus tahun 2019. Berangkat dari cangkir kopi pertama hingga ikrar janji setia di hadapan keluarga tercinta.",
            quote = "Dan di antara tanda-tanda (kebesaran)-Nya ialah Dia menciptakan pasangan-pasangan untukmu dari jenismu sendiri, agar kamu cenderung dan merasa tenteram kepadanya, dan Dia menjadikan di antaramu rasa kasih dan sayang. (QS. Ar-Rum: 21)",
            themeColorHex = "#9C6B28",
            musicTitle = "Gending Jawa & Janji Suci",
            bankName = "Bank Central Asia (BCA)",
            bankAccount = "8720192831",
            bankHolder = "Fajar Pratama",
            isPublished = true
        )
        invitationDao.insertInvitation(invitation1)

        // 4. Seed sample guests
        val guests = listOf(
            GuestEntity(
                id = 1,
                invitationId = 1,
                name = "Bapak Anies & Ibu",
                phone = "+6281211112222",
                token = "GUEST-ANI",
                rsvpStatus = "attending",
                guestCount = 2,
                message = "Selamat menempuh hidup baru Fajar & Dina. Semoga menjadi keluarga sakinah mawaddah warahmah.",
                respondedAt = System.currentTimeMillis() - 36000000L
            ),
            GuestEntity(
                id = 2,
                invitationId = 1,
                name = "Dr. Rahmat Hidayat",
                phone = "+6281322223333",
                token = "GUEST-RAH",
                rsvpStatus = "attending",
                guestCount = 1,
                message = "Barakallahu lakuma wa baraka alaikuma wa jama'a bainakuma fii khoir. Doa terbaik untuk kedua mempelai!",
                respondedAt = System.currentTimeMillis() - 24000000L
            ),
            GuestEntity(
                id = 3,
                invitationId = 1,
                name = "Siti Nurhaliza & Partner",
                phone = "+6281733334444",
                token = "GUEST-SIT",
                rsvpStatus = "pending",
                guestCount = 2
            ),
            GuestEntity(
                id = 4,
                invitationId = 1,
                name = "Bagus Priambodo",
                phone = "+6281844445555",
                token = "GUEST-BAG",
                rsvpStatus = "declined",
                guestCount = 1,
                message = "Mohon maaf belum bisa hadir langsung karena dinas luar kota. Selamat berbahagia ya Fajar & Dina!",
                respondedAt = System.currentTimeMillis() - 12000000L
            )
        )
        guestDao.insertGuests(guests)

        // 5. Seed greetings wall
        greetingDao.insertGreeting(
            GreetingEntity(
                invitationId = 1,
                guestName = "Bapak Anies & Ibu",
                message = "Selamat menempuh hidup baru Fajar & Dina. Semoga menjadi keluarga sakinah mawaddah warahmah.",
                statusAttendance = "attending"
            )
        )
        greetingDao.insertGreeting(
            GreetingEntity(
                invitationId = 1,
                guestName = "Dr. Rahmat Hidayat",
                message = "Barakallahu lakuma wa baraka alaikuma wa jama'a bainakuma fii khoir. Doa terbaik untuk kedua mempelai!",
                statusAttendance = "attending"
            )
        )
        greetingDao.insertGreeting(
            GreetingEntity(
                invitationId = 1,
                guestName = "Rekan Kerja Divisi IT",
                message = "Happy Wedding Mas Fajar! Langgeng sampai kakek nenek, jangan lupa bahagia selalu!",
                statusAttendance = "attending"
            )
        )

        // 6. Seed payment log
        paymentEventDao.insertPaymentEvent(
            PaymentEventEntity(
                eventId = "EVT-" + UUID.randomUUID().toString().substring(0, 10),
                orderNumber = orderNumber,
                grossAmount = 149000,
                status = "settlement",
                signatureVerified = true
            )
        )
    }

    // Customer / Reseller Checkout & Order Creation
    suspend fun createOrderAndDraftInvitation(
        customerId: Long,
        resellerId: Long?,
        themeId: Long,
        groomName: String,
        brideName: String,
        eventAt: Long,
        venue: String,
        address: String?,
        mapsUrl: String?,
        customSlug: String? = null
    ): OrderEntity = withContext(Dispatchers.IO) {
        val theme = themeDao.getThemeById(themeId) ?: throw IllegalArgumentException("Tema tidak ditemukan")
        val orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).uppercase(Locale.ROOT)

        val newOrder = OrderEntity(
            orderNumber = orderNumber,
            userId = customerId,
            resellerId = resellerId,
            themeId = theme.id,
            amount = theme.price, // Snapshot harga tema saat checkout
            currency = "IDR",
            status = "pending",
            paymentStatus = "pending"
        )
        val orderId = orderDao.insertOrder(newOrder)

        // Generated clean slug
        val baseSlug = if (!customSlug.isNullOrBlank()) {
            customSlug.lowercase(Locale.ROOT).replace("[^a-z0-9-]".toRegex(), "-")
        } else {
            "${groomName.split(" ").firstOrNull().orEmpty()}-${brideName.split(" ").firstOrNull().orEmpty()}"
                .lowercase(Locale.ROOT)
                .replace("[^a-z0-9-]".toRegex(), "")
        }
        val safeSlug = if (baseSlug.isBlank()) "wedding-" + UUID.randomUUID().toString().substring(0, 6) else "$baseSlug-${UUID.randomUUID().toString().substring(0, 4)}"

        val draftInvitation = InvitationEntity(
            orderId = orderId,
            slug = safeSlug,
            groomName = groomName,
            brideName = brideName,
            eventAt = eventAt,
            venue = venue,
            address = address,
            mapsUrl = mapsUrl,
            themeColorHex = theme.accentColorHex,
            isPublished = false
        )
        invitationDao.insertInvitation(draftInvitation)

        // Auto-generate Midtrans Payment Link
        val paymentLink = generateMidtransPaymentLink(orderNumber, theme.price)
        val updatedOrder = newOrder.copy(
            id = orderId,
            paymentProvider = paymentLink.provider,
            paymentReference = paymentLink.reference,
            paymentUrl = paymentLink.paymentUrl
        )
        orderDao.updateOrder(updatedOrder)
        return@withContext updatedOrder
    }

    // Midtrans Payment Link Generation Simulation
    fun generateMidtransPaymentLink(orderNumber: String, amount: Long): PaymentLinkResult {
        val ref = "MDT-" + UUID.randomUUID().toString().substring(0, 8).uppercase(Locale.ROOT)
        val vaNumber = "70012" + (10000000..99999999).random()
        val url = "https://app.midtrans.com/snap/v2/vtweb/$ref"
        val qris = "00020101021226590014ID.LINKAJA.WWW01189360091400001002340215$orderNumber"
        return PaymentLinkResult(
            provider = "Midtrans",
            reference = ref,
            paymentUrl = url,
            virtualAccount = vaNumber,
            qrisPayload = qris
        )
    }

    // Payment Webhook Simulation (Idempotent Callback Processing)
    suspend fun processWebhookPayment(orderNumber: String, isSuccess: Boolean = true): Boolean = withContext(Dispatchers.IO) {
        val order = orderDao.getOrderByNumber(orderNumber) ?: return@withContext false

        // Idempotency check: jika sudah paid, return true tanpa re-proses ganda
        if (order.paymentStatus == "paid") {
            return@withContext true
        }

        val eventId = "WH-" + UUID.randomUUID().toString().substring(0, 10).uppercase(Locale.ROOT)
        val statusText = if (isSuccess) "settlement" else "failure"

        // Log payment audit event
        paymentEventDao.insertPaymentEvent(
            PaymentEventEntity(
                eventId = eventId,
                orderNumber = orderNumber,
                grossAmount = order.amount,
                status = statusText,
                signatureVerified = true
            )
        )

        if (isSuccess) {
            val updatedOrder = order.copy(
                paymentStatus = "paid",
                status = "completed",
                paidAt = System.currentTimeMillis()
            )
            orderDao.updateOrder(updatedOrder)

            // Publish invitation
            val invitation = invitationDao.getInvitationByOrderId(order.id)
            if (invitation != null) {
                invitationDao.updateInvitation(invitation.copy(isPublished = true))
            }
            return@withContext true
        } else {
            val updatedOrder = order.copy(
                paymentStatus = "failed",
                status = "cancelled"
            )
            orderDao.updateOrder(updatedOrder)
            return@withContext false
        }
    }

    // Invitation updating
    suspend fun updateInvitation(invitation: InvitationEntity) = withContext(Dispatchers.IO) {
        invitationDao.updateInvitation(invitation)
    }

    // Guest operations
    suspend fun addGuest(invitationId: Long, name: String, phone: String?): GuestEntity = withContext(Dispatchers.IO) {
        val token = "GUEST-" + UUID.randomUUID().toString().substring(0, 6).uppercase(Locale.ROOT)
        val guest = GuestEntity(
            invitationId = invitationId,
            name = name,
            phone = phone,
            token = token,
            rsvpStatus = "pending",
            guestCount = 1
        )
        val id = guestDao.insertGuest(guest)
        guest.copy(id = id)
    }

    suspend fun deleteGuest(guest: GuestEntity) = withContext(Dispatchers.IO) {
        guestDao.deleteGuest(guest)
    }

    // Public RSVP Submission
    suspend fun submitRsvp(
        invitationId: Long,
        guestName: String,
        status: String, // "attending", "declined"
        count: Int,
        message: String?,
        token: String? = null
    ) = withContext(Dispatchers.IO) {
        // Find existing guest or create one
        var guest: GuestEntity? = null
        if (!token.isNullOrBlank()) {
            guest = guestDao.getGuestByInvitationAndToken(invitationId, token)
        }

        if (guest != null) {
            guestDao.updateGuest(
                guest.copy(
                    rsvpStatus = status,
                    guestCount = count,
                    message = message,
                    respondedAt = System.currentTimeMillis()
                )
            )
        } else {
            guestDao.insertGuest(
                GuestEntity(
                    invitationId = invitationId,
                    name = guestName,
                    rsvpStatus = status,
                    guestCount = count,
                    message = message,
                    respondedAt = System.currentTimeMillis()
                )
            )
        }

        // Post to public greetings wall if message provided
        if (!message.isNullOrBlank()) {
            greetingDao.insertGreeting(
                GreetingEntity(
                    invitationId = invitationId,
                    guestName = guestName,
                    message = message,
                    statusAttendance = status
                )
            )
        }
    }

    // Theme Management (Super Admin)
    suspend fun toggleThemeActive(theme: ThemeEntity) = withContext(Dispatchers.IO) {
        themeDao.updateTheme(theme.copy(isActive = !theme.isActive))
    }

    suspend fun updateThemePrice(theme: ThemeEntity, newPrice: Long) = withContext(Dispatchers.IO) {
        themeDao.updateTheme(theme.copy(price = newPrice))
    }

    suspend fun insertTheme(theme: ThemeEntity) = withContext(Dispatchers.IO) {
        themeDao.insertTheme(theme)
    }

    // Generate WhatsApp Share Message for Guest
    fun generateWhatsAppInvitationMessage(
        guest: GuestEntity,
        invitation: InvitationEntity
    ): String {
        val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        val dateStr = sdf.format(Date(invitation.eventAt))
        val url = "https://undangan.id/i/${invitation.slug}?to=${URLEncoder.encode(guest.name, StandardCharsets.UTF_8.toString())}&token=${guest.token}"

        return """
            Kepada Yth.
            *${guest.name}*
            
            Tanpa mengurangi rasa hormat, kami bermaksud mengundang Bapak/Ibu/Saudara/i untuk hadir dalam acara pernikahan kami:
            
            *${invitation.groomName} & ${invitation.brideName}*
            
            📅 Tanggal: $dateStr
            📍 Lokasi: ${invitation.venue}
            
            Informasi lengkap acara & konfirmasi kehadiran (RSVP) dapat diakses melalui link undangan berikut:
            $url
            
            Merupakan suatu kehormatan dan kebahagiaan bagi kami apabila berkenan hadir dan memberikan doa restu.
            
            Terima kasih.
            Kami yang berbahagia,
            *${invitation.groomName.split(" ").first()} & ${invitation.brideName.split(" ").first()}*
        """.trimIndent()
    }
}
