package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
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
import com.example.data.model.OrderEntity
import com.example.data.model.ThemeEntity
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CustomerDashboardScreen(
    viewModel: MainViewModel,
    onCreateNewOrder: () -> Unit,
    onEditInvitation: (Long) -> Unit,
    onManageGuests: (Long) -> Unit,
    onViewPublicInvitation: (String) -> Unit
) {
    val orders by viewModel.customerOrders.collectAsState()
    val allThemes by viewModel.allThemes.collectAsState()

    val totalOrders = orders.size
    val unpaidOrders = orders.count { it.paymentStatus != "paid" }
    val publishedInvitations = orders.count { it.paymentStatus == "paid" }

    var selectedOrderForPayment by remember { mutableStateOf<OrderEntity?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("customer_dashboard_screen")
    ) {
        // Welcome and Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Dashboard Customer",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Halo, Fajar Pratama 👋",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Kelola pesanan dan undangan digital Anda.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onCreateNewOrder,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("create_invitation_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Buat Undangan", fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Summary Stats Grid (Laravel Customer Dashboard Spec)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryStatCard(
                    title = "Total Pesanan",
                    value = "$totalOrders",
                    valueColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                SummaryStatCard(
                    title = "Menunggu Bayar",
                    value = "$unpaidOrders",
                    valueColor = Color(0xFFD97706),
                    modifier = Modifier.weight(1f)
                )
                SummaryStatCard(
                    title = "Undangan Aktif",
                    value = "$publishedInvitations",
                    valueColor = Color(0xFF059669),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Web Version Feature Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(AppScreen.WebsitePortal) }
                    .testTag("banner_website_version")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Versi Website Mandiri (HTML5)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "BARU",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "Akses Website Landing Page SaaS & Demo Undangan Web responsif. Ekspor kode HTML, preview mobile/desktop!",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "Buka",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section Title: Pesanan Terbaru
        item {
            Text(
                text = "Daftar Pesanan & Undangan",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (orders.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.MailOutline,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Belum ada pesanan undangan.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = onCreateNewOrder) {
                            Text("Pilih Tema Sekarang")
                        }
                    }
                }
            }
        } else {
            items(orders) { order ->
                val theme = allThemes.firstOrNull { it.id == order.themeId }
                OrderCardItem(
                    order = order,
                    theme = theme,
                    onPay = { selectedOrderForPayment = order },
                    onEdit = { onEditInvitation(order.id) },
                    onManageGuests = {
                        // find invitation id for this order
                        onManageGuests(order.id)
                    },
                    onViewPublic = {
                        onViewPublicInvitation(order.orderNumber)
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    // Midtrans Payment Simulation Dialog
    selectedOrderForPayment?.let { order ->
        MidtransPaymentSimulatorModal(
            order = order,
            onDismiss = { selectedOrderForPayment = null },
            onSimulatePaid = {
                viewModel.simulatePaymentWebhook(order.orderNumber, isSuccess = true)
                selectedOrderForPayment = null
            },
            onSimulateFailed = {
                viewModel.simulatePaymentWebhook(order.orderNumber, isSuccess = false)
                selectedOrderForPayment = null
            }
        )
    }
}

@Composable
fun SummaryStatCard(
    title: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = valueColor
                )
            )
        }
    }
}

@Composable
fun OrderCardItem(
    order: OrderEntity,
    theme: ThemeEntity?,
    onPay: () -> Unit,
    onEdit: () -> Unit,
    onManageGuests: () -> Unit,
    onViewPublic: () -> Unit
) {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    val orderDate = sdf.format(Date(order.createdAt))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("order_card_${order.orderNumber}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Order Number and Payment Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.orderNumber,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = orderDate,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (order.paymentStatus) {
                        "paid" -> Color(0xFFE8F5E9)
                        "failed" -> Color(0xFFFFEBEE)
                        else -> Color(0xFFFFF8E1)
                    }
                ) {
                    Text(
                        text = when (order.paymentStatus) {
                            "paid" -> "LUNAS (PAID)"
                            "failed" -> "GAGAL"
                            else -> "BELUM BAYAR"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = when (order.paymentStatus) {
                                "paid" -> Color(0xFF2E7D32)
                                "failed" -> Color(0xFFC62828)
                                else -> Color(0xFFB45309)
                            }
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(10.dp))

            // Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Tema: ${theme?.name ?: "Tema Pilihan"}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "Status: ${order.status.replace("_", " ").uppercase()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "Rp ${String.format("%,d", order.amount)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (order.paymentStatus != "paid") {
                    Button(
                        onClick = onPay,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pay_order_button")
                    ) {
                        Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Bayar (Midtrans)", fontSize = 12.sp)
                    }
                } else {
                    OutlinedButton(
                        onClick = onEdit,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("edit_invitation_button")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = onManageGuests,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("manage_guests_button")
                    ) {
                        Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tamu & WA", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onViewPublic,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("view_public_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Lihat", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun MidtransPaymentSimulatorModal(
    order: OrderEntity,
    onDismiss: () -> Unit,
    onSimulatePaid: () -> Unit,
    onSimulateFailed: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = Color(0xFF1E88E5)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simulator Midtrans Snap", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Ini adalah simulasi integrasi payment gateway Midtrans untuk Order #${order.orderNumber}.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Tagihan:", style = MaterialTheme.typography.bodySmall)
                            Text(
                                "Rp ${String.format("%,d", order.amount)}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("BCA Virtual Account:", style = MaterialTheme.typography.bodySmall)
                            Text(
                                "700128910283",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Idempotency Ref:", style = MaterialTheme.typography.bodySmall)
                            Text(order.orderNumber, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Pada produksi Laravel, callback ditandatangani webhook provider (SHA512 signature). Klik tombol di bawah untuk mensimulasikan notifikasi webhook server-to-server:",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = Color.DarkGray
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSimulatePaid,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                modifier = Modifier.testTag("simulate_payment_success")
            ) {
                Text("Simulasi Lunas (Webhook PAID)")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onSimulateFailed,
                modifier = Modifier.testTag("simulate_payment_fail")
            ) {
                Text("Gagal / Batal", color = Color.Red)
            }
        }
    )
}
