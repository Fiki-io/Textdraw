package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    primaryContainer = DarkNavySurfaceVariant,
    onPrimaryContainer = NeonCyan,
    secondary = AmberAccent,
    onSecondary = Color.Black,
    background = DarkNavyCanvas,
    onBackground = TextPrimary,
    surface = DarkNavySurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkNavySurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DarkNavyBorder,
    error = ErrorRed,
    onError = Color.Black
)

@Composable
fun SAMPTextDrawStudioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Professional gaming studio UI defaults to dark high-contrast
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
