package com.undanganmobile.data

import androidx.compose.ui.graphics.Color
import java.text.NumberFormat
import java.util.Locale

data class WeddingPackage(
    val id: String,
    val name: String,
    val tagline: String,
    val price: Long,
    val features: List<String>
)

data class PaymentMethod(
    val id: String,
    val name: String,
    val category: String, // "Transfer Bank", "QRIS Instant", "E-Wallet"
    val accountNumber: String,
    val accountHolder: String,
    val bankColor: Color,
    val badgeLabel: String,
    val instructions: List<String>
)

data class PaymentTransaction(
    val invoiceNumber: String,
    val customerName: String,
    val customerPhone: String,
    val coupleNames: String,
    val weddingPackage: WeddingPackage,
    val paymentMethod: PaymentMethod,
    val uniqueCode: Int,
    val totalAmount: Long,
    val senderAccountName: String = "",
    val senderBankName: String = "",
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Menunggu Verifikasi WhatsApp"
)

object PaymentRepository {

    val packages = listOf(
        WeddingPackage(
            id = "silver",
            name = "Paket Silver (Hemat)",
            tagline = "Cocok untuk acara akad intim",
            price = 75000L,
            features = listOf(
                "Masa Aktif 3 Bulan",
                "Musik Latar Romantis",
                "Navigasi Google Maps",
                "Hitung Mundur Acara",
                "Buku Tamu Sederhana (Maks 100 Tamu)"
            )
        ),
        WeddingPackage(
            id = "gold",
            name = "Paket Gold (Paling Populer)",
            tagline = "Pilihan favorit pengantin modern",
            price = 125000L,
            features = listOf(
                "Masa Aktif Selamanya",
                "Musik Latar Autoplay",
                "Navigasi Google Maps Presisi",
                "RSVP Online & Buku Tamu Realtime",
                "Amplop Digital Multi-Rekening & QRIS",
                "Galeri 10 Foto Prewedding",
                "Tamu Tanpa Batas (Unlimited)"
            )
        ),
        WeddingPackage(
            id = "platinum",
            name = "Paket Platinum (Eksklusif)",
            tagline = "Fitur terlengkap & prioritas",
            price = 199000L,
            features = listOf(
                "Semua Fitur Paket Gold",
                "Custom Domain Web (/i/nama-mempelai)",
                "Galeri Hingga 25 Foto + Video",
                "Generator WhatsApp Blast Tamu",
                "Bantuan Input Data dari Admin",
                "Prioritas Customer Support 24/7"
            )
        )
    )

