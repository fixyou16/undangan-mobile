package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.AppRole
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.AppBottomNavigation
import com.example.ui.components.AppHeaderBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    // Handle system back button
    BackHandler(enabled = currentScreen !is AppScreen.CustomerDashboard && currentScreen !is AppScreen.ThemeCatalog) {
        viewModel.navigateBack()
    }

    val screenTitle = when (currentScreen) {
        is AppScreen.ThemeCatalog -> "Katalog Tema Undangan"
        is AppScreen.InvitationViewer -> "Undangan Digital"
        is AppScreen.CustomerDashboard -> "Dashboard Pelanggan"
        is AppScreen.CustomerCheckout -> "Pemesanan Undangan"
        is AppScreen.CustomerInvitationEditor -> "Editor Undangan"
        is AppScreen.CustomerGuestManager -> "Daftar Tamu & WhatsApp"
        is AppScreen.ResellerDashboard -> "Portal Mitra Reseller"
        is AppScreen.AdminDashboard -> "Super Admin Platform"
        is AppScreen.AiStudio -> "AI Wedding Studio"
        is AppScreen.WebsitePortal -> "Versi Website & Portal SaaS"
    }

    val showBackButton = currentScreen is AppScreen.CustomerCheckout ||
            currentScreen is AppScreen.CustomerInvitationEditor ||
            currentScreen is AppScreen.CustomerGuestManager ||
            currentScreen is AppScreen.InvitationViewer ||
            currentScreen is AppScreen.WebsitePortal

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("main_scaffold"),
        topBar = {
            AppHeaderBar(
                viewModel = viewModel,
                title = screenTitle,
                showBackButton = showBackButton,
                onBackClick = { viewModel.navigateBack() }
            )
        },
        bottomBar = {
            if (currentScreen !is AppScreen.InvitationViewer) {
                AppBottomNavigation(
                    viewModel = viewModel,
                    currentScreen = currentScreen
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is AppScreen.ThemeCatalog -> {
                    ThemeCatalogScreen(
                        viewModel = viewModel,
                        onSelectTheme = { theme ->
                            viewModel.navigateTo(AppScreen.CustomerCheckout(theme.id))
                        },
                        onPreviewTheme = { theme ->
                            viewModel.navigateTo(AppScreen.InvitationViewer(theme.slug, "Tamu Terhormat"))
                        }
                    )
                }
                is AppScreen.InvitationViewer -> {
                    PublicInvitationScreen(
                        viewModel = viewModel,
                        slug = screen.slug,
                        guestName = screen.guestName,
                        onBack = { viewModel.navigateBack() }
                    )
                }
                is AppScreen.CustomerDashboard -> {
                    CustomerDashboardScreen(
                        viewModel = viewModel,
                        onCreateNewOrder = {
                            viewModel.navigateTo(AppScreen.ThemeCatalog)
                        },
                        onEditInvitation = { orderId ->
                            viewModel.navigateTo(AppScreen.CustomerInvitationEditor(orderId))
                        },
                        onManageGuests = { orderId ->
                            viewModel.navigateTo(AppScreen.CustomerGuestManager(orderId))
                        },
                        onViewPublicInvitation = { orderNumber ->
                            // Look for invitation slug or fallback to fajar-dina
                            viewModel.navigateTo(AppScreen.InvitationViewer("fajar-dina", "Tamu Terhormat"))
                        }
                    )
                }
                is AppScreen.CustomerCheckout -> {
                    CustomerCheckoutScreen(
                        viewModel = viewModel,
                        initialThemeId = screen.themeId,
                        onOrderCreated = {
                            viewModel.navigateTo(AppScreen.CustomerDashboard)
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }
                is AppScreen.CustomerInvitationEditor -> {
                    CustomerInvitationEditorScreen(
                        viewModel = viewModel,
                        orderId = screen.orderId,
                        onPreviewLive = { slug ->
                            viewModel.navigateTo(AppScreen.InvitationViewer(slug, "Tamu Terhormat"))
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }
                is AppScreen.CustomerGuestManager -> {
                    CustomerGuestManagerScreen(
                        viewModel = viewModel,
                        orderId = screen.invitationId,
                        onBack = { viewModel.navigateBack() }
                    )
                }
                is AppScreen.ResellerDashboard -> {
                    ResellerDashboardScreen(
                        viewModel = viewModel,
                        onCreateClientOrder = {
                            viewModel.navigateTo(AppScreen.CustomerCheckout(1))
                        }
                    )
                }
                is AppScreen.AdminDashboard -> {
                    AdminDashboardScreen(viewModel = viewModel)
                }
                is AppScreen.AiStudio -> {
                    AiStudioScreen(viewModel = viewModel)
                }
                is AppScreen.WebsitePortal -> {
                    WebsitePortalScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                }
            }
        }
    }
}
