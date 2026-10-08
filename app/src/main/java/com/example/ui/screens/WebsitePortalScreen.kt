package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.InvitationEntity
import com.example.ui.MainViewModel
import com.example.util.SaaSWebsiteHtmlGenerator
import com.example.util.WeddingWebsiteHtmlGenerator

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebsitePortalScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit = {}
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Landing Page SaaS, 1: Demo Undangan Web
    var isMobileViewport by remember { mutableStateOf(false) }
    var showDeployDialog by remember { mutableStateOf(false) }

    // Load an invitation for demo
    var demoInvitation by remember { mutableStateOf<InvitationEntity?>(null) }
    LaunchedEffect(Unit) {
        val sample = viewModel.repository.getInvitationBySlug("fajar-dina")
        demoInvitation = sample
    }

    val currentHtml = remember(selectedTab, demoInvitation) {
        if (selectedTab == 0) {
            SaaSWebsiteHtmlGenerator.generateLandingPageHtml()
        } else {
            val inv = demoInvitation ?: InvitationEntity(
                orderId = 1,
                slug = "fajar-dina",
                groomName = "Fajar Pratama",
                brideName = "Nadia Safira",
                eventAt = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),
                venue = "Grand Ballroom Hotel Sahid Jakarta",
                address = "Jl. Jend. Sudirman Kav. 86, Jakarta Pusat"
            )
            WeddingWebsiteHtmlGenerator.generateInvitationHtml(inv, "Tamu Terhormat")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("website_portal_screen")
    ) {
        // Control Toolbar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)) {
                // Segmented Tabs: SaaS Landing Page vs Web Invitation
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Website SaaS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_website_saas")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Mail, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Web Undangan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_web_invitation")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Actions row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Viewport Toggle
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FilledTonalIconToggleButton(
                            checked = !isMobileViewport,
                            onCheckedChange = { isMobileViewport = false },
                            modifier = Modifier.size(36.dp).testTag("btn_desktop_view")
                        ) {
                            Icon(Icons.Default.Laptop, contentDescription = "Desktop View", modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        FilledTonalIconToggleButton(
                            checked = isMobileViewport,
                            onCheckedChange = { isMobileViewport = true },
                            modifier = Modifier.size(36.dp).testTag("btn_mobile_view")
                        ) {
                            Icon(Icons.Default.Smartphone, contentDescription = "Mobile View", modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isMobileViewport) "Mobile (375px)" else "Desktop (Lebar)",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Action buttons: Copy HTML & Open Browser & Deploy Guide
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Website HTML", currentHtml)
                                clipboard.setPrimaryClip(clip)
                                viewModel.showSnackbar("Kode HTML Website disalin ke clipboard!")
                            },
                            modifier = Modifier.size(36.dp).testTag("btn_copy_html")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Salin Kode HTML", modifier = Modifier.size(18.dp))
                        }

                        IconButton(
                            onClick = { showDeployDialog = true },
                            modifier = Modifier.size(36.dp).testTag("btn_deploy_guide")
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = "Panduan Deploy", modifier = Modifier.size(18.dp))
                        }

                        IconButton(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://undangan.ku/"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    viewModel.showSnackbar("Browser eksternal dapat membuka file di /web/index.html")
                                }
                            },
                            modifier = Modifier.size(36.dp).testTag("btn_open_browser")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = "Buka Browser", modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        // Live WebView Container
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .background(Color(0xFF1E1B1E)),
            contentAlignment = Alignment.Center
        ) {
            val contentModifier = if (isMobileViewport) {
                Modifier
                    .fillMaxHeight()
                    .width(375.dp)
                    .padding(vertical = 12.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(3.dp, Color(0xFF333333), RoundedCornerShape(24.dp))
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
                        loadDataWithBaseURL("https://undangan.ku/", currentHtml, "text/html", "UTF-8", null)
                    }
                },
                update = { webView ->
                    webView.loadDataWithBaseURL("https://undangan.ku/", currentHtml, "text/html", "UTF-8", null)
                },
                modifier = contentModifier.testTag("website_webview")
            )
        }
    }

    if (showDeployDialog) {
        AlertDialog(
            onDismissRequest = { showDeployDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Panduan Deploy Versi Web", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "File website mandiri telah disimpan pada direktori /web di project ini:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("• /web/index.html (SaaS Landing Page)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("• /web/invitation.html (Undangan Web)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("• /web/README.md (Dokumentasi)", fontSize = 11.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Cara Hosting Gratis ke Internet:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "1. Vercel: Buka terminal, ketik 'cd web && npx vercel'\n2. Netlify: Upload folder /web ke app.netlify.com/drop\n3. GitHub Pages: Push repo dan aktifkan GitHub Pages di branch main.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showDeployDialog = false }) {
                    Text("Mengerti")
                }
            }
        )
    }
}
