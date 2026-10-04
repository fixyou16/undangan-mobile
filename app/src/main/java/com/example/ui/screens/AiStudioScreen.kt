package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel

@Composable
fun AiStudioScreen(viewModel: MainViewModel) {
    var selectedAiTab by remember { mutableIntStateOf(0) } // 0: Chatbot, 1: High Thinking Story, 2: Quotes, 3: Venue Search, 4: Image Analysis

    val tabs = listOf("🤖 Chat Planner", "🧠 High Thinking", "✨ Quotes & Ayat", "📍 Venue & Trend", "🖼️ Analisis Foto")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedAiTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 16.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedAiTab == index,
                    onClick = { selectedAiTab = index },
                    text = { Text(title, fontSize = 12.sp, fontWeight = if (selectedAiTab == index) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("ai_tab_$index")
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            when (selectedAiTab) {
                0 -> AiChatbotTab(viewModel)
                1 -> AiHighThinkingStoryTab(viewModel)
                2 -> AiQuotesGeneratorTab(viewModel)
                3 -> AiVenueSearchTab(viewModel)
                4 -> AiPhotoAnalyzerTab(viewModel)
            }
        }
    }
}

@Composable
fun AiChatbotTab(viewModel: MainViewModel) {
    val chatHistory by viewModel.chatHistory.collectAsState()
    val isGeneratingAi by viewModel.isGeneratingAi.collectAsState()
    var inputMessage by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(chatHistory.size) {
        if (chatHistory.isNotEmpty()) {
            listState.animateScrollToItem(chatHistory.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SupportAgent, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("AI Wedding Planner & Specialist", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text("Tanyakan format undangan, rundown acara, etika RSVP atau tips reseller.", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = Color.DarkGray)
                }
            }
        }

        // Messages Thread
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(chatHistory) { msg ->
                val isUser = msg.role == "user"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    if (!isUser) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 14.dp,
                            topEnd = 14.dp,
                            bottomStart = if (isUser) 14.dp else 2.dp,
                            bottomEnd = if (isUser) 2.dp else 14.dp
                        ),
                        color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        shadowElevation = 1.dp,
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = msg.content,
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                            color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            if (isGeneratingAi) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI sedang mengetik...", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Input field and send button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputMessage,
                onValueChange = { inputMessage = it },
                placeholder = { Text("Tanyakan konsep undangan...", fontSize = 13.sp) },
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.weight(1f).testTag("chat_input_field"),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (inputMessage.isNotBlank()) {
                        val text = inputMessage.trim()
                        inputMessage = ""
                        viewModel.sendChatMessage(text)
                    }
                },
                enabled = !isGeneratingAi,
                modifier = Modifier.testTag("send_chat_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Kirim", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun AiHighThinkingStoryTab(viewModel: MainViewModel) {
    val isGeneratingAi by viewModel.isGeneratingAi.collectAsState()
    var groom by remember { mutableStateOf("Fajar Pratama") }
    var bride by remember { mutableStateOf("Dina Lestari") }
    var howMet by remember { mutableStateOf("Pertama kali bertukar pandang di perpustakaan kampus saat menyusun tugas akhir") }
    var proposal by remember { mutableStateOf("Momen sunset di Pantai Parangtritis dengan cincin sederhana dan restu orang tua") }
    var vision by remember { mutableStateOf("Mengarungi bahtera rumah tangga yang sakinah, saling mendukung karir dan ibadah bersama") }
    var generatedStory by remember { mutableStateOf("") }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFF871A5B))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("High Thinking Love Story Crafter", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF871A5B)))
                    }
                    Text(
                        "Model gemini-3.1-pro-preview dengan ThinkingLevel.HIGH melakukan penalaran narasi mendalam untuk menghasilkan kisah cinta terindah.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = Color.DarkGray
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        item {
            OutlinedTextField(value = groom, onValueChange = { groom = it }, label = { Text("Nama Pria") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = bride, onValueChange = { bride = it }, label = { Text("Nama Wanita") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = howMet, onValueChange = { howMet = it }, label = { Text("Awal Mula Bertemu") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = proposal, onValueChange = { proposal = it }, label = { Text("Kisah Lamaran") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = vision, onValueChange = { vision = it }, label = { Text("Visi & Harapan Bersama") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    viewModel.generateLoveStory(groom, bride, howMet, proposal, vision) {
                        generatedStory = it
                    }
                },
                enabled = !isGeneratingAi,
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("generate_high_thinking_tab_button")
            ) {
                if (isGeneratingAi) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Deep Thinking sedang menyusun narasi...")
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate Love Story (High Thinking)")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (generatedStory.isNotBlank()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Hasil Narasi Cinta:", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(generatedStory, style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp))
                    }
                }
            }
        }
    }
}

