package com.undanganmobile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.undanganmobile.ui.PaymentAndReceiptDialog

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UndanganMobileTheme {
                Scaffold { innerPadding ->
                    WeddingInvitationDemo(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun WeddingInvitationDemo(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showPaymentDialog by remember { mutableStateOf(false) }
    var rsvpStatus by remember { mutableStateOf<String?>(null) } // "hadir" or "tidak_hadir"

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF8E9F4),
                            Color(0xFFFDF7F1),
                            Color(0xFFF2F7FF)
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "The Wedding of",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF6B5E62)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Nama Mempelai
            Text(
                text = "Rina & Arga",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8A5575),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Info Waktu & Tempat Acara
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sabtu, 21 Desember 2026",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4F4C5D)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "08.00 - Selesai WIB",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF6B5E62)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF8A5575)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Balai Nikah & Ballroom, Bandung",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Merupakan kehormatan bagi kami apabila Bapak/Ibu/Saudara/i berkenan hadir dan memberikan doa restu.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = Color(0xFF4F4C5D)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // =========================================================================
            // CARD KHUSUS: PEMBAYARAN MANUAL INDONESIA & CETAK STRUK
            // =========================================================================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_payment_section"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9F2)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE8C8B5))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFF8A5575), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Payments,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Pembayaran & Aktivasi",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF3E2D32)
                                )
                                Text(
                                    text = "Transfer Manual • Tanpa Biaya Admin",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = Color(0xFF7A6B70)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF25D366).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "via WhatsApp",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Tersedia berbagai pilihan pembayaran populer di Indonesia:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF5E4E53)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Badges Bank & E-Wallet Populer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        BankBadge("BCA", Color(0xFF005DAA))
                        BankBadge("Mandiri", Color(0xFF003876))
                        BankBadge("BRI", Color(0xFF00529C))
                        BankBadge("BNI", Color(0xFFEA5A0B))
                        BankBadge("BSI", Color(0xFF00A27E))
                        BankBadge("QRIS", Color(0xFFE52534))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Pelanggan cukup transfer ke rekening kami, simpan/cetak struk transaksi, dan kirimkan bukti transfer secara manual langsung ke WhatsApp Admin.",
                        fontSize = 12.sp,
                        color = Color(0xFF6B585E),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tombol Aksi Utama Pembayaran
                    Button(
                        onClick = { showPaymentDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_open_payment_modal"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A5575))
                    ) {
                        Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pilih Pembayaran & No. Rekening", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tombol Lihat & Cetak Struk
                    OutlinedButton(
                        onClick = { showPaymentDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_open_receipt_modal"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cetak Struk / Invoice Pembayaran", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Agenda Acara
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE0E7)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Agenda Acara",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3E2D32)
                    )

                    AgendaItem("08.00", "Persiapan tamu & registrasi")
                    AgendaItem("09.00", "Prosesi akad nikah khidmat")
                    AgendaItem("11.00", "Sambutan keluarga besar")
                    AgendaItem("12.00", "Resepsi & ramah tamah")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Konfirmasi Kehadiran (RSVP)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color(0xFF8A5575),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Konfirmasi Kehadiran (RSVP)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Mohon konfirmasi kehadiran Anda sebelum tanggal 10 Desember 2026.",
                        textAlign = TextAlign.Center,
                        color = Color(0xFF5E5A63)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                rsvpStatus = "hadir"
                                Toast.makeText(context, "Terima kasih telah mengonfirmasi Hadir!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = if (rsvpStatus == "hadir") ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                            else ButtonDefaults.buttonColors()
                        ) {
                            Text(if (rsvpStatus == "hadir") "✓ Hadir" else "Hadir")
                        }

                        OutlinedButton(
                            onClick = {
                                rsvpStatus = "tidak_hadir"
                                Toast.makeText(context, "Konfirmasi Tidak Hadir dicatat.", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (rsvpStatus == "tidak_hadir") "✓ Tidak Hadir" else "Tidak Hadir")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tombol Bagikan Undangan (Real Android Share Intent)
            Button(
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Undangan Pernikahan Rina & Arga\nSabtu, 21 Desember 2026 di Bandung.\nMohon doa restu Bapak/Ibu/Saudara/i!"
                        )
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Bagikan Undangan Pernikahan"))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Bagikan Undangan")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Modal Dialog Pembayaran Manual & Cetak Struk
    if (showPaymentDialog) {
        PaymentAndReceiptDialog(
            coupleNames = "Rina & Arga",
            onDismiss = { showPaymentDialog = false }
        )
    }
}

@Composable
fun BankBadge(label: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color,
        modifier = Modifier.height(24.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 7.dp)
        ) {
            Text(
                text = label,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun AgendaItem(time: String, title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = time,
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF8A5575),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF3E2D32)
        )
    }
}
