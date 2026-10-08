package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.AndroidView
import com.example.util.WeddingWebsiteHtmlGenerator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GreetingEntity
import com.example.data.model.InvitationEntity
import com.example.ui.MainViewModel
import com.example.util.MusicPlayerSimulator
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PublicInvitationScreen(
    viewModel: MainViewModel,
    slug: String,
    guestName: String = "Tamu Terhormat",
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    var invitation by remember { mutableStateOf<InvitationEntity?>(null) }
    var greetings by remember { mutableStateOf<List<GreetingEntity>>(emptyList()) }
    var isCoverOpened by remember { mutableStateOf(false) }

    LaunchedEffect(slug) {
        invitation = viewModel.repository.getInvitationBySlug(slug)
    }

    LaunchedEffect(invitation?.id) {
        invitation?.id?.let { invId ->
            viewModel.repository.getGreetingsByInvitation(invId).collect {
                greetings = it
            }
        }
    }

    val invite = invitation
    if (invite == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Memuat undangan digital...")
            }
        }
        return
    }

    val accentColor = remember(invite.themeColorHex) {
        try {
            Color(android.graphics.Color.parseColor(invite.themeColorHex))
        } catch (e: Exception) {
            Color(0xFF871A5B)
        }
    }

    val isPlayingMusic by MusicPlayerSimulator.isPlaying.collectAsState()

    var isWebsiteMode by remember { mutableStateOf(false) }
    var isMobileViewport by remember { mutableStateOf(false) }
    val websiteHtml = remember(invite, guestName) {
        WeddingWebsiteHtmlGenerator.generateInvitationHtml(invite, guestName)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Mode Switcher Top Bar (Native Compose vs Website HTML5)
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilterChip(
                        selected = !isWebsiteMode,
                        onClick = { isWebsiteMode = false },
                        label = { Text("App Mode", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.testTag("chip_app_mode")
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    FilterChip(
                        selected = isWebsiteMode,
                        onClick = { isWebsiteMode = true },
                        label = { Text("Versi Web (HTML5)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        leadingIcon = { Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.testTag("chip_web_mode")
                    )
                }

                if (isWebsiteMode) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = { isMobileViewport = !isMobileViewport },
                            modifier = Modifier.size(32.dp).testTag("btn_toggle_invitation_viewport")
                        ) {
                            Icon(
                                if (isMobileViewport) Icons.Default.Laptop else Icons.Default.Smartphone,
                                contentDescription = "Viewport",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Website HTML", websiteHtml)
                                clipboard.setPrimaryClip(clip)
                                viewModel.showSnackbar("Kode HTML Website disalin!")
                            },
                            modifier = Modifier.size(32.dp).testTag("btn_copy_invitation_html")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Salin HTML", modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = {
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    putExtra(Intent.EXTRA_TEXT, "Undangan Pernikahan ${invite.groomName} & ${invite.brideName}: https://undangan.ku/i/${invite.slug}?to=${Uri.encode(guestName)}")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Bagikan Link Undangan"))
                            },
                            modifier = Modifier.size(32.dp).testTag("btn_share_invitation_link")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Bagikan", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        if (isWebsiteMode) {
            // Live HTML5 Website View
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(Color(0xFF0C080D)),
                contentAlignment = Alignment.Center
            ) {
                val contentModifier = if (isMobileViewport) {
                    Modifier
                        .fillMaxHeight()
                        .width(375.dp)
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(2.dp, Color(0xFF444444), RoundedCornerShape(20.dp))
                } else {
                    Modifier.fillMaxSize()
                }

                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.useWideViewPort = true
                            settings.loadWithOverviewMode = true
                            webViewClient = WebViewClient()
                            webChromeClient = WebChromeClient()
                            loadDataWithBaseURL("https://undangan.ku/", websiteHtml, "text/html", "UTF-8", null)
                        }
                    },
                    update = { webView ->
                        webView.loadDataWithBaseURL("https://undangan.ku/", websiteHtml, "text/html", "UTF-8", null)
                    },
                    modifier = contentModifier.testTag("invitation_webview")
                )
            }
        } else {
            // Native App Box View
            Box(modifier = Modifier.fillMaxSize().weight(1f)) {
        // Main Invitation Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFFDF9))
                .testTag("public_invitation_scrollable")
        ) {
            // Hero Banner & Names
            item {
                InvitationHeroSection(
                    invitation = invite,
                    accentColor = accentColor,
                    guestName = guestName
                )
            }

            // Sacred Quote
            item {
                SacredQuoteSection(quote = invite.quote, accentColor = accentColor)
            }

            // Couple Details
            item {
                CoupleProfileSection(invitation = invite, accentColor = accentColor)
            }

            // Event Details (Akad & Resepsi) + Countdown Timer
            item {
                EventScheduleSection(
                    invitation = invite,
                    accentColor = accentColor,
                    onOpenMap = {
                        val mapsUri = invite.mapsUrl ?: "https://maps.google.com/?q=${Uri.encode(invite.venue)}"
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(mapsUri)))
                    }
                )
            }

            // Love Story Timeline
            item {
                LoveStorySection(invitation = invite, accentColor = accentColor)
            }

            // Gallery Section with Aspect Ratio Selector
            item {
                PreweddingGallerySection(accentColor = accentColor)
            }

            // RSVP Form Section
            item {
                RsvpSubmissionSection(
                    viewModel = viewModel,
                    invitation = invite,
                    initialGuestName = if (guestName == "Tamu Terhormat") "" else guestName,
                    accentColor = accentColor
                )
            }

            // Greetings Wall (Buku Tamu)
            item {
                GreetingsWallHeader(accentColor = accentColor)
            }

            if (greetings.isEmpty()) {
                item {
                    Text(
                        text = "Belum ada ucapan. Jadilah yang pertama memberikan doa restu!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    )
                }
            } else {
                items(greetings) { greeting ->
                    GreetingCardItem(greeting = greeting, accentColor = accentColor)
                }
            }

            // Digital Envelope / Amplop Digital & Gift
            item {
                DigitalEnvelopeSection(
                    invitation = invite,
                    accentColor = accentColor,
                    onCopyAccount = { accountNum ->
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Nomor Rekening", accountNum)
                        clipboard.setPrimaryClip(clip)
                        viewModel.showSnackbar("Nomor rekening $accountNum berhasil disalin ke clipboard!")
                    }
                )
            }

            // Health Protocol & Footer
            item {
                InvitationFooterSection(invitation = invite, accentColor = accentColor)
            }
        }

        // Floating Music Equalizer Controller
        FloatingActionButton(
            onClick = { MusicPlayerSimulator.togglePlay(invite.musicTitle) },
            containerColor = accentColor,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .size(52.dp)
                .testTag("floating_music_button")
        ) {
            Icon(
                imageVector = if (isPlayingMusic) Icons.Default.MusicNote else Icons.Default.MusicOff,
                contentDescription = if (isPlayingMusic) "Mute Musik" else "Putar Musik"
            )
        }

        // Grand Opening Envelope Modal (Shows until "Buka Undangan" is clicked)
        androidx.compose.animation.AnimatedVisibility(
            visible = !isCoverOpened,
            enter = fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            GrandOpeningCoverModal(
                invitation = invite,
                guestName = guestName,
                accentColor = accentColor,
                onOpen = {
                    isCoverOpened = true
                    MusicPlayerSimulator.play(invite.musicTitle)
                }
            )
        }
    }
}
}
}

