package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderEntity
import com.example.ui.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ResellerDashboardScreen(
    viewModel: MainViewModel,
    onCreateClientOrder: () -> Unit
) {
    val context = LocalContext.current
    val resellerOrders by viewModel.resellerOrders.collectAsState()
    val resellerCustomers by viewModel.resellerCustomers.collectAsState()

    val commissionRate = 0.25 // 25% komisi
    val paidOrders = resellerOrders.filter { it.paymentStatus == "paid" }
    val totalCommission = paidOrders.sumOf { (it.amount * commissionRate).toLong() }
    val totalVolume = paidOrders.sumOf { it.amount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("reseller_dashboard_screen")
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Portal Mitra Reseller",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color(0xFF1E88E5),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Mitra Wedding Bahagia 💼",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Kelola klien, buat order & pantau komisi penjualan.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onCreateClientOrder,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                    modifier = Modifier.testTag("create_client_order_button")
                ) {
                    Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Order Klien", fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Commission Stats Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Total Komisi (25%)", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Rp ${String.format("%,d", totalCommission)}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Volume Penjualan", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Rp ${String.format("%,d", totalVolume)}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E88E5)
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Klien Terhubung", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${resellerCustomers.size} Pengantin",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Total Order", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${resellerOrders.size} Pesanan",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Reseller Affiliate Link Box
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tautan Referral Afiliasi Reseller",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        )
                    )
                    Text(
                        text = "Bagikan tautan ini ke calon pengantin. Setiap pembelian otomatis menghasilkan komisi 25% untuk Anda.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF1E3A8A)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "https://undangan.id/?ref=MITRA-BAHAGIA",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = Color.DarkGray
                            )
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Link Referral", "https://undangan.id/?ref=MITRA-BAHAGIA")
                                    clipboard.setPrimaryClip(clip)
                                    viewModel.showSnackbar("Tautan referral berhasil disalin!")
                                },
                                modifier = Modifier.size(28.dp).testTag("copy_referral_link_button")
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Salin", tint = Color(0xFF1565C0), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Section: Daftar Pesanan Klien Reseller
        item {
            Text(
                text = "Riwayat Pesanan Pelanggan Reseller",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (resellerOrders.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("Belum ada pesanan dari klien Anda.", color = Color.Gray)
                    }
                }
            }
        } else {
            items(resellerOrders) { order ->
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
                val dateStr = sdf.format(Date(order.createdAt))
                val commission = (order.amount * commissionRate).toLong()

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
                                    text = if (order.paymentStatus == "paid") "Komisi Cair" else "Menunggu Pembayaran",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (order.paymentStatus == "paid") Color(0xFF2E7D32) else Color(0xFFB45309),
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Nilai Transaksi: Rp ${String.format("%,d", order.amount)}", style = MaterialTheme.typography.bodySmall)
                            Text(
                                "Komisi: Rp ${String.format("%,d", commission)}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                            )
                        }

                        Text("Dibuat pada $dateStr", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Color.Gray)
                    }
                }
            }
        }
    }
}
