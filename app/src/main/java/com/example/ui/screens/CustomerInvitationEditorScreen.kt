package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.data.model.InvitationEntity
import com.example.ui.MainViewModel

@Composable
fun CustomerInvitationEditorScreen(
    viewModel: MainViewModel,
    orderId: Long,
    onPreviewLive: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var invitation by remember { mutableStateOf<InvitationEntity?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(orderId) {
        invitation = viewModel.repository.getInvitationByOrderId(orderId)
        isLoading = false
    }

    if (isLoading || invitation == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val currentInvite = invitation!!

    var groomName by remember { mutableStateOf(currentInvite.groomName) }
    var brideName by remember { mutableStateOf(currentInvite.brideName) }
    var groomBio by remember { mutableStateOf(currentInvite.groomBio) }
    var brideBio by remember { mutableStateOf(currentInvite.brideBio) }
    var venue by remember { mutableStateOf(currentInvite.venue) }
    var address by remember { mutableStateOf(currentInvite.address ?: "") }
    var mapsUrl by remember { mutableStateOf(currentInvite.mapsUrl ?: "") }
    var quote by remember { mutableStateOf(currentInvite.quote ?: "") }
    var story by remember { mutableStateOf(currentInvite.story ?: "") }
    var themeColorHex by remember { mutableStateOf(currentInvite.themeColorHex) }
    var musicTitle by remember { mutableStateOf(currentInvite.musicTitle) }
    var bankName by remember { mutableStateOf(currentInvite.bankName) }
    var bankAccount by remember { mutableStateOf(currentInvite.bankAccount) }
    var bankHolder by remember { mutableStateOf(currentInvite.bankHolder) }
    var isPublished by remember { mutableStateOf(currentInvite.isPublished) }

    val isGeneratingAi by viewModel.isGeneratingAi.collectAsState()

    // Dialogs for AI generation
    var showAiStoryModal by remember { mutableStateOf(false) }
    var showAiQuoteModal by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("invitation_editor_screen")
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Editor Undangan Digital",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Link Publik: undangan.id/i/${currentInvite.slug}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Button(
                    onClick = { onPreviewLive(currentInvite.slug) },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.testTag("live_preview_button")
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Preview", fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Section: Profil Mempelai & Keluarga
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Profil Mempelai & Keluarga",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = groomName,
                        onValueChange = { groomName = it },
                        label = { Text("Nama Pengantin Pria & Gelar") },
                        modifier = Modifier.fillMaxWidth().testTag("editor_groom_name")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = groomBio,
                        onValueChange = { groomBio = it },
                        label = { Text("Keterangan Keluarga Pria (Putra dari...)") },
                        modifier = Modifier.fillMaxWidth().testTag("editor_groom_bio")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = brideName,
                        onValueChange = { brideName = it },
                        label = { Text("Nama Pengantin Wanita & Gelar") },
                        modifier = Modifier.fillMaxWidth().testTag("editor_bride_name")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = brideBio,
                        onValueChange = { brideBio = it },
                        label = { Text("Keterangan Keluarga Wanita (Putri dari...)") },
                        modifier = Modifier.fillMaxWidth().testTag("editor_bride_bio")
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Section: Love Story & High Thinking AI
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "2. Kisah Cinta (Love Story)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        OutlinedButton(
                            onClick = { showAiStoryModal = true },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("ai_story_crafter_button")
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("🧠 AI High Thinking", fontSize = 11.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = story,
                        onValueChange = { story = it },
                        label = { Text("Teks Narasi Love Story") },
                        minLines = 4,
                        modifier = Modifier.fillMaxWidth().testTag("editor_story_input")
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Section: Kutipan / Ayat Suci
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "3. Ayat / Kutipan Suci",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        OutlinedButton(
                            onClick = { showAiQuoteModal = true },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("ai_quote_generator_button")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("✨ Buat Ayat AI", fontSize = 11.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = quote,
                        onValueChange = { quote = it },
                        label = { Text("Kutipan Suci / Mutiara Kata") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("editor_quote_input")
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Section: Tampilan & Musik
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "4. Warna Aksen & Musik Latar",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Pilih Palet Aksen:", style = MaterialTheme.typography.bodySmall)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listOf(
                            "#871A5B" to "Wine Rose",
                            "#9C6B28" to "Royal Gold",
                            "#1B4D3E" to "Emerald",
                            "#A0522D" to "Terracotta",
                            "#1A2A3A" to "Midnight Navy"
                        ).forEach { (hex, name) ->
                            val color = Color(android.graphics.Color.parseColor(hex))
                            val isSelected = themeColorHex.equals(hex, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable { themeColorHex = hex },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = name, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = musicTitle,
                        onValueChange = { musicTitle = it },
                        label = { Text("Judul Trek Musik") },
                        leadingIcon = { Icon(Icons.Default.Audiotrack, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("editor_music_input")
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Section: Amplop Digital
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "5. Amplop Digital (Hadiah)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = bankName,
                        onValueChange = { bankName = it },
                        label = { Text("Nama Bank / E-Wallet (BCA, Mandiri, QRIS)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = bankAccount,
                        onValueChange = { bankAccount = it },
                        label = { Text("Nomor Rekening / No. HP") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = bankHolder,
                        onValueChange = { bankHolder = it },
                        label = { Text("Atas Nama Rekening") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Section: Status Publikasi
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Status Publikasi Undangan",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (isPublished) "Aktif dan dapat diakses tamu publik" else "Draft (Hanya pemilik yang dapat melihat)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isPublished,
                        onCheckedChange = { isPublished = it },
                        modifier = Modifier.testTag("publish_switch")
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Save Button
        item {
            Button(
                onClick = {
                    val updated = currentInvite.copy(
                        groomName = groomName,
                        brideName = brideName,
                        groomBio = groomBio,
                        brideBio = brideBio,
                        venue = venue,
                        address = address.ifBlank { null },
                        mapsUrl = mapsUrl.ifBlank { null },
                        quote = quote.ifBlank { null },
                        story = story.ifBlank { null },
                        themeColorHex = themeColorHex,
                        musicTitle = musicTitle,
                        bankName = bankName,
                        bankAccount = bankAccount,
                        bankHolder = bankHolder,
                        isPublished = isPublished
                    )
                    viewModel.saveInvitation(updated)
                    invitation = updated
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_invitation_button")
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simpan Perubahan Undangan", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Modal: AI High Thinking Love Story Crafter
    if (showAiStoryModal) {
        var howMet by remember { mutableStateOf("Bertemu saat kegiatan sosial di Yogyakarta tahun 2021") }
        var proposal by remember { mutableStateOf("Lamaran di tepi bukit senja dihadiri keluarga inti") }
        var vision by remember { mutableStateOf("Membangun keluarga sakinah dan berkarya bersama") }

        AlertDialog(
            onDismissRequest = { showAiStoryModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Psychology, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Story Crafter (Thinking Mode: HIGH)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Menggunakan model gemini-3.1-pro-preview dengan ThinkingLevel.HIGH untuk menenun narasi cinta puitis dan mendalam.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = howMet,
                        onValueChange = { howMet = it },
                        label = { Text("Awal Mula Bertemu") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = proposal,
                        onValueChange = { proposal = it },
                        label = { Text("Momen Lamaran / Kepastian") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = vision,
                        onValueChange = { vision = it },
                        label = { Text("Harapan & Visi Berdua") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.generateLoveStory(groomName, brideName, howMet, proposal, vision) { generatedStory ->
                            story = generatedStory
                            showAiStoryModal = false
                        }
                    },
                    enabled = !isGeneratingAi,
                    modifier = Modifier.testTag("generate_high_thinking_story_button")
                ) {
                    if (isGeneratingAi) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Menganalisis & Menulis...")
                    } else {
                        Text("Tulis Kisah Cinta")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showAiStoryModal = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Modal: AI Quote Generator
    if (showAiQuoteModal) {
        var category by remember { mutableStateOf("Islami (Al-Quran & Hadits)") }
        var tone by remember { mutableStateOf("Penuh Khidmat & Sakral") }

        AlertDialog(
            onDismissRequest = { showAiQuoteModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Quotes & Ayat Pernikahan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Dihasilkan secara cerdas dengan gemini-3.5-flash untuk kutipan undangan yang menyentuh hati.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    listOf("Islami (Al-Quran & Hadits)", "Universal & Romantis", "Adat Jawa Luhur").forEach { cat ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { category = cat }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(selected = category == cat, onClick = { category = cat })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(cat, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.generateRomanticQuote(category, tone) { generatedQuote ->
                            quote = generatedQuote
                            showAiQuoteModal = false
                        }
                    },
                    enabled = !isGeneratingAi,
                    modifier = Modifier.testTag("confirm_ai_quote_button")
                ) {
                    if (isGeneratingAi) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Memproses...")
                    } else {
                        Text("Gunakan Kutipan")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showAiQuoteModal = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