@Composable
fun GrandOpeningCoverModal(
    invitation: InvitationEntity,
    guestName: String,
    accentColor: Color,
    onOpen: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2C1824),
                        Color(0xFF1E1019),
                        Color(0xFF0F080D)
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Elegant Monogram Ring
            Surface(
                shape = CircleShape,
                color = accentColor.copy(alpha = 0.25f),
                border = BorderStroke(2.dp, accentColor),
                modifier = Modifier.size(90.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val groomInitial = invitation.groomName.firstOrNull()?.toString() ?: "F"
                    val brideInitial = invitation.brideName.firstOrNull()?.toString() ?: "D"
                    Text(
                        text = "$groomInitial & $brideInitial",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFE082),
                            letterSpacing = 2.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "THE WEDDING OF",
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 4.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Light
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${invitation.groomName.split(" ").first()} & ${invitation.brideName.split(" ").first()}",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD54F),
                    fontSize = 32.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Kepada Yth. Bapak/Ibu/Saudara/i:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = guestName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Mohon maaf bila ada kesalahan penulisan nama/gelar",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = Color.White.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = onOpen,
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 14.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                modifier = Modifier.testTag("open_invitation_button")
            ) {
                Icon(
                    imageVector = Icons.Default.MailOutline,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Buka Undangan",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }
        }
    }
}

@Composable
fun InvitationHeroSection(
    invitation: InvitationEntity,
    accentColor: Color,
    guestName: String
) {
    val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
    val eventDateStr = sdf.format(Date(invitation.eventAt))

    Surface(
        color = accentColor.copy(alpha = 0.08f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp, horizontal = 20.dp)
        ) {
            Text(
                text = "WALIMATUL 'URSY",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 3.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = accentColor
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${invitation.groomName}\n&\n${invitation.brideName}",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C1824),
                    lineHeight = 36.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = accentColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "📅 $eventDateStr",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = accentColor
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "📍 ${invitation.venue}",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SacredQuoteSection(quote: String?, accentColor: Color) {
    if (quote.isNullOrBlank()) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.FormatQuote,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = quote,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontStyle = FontStyle.Italic,
                    lineHeight = 22.sp
                ),
                textAlign = TextAlign.Center,
                color = Color(0xFF333333)
            )
        }
    }
}

