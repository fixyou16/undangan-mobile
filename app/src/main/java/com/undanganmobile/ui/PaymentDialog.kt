package com.undanganmobile.ui

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.undanganmobile.data.PaymentMethod
import com.undanganmobile.data.PaymentRepository
import com.undanganmobile.data.PaymentTransaction
import com.undanganmobile.data.WeddingPackage
import com.undanganmobile.util.ReceiptPrinterHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentAndReceiptDialog(
    coupleNames: String = "Rina & Arga",
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var currentStep by remember { mutableIntStateOf(0) } // 0: Pilih Metode, 1: Kirim WA, 2: Cetak Struk

    // State data transaksi
    var selectedPackage by remember { mutableStateOf(PaymentRepository.packages[1]) } // Default Paket Gold
    var selectedMethod by remember { mutableStateOf(PaymentRepository.paymentMethods[0]) } // Default BCA
    val uniqueCode by remember { mutableIntStateOf(Random.nextInt(100, 999)) }

    var customerName by remember { mutableStateOf("Budi Santoso") }
    var customerPhone by remember { mutableStateOf("08123456789") }
    var senderAccountName by remember { mutableStateOf("Budi Santoso") }
    var senderBankName by remember { mutableStateOf("BCA") }
    var notes by remember { mutableStateOf("Mohon segera diaktifkan ya min") }

    val invoiceNumber by remember {
        val dateCode = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val randomNum = Random.nextInt(1000, 9999)
        mutableStateOf("INV-$dateCode-$randomNum")
    }

    val transaction = remember(selectedPackage, selectedMethod, customerName, customerPhone, senderAccountName, senderBankName, notes, uniqueCode) {
        PaymentTransaction(
            invoiceNumber = invoiceNumber,
            customerName = customerName,
            customerPhone = customerPhone,
            coupleNames = coupleNames,
            weddingPackage = selectedPackage,
            paymentMethod = selectedMethod,
            uniqueCode = uniqueCode,
            totalAmount = selectedPackage.price + uniqueCode,
            senderAccountName = senderAccountName,
            senderBankName = senderBankName,
            notes = notes
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 28.dp),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Bar
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                if (currentStep > 0) currentStep-- else onDismiss()
                            }) {
                                Icon(
                                    imageVector = if (currentStep > 0) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Close,
                                    contentDescription = "Kembali"
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = when (currentStep) {
                                        0 -> "Pilih Pembayaran Manual"
                                        1 -> "Konfirmasi Bukti Transfer"
                                        else -> "Struk & Invoice Pembayaran"
                                    },
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Tanpa Biaya Admin • Verifikasi via WhatsApp",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Step Indicator
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text(
                                text = "Langkah ${currentStep + 1}/3",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Step Tab Navigation
                TabRow(
                    selectedTabIndex = currentStep,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = currentStep == 0,
                        onClick = { currentStep = 0 },
                        text = { Text("1. Bayar", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = currentStep == 1,
                        onClick = { currentStep = 1 },
                        text = { Text("2. Kirim WA", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = currentStep == 2,
                        onClick = { currentStep = 2 },
                        text = { Text("3. Cetak Struk", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                // Main Content View
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when (currentStep) {
                        0 -> StepSelectPayment(
                            packages = PaymentRepository.packages,
                            selectedPackage = selectedPackage,
                            onPackageSelected = { selectedPackage = it },
                            paymentMethods = PaymentRepository.paymentMethods,
                            selectedMethod = selectedMethod,
                            onMethodSelected = { selectedMethod = it },
                            transaction = transaction,
                            onNext = { currentStep = 1 }
                        )
                        1 -> StepConfirmWhatsApp(
                            transaction = transaction,
                            customerName = customerName,
                            onCustomerNameChange = { customerName = it },
                            customerPhone = customerPhone,
                            onCustomerPhoneChange = { customerPhone = it },
                            senderAccountName = senderAccountName,
                            onSenderAccountNameChange = { senderAccountName = it },
                            senderBankName = senderBankName,
                            onSenderBankNameChange = { senderBankName = it },
                            notes = notes,
                            onNotesChange = { notes = it },
                            onSendWhatsApp = {
                                ReceiptPrinterHelper.openWhatsAppConfirmation(context, transaction)
                            },
                            onViewReceipt = { currentStep = 2 }
                        )
                        2 -> StepReceiptView(
                            transaction = transaction,
                            onPrint = { ReceiptPrinterHelper.printReceipt(context, transaction) },
                            onCopyText = {
                                val text = ReceiptPrinterHelper.generateReceiptText(transaction)
                                ReceiptPrinterHelper.copyToClipboard(context, text, "Teks struk berhasil disalin!")
                            },
                            onShare = { ReceiptPrinterHelper.shareReceipt(context, transaction) },
                            onSendWhatsApp = { ReceiptPrinterHelper.openWhatsAppConfirmation(context, transaction) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * LANGKAH 1: Pilih Paket Undangan & Metode Pembayaran Manual
 */
@Composable
fun StepSelectPayment(
    packages: List<WeddingPackage>,
    selectedPackage: WeddingPackage,
    onPackageSelected: (WeddingPackage) -> Unit,
    paymentMethods: List<PaymentMethod>,
    selectedMethod: PaymentMethod,
    onMethodSelected: (PaymentMethod) -> Unit,
    transaction: PaymentTransaction,
    onNext: () -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Pilihan Paket Undangan
        item {
            Text(
                text = "Pilih Paket Undangan Digital",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                packages.forEach { pkg ->
                    val isSelected = pkg.id == selectedPackage.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPackageSelected(pkg) }
                            .testTag("package_${pkg.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { onPackageSelected(pkg) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = pkg.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = pkg.tagline,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = PaymentRepository.formatRupiah(pkg.price),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            }
        }

        // Pilihan Rekening Pembayaran Populer
        item {
            Text(
                text = "Metode Pembayaran (Transfer Bank & QRIS)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Pilih rekening tujuan transfer yang biasa Anda gunakan:",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                paymentMethods.forEach { method ->
                    val isSelected = method.id == selectedMethod.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMethodSelected(method) }
                            .testTag("payment_method_${method.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) method.bankColor.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                        ),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, method.bankColor)
                        else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = method.bankColor,
                                modifier = Modifier.size(width = 54.dp, height = 32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = method.badgeLabel,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = method.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = "${method.accountNumber} • a.n ${method.accountHolder}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            RadioButton(
                                selected = isSelected,
                                onClick = { onMethodSelected(method) }
                            )
                        }
                    }
                }
            }
        }

        // Kartu Instruksi Transfer Rekening Terpilih
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = selectedMethod.bankColor.copy(alpha = 0.08f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, selectedMethod.bankColor.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Rekening Tujuan Transfer:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = selectedMethod.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = selectedMethod.bankColor
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = selectedMethod.bankColor,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(
                                text = selectedMethod.badgeLabel,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // QRIS Barcode simulation jika memilih QRIS
                    if (selectedMethod.id == "qris") {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "QRIS STANDAR PEMBAYARAN NASIONAL",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE52534)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                // Canvas QR simulation
                                SimulatedQrCode(modifier = Modifier.size(160.dp))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Scan menggunakan GoPay, OVO, DANA, ShopeePay, BCA, Mandiri, dll",
                                    fontSize = 10.sp,
                                    textAlign = TextAlign.Center,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    } else {
                        // Nomor Rekening Card
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Nomor Rekening:",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = selectedMethod.accountNumber,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "a.n ${selectedMethod.accountHolder}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Button(
                                    onClick = {
                                        ReceiptPrinterHelper.copyToClipboard(
                                            context,
                                            selectedMethod.accountNumber,
                                            "Nomor rekening ${selectedMethod.name} berhasil disalin!"
                                        )
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = selectedMethod.bankColor),
                                    modifier = Modifier.testTag("btn_copy_account_number")
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Salin", fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Nominal Transfer dengan Kode Unik
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFF7ED),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFED7AA)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Total Nominal yang Harus Ditransfer:",
                                    fontSize = 11.sp,
                                    color = Color(0xFF9A3412)
                                )
                                Text(
                                    text = PaymentRepository.formatRupiah(transaction.totalAmount),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFFC2410C)
                                )
                                Text(
                                    text = "*Termasuk kode unik Rp ${transaction.uniqueCode} agar verifikasi otomatis",
                                    fontSize = 10.sp,
                                    color = Color(0xFFEA580C)
                                )
                            }

                            Button(
                                onClick = {
                                    ReceiptPrinterHelper.copyToClipboard(
                                        context,
                                        transaction.totalAmount.toString(),
                                        "Nominal transfer ${PaymentRepository.formatRupiah(transaction.totalAmount)} disalin!"
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC2410C))
                            ) {
                                Text("Salin Rp", fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Petunjuk Transfer:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    selectedMethod.instructions.forEachIndexed { idx, ins ->
                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                            Text(text = "${idx + 1}. ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = ins, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Tombol Lanjut ke Langkah 2
        item {
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_proceed_to_whatsapp"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Saya Sudah Transfer • Lanjut Kirim Bukti ke WA", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * LANGKAH 2: Konfirmasi Bukti Transfer via WhatsApp Admin
 */
@Composable
fun StepConfirmWhatsApp(
    transaction: PaymentTransaction,
    customerName: String,
    onCustomerNameChange: (String) -> Unit,
    customerPhone: String,
    onCustomerPhoneChange: (String) -> Unit,
    senderAccountName: String,
    onSenderAccountNameChange: (String) -> Unit,
    senderBankName: String,
    onSenderBankNameChange: (String) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    onSendWhatsApp: () -> Unit,
    onViewReceipt: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner Info WhatsApp
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA5D6A7)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFF25D366), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Konfirmasi Manual via WhatsApp",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )
                        )
                        Text(
                            text = "Sistem kami akan menyiapkan pesan rincian otomatis. Anda cukup mengirimkan pesan tersebut dan melampirkan screenshot bukti transfer.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }
        }

        // Summary Tagihan Singkat
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("No. Invoice:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(transaction.invoiceNumber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Paket:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(transaction.weddingPackage.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Transfer:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            PaymentRepository.formatRupiah(transaction.totalAmount),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Form Pengirim
        item {
            Text(
                text = "Data Pengirim Pembayaran",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = customerName,
                onValueChange = onCustomerNameChange,
                label = { Text("Nama Pemesan Undangan") },
                modifier = Modifier.fillMaxWidth().testTag("input_customer_name"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = customerPhone,
                onValueChange = onCustomerPhoneChange,
                label = { Text("Nomor WhatsApp Anda") },
                modifier = Modifier.fillMaxWidth().testTag("input_customer_phone"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = senderAccountName,
                onValueChange = onSenderAccountNameChange,
                label = { Text("Nama Pemilik Rekening Pengirim (Atas Nama)") },
                modifier = Modifier.fillMaxWidth().testTag("input_sender_account_name"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = senderBankName,
                onValueChange = onSenderBankNameChange,
                label = { Text("Bank / E-Wallet Asal Pengirim (misal: BCA, DANA, BRI)") },
                modifier = Modifier.fillMaxWidth().testTag("input_sender_bank_name"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = onNotesChange,
                label = { Text("Catatan Tambahan (Opsional)") },
                modifier = Modifier.fillMaxWidth().testTag("input_notes"),
                maxLines = 2
            )
        }

        // Tombol Kirim WhatsApp & Lihat Struk
        item {
            Button(
                onClick = onSendWhatsApp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_send_whatsapp_proof"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
            ) {
                Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Buka WhatsApp & Kirim Bukti Transfer", fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onViewReceipt,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_view_receipt"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Receipt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Lihat & Cetak Struk Pembayaran", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * LANGKAH 3: Tampilan Struk Kasir / Invoice Thermal & Fitur Cetak (PrintManager)
 */
@Composable
fun StepReceiptView(
    transaction: PaymentTransaction,
    onPrint: () -> Unit,
    onCopyText: () -> Unit,
    onShare: () -> Unit,
    onSendWhatsApp: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID")) }
    val dateStr = remember(transaction.timestamp) { dateFormat.format(Date(transaction.timestamp)) + " WIB" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Kartu Struk Kasir / Thermal Paper Style
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDDDDD)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("thermal_receipt_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Struk
                    Text(
                        text = "UNDANGAN MOBILE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.Black
                    )
                    Text(
                        text = "Platform SaaS Undangan Pernikahan",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.DarkGray
                    )
                    Text(
                        text = "CS WhatsApp: +62 812-3456-7890",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.Gray
                    )

                    DashedDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // Detail Invoice
                    ReceiptRow("No. Invoice", transaction.invoiceNumber, isBold = true)
                    ReceiptRow("Tanggal", dateStr)
                    ReceiptRow("Pengantin", transaction.coupleNames, isBold = true)
                    ReceiptRow("Pemesan", transaction.customerName)
                    ReceiptRow("Kontak", transaction.customerPhone)

                    DashedDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // Rincian Item
                    ReceiptRow(transaction.weddingPackage.name, PaymentRepository.formatRupiah(transaction.weddingPackage.price))
                    ReceiptRow("Kode Unik Transaksi", "+ " + PaymentRepository.formatRupiah(transaction.uniqueCode.toLong()))

                    Spacer(modifier = Modifier.height(8.dp))

                    // Total Tagihan Box
                    Surface(
                        color = Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TOTAL TAGIHAN",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color.Black
                            )
                            Text(
                                text = PaymentRepository.formatRupiah(transaction.totalAmount),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF8A5575)
                            )
                        }
                    }

                    DashedDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // Rekening Tujuan
                    ReceiptRow("Metode Bayar", transaction.paymentMethod.name)
                    ReceiptRow("No. Rekening", transaction.paymentMethod.accountNumber, isBold = true)
                    ReceiptRow("Atas Nama", transaction.paymentMethod.accountHolder)
                    if (transaction.senderAccountName.isNotBlank()) {
                        ReceiptRow("Pengirim", "${transaction.senderAccountName} (${transaction.senderBankName})")
                    }

                    DashedDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // Status Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "STATUS:",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Surface(
                            color = Color(0xFFFFF3CD),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFEEBA))
                        ) {
                            Text(
                                text = "MENUNGGU BUKTI WA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF856404),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Mini Barcode Simulation
                    SimulatedBarcode(modifier = Modifier.fillMaxWidth().height(36.dp))
                    Text(
                        text = transaction.invoiceNumber,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Simpan struk ini sebagai bukti pembayaran Anda.\nKirim foto transfer ke WhatsApp Admin untuk aktivasi.",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center,
                        color = Color.DarkGray
                    )
                }
            }
        }

        // Action Buttons: Cetak & Share
        item {
            // Tombol Cetak Dokumen / Simpan PDF
            Button(
                onClick = onPrint,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_print_receipt"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Print, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cetak Struk / Simpan sebagai PDF", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCopyText,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Salin Teks", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onShare,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Bagikan", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onSendWhatsApp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
            ) {
                Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Kirimkan Struk ke WhatsApp CS", fontWeight = FontWeight.SemiBold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ReceiptRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = Color.DarkGray
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = Color.Black
        )
    }
}

@Composable
fun DashedDivider(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxWidth().height(1.dp)) {
        val dashWidth = 10f
        val gapWidth = 6f
        var currentX = 0f
        while (currentX < size.width) {
            drawLine(
                color = Color.Gray,
                start = Offset(currentX, 0f),
                end = Offset((currentX + dashWidth).coerceAtMost(size.width), 0f),
                strokeWidth = 2f
            )
            currentX += dashWidth + gapWidth
        }
    }
}

@Composable
fun SimulatedBarcode(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val barCount = 45
        val barWidth = size.width / (barCount * 1.5f)
        var x = 0f
        for (i in 0 until barCount) {
            val isThick = (i % 3 == 0 || i % 7 == 0)
            val w = if (isThick) barWidth * 1.8f else barWidth
            drawRect(
                color = Color.Black,
                topLeft = Offset(x, 0f),
                size = Size(w, size.height)
            )
            x += w + barWidth * 0.8f
            if (x >= size.width) break
        }
    }
}

@Composable
fun SimulatedQrCode(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val gridSize = 19
        val cellSize = size.width / gridSize

        // Latar putih
        drawRect(Color.White, Offset.Zero, size)

        // Gambar modul QR acak terstruktur
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                // Tiga kotak pojok finder pattern
                val isFinderTopLeft = row < 5 && col < 5
                val isFinderTopRight = row < 5 && col >= gridSize - 5
                val isFinderBottomLeft = row >= gridSize - 5 && col < 5

                val shouldDraw = when {
                    isFinderTopLeft || isFinderTopRight || isFinderBottomLeft -> {
                        // Kotak luar dan titik tengah
                        (row == 0 || row == 4 || col == 0 || col == 4) ||
                        (row == 0 || row == 4 || col == gridSize - 5 || col == gridSize - 1) ||
                        (row == gridSize - 5 || row == gridSize - 1 || col == 0 || col == 4) ||
                        (row == 2 && col == 2) ||
                        (row == 2 && col == gridSize - 3) ||
                        (row == gridSize - 3 && col == 2)
                    }
                    else -> ((row * 7 + col * 13 + row * col) % 3 == 0)
                }

                if (shouldDraw) {
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(col * cellSize, row * cellSize),
                        size = Size(cellSize, cellSize)
                    )
                }
            }
        }
    }
}
