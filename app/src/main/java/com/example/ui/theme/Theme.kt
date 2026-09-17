package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PortalCyan,
    onPrimary = VoidDark,
    primaryContainer = VoidSurfaceVariant,
    onPrimaryContainer = PortalCyan,
    secondary = PortalViolet,
    onSecondary = TextPrimaryDark,
    secondaryContainer = VoidCard,
    onSecondaryContainer = PortalSky,
    tertiary = PortalSky,
    onTertiary = VoidDark,
    background = VoidDark,
    onBackground = TextPrimaryDark,
    surface = VoidSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = VoidSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = VoidBorder
)

private val LightColorScheme = lightColorScheme(
    primary = PortalPurple,
    onPrimary = LightSurface,
    primaryContainer = LightSurfaceVariant,
    onPrimaryContainer = PortalPurple,
    secondary = PortalSky,
    onSecondary = LightSurface,
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = PortalPurple,
    tertiary = PortalCyan,
    onTertiary = TextPrimaryLight,
    background = LightCanvas,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = LightBorder
)

@Composable
fun PortalBreakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
