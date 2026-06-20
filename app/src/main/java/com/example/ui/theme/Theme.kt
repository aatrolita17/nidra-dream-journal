package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = AmethystPrimary,
    secondary = AmethystSecondary,
    tertiary = AmethystTertiary,
    background = ObsidianBackground,
    surface = ObsidianSurface,
    surfaceVariant = ObsidianSurfaceVariant,
    onPrimary = Color.White,
    onSecondary = ObsidianBackground,
    onTertiary = ObsidianBackground,
    onBackground = MoonSilver,
    onSurface = MoonSilver,
    onSurfaceVariant = MoonSilver
  )

private val LightColorScheme = DarkColorScheme // Standard immersive dark-mode to avoid blinding white screens for a Sleep app!

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force immersive dark mode for Nidra (Sleep/Dream App) to feel ethereal
  dynamicColor: Boolean = false, // Disable dynamic colors to preserve our beautifully crafted cosmic brand colors
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