    val paymentMethods = listOf(
        PaymentMethod(
            id = "bca",
            name = "BCA (Bank Central Asia)",
            category = "Transfer Bank",
            accountNumber = "8720192831",
            accountHolder = "PT UNDANGAN DIGITAL KITA",
            bankColor = Color(0xFF005DAA),
            badgeLabel = "BCA",
            instructions = listOf(
                "Buka aplikasi BCA Mobile / KlikBCA / ATM BCA",
                "Pilih menu Transfer > Antar Rekening BCA",
                "Masukkan No. Rekening: 8720192831",
                "Pastikan nama penerima: PT UNDANGAN DIGITAL KITA",
                "Masukkan nominal transfer TEPAT beserta kode unik",
                "Simpan bukti transfer dan kirimkan ke WhatsApp Admin"
            )
        ),
        PaymentMethod(
            id = "mandiri",
            name = "Bank Mandiri",
            category = "Transfer Bank",
            accountNumber = "1370019283741",
            accountHolder = "UNDANGAN MOBILE INDONESIA",
            bankColor = Color(0xFF003876),
            badgeLabel = "MANDIRI",
            instructions = listOf(
                "Buka aplikasi Livin' by Mandiri / ATM Mandiri",
                "Pilih menu Transfer Rupiah > Transfer ke Mandiri",
                "Masukkan No. Rekening: 1370019283741",
                "Pastikan nama penerima: UNDANGAN MOBILE INDONESIA",
                "Masukkan nominal TEPAT sesuai tagihan",
                "Simpan bukti transfer dan kirimkan ke WhatsApp Admin"
            )
        ),
        PaymentMethod(
            id = "bri",
            name = "Bank BRI",
            category = "Transfer Bank",
            accountNumber = "034101002938531",
            accountHolder = "UNDANGAN MOBILE INDONESIA",
            bankColor = Color(0xFF00529C),
            badgeLabel = "BRI",
            instructions = listOf(
                "Buka aplikasi BRImo / ATM BRI",
                "Pilih Transfer > Tambah Daftar Baru > Bank BRI",
                "Masukkan No. Rekening: 034101002938531",
                "Cek nama penerima: UNDANGAN MOBILE INDONESIA",
                "Kirimkan nominal sesuai rincian struk invoice",
                "Simpan struk / bukti transfer untuk konfirmasi WA"
            )
        ),
        PaymentMethod(
            id = "bni",
            name = "Bank BNI",
            category = "Transfer Bank",
            accountNumber = "0981726354",
            accountHolder = "UNDANGAN MOBILE INDONESIA",
            bankColor = Color(0xFFEA5A0B),
            badgeLabel = "BNI",
            instructions = listOf(
                "Buka aplikasi BNI Mobile Banking / ATM BNI",
                "Pilih Transfer > Antar BNI",
                "Masukkan No. Rekening: 0981726354",
                "Pastikan nama: UNDANGAN MOBILE INDONESIA",
                "Masukkan nominal pas dan selesaikan transaksi",
                "Kirim bukti struk transfer ke WhatsApp Admin"
            )
        ),
        PaymentMethod(
            id = "bsi",
            name = "Bank Syariah Indonesia (BSI)",
            category = "Transfer Bank Syariah",
            accountNumber = "7182930415",
            accountHolder = "UNDANGAN MOBILE INDONESIA",
            bankColor = Color(0xFF00A27E),
            badgeLabel = "BSI",
            instructions = listOf(
                "Buka BSI Mobile / ATM BSI",
                "Pilih menu Transfer > Antar BSI",
                "Masukkan No. Rekening: 7182930415",
                "Pastikan nama penerima: UNDANGAN MOBILE INDONESIA",
                "Transfer sesuai nominal dan simpan bukti transfer"
            )
        ),
        PaymentMethod(
            id = "qris",
            name = "QRIS Universal (Semua Aplikasi)",
            category = "QRIS Instant",
            accountNumber = "NMID: ID1020304050607",
            accountHolder = "UNDANGAN MOBILE OFFICIAL",
            bankColor = Color(0xFFE52534),
            badgeLabel = "QRIS",
            instructions = listOf(
                "Buka aplikasi m-Banking (BCA, Mandiri, BRI, BNI) atau E-Wallet (GoPay, OVO, DANA, ShopeePay)",
                "Pilih menu 'Scan QRIS'",
                "Arahkan kamera ke Barcode QRIS di bawah",
                "Masukkan nominal transfer sesuai tagihan struk",
                "Selesaikan pembayaran & screenshot bukti sukses transfer"
            )
        ),
        PaymentMethod(
            id = "dana_gopay",
            name = "DANA / GoPay / ShopeePay",
            category = "E-Wallet",
            accountNumber = "081234567890",
            accountHolder = "ADMIN UNDANGAN MOBILE",
            bankColor = Color(0xFF118EEA),
            badgeLabel = "E-WALLET",
            instructions = listOf(
                "Buka aplikasi DANA / GoPay / ShopeePay",
                "Pilih menu Kirim / Transfer ke Nomor HP",
                "Masukkan No. HP: 0812-3456-7890",
                "Konfirmasi nama penerima: ADMIN UNDANGAN MOBILE",
                "Kirim saldo sesuai total nominal struk",
                "Kirimkan tangkapan layar pembayaran ke WhatsApp Admin"
            )
        )
    )

    fun formatRupiah(amount: Long): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatter.maximumFractionDigits = 0
        return formatter.format(amount).replace("Rp", "Rp ")
    }
}
