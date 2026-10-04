# Undangan Mobile

Aplikasi demo Android untuk undangan pernikahan digital yang siap dipresentasikan di emulator atau device.

## Fitur demo
- Halaman utama undangan dengan desain elegan
- Detail momen acara dan timeline acara
- Section lokasi dan peta placeholder
- Tombol RSVP dan share
- Layout yang responsif untuk demo

## Teknologi
- Kotlin
- Jetpack Compose
- Material 3
- Android SDK

## Persyaratan
- Android Studio Ladybug / Koala atau versi terbaru
- JDK 17
- Android SDK 34
- Emulator atau device Android

## Cara membuka project
1. Clone repository
2. Buka folder di Android Studio
3. Tunggu Gradle sync selesai
4. Pilih emulator/device
5. Run `app`

## Demo cepat
- Main screen menampilkan judul undangan, tanggal acara, lokasi, dan CTA RSVP
- Tombol `Konfirmasi Kehadiran` siap dipakai untuk demo interaksi

## Struktur proyek
```text
undangan-mobile/
├── app/
│   ├── src/main/java/com/undanganmobile/
│   ├── src/main/res/
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── .gitignore
├── README.md
├── SETUP.md
└── CONTRIBUTING.md
```

## Catatan
Project ini dibuat sebagai starter app yang siap untuk live demo, lalu bisa dikembangkan menjadi app undangan penuh dengan fitur RSVP, gallery, dan notifikasi.
