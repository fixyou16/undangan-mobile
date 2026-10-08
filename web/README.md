# Remix UndanganKu — Versi Website (Web Edition)

Folder ini berisi versi website mandiri (**Standalone Web Version**) dari **Remix UndanganKu**:

1. **`index.html`**: Website Landing Page Resmi Platform SaaS Remix UndanganKu (Katalog Tema, Fitur, Paket Harga, Simulasi Cuan Reseller, FAQ, Kontak).
2. **`invitation.html`**: Template Website Undangan Digital responsif (Cover interaktif dengan animasi segel lilin, pemutar musik romantis, hitung mundur, profil mempelai, jadwal acara, Google Maps, RSVP online, amplop digital copy-rekening, dan buku tamu).

## Cara Menjalankan & Preview Lokal

Cukup buka file `index.html` atau `invitation.html` langsung di browser Anda (Google Chrome, Firefox, Safari, Edge), atau gunakan static web server lokal:

```bash
# Menggunakan Python
cd web
python3 -m http.server 3000

# Menggunakan Node.js npx serve
npx serve web
```
Lalu buka di browser: `http://localhost:3000`

## Cara Deploy ke Internet (Gratis)

### 1. Vercel
Jalankan di terminal:
```bash
cd web
npx vercel
```
Ikuti instruksi prompt dan website Anda akan langsung tayang dengan domain gratis `*.vercel.app`.

### 2. Netlify Drop
Kunjungi [Netlify Drop](https://app.netlify.com/drop), lalu *drag & drop* folder `web` ini. Website Anda akan langsung online seketika.

### 3. GitHub Pages
1. Push repository ini ke GitHub.
2. Buka menu **Settings > Pages**.
3. Pilih branch `main` dan folder `/web` (atau root) lalu klik **Save**.
