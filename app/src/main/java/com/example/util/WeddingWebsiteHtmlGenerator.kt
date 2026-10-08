package com.example.util

import com.example.data.model.InvitationEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object WeddingWebsiteHtmlGenerator {

    fun generateInvitationHtml(
        invitation: InvitationEntity,
        guestName: String = "Tamu Terhormat"
    ): String {
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        val timeFormat = SimpleDateFormat("HH:mm", Locale("id", "ID"))
        val eventDateStr = dateFormat.format(Date(invitation.eventAt))
        val eventTimeStr = timeFormat.format(Date(invitation.eventAt)) + " WIB - Selesai"
        val themeColor = if (invitation.themeColorHex.isNotBlank()) invitation.themeColorHex else "#871A5B"
        val mapsUrl = invitation.mapsUrl ?: "https://maps.google.com/?q=${invitation.venue}"

        return """
<!DOCTYPE html>
<html lang="id">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Undangan Pernikahan: ${invitation.groomName} & ${invitation.brideName}</title>
  <!-- Tailwind CSS -->
  <script src="https://cdn.tailwindcss.com"></script>
  <!-- Google Fonts -->
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Cinzel:wght@500;700;800&family=Great+Vibes&family=Plus+Jakarta+Sans:wght@300;400;500;600;700&display=swap" rel="stylesheet">
  <style>
    body {
      font-family: 'Plus Jakarta Sans', sans-serif;
      scroll-behavior: smooth;
      background-color: #0c080d;
      color: #f7f3f6;
    }
    .font-serif-title {
      font-family: 'Cinzel', serif;
    }
    .font-cursive {
      font-family: 'Great Vibes', cursive;
    }
    .accent-bg {
      background-color: $themeColor;
    }
    .accent-text {
      color: $themeColor;
    }
    .accent-border {
      border-color: $themeColor;
    }
    .glass-card {
      background: rgba(255, 255, 255, 0.05);
      backdrop-filter: blur(16px);
      -webkit-backdrop-filter: blur(16px);
      border: 1px solid rgba(255, 255, 255, 0.12);
    }
    @keyframes spin-slow {
      from { transform: rotate(0deg); }
      to { transform: rotate(360deg); }
    }
    .animate-spin-slow {
      animation: spin-slow 12s linear infinite;
    }
    @keyframes pulse-glow {
      0%, 100% { opacity: 0.6; transform: scale(1); }
      50% { opacity: 1; transform: scale(1.05); }
    }
    .pulse-animation {
      animation: pulse-glow 3s ease-in-out infinite;
    }
  </style>
</head>
<body class="antialiased selection:bg-pink-700 selection:text-white">

  <!-- ================= COVER / ENVELOPE OVERLAY ================= -->
  <div id="coverOverlay" class="fixed inset-0 z-50 flex flex-col items-center justify-between p-6 bg-gradient-to-b from-stone-950 via-purple-950/40 to-stone-950 transition-all duration-1000 ease-in-out">
    <div class="pt-8 text-center">
      <span class="inline-block px-4 py-1.5 rounded-full text-xs tracking-widest uppercase bg-white/10 border border-white/20 text-purple-200 mb-4">
        The Wedding Of
      </span>
      <h1 class="text-4xl md:text-6xl font-cursive accent-text drop-shadow-md">
        ${invitation.groomName} & ${invitation.brideName}
      </h1>
      <p class="text-sm text-stone-300 mt-2 font-serif-title tracking-wider">$eventDateStr</p>
    </div>

    <!-- Center Envelope Art -->
    <div class="my-auto flex flex-col items-center">
      <div class="relative w-36 h-36 md:w-44 md:h-44 rounded-full border-2 accent-border p-2 pulse-animation">
        <div class="w-full h-full rounded-full bg-cover bg-center overflow-hidden shadow-2xl flex items-center justify-center bg-stone-900 border border-white/10">
          <svg class="w-16 h-16 accent-text" fill="currentColor" viewBox="0 0 24 24">
            <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
          </svg>
        </div>
      </div>
      
      <div class="mt-6 text-center max-w-sm">
        <p class="text-xs uppercase tracking-widest text-stone-400">Kepada Yth. Bapak/Ibu/Saudara/i:</p>
        <h3 class="text-xl font-bold text-white mt-1">$guestName</h3>
        <p class="text-xs text-stone-400 mt-1 italic">*Mohon maaf bila ada kesalahan penulisan nama/gelar</p>
      </div>
    </div>

    <!-- Open Envelope Button -->
    <div class="pb-10 w-full max-w-xs text-center">
      <button onclick="openInvitation()" class="w-full py-3.5 px-6 rounded-2xl accent-bg text-white font-semibold text-sm tracking-wide shadow-lg hover:brightness-110 active:scale-95 transition-all flex items-center justify-center gap-2">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"/>
        </svg>
        <span>Buka Undangan</span>
      </button>
    </div>
  </div>

  <!-- ================= FLOATING MUSIC CONTROL ================= -->
  <div class="fixed top-5 right-5 z-40">
    <button id="musicToggleBtn" onclick="toggleAudio()" class="w-11 h-11 rounded-full glass-card flex items-center justify-center text-white shadow-xl hover:scale-105 transition-transform">
      <svg id="musicIconPlaying" class="w-5 h-5 accent-text animate-spin-slow" fill="currentColor" viewBox="0 0 24 24">
        <path d="M12 3v10.55c-.59-.34-1.27-.55-2-.55-2.21 0-4 1.79-4 4s1.79 4 4 4 4-1.79 4-4V7h4V3h-6z"/>
      </svg>
      <svg id="musicIconPaused" class="w-5 h-5 text-stone-400 hidden" fill="currentColor" viewBox="0 0 24 24">
        <path d="M4.27 3L3 4.27l9 9v.28c-.59-.34-1.27-.55-2-.55-2.21 0-4 1.79-4 4s1.79 4 4 4 4-1.79 4-4v-1.73l4.27 4.27 1.27-1.27L4.27 3zM14 7h4V3h-6v5.18l2 2z"/>
      </svg>
    </button>
  </div>

  <!-- ================= FLOATING BOTTOM NAVIGATION ================= -->
  <div class="fixed bottom-4 inset-x-0 z-40 flex justify-center px-4">
    <nav class="glass-card px-4 py-2.5 rounded-full flex items-center gap-4 shadow-2xl text-xs">
      <a href="#hero" class="hover:text-pink-400 transition-colors">Beranda</a>
      <a href="#couple" class="hover:text-pink-400 transition-colors">Mempelai</a>
      <a href="#events" class="hover:text-pink-400 transition-colors">Acara</a>
      <a href="#story" class="hover:text-pink-400 transition-colors">Kisah</a>
      <a href="#rsvp" class="hover:text-pink-400 transition-colors">RSVP</a>
      <a href="#gift" class="hover:text-pink-400 transition-colors">Amplop</a>
      <a href="#greetings" class="hover:text-pink-400 transition-colors">Ucapan</a>
    </nav>
  </div>

  <!-- ================= MAIN INVITATION PAGE ================= -->
  <main class="max-w-2xl mx-auto px-4 pb-28">

    <!-- HERO SECTION -->
    <section id="hero" class="min-h-screen flex flex-col items-center justify-center text-center py-20">
      <span class="text-xs uppercase tracking-widest text-stone-400 mb-3">Walimatul 'Urs</span>
      <h2 class="text-5xl md:text-7xl font-cursive accent-text mb-4">
        ${invitation.groomName} & ${invitation.brideName}
      </h2>
      <p class="font-serif-title text-sm tracking-wider text-stone-300 max-w-md">$eventDateStr</p>
      
      <!-- Countdown Timer -->
      <div class="grid grid-cols-4 gap-3 my-10 max-w-sm w-full">
        <div class="glass-card p-3 rounded-xl text-center">
          <span id="days" class="text-2xl font-bold accent-text">00</span>
          <p class="text-[10px] text-stone-400 uppercase tracking-wider">Hari</p>
        </div>
        <div class="glass-card p-3 rounded-xl text-center">
          <span id="hours" class="text-2xl font-bold accent-text">00</span>
          <p class="text-[10px] text-stone-400 uppercase tracking-wider">Jam</p>
        </div>
        <div class="glass-card p-3 rounded-xl text-center">
          <span id="minutes" class="text-2xl font-bold accent-text">00</span>
          <p class="text-[10px] text-stone-400 uppercase tracking-wider">Menit</p>
        </div>
        <div class="glass-card p-3 rounded-xl text-center">
          <span id="seconds" class="text-2xl font-bold accent-text">00</span>
          <p class="text-[10px] text-stone-400 uppercase tracking-wider">Detik</p>
        </div>
      </div>

      <!-- Save the date button -->
      <a href="https://calendar.google.com/calendar/render?action=TEMPLATE&text=${invitation.groomName}+%26+${invitation.brideName}+Wedding&dates=20261120T020000Z/20261120T140000Z&details=Undangan+Pernikahan+${invitation.groomName}+%26+${invitation.brideName}&location=${invitation.venue}" target="_blank" class="px-5 py-2.5 rounded-full border border-white/20 glass-card text-xs hover:border-pink-500 transition-all flex items-center gap-2">
        <svg class="w-4 h-4 text-pink-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
        </svg>
        Simpan ke Google Calendar
      </a>
    </section>

    <!-- SACRED QUOTE -->
    <section class="py-12 text-center">
      <div class="glass-card p-6 md:p-8 rounded-3xl relative overflow-hidden">
        <div class="text-2xl text-stone-500 mb-3">❝</div>
        <p class="text-xs md:text-sm leading-relaxed text-stone-300 italic font-serif-title">
          ${invitation.quote ?: "Dan di antara tanda-tanda kebesaran-Nya ialah Dia menciptakan pasangan-pasangan untukmu dari jenismu sendiri, agar kamu cenderung dan merasa tenteram kepadanya, dan Dia menjadikan di antaramu rasa kasih dan sayang. (QS. Ar-Rum: 21)"}
        </p>
        <div class="mt-4 flex justify-center items-center gap-2">
          <div class="h-[1px] w-8 accent-bg"></div>
          <span class="text-xs accent-text font-bold">QS. Ar-Rum : 21</span>
          <div class="h-[1px] w-8 accent-bg"></div>
        </div>
      </div>
    </section>

    <!-- COUPLE PROFILE -->
    <section id="couple" class="py-16">
      <div class="text-center mb-12">
        <span class="text-xs uppercase tracking-widest text-stone-400">Mempelai Bahagia</span>
        <h2 class="text-3xl font-serif-title font-bold text-white mt-1">Kedua Mempelai</h2>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
        <!-- Groom Card -->
        <div class="glass-card p-6 rounded-3xl text-center flex flex-col items-center">
          <div class="w-28 h-28 rounded-full border-2 accent-border p-1 mb-4 shadow-xl">
            <div class="w-full h-full rounded-full bg-stone-800 flex items-center justify-center font-cursive text-3xl accent-text">
              ${invitation.groomName.firstOrNull() ?: 'P'}
            </div>
          </div>
          <h3 class="text-2xl font-serif-title font-bold text-white">${invitation.groomName}</h3>
          <p class="text-xs text-stone-300 mt-2 leading-relaxed">${invitation.groomBio}</p>
          <div class="mt-4 inline-flex items-center gap-1.5 text-xs text-stone-400">
            <span>@${invitation.groomName.lowercase().replace(" ", "")}</span>
          </div>
        </div>

        <!-- Bride Card -->
        <div class="glass-card p-6 rounded-3xl text-center flex flex-col items-center">
          <div class="w-28 h-28 rounded-full border-2 accent-border p-1 mb-4 shadow-xl">
            <div class="w-full h-full rounded-full bg-stone-800 flex items-center justify-center font-cursive text-3xl accent-text">
              ${invitation.brideName.firstOrNull() ?: 'W'}
            </div>
          </div>
          <h3 class="text-2xl font-serif-title font-bold text-white">${invitation.brideName}</h3>
          <p class="text-xs text-stone-300 mt-2 leading-relaxed">${invitation.brideBio}</p>
          <div class="mt-4 inline-flex items-center gap-1.5 text-xs text-stone-400">
            <span>@${invitation.brideName.lowercase().replace(" ", "")}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- EVENTS SCHEDULE -->
    <section id="events" class="py-16">
      <div class="text-center mb-12">
        <span class="text-xs uppercase tracking-widest text-stone-400">Rangkaian Acara</span>
        <h2 class="text-3xl font-serif-title font-bold text-white mt-1">Waktu & Lokasi Acara</h2>
      </div>

      <div class="space-y-6">
        <!-- Akad Nikah -->
        <div class="glass-card p-6 rounded-3xl">
          <div class="flex items-center justify-between border-b border-white/10 pb-4 mb-4">
            <h3 class="text-xl font-bold font-serif-title text-white">Akad Nikah</h3>
            <span class="px-3 py-1 rounded-full text-[10px] accent-bg text-white font-medium">Khidmat</span>
          </div>
          <div class="space-y-2 text-sm text-stone-300">
            <div class="flex items-center gap-3">
              <svg class="w-5 h-5 accent-text shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/></svg>
              <span>$eventDateStr</span>
            </div>
            <div class="flex items-center gap-3">
              <svg class="w-5 h-5 accent-text shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>
              <span>08:00 - 10:00 WIB</span>
            </div>
            <div class="flex items-center gap-3">
              <svg class="w-5 h-5 accent-text shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/></svg>
              <span>${invitation.venue}</span>
            </div>
          </div>
        </div>

        <!-- Resepsi Pernikahan -->
        <div class="glass-card p-6 rounded-3xl">
          <div class="flex items-center justify-between border-b border-white/10 pb-4 mb-4">
            <h3 class="text-xl font-bold font-serif-title text-white">Resepsi Pernikahan</h3>
            <span class="px-3 py-1 rounded-full text-[10px] accent-bg text-white font-medium">Perayaan</span>
          </div>
          <div class="space-y-2 text-sm text-stone-300">
            <div class="flex items-center gap-3">
              <svg class="w-5 h-5 accent-text shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/></svg>
              <span>$eventDateStr</span>
            </div>
            <div class="flex items-center gap-3">
              <svg class="w-5 h-5 accent-text shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>
              <span>11:00 - Selesai WIB</span>
            </div>
            <div class="flex items-center gap-3">
              <svg class="w-5 h-5 accent-text shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/></svg>
              <span>${invitation.address ?: invitation.venue}</span>
            </div>
          </div>
          
          <div class="mt-6">
            <a href="$mapsUrl" target="_blank" class="w-full py-3 rounded-xl accent-bg text-white text-xs font-semibold text-center block shadow-lg hover:brightness-110 active:scale-95 transition-all">
              Buka Peta Google Maps
            </a>
          </div>
        </div>
      </div>
    </section>

    <!-- LOVE STORY -->
    <section id="story" class="py-16">
      <div class="text-center mb-12">
        <span class="text-xs uppercase tracking-widest text-stone-400">Kisah Kami</span>
        <h2 class="text-3xl font-serif-title font-bold text-white mt-1">Our Love Story</h2>
      </div>

      <div class="space-y-6">
        <div class="glass-card p-5 rounded-2xl border-l-4 accent-border">
          <span class="text-xs accent-text font-bold uppercase">2021 — Awal Pertemuan</span>
          <p class="text-xs text-stone-300 mt-2 leading-relaxed">
            Berawal dari perkenalan sederhana dalam kegiatan sosial dan perkuliahan. Dari percakapan kecil, tercipta frekuensi pemikiran dan kenyamanan yang mendalam.
          </p>
        </div>
        <div class="glass-card p-5 rounded-2xl border-l-4 accent-border">
          <span class="text-xs accent-text font-bold uppercase">2024 — Komitmen & Lamaran</span>
          <p class="text-xs text-stone-300 mt-2 leading-relaxed">
            Setelah melewati berbagai proses dan saling mengenal keluarga besar, dengan memohon ridho orang tua, ikrar lamaran resmi dilaksanakan dengan penuh haru.
          </p>
        </div>
        <div class="glass-card p-5 rounded-2xl border-l-4 accent-border">
          <span class="text-xs accent-text font-bold uppercase">2026 — Menuju Janji Suci</span>
          <p class="text-xs text-stone-300 mt-2 leading-relaxed">
            Kini dengan ketulusan hati, kami siap melangkah ke jenjang pernikahan suci untuk membangun keluarga yang sakinah, mawaddah, dan warahmah.
          </p>
        </div>
      </div>
    </section>

    <!-- RSVP ONLINE -->
    <section id="rsvp" class="py-16">
      <div class="text-center mb-10">
        <span class="text-xs uppercase tracking-widest text-stone-400">Konfirmasi Kehadiran</span>
        <h2 class="text-3xl font-serif-title font-bold text-white mt-1">RSVP Online</h2>
        <p class="text-xs text-stone-400 mt-2">Bantu kami mempersiapkan hidangan dengan mengonfirmasi kehadiran Anda.</p>
      </div>

      <form id="rsvpForm" onsubmit="submitRsvp(event)" class="glass-card p-6 md:p-8 rounded-3xl space-y-4">
        <div>
          <label class="block text-xs font-semibold text-stone-300 mb-1">Nama Lengkap</label>
          <input type="text" id="rsvpName" value="$guestName" required class="w-full px-4 py-2.5 rounded-xl bg-white/10 border border-white/20 text-white text-sm focus:outline-none focus:border-pink-500">
        </div>

        <div>
          <label class="block text-xs font-semibold text-stone-300 mb-1">Status Kehadiran</label>
          <select id="rsvpStatus" class="w-full px-4 py-2.5 rounded-xl bg-stone-900 border border-white/20 text-white text-sm focus:outline-none focus:border-pink-500">
            <option value="Hadir">Saya Pasti Hadir</option>
            <option value="Ragu-ragu">Masih Ragu-ragu</option>
            <option value="Tidak Hadir">Mohon Maaf, Berhalangan Hadir</option>
          </select>
        </div>

        <div>
          <label class="block text-xs font-semibold text-stone-300 mb-1">Jumlah Tamu</label>
          <select id="rsvpCount" class="w-full px-4 py-2.5 rounded-xl bg-stone-900 border border-white/20 text-white text-sm focus:outline-none focus:border-pink-500">
            <option value="1">1 Orang</option>
            <option value="2">2 Orang</option>
            <option value="3">3 Orang</option>
          </select>
        </div>

        <button type="submit" class="w-full py-3.5 rounded-xl accent-bg text-white font-semibold text-sm shadow-lg hover:brightness-110 active:scale-95 transition-all">
          Kirim Konfirmasi Kehadiran
        </button>
      </form>
      <div id="rsvpAlert" class="mt-4 p-4 rounded-xl bg-emerald-950/80 border border-emerald-500/50 text-emerald-200 text-xs text-center hidden">
        Terima kasih! Konfirmasi kehadiran Anda berhasil tercatat di sistem buku tamu.
      </div>
    </section>

    <!-- DIGITAL ENVELOPE / WEDDING GIFT -->
    <section id="gift" class="py-16">
      <div class="text-center mb-10">
        <span class="text-xs uppercase tracking-widest text-stone-400">Tanda Kasih</span>
        <h2 class="text-3xl font-serif-title font-bold text-white mt-1">Amplop Digital & Kado</h2>
        <p class="text-xs text-stone-400 mt-2">Doa restu Anda merupakan karunia terindah, namun jika ingin memberikan tanda kasih secara digital:</p>
      </div>

      <div class="glass-card p-6 md:p-8 rounded-3xl text-center space-y-6">
        <div class="p-5 rounded-2xl bg-gradient-to-r from-stone-900 to-stone-800 border border-white/10 text-left">
          <div class="flex justify-between items-center mb-4">
            <span class="text-lg font-bold tracking-wider accent-text">${invitation.bankName}</span>
            <span class="text-xs text-stone-400">Transfer Bank</span>
          </div>
          <p class="text-xs text-stone-400">Nomor Rekening:</p>
          <div class="flex items-center justify-between mt-1">
            <span id="accNumber" class="text-xl font-mono font-bold tracking-widest text-white">${invitation.bankAccount}</span>
            <button onclick="copyToClipboard('${invitation.bankAccount}')" class="px-3 py-1.5 rounded-lg accent-bg text-white text-xs font-medium hover:brightness-110 active:scale-95 transition-all">
              Salin No.
            </button>
          </div>
          <p class="text-xs text-stone-300 mt-2">Atas Nama: <strong class="text-white">${invitation.bankHolder}</strong></p>
        </div>

        <div id="copyToast" class="text-xs text-emerald-400 hidden">
          ✓ Nomor rekening berhasil disalin ke clipboard!
        </div>
      </div>
    </section>

    <!-- GUESTBOOK & WISHES -->
    <section id="greetings" class="py-16">
      <div class="text-center mb-10">
        <span class="text-xs uppercase tracking-widest text-stone-400">Buku Tamu</span>
        <h2 class="text-3xl font-serif-title font-bold text-white mt-1">Ucapan & Doa Restu</h2>
      </div>

      <div class="glass-card p-6 rounded-3xl mb-8">
        <form onsubmit="postGreeting(event)" class="space-y-4">
          <div>
            <input type="text" id="guestWishName" placeholder="Nama Anda" value="$guestName" required class="w-full px-4 py-2.5 rounded-xl bg-white/10 border border-white/20 text-white text-sm focus:outline-none focus:border-pink-500">
          </div>
          <div>
            <textarea id="guestWishText" rows="3" placeholder="Tuliskan doa restu untuk kedua mempelai..." required class="w-full px-4 py-2.5 rounded-xl bg-white/10 border border-white/20 text-white text-sm focus:outline-none focus:border-pink-500"></textarea>
          </div>
          <button type="submit" class="w-full py-3 rounded-xl accent-bg text-white font-semibold text-sm hover:brightness-110 active:scale-95 transition-all">
            Kirim Doa & Ucapan
          </button>
        </form>
      </div>

      <!-- Greetings List -->
      <div id="greetingsList" class="space-y-3">
        <div class="glass-card p-4 rounded-2xl">
          <div class="flex items-center justify-between mb-1">
            <h4 class="text-xs font-bold text-white">Keluarga Besar Bpk. Ahmad</h4>
            <span class="text-[10px] text-stone-400">Baru saja</span>
          </div>
          <p class="text-xs text-stone-300 leading-relaxed">
            Barakallahu lakuma wa baraka 'alaikuma wa jama'a bainakuma fii khoir. Selamat menempuh hidup baru untuk kedua mempelai!
          </p>
        </div>
        <div class="glass-card p-4 rounded-2xl">
          <div class="flex items-center justify-between mb-1">
            <h4 class="text-xs font-bold text-white">Rizky & Rina</h4>
            <span class="text-[10px] text-stone-400">1 jam lalu</span>
          </div>
          <p class="text-xs text-stone-300 leading-relaxed">
            Happy wedding sahabatku! Semoga langgeng sampai maut memisahkan dan senantiasa dikaruniai keturunan yang sholeh dan sholehah. Amin!
          </p>
        </div>
      </div>
    </section>

    <!-- FOOTER -->
    <footer class="pt-10 pb-20 text-center border-t border-white/10">
      <p class="text-xs text-stone-400">Dibuat dengan penuh cinta untuk pernikahan</p>
      <h3 class="text-xl font-cursive accent-text mt-1">${invitation.groomName} & ${invitation.brideName}</h3>
      <p class="text-[10px] text-stone-500 mt-4">Powered by <strong>Remix UndanganKu SaaS Platform</strong></p>
    </footer>

  </main>

  <script>
    // Audio synthesizer simulator for romantic background ambiance
    let audioCtx = null;
    let isPlaying = false;
    let oscInterval = null;

    function startMusic() {
      try {
        if (!audioCtx) {
          audioCtx = new (window.AudioContext || window.webkitAudioContext)();
        }
        if (audioCtx.state === 'suspended') {
          audioCtx.resume();
        }
        isPlaying = true;
        document.getElementById('musicIconPlaying').classList.remove('hidden');
        document.getElementById('musicIconPaused').classList.add('hidden');
        
        // Play soft romantic arpeggios
        const notes = [261.63, 329.63, 392.00, 523.25, 440.00, 349.23];
        let noteIdx = 0;
        oscInterval = setInterval(() => {
          if (!isPlaying || !audioCtx) return;
          const osc = audioCtx.createOscillator();
          const gain = audioCtx.createGain();
          osc.type = 'sine';
          osc.frequency.value = notes[noteIdx % notes.length];
          gain.gain.setValueAtTime(0.02, audioCtx.currentTime);
          gain.gain.exponentialRampToValueAtTime(0.0001, audioCtx.currentTime + 1.8);
          osc.connect(gain);
          gain.connect(audioCtx.destination);
          osc.start();
          osc.stop(audioCtx.currentTime + 1.9);
          noteIdx++;
        }, 800);
      } catch (e) {
        console.log("Audio notice:", e);
      }
    }

    function stopMusic() {
      isPlaying = false;
      if (oscInterval) clearInterval(oscInterval);
      document.getElementById('musicIconPlaying').classList.add('hidden');
      document.getElementById('musicIconPaused').classList.remove('hidden');
    }

    function toggleAudio() {
      if (isPlaying) {
        stopMusic();
      } else {
        startMusic();
      }
    }

    function openInvitation() {
      const cover = document.getElementById('coverOverlay');
      cover.style.transform = 'translateY(-100%)';
      cover.style.opacity = '0';
      setTimeout(() => {
        cover.style.display = 'none';
      }, 1000);
      startMusic();
    }

    // Countdown Timer logic
    const targetDate = ${invitation.eventAt};
    function updateCountdown() {
      const now = new Date().getTime();
      const diff = Math.max(0, targetDate - now);
      const days = Math.floor(diff / (1000 * 60 * 60 * 24));
      const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
      const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
      const seconds = Math.floor((diff % (1000 * 60)) / 1000);

      document.getElementById('days').textContent = String(days).padStart(2, '0');
      document.getElementById('hours').textContent = String(hours).padStart(2, '0');
      document.getElementById('minutes').textContent = String(minutes).padStart(2, '0');
      document.getElementById('seconds').textContent = String(seconds).padStart(2, '0');
    }
    setInterval(updateCountdown, 1000);
    updateCountdown();

    function copyToClipboard(text) {
      if (navigator.clipboard) {
        navigator.clipboard.writeText(text);
      }
      const toast = document.getElementById('copyToast');
      toast.classList.remove('hidden');
      setTimeout(() => toast.classList.add('hidden'), 3500);
    }

    function submitRsvp(e) {
      e.preventDefault();
      document.getElementById('rsvpAlert').classList.remove('hidden');
      const submitBtn = e.target.querySelector('button');
      submitBtn.textContent = '✓ Terkonfirmasi';
      submitBtn.disabled = true;
    }

    function postGreeting(e) {
      e.preventDefault();
      const name = document.getElementById('guestWishName').value;
      const text = document.getElementById('guestWishText').value;
      if (!text.trim()) return;

      const card = document.createElement('div');
      card.className = 'glass-card p-4 rounded-2xl animate-fade-in';
      card.innerHTML = `
        <div class="flex items-center justify-between mb-1">
          <h4 class="text-xs font-bold text-white">${'$'}{name}</h4>
          <span class="text-[10px] accent-text font-bold">Baru saja</span>
        </div>
        <p class="text-xs text-stone-300 leading-relaxed">${'$'}{text}</p>
      `;
      const list = document.getElementById('greetingsList');
      list.insertBefore(card, list.firstChild);
      document.getElementById('guestWishText').value = '';
    }
  </script>
</body>
</html>
        """.trimIndent()
    }
}
