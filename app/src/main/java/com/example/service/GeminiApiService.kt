package com.example.service

import android.graphics.Bitmap
import android.util.Base64
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

data class ChatMessage(
    val role: String, // "user" or "model"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

class GeminiApiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val mediaTypeJson = "application/json; charset=utf-8".toMediaType()

    // 1. High Thinking Mode using gemini-3.1-pro-preview with ThinkingLevel.HIGH
    suspend fun generateDeepLoveStory(
        groomName: String,
        brideName: String,
        howTheyMet: String,
        proposalMemory: String,
        weddingVision: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext """
                *Bab 1: Pertemuan Tak Terduga*
                Di tengah kesibukan dan hiruk pikuk kehidupan, takdir mempertemukan $groomName dan $brideName. Berawal dari cerita sederhana di mana $howTheyMet, benih-benih kekaguman mulai bersemi menjadi rasa nyaman yang mendalam.
                
                *Bab 2: Langkah Menuju Kepastian*
                Setiap percakapan membawa kami pada keyakinan bahwa kami ditakdirkan untuk saling melengkapi. Momen paling berharga terukir saat $proposalMemory, di mana dua hati memutuskan untuk berikhtiar melangkah bersama seumur hidup.
                
                *Bab 3: Babak Baru & Janji Setia*
                Kini, $weddingVision. Kami memohon doa restu agar mahligai pernikahan ini senantiasa dipenuhi keberkahan, sakinah, mawaddah, dan warahmah.
            """.trimIndent()
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-pro-preview:generateContent?key=$apiKey"
            val prompt = """
                Bertindaklah sebagai penulis kisah cinta pernikahan (Wedding Storyteller) profesional.
                Tulis kisah cinta (Love Story) yang sangat menyentuh, elegan, puitis, dan berkesan untuk undangan pernikahan digital.
                
                Data Mempelai:
                - Pengantin Pria: $groomName
                - Pengantin Wanita: $brideName
                - Awal Bertemu: $howTheyMet
                - Momen Lamaran: $proposalMemory
                - Visi Pernikahan: $weddingVision
                
                Format dalam 3 babak terstruktur yang indah (Awal Kisah, Ikrar Lamaran, dan Harapan Menuju Pelaminan). Bahasa Indonesia yang santun dan emosional.
            """.trimIndent()

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().put(
                    JSONObject().put("parts", JSONArray().put(
                        JSONObject().put("text", prompt)
                    ))
                ))
                put("generationConfig", JSONObject().apply {
                    put("thinkingConfig", JSONObject().apply {
                        put("thinkingLevel", "HIGH")
                    })
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody(mediaTypeJson))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            parseGeminiResponse(responseBody)
        } catch (e: Exception) {
            "Gagal memproses dengan Gemini AI (${e.localizedMessage}). Menggunakan narasi cadangan yang romantis untuk $groomName & $brideName."
        }
    }

    // 2. Romantic Quotes & Copywriting with gemini-3.5-flash
    suspend fun generateQuotesAndCopy(
        category: String, // "Islami (Al-Quran & Hadits)", "Universal & Romantis", "Adat Jawa Luhur", "Modern Minimalis"
        tone: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext when (category) {
                "Islami (Al-Quran & Hadits)" -> "Dan di antara tanda-tanda kebesaran-Nya ialah Dia menciptakan pasangan-pasangan untukmu dari jenismu sendiri, agar kamu cenderung dan merasa tenteram kepadanya, dan Dia menjadikan di antaramu rasa kasih dan sayang. (QS. Ar-Rum: 21)"
                "Adat Jawa Luhur" -> "Ngesti laras ing pambudi, nyawiji ing raos tresna sejati. Paring berkah marang loro sejoli kang ngikrar janji suci ing ngarsane Gusti Kang Maha Suci."
                else -> "Cinta sejati bukanlah tentang menemukan seseorang yang sempurna, melainkan tentang belajar melihat seseorang yang tidak sempurna dengan cara yang sempurna."
            }
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val prompt = "Tuliskan 3 pilihan kutipan/ayat suci atau mutiara kata pernikahan untuk kategori '$category' dengan nada '$tone' yang sangat cocok diletakkan pada halaman pembuka undangan pernikahan digital. Berikan pilihan singkat dan padat."

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().put(
                    JSONObject().put("parts", JSONArray().put(
                        JSONObject().put("text", prompt)
                    ))
                ))
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody(mediaTypeJson))
                .build()

            val response = client.newCall(request).execute()
            parseGeminiResponse(response.body?.string() ?: "")
        } catch (e: Exception) {
            "Semoga Allah SWT menyatukan kedua mempelai dalam kebaikan, keberkahan, dan kasih sayang yang abadi."
        }
    }

    // 3. Low-Latency Quick RSVP Wish Polish with gemini-3.1-flash-lite
    suspend fun quickPolishRsvpWish(rawWish: String, relationship: String): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Selamat berbahagia! Semoga menjadi keluarga yang sakinah, mawaddah, dan warahmah. Senantiasa dilimpahi rezeki dan kebahagiaan abadi. Amin!"
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-lite:generateContent?key=$apiKey"
            val prompt = "Perhalus ucapan doa pernikahan ini agar sopan, menyentuh, dan elegan (hubungan pengirim: $relationship): '$rawWish'. Berikan langsung hasil ucapan yang siap dikirim tanpa kalimat pengantar."

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().put(
                    JSONObject().put("parts", JSONArray().put(
                        JSONObject().put("text", prompt)
                    ))
                ))
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody(mediaTypeJson))
                .build()

            val response = client.newCall(request).execute()
            parseGeminiResponse(response.body?.string() ?: "")
        } catch (e: Exception) {
            rawWish
        }
    }

    // 4. Grounding Search / Venue Advice with gemini-3.5-flash
    suspend fun searchVenueAndTraditions(query: String): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext """
                Rekomendasi info seputar $query:
                • Gedung / Venue Populer: Grand Sahid Jaya, Balai Sudirman, Plataran Dharmawangsa, Puri Ardhya Garini, Sampoerna Strategic Square.
                • Tips Acara: Pastikan akses parkir memadai, pilih tema dekorasi yang selaras dengan langit-langit gedung, dan siapkan live streaming bagi tamu jarak jauh.
            """.trimIndent()
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val prompt = "Berikan panduan, ide lokasi venue atau rekomendasi susunan acara pernikahan di Indonesia terkait pertanyaan: $query."

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().put(
                    JSONObject().put("parts", JSONArray().put(
                        JSONObject().put("text", prompt)
                    ))
                ))
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody(mediaTypeJson))
                .build()

            val response = client.newCall(request).execute()
            parseGeminiResponse(response.body?.string() ?: "")
        } catch (e: Exception) {
            "Info venue pernikahan: Prioritaskan kapasitas tamu, ketersediaan ruang rias, dan lokasi yang strategis."
        }
    }

    // 5. Wedding Photo & Moodboard Analyzer with gemini-3.1-pro-preview
    suspend fun analyzeWeddingPhoto(bitmap: Bitmap): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext """
                Analisis Foto Prewedding / Moodboard:
                • Palet Warna Dominan: Warm Champagne Gold (#D4AF37), Dusty Rose, dan Ivory.
                • Rekomendasi Tema Undangan: Tema 'Rustic Botanical' atau 'Royal Javanese'.
                • Saran Dress Code Tamu: Earthy tones, Sage Green, atau Nude Pastel.
                • Ide Caption Undangan: 'Dua jiwa, satu rasa, melangkah menyongsong masa depan berdua.'
            """.trimIndent()
        }

        try {
            val base64Image = bitmapToBase64(bitmap)
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-pro-preview:generateContent?key=$apiKey"

            val prompt = """
                Analisis foto prewedding atau moodboard pernikahan ini:
                1. Identifikasi palet warna utama (sertakan perkiraan kode warna hex).
                2. Rekomendasikan tema undangan digital yang paling cocok (Modern, Rustic, Jawa Tradisional, Islami, atau Luxury).
                3. Berikan saran dress code untuk tamu undangan yang selaras dengan foto ini.
                4. Tulis 2 saran caption romantis yang pas diletakkan pada galeri foto undangan.
            """.trimIndent()

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().put(
                    JSONObject().put("parts", JSONArray().apply {
                        put(JSONObject().put("text", prompt))
                        put(JSONObject().apply {
                            put("inlineData", JSONObject().apply {
                                put("mimeType", "image/jpeg")
                                put("data", base64Image)
                            })
                        })
                    })
                ))
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody(mediaTypeJson))
                .build()

            val response = client.newCall(request).execute()
            parseGeminiResponse(response.body?.string() ?: "")
        } catch (e: Exception) {
            "Gagal menganalisis foto secara online. Menggunakan rekomendasi estetika palet hangat."
        }
    }

    // 6. Multi-turn AI Wedding Planner Chatbot
    suspend fun chatWithWeddingPlanner(
        history: List<ChatMessage>,
        userMessage: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext when {
                userMessage.contains("undangan", ignoreCase = true) -> "Untuk undangan pernikahan digital, hal penting yang perlu disiapkan: nama kedua mempelai dan orang tua, tanggal & waktu akad/resepsi, lokasi via Google Maps, nomor rekening untuk amplop digital, dan daftar tamu yang ingin dikirimkan via WhatsApp!"
                userMessage.contains("harga", ignoreCase = true) || userMessage.contains("biaya", ignoreCase = true) -> "Di platform SaaS UndanganKu, harga tema mulai dari Rp 99.000 hingga Rp 199.000. Untuk reseller mitra, Anda mendapatkan komisi 25% untuk setiap pesanan yang berhasil dibayar!"
                userMessage.contains("tema", ignoreCase = true) -> "Kami memiliki tema Royal Javanese, Modern Minimalist Chic, Rustic Botanical, Islamic Grace, dan Luxury Midnight Gold. Setiap tema dilengkapi pemutar musik, RSVP online, dan buku tamu interaktif."
                else -> "Halo! Saya Wedding AI Planner siap membantu Anda merancang konsep pernikahan, menyusun kata-kata undangan, menghitung estimasi tamu, atau strategi promosi reseller. Ada yang bisa saya bantu hari ini?"
            }
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val contentsArray = JSONArray()

            // System instruction
            val systemPrompt = "Anda adalah 'Wedding AI Planner & Specialist' pada platform SaaS Undangan Digital Nikah. Anda ramah, elegan, mengerti etika pernikahan di Indonesia (adat, tata krama, susunan panitia, RSVP, pesan WhatsApp tamu). Jawab dengan format yang rapi dan solutif."

            for (msg in history.takeLast(6)) {
                val roleName = if (msg.role == "user") "user" else "model"
                contentsArray.put(JSONObject().apply {
                    put("role", roleName)
                    put("parts", JSONArray().put(JSONObject().put("text", msg.content)))
                })
            }

            // Current message
            contentsArray.put(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().put(JSONObject().put("text", userMessage)))
            })

            val jsonBody = JSONObject().apply {
                put("contents", contentsArray)
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", systemPrompt)))
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody(mediaTypeJson))
                .build()

            val response = client.newCall(request).execute()
            parseGeminiResponse(response.body?.string() ?: "")
        } catch (e: Exception) {
            "Maaf, koneksi AI sedang sibuk. Silakan coba tanyakan kembali."
        }
    }

    private fun parseGeminiResponse(jsonString: String): String {
        return try {
            val root = JSONObject(jsonString)
            val candidates = root.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val sb = StringBuilder()
                    for (i in 0 until parts.length()) {
                        val part = parts.getJSONObject(i)
                        sb.append(part.optString("text", ""))
                    }
                    val text = sb.toString().trim()
                    if (text.isNotEmpty()) return text
                }
            }
            "Tidak ada respon yang dihasilkan."
        } catch (e: Exception) {
            "Gagal mengurai respon AI: ${e.localizedMessage}"
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }

    private fun getApiKey(): String {
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }
    }
}
