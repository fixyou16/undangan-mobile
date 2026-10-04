package com.undanganmobile

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF8A5575),
    secondary = Color(0xFFE2B7C7),
    background = Color(0xFFF7F1F5),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color(0xFF3E2D32),
    onBackground = Color(0xFF3E2D32),
    onSurface = Color(0xFF3E2D32)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFD7A4B4),
    secondary = Color(0xFF8A5575),
    background = Color(0xFF1F1A1C),
    surface = Color(0xFF2C2427),
    onPrimary = Color(0xFF1F1A1C),
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun UndanganMobileTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
