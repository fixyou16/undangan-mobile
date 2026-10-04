package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ThemeEntity
import com.example.ui.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminDashboardScreen(
    viewModel: MainViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Themes, 1: Orders, 2: Webhooks, 3: Users

    val allThemes by viewModel.allThemes.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()
    val paymentEvents by viewModel.paymentEvents.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    var themeToEditPrice by remember { mutableStateOf<ThemeEntity?>(null) }
    var showAddThemeDialog by remember { mutableStateOf(false) }

    val totalRevenue = allOrders.filter { it.paymentStatus == "paid" }.sumOf { it.amount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("admin_dashboard_screen")
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Super Admin Console",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Manajemen Platform SaaS",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Kelola katalog tema, pantau transaksi & audit webhook.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFE8F5E9)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text("Omset Platform", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                        Text(
                            "Rp ${String.format("%,d", totalRevenue)}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Tab Selector Row
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Tema (${allThemes.size})", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Order (${allOrders.size})", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Webhook", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("User (${allUsers.size})", fontSize = 12.sp) }
                )
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                // THEMES MANAGEMENT
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Katalog Tema Undangan", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Button(
                            onClick = { showAddThemeDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("admin_add_theme_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tambah Tema", fontSize = 11.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(allThemes) { theme ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .testTag("admin_theme_card_${theme.slug}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(theme.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                    Text("Slug: ${theme.slug} • Kategori: ${theme.category}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = Color.Gray)
                                }

                                Switch(
                                    checked = theme.isActive,
                                    onCheckedChange = { viewModel.toggleThemeStatus(theme) },
                                    modifier = Modifier.testTag("toggle_theme_${theme.slug}")
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Harga: Rp ${String.format("%,d", theme.price)}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )

                                OutlinedButton(
                                    onClick = { themeToEditPrice = theme },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Ubah Harga", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                // ORDERS TAB
                items(allOrders) { order ->
                    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(order.orderNumber, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (order.paymentStatus == "paid") Color(0xFFE8F5E9) else Color(0xFFFFF8E1)
                                ) {
                                    Text(
                                        text = order.paymentStatus.uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (order.paymentStatus == "paid") Color(0xFF2E7D32) else Color(0xFFB45309),
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Nominal: Rp ${String.format("%,d", order.amount)}", style = MaterialTheme.typography.bodyMedium)
                            Text("Dibuat pada: ${sdf.format(Date(order.createdAt))}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = Color.Gray)
                        }
                    }
                }
            }
            2 -> {
                // PAYMENT AUDIT WEBHOOK LOGS
                item {
                    Text(
                        text = "Log Audit Webhook Midtrans (Idempoten)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Mencatat event webhook incoming bertanda tangan valid.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (paymentEvents.isEmpty()) {
                    item {
                        Text("Belum ada event webhook tercatat.", color = Color.Gray)
                    }
                } else {
                    items(paymentEvents) { evt ->
                        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale("id", "ID"))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(evt.eventId, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                    Text("Signature: VALID ✅", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Order Ref: ${evt.orderNumber} • Gross: Rp ${String.format("%,d", evt.grossAmount)}", style = MaterialTheme.typography.bodySmall)
                                Text("Diterima: ${sdf.format(Date(evt.receivedAt))}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Color.Gray)
                            }
                        }
                    }
                }
            }
            3 -> {
                // USERS TAB
                items(allUsers) { user ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(user.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when (user.role) {
                                        "super_admin" -> Color(0xFFE8F5E9)
                                        "reseller" -> Color(0xFFE3F2FD)
                                        else -> Color(0xFFF3E5F5)
                                    }
                                ) {
                                    Text(
                                        text = user.role.uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = when (user.role) {
                                                "super_admin" -> Color(0xFF2E7D32)
                                                "reseller" -> Color(0xFF1565C0)
                                                else -> Color(0xFF871A5B)
                                            },
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(user.email, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                            if (user.referredByResellerId != null) {
                                Text("Mitra Reseller ID: #${user.referredByResellerId}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal: Edit Theme Price
    themeToEditPrice?.let { theme ->
        var priceInput by remember { mutableStateOf(theme.price.toString()) }
        AlertDialog(
            onDismissRequest = { themeToEditPrice = null },
            title = { Text("Ubah Harga Tema: ${theme.name}", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Masukkan harga lisensi baru dalam Rupiah bulat (IDR):", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = priceInput,
                        onValueChange = { priceInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Harga (Rp)") },
                        prefix = { Text("Rp ") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_theme_price_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newPrice = priceInput.toLongOrNull() ?: theme.price
                        viewModel.updateThemePrice(theme, newPrice)
                        themeToEditPrice = null
                    },
                    modifier = Modifier.testTag("save_theme_price_button")
                ) {
                    Text("Simpan Harga")
                }
            },
            dismissButton = {
                TextButton(onClick = { themeToEditPrice = null }) {
                    Text("Batal")
                }
            }
        )
    }

    // Modal: Add New Theme
    if (showAddThemeDialog) {
        var newName by remember { mutableStateOf("") }
        var newSlug by remember { mutableStateOf("") }
        var newCategory by remember { mutableStateOf("Modern") }
        var newPrice by remember { mutableStateOf("119000") }
        var newDesc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddThemeDialog = false },
            title = { Text("Tambah Tema Baru", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = {
                            newName = it
                            if (newSlug.isBlank()) {
                                newSlug = it.lowercase(Locale.ROOT).replace("[^a-z0-9]".toRegex(), "-")
                            }
                        },
                        label = { Text("Nama Tema") },
                        modifier = Modifier.fillMaxWidth().testTag("add_theme_name_input")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newSlug,
                        onValueChange = { newSlug = it },
                        label = { Text("Slug Unik") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPrice,
                        onValueChange = { newPrice = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Harga (Rp)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newDesc,
                        onValueChange = { newDesc = it },
                        label = { Text("Deskripsi Tema") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            val priceVal = newPrice.toLongOrNull() ?: 99000
                            viewModel.addNewTheme(
                                ThemeEntity(
                                    name = newName,
                                    slug = newSlug.ifBlank { "theme-" + UUID.randomUUID().toString().substring(0, 4) },
                                    description = newDesc.ifBlank { "Tema pernikahan digital eksklusif." },
                                    previewImage = "theme_custom",
                                    price = priceVal,
                                    category = newCategory,
                                    isActive = true
                                )
                            )
                            showAddThemeDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_theme_button")
                ) {
                    Text("Tambah")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddThemeDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
