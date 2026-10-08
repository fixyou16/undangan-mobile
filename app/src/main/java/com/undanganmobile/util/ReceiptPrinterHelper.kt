package com.undanganmobile.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.undanganmobile.data.PaymentRepository
import com.undanganmobile.data.PaymentTransaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReceiptPrinterHelper {

    const val ADMIN_WHATSAPP_NUMBER = "6281234567890"

    /**
     * Membuka WhatsApp secara langsung dengan teks konfirmasi pembayaran yang telah diformat lengkap.
     */
    fun openWhatsAppConfirmation(
        context: Context,
        transaction: PaymentTransaction,
        adminNumber: String = ADMIN_WHATSAPP_NUMBER
    ) {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
        val dateStr = dateFormat.format(Date(transaction.timestamp)) + " WIB"

        val message = """
Halo Admin Undangan Mobile! 👋
Saya ingin konfirmasi pembayaran manual untuk pesanan undangan pernikahan digital kami:

📄 *No. Invoice:* ${transaction.invoiceNumber}
👰🤵 *Nama Mempelai:* ${transaction.coupleNames}
👤 *Nama Pemesan:* ${transaction.customerName} (${transaction.customerPhone})
📦 *Pilihan Paket:* ${transaction.weddingPackage.name}
🏦 *Tujuan Transfer:* ${transaction.paymentMethod.name}
🔢 *No. Rekening Tujuan:* ${transaction.paymentMethod.accountNumber}
💳 *Pengirim:* ${if (transaction.senderAccountName.isNotBlank()) transaction.senderAccountName else transaction.customerName} ${if (transaction.senderBankName.isNotBlank()) "(${transaction.senderBankName})" else ""}
💵 *Total Transfer:* ${PaymentRepository.formatRupiah(transaction.totalAmount)}
⏰ *Waktu:* $dateStr
${if (transaction.notes.isNotBlank()) "📝 *Catatan:* ${transaction.notes}\n" else ""}
Berikut foto/screenshot bukti transfernya terlampir min. Mohon diverifikasi agar undangan kami segera aktif. Terima kasih banyak! 🙏
        """.trimIndent()

        try {
            val encodedMessage = Uri.encode(message)
            // Coba buka aplikasi WhatsApp langsung
            val waIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://api.whatsapp.com/send?phone=$adminNumber&text=$encodedMessage")
                setPackage("com.whatsapp")
            }
            context.startActivity(waIntent)
        } catch (e: Exception) {
            try {
                // Fallback jika bukan WhatsApp standar (misal WA Business atau via Browser)
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://api.whatsapp.com/send?phone=$adminNumber&text=${Uri.encode(message)}")
                )
                context.startActivity(browserIntent)
            } catch (err: Exception) {
                // Fallback salin ke clipboard jika WhatsApp tidak terpasang
                copyToClipboard(context, message, "Pesan WhatsApp disalin ke clipboard!")
                Toast.makeText(context, "WhatsApp tidak ditemukan. Pesan disalin ke clipboard.", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Cetak Struk menggunakan PrintManager bawaan Android (bisa Cetak ke Printer atau Simpan sebagai PDF).
     */
    fun printReceipt(context: Context, transaction: PaymentTransaction) {
        try {
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
            if (printManager == null) {
                Toast.makeText(context, "Layanan cetak dokumen tidak tersedia di perangkat ini.", Toast.LENGTH_SHORT).show()
                return
            }

            val htmlContent = generateReceiptHtml(transaction)
            val webView = WebView(context)
            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    val printAdapter = webView.createPrintDocumentAdapter("Struk_${transaction.invoiceNumber}")
                    val printJobName = "Struk Undangan Mobile - ${transaction.invoiceNumber}"
                    val printAttributes = PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A5)
                        .setColorMode(PrintAttributes.COLOR_MODE_MONOCHROME)
                        .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                        .build()
                    printManager.print(printJobName, printAdapter, printAttributes)
                }
            }
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
            Toast.makeText(context, "Membuka dialog cetak struk...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal mencetak: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Generate format teks struk kasir termal.
     */
    fun generateReceiptText(transaction: PaymentTransaction): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale("id", "ID"))
        val dateStr = dateFormat.format(Date(transaction.timestamp))
        val separator = "------------------------------------------"
        val doubleSeparator = "=========================================="

        return """
$doubleSeparator
           UNDANGAN MOBILE INDONESIA
        BUKTI TAGIHAN & PEMBAYARAN MANUAL
           WhatsApp CS: +$ADMIN_WHATSAPP_NUMBER
$doubleSeparator
No. Invoice : ${transaction.invoiceNumber}
Tanggal     : $dateStr
Mempelai    : ${transaction.coupleNames}
Pemesan     : ${transaction.customerName}
Kontak WA   : ${transaction.customerPhone}
$separator
RINCIAN PEMBAYARAN:
1. ${transaction.weddingPackage.name}
   Harga Paket          : ${PaymentRepository.formatRupiah(transaction.weddingPackage.price)}
2. Kode Unik Transaksi  : ${PaymentRepository.formatRupiah(transaction.uniqueCode.toLong())}
$separator
TOTAL TAGIHAN           : ${PaymentRepository.formatRupiah(transaction.totalAmount)}
$separator
REKENING TUJUAN TRANSFER:
Bank/Metode : ${transaction.paymentMethod.name}
No. Rekening: ${transaction.paymentMethod.accountNumber}
Atas Nama   : ${transaction.paymentMethod.accountHolder}

DATA PENGIRIM TRANSFER:
Nama Pemilik Rekening : ${if (transaction.senderAccountName.isNotBlank()) transaction.senderAccountName else transaction.customerName}
Bank Asal Pengirim    : ${if (transaction.senderBankName.isNotBlank()) transaction.senderBankName else "-"}
$separator
STATUS : [ ${transaction.status.uppercase(Locale.getDefault())} ]
$doubleSeparator
INSTRUKSI KONFIRMASI:
1. Transfer TEPAT sebesar ${PaymentRepository.formatRupiah(transaction.totalAmount)}
2. Simpan struk ini / foto screenshot m-Banking
3. Kirim bukti bayar ke WhatsApp Admin: +$ADMIN_WHATSAPP_NUMBER
4. Undangan digital Anda akan langsung aktif!

       Terima kasih atas kepercayaan Anda
$doubleSeparator
        """.trimIndent()
    }

    /**
     * Generate HTML struk kasir yang elegan untuk cetak printer thermal / PDF.
     */
    fun generateReceiptHtml(transaction: PaymentTransaction): String {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
        val dateStr = dateFormat.format(Date(transaction.timestamp)) + " WIB"

        return """
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Struk Pembayaran - ${transaction.invoiceNumber}</title>
    <style>
        body {
            font-family: 'Courier New', Courier, monospace;
            width: 100%;
            max-width: 480px;
            margin: 0 auto;
            padding: 20px;
            color: #111;
            background: #fff;
            font-size: 13px;
            line-height: 1.4;
        }
        .text-center { text-align: center; }
        .text-right { text-align: right; }
        .bold { font-weight: bold; }
        .title { font-size: 18px; margin-bottom: 2px; }
        .subtitle { font-size: 11px; margin-bottom: 12px; color: #444; }
        .divider { border-top: 1px dashed #444; margin: 10px 0; }
        .double-divider { border-top: 2px dashed #111; margin: 12px 0; }
        table { width: 100%; border-collapse: collapse; }
        td { padding: 3px 0; vertical-align: top; }
        .badge {
            display: inline-block;
            padding: 3px 8px;
            background: #eee;
            border: 1px solid #999;
            font-weight: bold;
            font-size: 11px;
            border-radius: 4px;
        }
        .total-box {
            background: #f7f7f7;
            border: 1px solid #ccc;
            padding: 8px;
            margin: 8px 0;
            border-radius: 4px;
        }
        .footer { font-size: 11px; color: #555; margin-top: 16px; text-align: center; }
        @media print {
            body { padding: 0; width: 100%; }
        }
    </style>
</head>
<body>
    <div class="text-center">
        <div class="title bold">UNDANGAN MOBILE</div>
        <div class="subtitle">Platform SaaS Undangan Pernikahan Digital<br>WhatsApp Admin: +$ADMIN_WHATSAPP_NUMBER</div>
    </div>

    <div class="double-divider"></div>

    <table>
        <tr>
            <td class="bold">No. Invoice</td>
            <td class="text-right bold">${transaction.invoiceNumber}</td>
        </tr>
        <tr>
            <td>Tanggal</td>
            <td class="text-right">$dateStr</td>
        </tr>
        <tr>
            <td>Pengantin</td>
            <td class="text-right bold">${transaction.coupleNames}</td>
        </tr>
        <tr>
            <td>Pemesan</td>
            <td class="text-right">${transaction.customerName}</td>
        </tr>
        <tr>
            <td>Kontak WA</td>
            <td class="text-right">${transaction.customerPhone}</td>
        </tr>
    </table>

    <div class="divider"></div>

    <table>
        <tr>
            <td class="bold" colspan="2">RINCIAN PESANAN</td>
        </tr>
        <tr>
            <td>${transaction.weddingPackage.name}</td>
            <td class="text-right">${PaymentRepository.formatRupiah(transaction.weddingPackage.price)}</td>
        </tr>
        <tr>
            <td>Kode Unik Transaksi</td>
            <td class="text-right">+ ${PaymentRepository.formatRupiah(transaction.uniqueCode.toLong())}</td>
        </tr>
    </table>

    <div class="total-box">
        <table>
            <tr>
                <td class="bold" style="font-size: 14px;">TOTAL TAGIHAN</td>
                <td class="text-right bold" style="font-size: 15px;">${PaymentRepository.formatRupiah(transaction.totalAmount)}</td>
            </tr>
        </table>
    </div>

    <div class="divider"></div>

    <table>
        <tr>
            <td class="bold" colspan="2">TUJUAN REKENING TRANSFER:</td>
        </tr>
        <tr>
            <td>Metode/Bank</td>
            <td class="text-right bold">${transaction.paymentMethod.name}</td>
        </tr>
        <tr>
            <td>No. Rekening</td>
            <td class="text-right bold" style="font-size: 14px;">${transaction.paymentMethod.accountNumber}</td>
        </tr>
        <tr>
            <td>Atas Nama</td>
            <td class="text-right">${transaction.paymentMethod.accountHolder}</td>
        </tr>
    </table>

    <div class="divider"></div>

    <table>
        <tr>
            <td>Status</td>
            <td class="text-right"><span class="badge">${transaction.status.uppercase(Locale.getDefault())}</span></td>
        </tr>
    </table>

    <div class="double-divider"></div>

    <div class="footer">
        <p class="bold">PETUNJUK KONFIRMASI PEMBAYARAN:</p>
        <p>1. Transfer sesuai nominal tepat: <strong>${PaymentRepository.formatRupiah(transaction.totalAmount)}</strong></p>
        <p>2. Simpan struk ini sebagai bukti pembayaran yang sah.</p>
        <p>3. Kirimkan foto bukti transfer ke WhatsApp CS kami: <strong>+$ADMIN_WHATSAPP_NUMBER</strong></p>
        <p style="margin-top: 12px; font-style: italic;">Semoga hari bahagia pernikahan Anda penuh berkah!</p>
    </div>
</body>
</html>
        """.trimIndent()
    }

    fun copyToClipboard(context: Context, text: String, toastMessage: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Payment Info", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
    }

    fun shareReceipt(context: Context, transaction: PaymentTransaction) {
        val text = generateReceiptText(transaction)
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Bagikan Struk Pembayaran")
        context.startActivity(shareIntent)
    }
}
