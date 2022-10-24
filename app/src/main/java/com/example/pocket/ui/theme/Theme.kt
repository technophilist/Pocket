package com.example.pocket.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorPalette = darkColors(
    primary = Red300,
    primaryVariant = Red700,
    onPrimary = Color.White,
    error = AmberA100
)

private val LightColorPalette = lightColors(
    primary = Red700,
    primaryVariant = Red900,
    onPrimary = Color.White,
    error = Amber700
)

@Deprecated("Use the other overload.")
@Composable
fun PocketAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette
    MaterialTheme(typography = Typography, colors = colors) { content() }
}

private val DarkColorScheme = darkColorScheme(
    surface = Red300,
    onSurface = Color.White,
    background = Color(0xFF121212),
    primary = Red300,
    secondary = Red700,
    onPrimary = Color.White,
    error = AmberA100
)
private val LightColorScheme = lightColorScheme(
    surface = Red700,
    onSurface = Color.White,
    background = Color.White,
    primary = Red700,
    secondary = Red900,
    onPrimary = Color.White,
    error = Amber700
)

@Composable
fun PocketAppTheme(
    isDarkModeEnabled: Boolean = isSystemInDarkTheme(),
    isDynamicColorsThemeEnabled: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && isDynamicColorsThemeEnabled -> {
            val context = LocalContext.current
            if (isDarkModeEnabled) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        isDarkModeEnabled -> DarkColorScheme
        else -> LightColorScheme
    }
    androidx.compose.material3.MaterialTheme(
        colorScheme = colorScheme,
        typography = M3Typography,
        content = content
    )
}