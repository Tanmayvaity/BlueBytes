package com.github.tanmayvaity.bluebytes.ui.theme

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

// Light theme colors
private val Blue10 = Color(0xFF001F33)
private val Blue20 = Color(0xFF003D66)
private val Blue30 = Color(0xFF005C99)
private val Blue40 = Color(0xFF007ACC)
private val Blue80 = Color(0xFF99D6FF)
private val Blue90 = Color(0xFFCCEBFF)

private val BlueGrey10 = Color(0xFF1A1C1E)
private val BlueGrey20 = Color(0xFF2F3133)
private val BlueGrey30 = Color(0xFF45474A)
private val BlueGrey80 = Color(0xFFC4C6C9)
private val BlueGrey90 = Color(0xFFE0E2E5)

private val Cyan40 = Color(0xFF00838F)
private val Cyan80 = Color(0xFF80DEEA)

private val Red40 = Color(0xFFBA1A1A)
private val Red80 = Color(0xFFFFB4AB)
private val Red90 = Color(0xFFFFDAD6)

private val DarkColorScheme = darkColorScheme(
    primary = Blue80,
    onPrimary = Blue20,
    primaryContainer = Blue30,
    onPrimaryContainer = Blue90,
    secondary = BlueGrey80,
    onSecondary = BlueGrey20,
    secondaryContainer = BlueGrey30,
    onSecondaryContainer = BlueGrey90,
    tertiary = Cyan80,
    onTertiary = Color(0xFF003739),
    tertiaryContainer = Color(0xFF004F52),
    onTertiaryContainer = Color(0xFFB2EBF2),
    error = Red80,
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Red90,
    background = Color(0xFF0F1419),
    onBackground = Color(0xFFE2E2E6),
    surface = Color(0xFF0F1419),
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = BlueGrey30,
    onSurfaceVariant = BlueGrey80,
    outline = Color(0xFF8D9199),
    surfaceContainer = Color(0xFF0A1929),  // Deeper blue for bottom nav
)

private val LightColorScheme = lightColorScheme(
    primary = Blue40,
    onPrimary = Color.White,
    primaryContainer = Blue90,
    onPrimaryContainer = Blue10,
    secondary = Color(0xFF535F70),
    onSecondary = Color.White,
    secondaryContainer = BlueGrey90,
    onSecondaryContainer = BlueGrey10,
    tertiary = Cyan40,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFB2EBF2),
    onTertiaryContainer = Color(0xFF002022),
    error = Red40,
    onError = Color.White,
    errorContainer = Red90,
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF8FAFF),
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFF8FAFF),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFDFE3EB),
    onSurfaceVariant = Color(0xFF43474E),
    outline = Color(0xFF73777F),
    surfaceContainer = Color(0xFFE3EEFF),  // Light blue tint for bottom nav
)

@Composable
fun BlueBytesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to use our blue theme by default
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