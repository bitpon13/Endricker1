package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SophisticatedPrimary,
    secondary = SophisticatedSecondary,
    tertiary = BrazilYellow,
    background = SophisticatedBg,
    surface = SophisticatedSurface,
    outline = SophisticatedOutline,
    onPrimary = SophisticatedOnPrimaryContainer,
    onSecondary = SophisticatedOnSecondaryContainer,
    onTertiary = Color.Black,
    onBackground = SophisticatedTextPrimary,
    onSurface = SophisticatedTextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = BrazilGreen,
    secondary = BrazilBlue,
    tertiary = BrazilYellow,
    background = Color(0xFFEAF5EB),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark Theme for Sophisticated Dark game feel
    dynamicColor: Boolean = false, // Keep themed colors stable for soccer vibe
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
