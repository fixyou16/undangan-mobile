# Setup Guide

Panduan singkat untuk menjalankan aplikasi di local environment.

## 1. Install tools
- Android Studio latest
- JDK 17
- Android SDK Platform 34
- Emulator atau device Android

## 2. Buka project
```bash
git clone https://github.com/fixyou16/undangan-mobile.git
cd undangan-mobile
```
Lalu buka folder project di Android Studio.

## 3. Sync Gradle
Android Studio akan otomatis melakukan sync jika dependency belum terpenuhi.

## 4. Jalankan demo
- Pilih device/emulator
- Klik Run `app`

## 5. Jika error muncul
- Pastikan SDK Android 34 sudah terinstall
- Sync project dengan Gradle
- Restart Android Studio jika build cache corrupt

## 6. Optional: build via CLI
```bash
./gradlew assembleDebug
```

Jika `gradlew` belum tersedia di repo, cukup gunakan Android Studio untuk menjalankan project. Android Studio membawa Gradle sendiri.
