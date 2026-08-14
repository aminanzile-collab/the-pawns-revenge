package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = ObsidianDark,
    primaryContainer = GoldDark,
    onPrimaryContainer = TextGold,
    secondary = RoyalPurple,
    onSecondary = Color.White,
    secondaryContainer = RoyalPurpleDark,
    onSecondaryContainer = TextLight,
    tertiary = FireOrange,
    onTertiary = ObsidianDark,
    background = ObsidianDark,
    onBackground = TextLight,
    surface = ChessBoardDark,
    onSurface = TextLight,
    surfaceVariant = ChessBoardTile,
    onSurfaceVariant = TextMuted,
    error = BloodRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
