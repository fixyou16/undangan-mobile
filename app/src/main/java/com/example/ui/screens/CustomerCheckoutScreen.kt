package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.AppRole
import com.example.ui.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerCheckoutScreen(
    viewModel: MainViewModel,
    initialThemeId: Long,
    onOrderCreated: (Long) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val allThemes by viewModel.allThemes.collectAsState()
    val resellerCustomers by viewModel.resellerCustomers.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    var selectedThemeId by remember { mutableLongStateOf(initialThemeId) }
    val selectedTheme = remember(allThemes, selectedThemeId) {
        allThemes.firstOrNull { it.id == selectedThemeId } ?: allThemes.firstOrNull()
    }

    var groomName by remember { mutableStateOf("Bagus Santoso, S.T.") }
    var brideName by remember { mutableStateOf("Rina Kartika, S.Pd.") }
    var venue by remember { mutableStateOf("Gedung Pertemuan Graha Samudra") }
    var address by remember { mutableStateOf("Jl. Yos Sudarso No. 12, Surabaya") }
    var mapsUrl by remember { mutableStateOf("https://maps.google.com/?q=Graha+Samudra+Surabaya") }
    var customSlug by remember { mutableStateOf("bagus-rina") }

    // Event Date picker simulation (default 30 days ahead)
    val defaultCalendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 30) }
    var eventAtMillis by remember { mutableLongStateOf(defaultCalendar.timeInMillis) }

    // Reseller customer selection
    var selectedCustomerId by remember { mutableLongStateOf(resellerCustomers.firstOrNull()?.id ?: 4L) }

    val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy - HH:mm 'WIB'", Locale("id", "ID"))

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("customer_checkout_screen")
    ) {
        item {
            Text(
                text = "Formulir Pemesanan Undangan",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Validasi data mempelai & rincian acara pernikahan.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // If Reseller, show Customer target selector
        if (currentRole == AppRole.RESELLER) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Mode Reseller: Buat Pesanan untuk Klien",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1565C0)
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Pilih pelanggan dari daftar referral Anda:",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        resellerCustomers.forEach { cust ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                RadioButton(
                                    selected = selectedCustomerId == cust.id,
                                    onClick = { selectedCustomerId = cust.id }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("${cust.name} (${cust.email})", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }

        // Selected Theme Summary Box
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tema Terpilih",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedTheme?.name ?: "Tema Pilihan",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Rp ${String.format("%,d", selectedTheme?.price ?: 99000)}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                    Text(
                        text = "Kategori: ${selectedTheme?.category} • Fitur: Musik, Galeri, RSVP, Buku Tamu",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Couple Information
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Data Mempelai",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = groomName,
                        onValueChange = { groomName = it },
                        label = { Text("Nama Pengantin Pria (Groom) *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_groom_name"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = brideName,
                        onValueChange = { brideName = it },
                        label = { Text("Nama Pengantin Wanita (Bride) *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_bride_name"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = customSlug,
                        onValueChange = { customSlug = it },
                        label = { Text("Kustom Link / Slug Undangan (/i/...) *") },
                        prefix = { Text("undangan.id/i/", color = Color.Gray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_slug_input"),
                        singleLine = true
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Event Schedule & Location
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. Jadwal & Lokasi Acara",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Tanggal & Waktu Resepsi:", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    text = sdf.format(Date(eventAtMillis)),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = venue,
                        onValueChange = { venue = it },
                        label = { Text("Nama Gedung / Tempat Acara *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_venue_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Alamat Lengkap") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_address_input"),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = mapsUrl,
                        onValueChange = { mapsUrl = it },
                        label = { Text("Tautan Google Maps") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_maps_input"),
                        singleLine = true
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Summary and Submit
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ringkasan Pembayaran",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Harga Lisensi Tema:", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "Rp ${String.format("%,d", selectedTheme?.price ?: 99000)}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Biaya Layanan & Hosting:", style = MaterialTheme.typography.bodySmall)
                        Text("Rp 0 (GRATIS)", style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF059669), fontWeight = FontWeight.Bold))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Tagihan:", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        Text(
                            "Rp ${String.format("%,d", selectedTheme?.price ?: 99000)}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (groomName.isBlank() || brideName.isBlank() || venue.isBlank()) {
                        viewModel.showSnackbar("Silakan lengkapi nama mempelai dan nama lokasi acara.")
                        return@Button
                    }
                    viewModel.createOrder(
                        themeId = selectedTheme?.id ?: 1L,
                        groomName = groomName,
                        brideName = brideName,
                        eventAt = eventAtMillis,
                        venue = venue,
                        address = address.ifBlank { null },
                        mapsUrl = mapsUrl.ifBlank { null },
                        customSlug = customSlug.ifBlank { null },
                        forCustomerId = if (currentRole == AppRole.RESELLER) selectedCustomerId else null
                    ) { order ->
                        onOrderCreated(order.id)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("confirm_order_button")
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Konfirmasi & Buat Pesanan", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
