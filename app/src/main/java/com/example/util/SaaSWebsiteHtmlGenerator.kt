package com.example.util

object SaaSWebsiteHtmlGenerator {

    fun generateLandingPageHtml(): String {
        return """
<!DOCTYPE html>
<html lang="id">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Remix UndanganKu — Platform SaaS Undangan Digital Pernikahan</title>
  <!-- Tailwind CSS -->
  <script src="https://cdn.tailwindcss.com"></script>
  <!-- Google Fonts -->
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,600;0,700;1,600&family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
  <style>
    body {
      font-family: 'Plus Jakarta Sans', sans-serif;
      scroll-behavior: smooth;
    }
    .font-serif-luxury {
      font-family: 'Playfair Display', serif;
    }
    .gradient-hero {
      background: radial-gradient(circle at top right, rgba(135, 26, 91, 0.15), transparent 50%),
                  radial-gradient(circle at bottom left, rgba(30, 136, 229, 0.12), transparent 50%);
    }
    .primary-accent {
      color: #871A5B;
    }
    .bg-primary-accent {
      background-color: #871A5B;
    }
  </style>
</head>
<body class="bg-stone-900 text-stone-100 antialiased selection:bg-pink-700 selection:text-white">

  <!-- ================= NAVBAR ================= -->
  <nav class="sticky top-0 z-50 backdrop-blur-md bg-stone-900/80 border-b border-stone-800">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
      <div class="flex items-center gap-3">
        <div class="w-9 h-9 rounded-xl bg-gradient-to-br from-pink-600 to-purple-800 flex items-center justify-center font-bold text-white shadow-lg">
          U
        </div>
        <span class="font-serif-luxury text-xl font-bold tracking-tight text-white">Remix <span class="text-pink-400">UndanganKu</span></span>
      </div>
      
      <div class="hidden md:flex items-center gap-8 text-sm font-medium text-stone-300">
        <a href="#fitur" class="hover:text-pink-400 transition-colors">Fitur Unggulan</a>
        <a href="#tema" class="hover:text-pink-400 transition-colors">Katalog Tema</a>
        <a href="#harga" class="hover:text-pink-400 transition-colors">Paket Harga</a>
        <a href="#reseller" class="hover:text-pink-400 transition-colors">Mitra Reseller</a>
        <a href="#kalkulator" class="hover:text-pink-400 transition-colors">Simulasi Cuan</a>
        <a href="#faq" class="hover:text-pink-400 transition-colors">FAQ</a>
      </div>

      <div class="flex items-center gap-3">
        <a href="#harga" class="px-4 py-2 rounded-xl text-xs font-semibold bg-gradient-to-r from-pink-600 to-purple-700 text-white shadow-md hover:brightness-110 active:scale-95 transition-all">
          Buat Undangan
        </a>
      </div>
    </div>
  </nav>

  <!-- ================= HERO SECTION ================= -->
  <header class="relative pt-20 pb-28 overflow-hidden gradient-hero">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center relative z-10">
      <span class="inline-flex items-center gap-2 px-3.5 py-1 rounded-full text-xs font-semibold bg-pink-950/60 border border-pink-700/40 text-pink-300 mb-6">
        <span class="w-2 h-2 rounded-full bg-pink-400 animate-ping"></span>
        Platform SaaS Undangan Digital Pernikahan #1
      </span>
      <h1 class="text-4xl sm:text-6xl lg:text-7xl font-serif-luxury font-bold text-white tracking-tight leading-tight max-w-4xl mx-auto">
        Kirim Undangan Nikah Impian, Elegan & Instan dalam <span class="bg-gradient-to-r from-pink-400 to-purple-300 bg-clip-text text-transparent">Hitungan Menit</span>
      </h1>
      <p class="text-base sm:text-lg text-stone-300 max-w-2xl mx-auto mt-6 leading-relaxed">
        Dilengkapi RSVP Online interaktif, Buku Tamu digital, Amplop QRIS/Bank otomatis, integrasi Google Maps, pemutar musik romantis, dan generator WhatsApp blast untuk para tamu.
      </p>

      <div class="mt-8 flex flex-col sm:flex-row items-center justify-center gap-4">
        <a href="#tema" class="w-full sm:w-auto px-8 py-3.5 rounded-2xl bg-gradient-to-r from-pink-600 to-purple-700 text-white font-semibold text-sm shadow-xl shadow-pink-900/30 hover:brightness-110 active:scale-95 transition-all">
          Lihat Katalog Tema
        </a>
        <a href="#reseller" class="w-full sm:w-auto px-8 py-3.5 rounded-2xl bg-stone-800 border border-stone-700 text-white font-semibold text-sm hover:bg-stone-700 active:scale-95 transition-all">
          Gabung Mitra Reseller (Komisi 25%)
        </a>
      </div>

      <!-- Trust Badges -->
      <div class="mt-14 pt-8 border-t border-stone-800/80 grid grid-cols-2 md:grid-cols-4 gap-6 max-w-4xl mx-auto text-left">
        <div>
          <h4 class="text-2xl font-bold text-white">10.000+</h4>
          <p class="text-xs text-stone-400 mt-1">Undangan Dibuat & Disebar</p>
        </div>
        <div>
          <h4 class="text-2xl font-bold text-pink-400">99.9%</h4>
          <p class="text-xs text-stone-400 mt-1">Uptime Server Cepat & Ringan</p>
        </div>
        <div>
          <h4 class="text-2xl font-bold text-white">25%</h4>
          <p class="text-xs text-stone-400 mt-1">Komisi Reseller Langsung Cair</p>
        </div>
        <div>
          <h4 class="text-2xl font-bold text-purple-400">4.9 / 5.0</h4>
          <p class="text-xs text-stone-400 mt-1">Kepuasan Pengantin</p>
        </div>
      </div>
    </div>
  </header>

  <!-- ================= FITUR UNGGULAN ================= -->
  <section id="fitur" class="py-24 bg-stone-950/60 border-y border-stone-800">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="text-center max-w-3xl mx-auto mb-16">
        <span class="text-xs uppercase tracking-widest text-pink-400 font-semibold">Fitur Lengkap Modern</span>
        <h2 class="text-3xl sm:text-4xl font-serif-luxury font-bold text-white mt-2">Semua yang Anda Butuhkan untuk Undangan Sempurna</h2>
        <p class="text-sm text-stone-400 mt-3">Tidak perlu coding atau keahlian teknis. Desain undangan pernikahan mewah langsung selesai dari genggaman Anda.</p>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
        <!-- Feature 1 -->
        <div class="p-8 rounded-3xl bg-stone-900 border border-stone-800 hover:border-pink-500/50 transition-all group">
          <div class="w-12 h-12 rounded-2xl bg-pink-950 flex items-center justify-center text-pink-400 mb-6 group-hover:scale-110 transition-transform">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"/></svg>
          </div>
          <h3 class="text-lg font-bold text-white">RSVP & Buku Tamu Realtime</h3>
          <p class="text-xs text-stone-400 mt-2 leading-relaxed">
            Dapatkan konfirmasi kehadiran tamu secara otomatis langsung tersimpan ke database. Tamu juga dapat menyampaikan doa restu yang tampil cantik di dinding ucapan.
          </p>
        </div>

        <!-- Feature 2 -->
        <div class="p-8 rounded-3xl bg-stone-900 border border-stone-800 hover:border-pink-500/50 transition-all group">
          <div class="w-12 h-12 rounded-2xl bg-purple-950 flex items-center justify-center text-purple-400 mb-6 group-hover:scale-110 transition-transform">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"/></svg>
          </div>
          <h3 class="text-lg font-bold text-white">Amplop Digital & QRIS</h3>
          <p class="text-xs text-stone-400 mt-2 leading-relaxed">
            Memudahkan tamu yang berhalangan hadir atau ingin memberi hadiah pernikahan langsung melalui transfer rekening Bank BCA, Mandiri, BRI atau scan barcode QRIS.
          </p>
        </div>

        <!-- Feature 3 -->
        <div class="p-8 rounded-3xl bg-stone-900 border border-stone-800 hover:border-pink-500/50 transition-all group">
          <div class="w-12 h-12 rounded-2xl bg-emerald-950 flex items-center justify-center text-emerald-400 mb-6 group-hover:scale-110 transition-transform">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/></svg>
          </div>
          <h3 class="text-lg font-bold text-white">Generator WhatsApp Blast</h3>
          <p class="text-xs text-stone-400 mt-2 leading-relaxed">
            Buat pesan WhatsApp personal otomatis untuk tiap nama tamu, lengkap dengan tautan unik <code class="text-emerald-300">/i/slug?to=NamaTamu</code> dengan 1x klik kirim.
          </p>
        </div>

        <!-- Feature 4 -->
        <div class="p-8 rounded-3xl bg-stone-900 border border-stone-800 hover:border-pink-500/50 transition-all group">
          <div class="w-12 h-12 rounded-2xl bg-blue-950 flex items-center justify-center text-blue-400 mb-6 group-hover:scale-110 transition-transform">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19V6l12-3v13M9 19c0 1.105-1.343 2-3 2s-3-.895-3-2 1.343-2 3-2 3 .895 3 2zm12-3c0 1.105-1.343 2-3 2s-3-.895-3-2 1.343-2 3-2 3 .895 3 2zM9 10l12-3"/></svg>
          </div>
          <h3 class="text-lg font-bold text-white">Autoplay Musik Romantis</h3>
          <p class="text-xs text-stone-400 mt-2 leading-relaxed">
            Iringi pembukaan amplop digital dengan lagu-lagu pernikahan terindah secara otomatis saat tamu menekan tombol buka undangan.
          </p>
        </div>

        <!-- Feature 5 -->
        <div class="p-8 rounded-3xl bg-stone-900 border border-stone-800 hover:border-pink-500/50 transition-all group">
          <div class="w-12 h-12 rounded-2xl bg-amber-950 flex items-center justify-center text-amber-400 mb-6 group-hover:scale-110 transition-transform">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"/></svg>
          </div>
          <h3 class="text-lg font-bold text-white">Navigasi Google Maps</h3>
          <p class="text-xs text-stone-400 mt-2 leading-relaxed">
            Tamu tidak akan tersesat. Tombol langsung terhubung ke Google Maps dengan titik presisi lokasi akad dan gedung resepsi pernikahan.
          </p>
        </div>

        <!-- Feature 6 -->
        <div class="p-8 rounded-3xl bg-stone-900 border border-stone-800 hover:border-pink-500/50 transition-all group">
          <div class="w-12 h-12 rounded-2xl bg-rose-950 flex items-center justify-center text-rose-400 mb-6 group-hover:scale-110 transition-transform">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/></svg>
          </div>
          <h3 class="text-lg font-bold text-white">AI Wedding Copywriter</h3>
          <p class="text-xs text-stone-400 mt-2 leading-relaxed">
            Bingung merangkai kata cinta? Asisten AI siap menyusun Love Story puitis, kutipan mutiara adat/Islami, serta saran dress code tamu yang serasi.
          </p>
        </div>
      </div>
    </div>
  </section>

  <!-- ================= TEMA UNGGULAN ================= -->
  <section id="tema" class="py-24">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="text-center max-w-3xl mx-auto mb-16">
        <span class="text-xs uppercase tracking-widest text-pink-400 font-semibold">Desain Eksklusif</span>
        <h2 class="text-3xl sm:text-4xl font-serif-luxury font-bold text-white mt-2">Koleksi Tema Pernikahan Terfavorit</h2>
        <p class="text-sm text-stone-400 mt-3">Setiap tema dirancang responsif, estetik, dan nyaman dibaca di smartphone, tablet, maupun laptop.</p>
      </div>

      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
        <!-- Theme 1 -->
        <div class="bg-stone-900 border border-stone-800 rounded-3xl overflow-hidden hover:border-pink-500 transition-all group">
          <div class="h-48 bg-gradient-to-br from-pink-900 via-purple-950 to-stone-950 p-6 flex flex-col justify-between">
            <span class="px-3 py-1 rounded-full text-[10px] font-bold bg-white/20 text-white w-max">Modern Minimalist</span>
            <div>
              <h4 class="font-serif-luxury text-2xl text-white">Chic Rose Elegance</h4>
              <p class="text-xs text-stone-300">Clean, Typography Elegan, Aesthetic</p>
            </div>
          </div>
          <div class="p-6">
            <p class="text-xs text-stone-400 leading-relaxed">Cocok untuk pernikahan intim modern dengan aksen warna dusty rose, font modern, dan transisi halus.</p>
            <div class="mt-4 flex items-center justify-between">
              <span class="text-lg font-bold text-white">Rp 99.000</span>
              <span class="text-xs text-pink-400 font-semibold">Termasuk Musik & RSVP</span>
            </div>
          </div>
        </div>

        <!-- Theme 2 -->
        <div class="bg-stone-900 border border-stone-800 rounded-3xl overflow-hidden hover:border-pink-500 transition-all group">
          <div class="h-48 bg-gradient-to-br from-amber-900 via-stone-950 to-stone-950 p-6 flex flex-col justify-between">
            <span class="px-3 py-1 rounded-full text-[10px] font-bold bg-amber-500/20 text-amber-300 w-max">Adat Tradisional</span>
            <div>
              <h4 class="font-serif-luxury text-2xl text-white">Royal Javanese Gold</h4>
              <p class="text-xs text-stone-300">Batik Parang, Ornamen Keraton, Sakral</p>
            </div>
          </div>
          <div class="p-6">
            <p class="text-xs text-stone-400 leading-relaxed">Sentuhan nuansa adat Jawa keraton dengan latar keemasan, kutipan bahasa Jawa halus, dan tembang syahdu.</p>
            <div class="mt-4 flex items-center justify-between">
              <span class="text-lg font-bold text-white">Rp 149.000</span>
              <span class="text-xs text-amber-400 font-semibold">Favorit Tradisional</span>
            </div>
          </div>
        </div>

        <!-- Theme 3 -->
        <div class="bg-stone-900 border border-stone-800 rounded-3xl overflow-hidden hover:border-pink-500 transition-all group">
          <div class="h-48 bg-gradient-to-br from-emerald-950 via-teal-950 to-stone-950 p-6 flex flex-col justify-between">
            <span class="px-3 py-1 rounded-full text-[10px] font-bold bg-emerald-500/20 text-emerald-300 w-max">Rustic & Outdoor</span>
            <div>
              <h4 class="font-serif-luxury text-2xl text-white">Rustic Botanical Leaf</h4>
              <p class="text-xs text-stone-300">Earthy Tone, Daun Sage, Warm Vibe</p>
            </div>
          </div>
          <div class="p-6">
            <p class="text-xs text-stone-400 leading-relaxed">Konsep pesta kebun atau semi-outdoor dengan ornamen dedaunan botani alami, palet sage green dan kayu hangat.</p>
            <div class="mt-4 flex items-center justify-between">
              <span class="text-lg font-bold text-white">Rp 129.000</span>
              <span class="text-xs text-emerald-400 font-semibold">Outdoor & Garden</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>

  <!-- ================= PAKET HARGA ================= -->
  <section id="harga" class="py-24 bg-stone-950/60 border-y border-stone-800">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="text-center max-w-3xl mx-auto mb-16">
        <span class="text-xs uppercase tracking-widest text-pink-400 font-semibold">Investasi Terjangkau</span>
        <h2 class="text-3xl sm:text-4xl font-serif-luxury font-bold text-white mt-2">Pilihan Paket Undangan Digital</h2>
        <p class="text-sm text-stone-400 mt-3">Sekali bayar, aktif selamanya. Bebas sebar ke ribuan tamu tanpa batas biaya cetak.</p>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-8 items-stretch">
        <!-- Paket Silver -->
        <div class="bg-stone-900 border border-stone-800 rounded-3xl p-8 flex flex-col justify-between">
          <div>
            <h3 class="text-lg font-bold text-stone-300">Paket Silver</h3>
            <p class="text-xs text-stone-400 mt-1">Pilihan tepat untuk acara santai & intim</p>
            <div class="mt-6 mb-6">
              <span class="text-3xl font-bold text-white">Rp 99.000</span>
              <span class="text-xs text-stone-500"> / selamanya</span>
            </div>
            <ul class="space-y-3 text-xs text-stone-300">
              <li class="flex items-center gap-2">✓ Aktif Selamanya</li>
              <li class="flex items-center gap-2">✓ Hingga 300 Tamu Undangan</li>
              <li class="flex items-center gap-2">✓ Hitung Mundur & Google Maps</li>
              <li class="flex items-center gap-2">✓ Galeri 5 Foto Prewedding</li>
              <li class="flex items-center gap-2">✓ Amplop Digital 1 Rekening</li>
            </ul>
          </div>
          <button class="mt-8 w-full py-3 rounded-xl bg-stone-800 hover:bg-stone-700 text-white text-xs font-semibold transition-all">
            Pilih Paket Silver
          </button>
        </div>

        <!-- Paket Gold (POPULAR) -->
        <div class="bg-gradient-to-b from-stone-900 to-pink-950/40 border-2 border-pink-500 rounded-3xl p-8 flex flex-col justify-between relative shadow-2xl shadow-pink-950/40">
          <div class="absolute -top-3.5 left-1/2 -translate-x-1/2 px-4 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider bg-pink-600 text-white shadow-md">
            Paling Populer
          </div>
          <div>
            <h3 class="text-lg font-bold text-white">Paket Gold VIP</h3>
            <p class="text-xs text-pink-300 mt-1">Paket favorit pengantin dengan fitur komplit</p>
            <div class="mt-6 mb-6">
              <span class="text-3xl font-bold text-white">Rp 149.000</span>
              <span class="text-xs text-stone-400"> / selamanya</span>
            </div>
            <ul class="space-y-3 text-xs text-stone-200">
              <li class="flex items-center gap-2">✓ <strong>Semua Fitur Silver</strong></li>
              <li class="flex items-center gap-2">✓ <strong>Tamu Tanpa Batas (Unlimited)</strong></li>
              <li class="flex items-center gap-2">✓ RSVP & Buku Tamu Realtime</li>
              <li class="flex items-center gap-2">✓ Amplop Digital Multi Rekening & QRIS</li>
              <li class="flex items-center gap-2">✓ Galeri 15 Foto + Love Story</li>
              <li class="flex items-center gap-2">✓ Generator WhatsApp Blast Otomatis</li>
            </ul>
          </div>
          <button class="mt-8 w-full py-3 rounded-xl bg-gradient-to-r from-pink-600 to-purple-600 hover:brightness-110 text-white text-xs font-bold shadow-lg transition-all">
            Pilih Paket Gold
          </button>
        </div>

        <!-- Paket Platinum -->
        <div class="bg-stone-900 border border-stone-800 rounded-3xl p-8 flex flex-col justify-between">
          <div>
            <h3 class="text-lg font-bold text-stone-300">Paket Platinum Royal</h3>
            <p class="text-xs text-stone-400 mt-1">Eksklusif dengan Custom Domain</p>
            <div class="mt-6 mb-6">
              <span class="text-3xl font-bold text-white">Rp 199.000</span>
              <span class="text-xs text-stone-500"> / selamanya</span>
            </div>
            <ul class="space-y-3 text-xs text-stone-300">
              <li class="flex items-center gap-2">✓ Semua Fitur Gold VIP</li>
              <li class="flex items-center gap-2">✓ Dukungan Custom Domain (.com / .id)</li>
              <li class="flex items-center gap-2">✓ AI Wedding Assistant Copywriter</li>
              <li class="flex items-center gap-2">✓ Bantuan Input Data dari Tim Desainer</li>
              <li class="flex items-center gap-2">✓ Prioritas Dukungan 24/7</li>
            </ul>
          </div>
          <button class="mt-8 w-full py-3 rounded-xl bg-stone-800 hover:bg-stone-700 text-white text-xs font-semibold transition-all">
            Pilih Platinum Royal
          </button>
        </div>
      </div>
    </div>
  </section>

  <!-- ================= RESELLER & SIMULASI CUAN ================= -->
  <section id="reseller" class="py-24">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="bg-gradient-to-br from-stone-900 via-purple-950/40 to-stone-900 border border-purple-500/30 rounded-3xl p-8 md:p-14">
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
          <div>
            <span class="px-3 py-1 rounded-full text-xs font-bold bg-blue-500/20 text-blue-300 border border-blue-500/30">
              Peluang Bisnis SaaS Tanpa Modal
            </span>
            <h2 class="text-3xl sm:text-4xl font-serif-luxury font-bold text-white mt-4 leading-tight">
              Raih Penghasilan Jutaan Rupiah dengan Menjadi Mitra Reseller
            </h2>
            <p class="text-sm text-stone-300 mt-4 leading-relaxed">
              Dapatkan <strong>komisi 25% langsung</strong> untuk setiap pesanan undangan digital yang Anda bantu buatkan untuk teman, saudara, maupun klien wedding organizer Anda.
            </p>
            <div class="mt-6 space-y-3 text-xs text-stone-300">
              <div class="flex items-center gap-3">
                <span class="w-6 h-6 rounded-full bg-emerald-900/60 text-emerald-400 flex items-center justify-center font-bold">✓</span>
                <span>Dashboard reseller transparan untuk pantau status order & komisi</span>
              </div>
              <div class="flex items-center gap-3">
                <span class="w-6 h-6 rounded-full bg-emerald-900/60 text-emerald-400 flex items-center justify-center font-bold">✓</span>
                <span>Tautan referral & materi promosi siap pakai di Instagram/TikTok</span>
              </div>
              <div class="flex items-center gap-3">
                <span class="w-6 h-6 rounded-full bg-emerald-900/60 text-emerald-400 flex items-center justify-center font-bold">✓</span>
                <span>Pencairan dana otomatis tanpa potongan biaya admin</span>
              </div>
            </div>
          </div>

          <!-- Kalkulator Simulasi Cuan -->
          <div id="kalkulator" class="bg-stone-900/90 border border-stone-700/80 rounded-2xl p-6 md:p-8 shadow-xl">
            <h3 class="text-base font-bold text-white mb-4">Simulasi Potensi Cuan Reseller / Bulan</h3>
            
            <div class="space-y-4">
              <div>
                <div class="flex justify-between text-xs text-stone-300 mb-1">
                  <span>Jumlah Pesanan Klien / Bulan:</span>
                  <span id="orderCountLabel" class="font-bold text-pink-400">20 Pesanan</span>
                </div>
                <input type="range" id="orderRange" min="1" max="100" value="20" oninput="calculateCommission()" class="w-full accent-pink-600">
              </div>

              <div>
                <label class="block text-xs text-stone-300 mb-1">Rata-rata Paket Dipilih:</label>
                <select id="packagePriceSelect" onchange="calculateCommission()" class="w-full px-3 py-2 rounded-xl bg-stone-800 border border-stone-700 text-xs text-white">
                  <option value="99000">Paket Silver (Rp 99.000)</option>
                  <option value="149000" selected>Paket Gold VIP (Rp 149.000)</option>
                  <option value="199000">Paket Platinum Royal (Rp 199.000)</option>
                </select>
              </div>

              <div class="pt-4 border-t border-stone-800">
                <p class="text-xs text-stone-400">Estimasi Komisi Bersih Anda:</p>
                <div class="flex items-baseline gap-2 mt-1">
                  <span id="commissionResult" class="text-3xl font-bold text-emerald-400 font-mono">Rp 745.000</span>
                  <span class="text-xs text-stone-400">/ bulan</span>
                </div>
                <p class="text-[11px] text-stone-500 mt-2">*Perhitungan berdasarkan tarif bagi hasil 25% dari total omzet penjualan klien.</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>

  <!-- ================= FAQ SECTION ================= -->
  <section id="faq" class="py-24 bg-stone-950/60 border-t border-stone-800">
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="text-center mb-16">
        <span class="text-xs uppercase tracking-widest text-pink-400 font-semibold">Tanya Jawab</span>
        <h2 class="text-3xl sm:text-4xl font-serif-luxury font-bold text-white mt-2">Pertanyaan yang Sering Diajukan</h2>
      </div>

      <div class="space-y-4 text-xs md:text-sm">
        <details class="bg-stone-900 border border-stone-800 rounded-2xl p-5 cursor-pointer">
          <summary class="font-bold text-white">Berapa lama proses pembuatan undangan pernikahan?</summary>
          <p class="text-stone-400 mt-2 leading-relaxed">
            Hanya 5 sampai 10 menit! Begitu Anda mengisi nama pengantin, jadwal acara, dan foto, undangan digital Anda langsung aktif seketika dan dapat langsung dibagikan via WhatsApp.
          </p>
        </details>

        <details class="bg-stone-900 border border-stone-800 rounded-2xl p-5 cursor-pointer">
          <summary class="font-bold text-white">Apakah ada batasan jumlah tamu yang bisa dikirimkan link undangan?</summary>
          <p class="text-stone-400 mt-2 leading-relaxed">
            Pada Paket Gold VIP dan Platinum, tidak ada batasan sama sekali (Unlimited). Anda dapat mengirimkan ke ratusan bahkan ribuan tamu dengan nama yang tertera personal untuk masing-masing tamu.
          </p>
        </details>

        <details class="bg-stone-900 border border-stone-800 rounded-2xl p-5 cursor-pointer">
          <summary class="font-bold text-white">Bagaimana cara tamu memberikan amplop digital?</summary>
          <p class="text-stone-400 mt-2 leading-relaxed">
            Tamu cukup menekan tombol 'Salin No. Rekening' atau memindai barcode QRIS yang tampil pada halaman undangan, lalu melakukan transfer dari mobile banking atau e-wallet mereka.
          </p>
        </details>

        <details class="bg-stone-900 border border-stone-800 rounded-2xl p-5 cursor-pointer">
          <summary class="font-bold text-white">Apakah musik undangan bisa otomatis terputar?</summary>
          <p class="text-stone-400 mt-2 leading-relaxed">
            Ya! Begitu tamu menekan tombol 'Buka Undangan', musik romantis pilihan Anda akan langsung mengalun merdu mempermanis suasana.
          </p>
        </details>
      </div>
    </div>
  </section>

  <!-- ================= FOOTER ================= -->
  <footer class="py-12 bg-stone-950 border-t border-stone-800 text-xs text-stone-500">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col md:flex-row items-center justify-between gap-6">
      <div class="flex items-center gap-3">
        <div class="w-7 h-7 rounded-lg bg-pink-700 flex items-center justify-center font-bold text-white text-xs">U</div>
        <span class="font-serif-luxury text-sm font-bold text-white">Remix UndanganKu</span>
        <span>© 2026. Hak Cipta Dilindungi.</span>
      </div>
      <div class="flex gap-6 text-stone-400">
        <a href="#fitur" class="hover:text-white transition-colors">Fitur</a>
        <a href="#tema" class="hover:text-white transition-colors">Tema</a>
        <a href="#harga" class="hover:text-white transition-colors">Harga</a>
        <a href="#reseller" class="hover:text-white transition-colors">Reseller</a>
      </div>
    </div>
  </footer>

  <script>
    function calculateCommission() {
      const count = parseInt(document.getElementById('orderRange').value, 10);
      const price = parseInt(document.getElementById('packagePriceSelect').value, 10);
      document.getElementById('orderCountLabel').textContent = count + ' Pesanan';
      
      const totalRevenue = count * price;
      const commission = Math.round(totalRevenue * 0.25);
      
      const formatter = new Intl.NumberFormat('id-ID', {
        style: 'currency',
        currency: 'IDR',
        maximumFractionDigits: 0
      });
      document.getElementById('commissionResult').textContent = formatter.format(commission);
    }
  </script>
</body>
</html>
        """.trimIndent()
    }
}
