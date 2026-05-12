package com.example.myapplication.ui.theme

import android.app.Activity
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
    primary = BrownPrimaryDark,
    onPrimary = WarmBackgroundDark,
    primaryContainer = BrownPrimaryContainerDark,
    onPrimaryContainer = Color(0xFFFFF3EF),
    secondary = GreenAccentDark,
    onSecondary = Color(0xFF0C1F0E),
    secondaryContainer = GreenAccentContainerDark,
    onSecondaryContainer = Color(0xFFDDF6DE),
    tertiary = Color(0xFFFFB15C),
    background = WarmBackgroundDark,
    onBackground = Color(0xFFEDE0DB),
    surface = WarmSurfaceDark,
    onSurface = Color(0xFFEDE0DB),
    surfaceVariant = WarmSurfaceVariantDark,
    onSurfaceVariant = Color(0xFFD2C1BB),
    outline = WarmOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = BrownPrimary,
    onPrimary = Color.White,
    primaryContainer = BrownPrimaryContainer,
    onPrimaryContainer = Color(0xFF3E2723),
    secondary = GreenAccent,
    onSecondary = Color.White,
    secondaryContainer = GreenAccentContainer,
    onSecondaryContainer = Color(0xFF0F3B13),
    tertiary = Color(0xFFFF8A00),
    background = WarmBackground,
    onBackground = Color(0xFF2D211D),
    surface = WarmSurface,
    onSurface = Color(0xFF2D211D),
    surfaceVariant = WarmSurfaceVariant,
    onSurfaceVariant = Color(0xFF5F524D),
    outline = WarmOutline
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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
