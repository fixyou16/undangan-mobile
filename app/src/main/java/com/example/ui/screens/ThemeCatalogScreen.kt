package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ThemeEntity
import com.example.ui.AppRole
import com.example.ui.AppScreen
import com.example.ui.MainViewModel

@Composable
fun ThemeCatalogScreen(
    viewModel: MainViewModel,
    onSelectTheme: (ThemeEntity) -> Unit,
    onPreviewTheme: (ThemeEntity) -> Unit
) {
    val activeThemes by viewModel.activeThemes.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    var selectedCategory by remember { mutableStateOf("Semua") }

    val categories = listOf("Semua", "Tradisional", "Modern", "Rustic", "Islami", "Luxury")
    val filteredThemes = remember(activeThemes, selectedCategory) {
        if (selectedCategory == "Semua") activeThemes else activeThemes.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("theme_catalog_screen")
    ) {
        // Hero Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Katalog Tema Digital 2026",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        if (currentRole == AppRole.RESELLER) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1E88E5).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Komisi Mitra 25%",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF1E88E5),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Pilih Desain Eksklusif untuk Hari Bahagiamu",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Semua tema responsif mobile, dilengkapi pemutar musik sakral, RSVP online, integrasi Google Maps, dan buku tamu doa restu.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Category filter chips
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp) },
                        modifier = Modifier.testTag("filter_chip_$cat")
                    )
                }
            }
        }

        // Themes list
        items(filteredThemes) { theme ->
            ThemeCardItem(
                theme = theme,
                currentRole = currentRole,
                onSelect = { onSelectTheme(theme) },
                onPreview = { onPreviewTheme(theme) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ThemeCardItem(
    theme: ThemeEntity,
    currentRole: AppRole,
    onSelect: () -> Unit,
    onPreview: () -> Unit
) {
    val accentColor = remember(theme.accentColorHex) {
        try {
            Color(android.graphics.Color.parseColor(theme.accentColorHex))
        } catch (e: Exception) {
            Color(0xFF871A5B)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("theme_card_${theme.slug}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Theme Top Banner Mockup
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                accentColor.copy(alpha = 0.85f),
                                accentColor.copy(alpha = 0.55f)
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.align(Alignment.CenterStart)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.25f)
                    ) {
                        Text(
                            text = theme.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = theme.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Format Undangan Web Interaktif",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f))
                    )
                }

                // Decorative icon
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier
                        .size(54.dp)
                        .align(Alignment.CenterEnd)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = when (theme.category) {
                                "Tradisional" -> Icons.Default.TempleBuddhist
                                "Islami" -> Icons.Default.Mosque
                                "Rustic" -> Icons.Default.Spa
                                "Luxury" -> Icons.Default.Diamond
                                else -> Icons.Default.Favorite
                            },
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // Description and pricing
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = theme.description ?: "Desain eksklusif dengan tipografi modern dan palet elegan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Feature pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    theme.features.split(",").take(3).forEach { feat ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = "✓ ${feat.trim()}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                // Price & Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Harga Lisensi",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Rp ${String.format("%,d", theme.price)}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )

                        if (currentRole == AppRole.RESELLER) {
                            val commission = (theme.price * 0.25).toLong()
                            Text(
                                text = "+ Komisi: Rp ${String.format("%,d", commission)}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onPreview,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("preview_theme_${theme.slug}")
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Preview")
                        }

                        Button(
                            onClick = onSelect,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("select_theme_${theme.slug}")
                        ) {
                            Text("Pilih")
                        }
                    }
                }
            }
        }
    }
}
