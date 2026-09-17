package com.example.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = TealAccent,
    onPrimary = Color.White,
    primaryContainer = TealAccentSoft,
    onPrimaryContainer = TealAccent,
    secondary = Navy,
    onSecondary = Color.White,
    background = StateBgNormal,
    onBackground = Ink,
    surface = CardBg,
    onSurface = Ink,
    surfaceVariant = LineBorder,
    onSurfaceVariant = Muted,
    error = RedAlert,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = TealAccent,
    onPrimary = Color.White,
    secondary = Navy,
    onSecondary = Color.White,
    background = Color(0xFF101B24),
    onBackground = Color(0xFFECEFF1),
    surface = Color(0xFF1A2632),
    onSurface = Color(0xFFECEFF1),
    error = RedAlert,
    onError = Color.White
)

@Composable
fun Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