@Composable
fun CoupleProfileSection(invitation: InvitationEntity, accentColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Kedua Mempelai",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        )
        Text(
            text = "Maha Suci Allah yang telah menciptakan makhluk-Nya berpasang-pasangan",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Groom Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = accentColor.copy(alpha = 0.2f),
                    modifier = Modifier.size(60.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = invitation.groomName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = invitation.groomBio,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "&",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Bride Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = accentColor.copy(alpha = 0.2f),
                    modifier = Modifier.size(60.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = invitation.brideName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = invitation.brideBio,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun EventScheduleSection(
    invitation: InvitationEntity,
    accentColor: Color,
    onOpenMap: () -> Unit
) {
    // Dynamic countdown timer
    var remainingTime by remember { mutableStateOf(invitation.eventAt - System.currentTimeMillis()) }

    LaunchedEffect(invitation.eventAt) {
        while (true) {
            remainingTime = invitation.eventAt - System.currentTimeMillis()
            delay(1000)
        }
    }

    val days = (remainingTime / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
    val hours = ((remainingTime / (1000 * 60 * 60)) % 24).coerceAtLeast(0)
    val minutes = ((remainingTime / (1000 * 60)) % 60).coerceAtLeast(0)
    val seconds = ((remainingTime / 1000) % 60).coerceAtLeast(0)

    val sdfTime = SimpleDateFormat("HH:mm", Locale("id", "ID"))
    val sdfDate = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
    val dateText = sdfDate.format(Date(invitation.eventAt))
    val timeText = sdfTime.format(Date(invitation.eventAt))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Waktu & Tempat Acara",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Countdown blocks
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CountdownBlock(number = "$days", label = "Hari", accentColor = accentColor)
                CountdownBlock(number = "$hours", label = "Jam", accentColor = accentColor)
                CountdownBlock(number = "$minutes", label = "Menit", accentColor = accentColor)
                CountdownBlock(number = "$seconds", label = "Detik", accentColor = accentColor)
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Akad Nikah
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Akad Nikah",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(text = "Pukul 08:00 - 10:00 WIB", style = MaterialTheme.typography.bodyMedium)
                    Text(text = dateText, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Resepsi Pernikahan
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Celebration,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Resepsi Pernikahan",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(text = "Pukul $timeText WIB - Selesai", style = MaterialTheme.typography.bodyMedium)
                    Text(text = dateText, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = invitation.venue,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    if (!invitation.address.isNullOrBlank()) {
                        Text(
                            text = invitation.address,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onOpenMap,
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_maps_button")
            ) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Buka Petunjuk Google Maps")
            }
        }
    }
}

@Composable
fun CountdownBlock(number: String, label: String, accentColor: Color) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = accentColor.copy(alpha = 0.1f),
        modifier = Modifier.size(68.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun LoveStorySection(invitation: InvitationEntity, accentColor: Color) {
    if (invitation.story.isNullOrBlank()) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = accentColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Kisah Cinta Kami (Love Story)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = invitation.story,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                color = Color(0xFF333333)
            )
        }
    }
}

@Composable
fun PreweddingGallerySection(accentColor: Color) {
    var selectedRatio by remember { mutableStateOf("1:1") } // "1:1", "4:3", "16:9"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Galeri Prewedding",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            )
            Text(
                text = "Momen berharga dalam balutan keanggunan",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Aspect Ratio Selector Chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Format Rasio:", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                listOf("1:1", "4:3", "16:9").forEach { ratio ->
                    FilterChip(
                        selected = selectedRatio == ratio,
                        onClick = { selectedRatio = ratio },
                        label = { Text(ratio, fontSize = 11.sp) },
                        modifier = Modifier.testTag("ratio_chip_$ratio")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            val aspectRatioFloat = when (selectedRatio) {
                "4:3" -> 4f / 3f
                "16:9" -> 16f / 9f
                else -> 1f
            }

            // Gallery Mockup Photos
            listOf(
                "Momen Pertunangan di Yogyakarta",
                "Sesi Prewedding Adat Tradisional",
                "Senja Bersama di Pantai Parangtritis"
            ).forEachIndexed { index, caption ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = accentColor.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(aspectRatioFloat)
                        .padding(bottom = 12.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = caption,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Rasio $selectedRatio • Format Digital HD",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RsvpSubmissionSection(
    viewModel: MainViewModel,
    invitation: InvitationEntity,
    initialGuestName: String,
    accentColor: Color
) {
    var guestName by remember { mutableStateOf(initialGuestName) }
    var selectedStatus by remember { mutableStateOf("attending") } // "attending", "declined"
    var guestCount by remember { mutableIntStateOf(1) }
    var message by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("Teman Kampus") }

    val isGeneratingAi by viewModel.isGeneratingAi.collectAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Konfirmasi Kehadiran (RSVP)",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            )
            Text(
                text = "Mohon konfirmasikan kehadiran Anda untuk membantu persiapan kami",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = guestName,
                onValueChange = { guestName = it },
                label = { Text("Nama Lengkap") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("rsvp_name_input"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Status Kehadiran:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedStatus == "attending",
                    onClick = { selectedStatus = "attending" },
                    label = { Text("✅ Hadir") },
                    modifier = Modifier.testTag("rsvp_status_attending")
                )
                FilterChip(
                    selected = selectedStatus == "declined",
                    onClick = { selectedStatus = "declined" },
                    label = { Text("❌ Tidak Hadir") },
                    modifier = Modifier.testTag("rsvp_status_declined")
                )
            }

            if (selectedStatus == "attending") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Jumlah Tamu:", style = MaterialTheme.typography.bodySmall)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (guestCount > 1) guestCount-- }) {
                            Icon(Icons.Default.Remove, contentDescription = "Kurang")
                        }
                        Text("$guestCount Orang", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        IconButton(onClick = { if (guestCount < 4) guestCount++ }) {
                            Icon(Icons.Default.Add, contentDescription = "Tambah")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Ucapan Doa & Harapan") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("rsvp_message_input"),
                minLines = 3,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(8.dp))

            // AI Fast Polish Button (gemini-3.1-flash-lite)
            OutlinedButton(
                onClick = {
                    val baseWish = if (message.isNotBlank()) message else "Selamat ya semoga bahagia lancar acaranya"
                    viewModel.quickPolishWish(baseWish, relationship) { polished ->
                        message = polished
                    }
                },
                enabled = !isGeneratingAi,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("rsvp_ai_polish_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = accentColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isGeneratingAi) "Memoles Doa dengan AI..." else "✨ Poles Ucapan dengan AI (Cepat)",
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (guestName.isBlank()) {
                        viewModel.showSnackbar("Silakan masukkan nama Anda terlebih dahulu.")
                        return@Button
                    }
                    viewModel.submitRsvp(
                        invitationId = invitation.id,
                        name = guestName,
                        status = selectedStatus,
                        count = guestCount,
                        message = message.ifBlank { null },
                        token = null
                    ) {
                        message = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("rsvp_submit_button")
            ) {
                Text("Kirim Konfirmasi Kehadiran", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun GreetingsWallHeader(accentColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Forum, contentDescription = null, tint = accentColor)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Buku Tamu & Doa Restu",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            )
        }
    }
}

@Composable
fun GreetingCardItem(greeting: GreetingEntity, accentColor: Color) {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    val dateStr = sdf.format(Date(greeting.createdAt))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = accentColor.copy(alpha = 0.15f),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = greeting.guestName.firstOrNull()?.toString() ?: "T",
                                fontWeight = FontWeight.Bold,
                                color = accentColor,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = greeting.guestName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (greeting.statusAttendance == "attending") Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                ) {
                    Text(
                        text = if (greeting.statusAttendance == "attending") "Hadir" else "Berhalangan",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (greeting.statusAttendance == "attending") Color(0xFF2E7D32) else Color(0xFFC62828),
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = greeting.message,
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                color = Color(0xFF444444)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = dateStr,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = Color.Gray
            )
        }
    }
}

@Composable
fun DigitalEnvelopeSection(
    invitation: InvitationEntity,
    accentColor: Color,
    onCopyAccount: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Amplop Digital & Kado Kasih",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            )
            Text(
                text = "Doa restu Anda adalah karunia terindah bagi kami. Namun jika berkenan memberikan tanda kasih:",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            // Bank Account Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = accentColor.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = invitation.bankName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                            )
                        )
                        IconButton(
                            onClick = { onCopyAccount(invitation.bankAccount) },
                            modifier = Modifier.testTag("copy_bank_account_button")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Salin Nomor Rekening", tint = accentColor)
                        }
                    }
                    Text(
                        text = invitation.bankAccount,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    )
                    Text(
                        text = "a.n. ${invitation.bankHolder}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

@Composable
fun InvitationFooterSection(invitation: InvitationEntity, accentColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Merupakan suatu kehormatan dan kebahagiaan bagi kami apabila Bapak/Ibu/Saudara/i berkenan hadir dan memberikan doa restu.",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Kami yang berbahagia,",
            style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
            color = Color.DarkGray
        )
        Text(
            text = "${invitation.groomName.split(" ").first()} & ${invitation.brideName.split(" ").first()}",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Powered by UndanganKu • SaaS Undangan Digital Indonesia",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = Color.LightGray
        )
    }
}
