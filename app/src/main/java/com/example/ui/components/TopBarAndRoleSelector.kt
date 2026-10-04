package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppRole
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.util.MusicPlayerSimulator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHeaderBar(
    viewModel: MainViewModel,
    title: String,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val isPlayingMusic by MusicPlayerSimulator.isPlaying.collectAsState()
    val currentTrack by MusicPlayerSimulator.currentTrack.collectAsState()
    var showRoleDialog by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 3.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            )
                        }
                        Text(
                            text = "Platform SaaS Undangan Digital",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.testTag("app_bar_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali"
                            )
                        }
                    } else {
                        IconButton(
                            onClick = { viewModel.navigateTo(AppScreen.ThemeCatalog) },
                            modifier = Modifier.testTag("app_bar_home_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Beranda",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    // Audio toggle button with animated indicator
                    IconButton(
                        onClick = { MusicPlayerSimulator.togglePlay() },
                        modifier = Modifier.testTag("music_toggle_button")
                    ) {
                        if (isPlayingMusic) {
                            AudioEqualizerAnimation()
                        } else {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = "Nyalakan Musik",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Role Chip Selector
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(android.graphics.Color.parseColor(currentRole.badgeColorHex)).copy(alpha = 0.15f),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable { showRoleDialog = true }
                            .testTag("role_switcher_chip")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(android.graphics.Color.parseColor(currentRole.badgeColorHex)))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (currentRole) {
                                    AppRole.CUSTOMER -> "Customer"
                                    AppRole.RESELLER -> "Reseller"
                                    AppRole.SUPER_ADMIN -> "Admin"
                                    AppRole.PUBLIC_GUEST -> "Tamu"
                                },
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(android.graphics.Color.parseColor(currentRole.badgeColorHex))
                                )
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Ganti Role",
                                tint = Color(android.graphics.Color.parseColor(currentRole.badgeColorHex)),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            )

            // Music status indicator banner if playing
            if (isPlayingMusic) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Audiotrack,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Memutar: $currentTrack",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Text(
                            text = "Jeda",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable { MusicPlayerSimulator.pause() }
                                .padding(4.dp)
                        )
                    }
                }
            }
        }
    }

    if (showRoleDialog) {
        AlertDialog(
            onDismissRequest = { showRoleDialog = false },
            title = {
                Text(
                    text = "Pilih Hak Akses (Role RBAC)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Aplikasi ini mengimplementasikan multi-role SaaS. Pilih peran untuk mengeksplorasi modul sistem:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    AppRole.entries.forEach { role ->
                        val isSelected = role == currentRole
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    viewModel.switchRole(role)
                                    showRoleDialog = false
                                }
                                .testTag("select_role_${role.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        viewModel.switchRole(role)
                                        showRoleDialog = false
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = role.label,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    )
                                    Text(
                                        text = when (role) {
                                            AppRole.CUSTOMER -> "Beli tema, kelola detail pengantin, tamu & bagikan via WA"
                                            AppRole.RESELLER -> "Kelola pesanan klien, pantau komisi 25%, tautan afiliasi"
                                            AppRole.SUPER_ADMIN -> "Kelola tema, ubah harga, pantau order & audit webhook"
                                            AppRole.PUBLIC_GUEST -> "Simulasi tamu membuka /i/{slug} & kirim RSVP buku tamu"
                                        },
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRoleDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }
}

@Composable
fun AudioEqualizerAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "eq")
    val h1 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(400, easing = LinearEasing), RepeatMode.Reverse),
        label = "h1"
    )
    val h2 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(550, easing = LinearEasing), RepeatMode.Reverse),
        label = "h2"
    )
    val h3 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(350, easing = LinearEasing), RepeatMode.Reverse),
        label = "h3"
    )

    Row(
        modifier = Modifier.size(24.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight(h1)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
        )
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight(h2)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
        )
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight(h3)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
        )
    }
}

@Composable
fun AppBottomNavigation(
    viewModel: MainViewModel,
    currentScreen: AppScreen
) {
    val currentRole by viewModel.currentRole.collectAsState()

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentScreen is AppScreen.ThemeCatalog,
            onClick = { viewModel.navigateTo(AppScreen.ThemeCatalog) },
            icon = { Icon(Icons.Default.Style, contentDescription = "Katalog Tema") },
            label = { Text("Tema", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_themes")
        )

        when (currentRole) {
            AppRole.CUSTOMER -> {
                NavigationBarItem(
                    selected = currentScreen is AppScreen.CustomerDashboard,
                    onClick = { viewModel.navigateTo(AppScreen.CustomerDashboard) },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Pesanan", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_customer_dashboard")
                )
            }
            AppRole.RESELLER -> {
                NavigationBarItem(
                    selected = currentScreen is AppScreen.ResellerDashboard,
                    onClick = { viewModel.navigateTo(AppScreen.ResellerDashboard) },
                    icon = { Icon(Icons.Default.MonetizationOn, contentDescription = "Komisi") },
                    label = { Text("Reseller", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_reseller_dashboard")
                )
            }
            AppRole.SUPER_ADMIN -> {
                NavigationBarItem(
                    selected = currentScreen is AppScreen.AdminDashboard,
                    onClick = { viewModel.navigateTo(AppScreen.AdminDashboard) },
                    icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
                    label = { Text("Admin", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_admin_dashboard")
                )
            }
            AppRole.PUBLIC_GUEST -> {
                NavigationBarItem(
                    selected = currentScreen is AppScreen.InvitationViewer,
                    onClick = { viewModel.navigateTo(AppScreen.InvitationViewer("fajar-dina", "Tamu Terhormat")) },
                    icon = { Icon(Icons.Default.Mail, contentDescription = "Undangan") },
                    label = { Text("Undangan", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_guest_invitation")
                )
            }
        }

        NavigationBarItem(
            selected = currentScreen is AppScreen.AiStudio,
            onClick = { viewModel.navigateTo(AppScreen.AiStudio) },
            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "AI Wedding") },
            label = { Text("AI Studio", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_ai_studio")
        )
    }
}