@Composable
fun AiQuotesGeneratorTab(viewModel: MainViewModel) {
    val isGeneratingAi by viewModel.isGeneratingAi.collectAsState()
    var selectedCat by remember { mutableStateOf("Islami (Al-Quran & Hadits)") }
    var selectedTone by remember { mutableStateOf("Sakral & Menyentuh") }
    var generatedQuotes by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Pilih Kategori Kutipan / Doa:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(8.dp))

        listOf(
            "Islami (Al-Quran & Hadits)",
            "Universal & Romantis Puitis",
            "Adat Jawa Luhur (Gending & Budi Pekerti)"
        ).forEach { cat ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (selectedCat == cat) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { selectedCat = cat }
            ) {
                Text(
                    text = cat,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = if (selectedCat == cat) FontWeight.Bold else FontWeight.Normal),
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                viewModel.generateRomanticQuote(selectedCat, selectedTone) {
                    generatedQuotes = it
                }
            },
            enabled = !isGeneratingAi,
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("generate_quotes_tab_button")
        ) {
            if (isGeneratingAi) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Menghasilkan Kutipan...")
            } else {
                Text("Buat Pilihan Kutipan (gemini-3.5-flash)")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (generatedQuotes.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Rekomendasi Kutipan:", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(generatedQuotes, style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp))
                }
            }
        }
    }
}

@Composable
fun AiVenueSearchTab(viewModel: MainViewModel) {
    val isGeneratingAi by viewModel.isGeneratingAi.collectAsState()
    var query by remember { mutableStateOf("Gedung pernikahan kapasitas 500 orang di Jakarta Selatan dengan akses strategis") }
    var searchResult by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Pencarian Venue & Tren (Google Search & Maps Grounding)", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        Text("Dapatkan wawasan lokasi gedung, paket katering, atau adat pernikahan.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Pertanyaan seputar venue atau tren") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                viewModel.searchVenueAdvice(query) {
                    searchResult = it
                }
            },
            enabled = !isGeneratingAi,
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("search_venue_tab_button")
        ) {
            if (isGeneratingAi) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mencari Data Google...")
            } else {
                Icon(Icons.Default.Search, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cari Rekomendasi Venue & Adat")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (searchResult.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Hasil Analisis Grounding:", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(searchResult, style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp))
                }
            }
        }
    }
}

@Composable
fun AiPhotoAnalyzerTab(viewModel: MainViewModel) {
    val isGeneratingAi by viewModel.isGeneratingAi.collectAsState()
    var analysisResult by remember { mutableStateOf("") }

    // Generates a mock prewedding bitmap for vision analysis
    val sampleBitmap = remember {
        val bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply { color = android.graphics.Color.parseColor("#9C6B28") }
        canvas.drawCircle(150f, 150f, 140f, paint)
        paint.color = android.graphics.Color.parseColor("#FFFDF9")
        canvas.drawCircle(150f, 150f, 120f, paint)
        bitmap
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Analisis Foto Prewedding & Moodboard", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        Text("Gunakan gemini-3.1-pro-preview untuk menganalisis palet warna foto, kecocokan tema undangan, dan rekomendasi dress code tamu.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Sample Prewedding Moodboard (Gold & Champagne Rose)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                Text("Simulasi foto busana adat tradisional keemasan", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                viewModel.analyzePreweddingPhoto(sampleBitmap) {
                    analysisResult = it
                }
            },
            enabled = !isGeneratingAi,
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("analyze_photo_tab_button")
        ) {
            if (isGeneratingAi) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Menganalisis Visual dengan Gemini Pro...")
            } else {
                Icon(Icons.Default.ImageSearch, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Analisis Moodboard Foto (Gemini Vision)")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (analysisResult.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Hasil Analisis Estetika:", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(analysisResult, style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp))
                }
            }
        }
    }
}
