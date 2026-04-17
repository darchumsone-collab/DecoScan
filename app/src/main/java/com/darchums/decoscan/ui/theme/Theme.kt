package com.darchums.decoscan.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DecoSecondary,
    secondary = DecoTertiary,
    tertiary = Pink80,
    background = DecoTextPrimary,
    onBackground = DecoBackground
)

private val LightColorScheme = lightColorScheme(
    primary = DecoPrimary,
    secondary = DecoSecondary,
    tertiary = DecoTertiary,
    background = DecoBackground,
    onBackground = DecoTextPrimary,
    surface = DecoBackground,
    onSurface = DecoTextPrimary
)

@Composable
fun DecoScanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
