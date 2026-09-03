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

private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E4F0),
    onPrimaryContainer = NavyPrimary,
    secondary = EmeraldTrust,
    onSecondary = Color.White,
    secondaryContainer = EmeraldTrustLight,
    onSecondaryContainer = Color(0xFF004D40),
    tertiary = AmberWarning,
    onTertiary = Color.White,
    tertiaryContainer = AmberWarningLight,
    onTertiaryContainer = Color(0xFFE65100),
    error = CoralStop,
    onError = Color.White,
    errorContainer = CoralStopLight,
    onErrorContainer = CoralStop,
    background = SurfaceLight,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceMuted,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    outlineVariant = GreyLightRing
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF82B1FF),
    onPrimary = Color(0xFF001E3C),
    primaryContainer = Color(0xFF1E3A5F),
    onPrimaryContainer = Color(0xFFD6E4F0),
    secondary = Color(0xFF64FFDA),
    onSecondary = Color(0xFF00332C),
    secondaryContainer = Color(0xFF004D40),
    onSecondaryContainer = Color(0xFFA7FFEB),
    tertiary = Color(0xFFFFB74D),
    onTertiary = Color(0xFF4A2800),
    background = Color(0xFF0B141E),
    onBackground = Color(0xFFECEFF1),
    surface = Color(0xFF121E2C),
    onSurface = Color(0xFFECEFF1),
    outline = Color(0xFF2C3E50)
)

@Composable
fun PaysureTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our brand colors by default for consistent safety cues
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
