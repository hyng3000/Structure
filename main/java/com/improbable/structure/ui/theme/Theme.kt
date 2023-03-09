package com.improbable.structure.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorPalette = darkColors(
    primary = structBlack,
    primaryVariant = structGreen,
    secondary = structBeige
)

private val LightColorPalette = lightColors(
    primary = structGreen,
    primaryVariant = structBlack,
    secondary = structBeige,
    background = structWhite,
    surface = structWhite,
    onPrimary = structWhite,
    onSecondary = structBlack,
    onBackground = structBlack,
    onSurface = structBlack,
)

@Composable
fun StructureTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.primary.toArgb()
            window.navigationBarColor = colors.primary.toArgb()

            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = darkTheme
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightNavigationBars = darkTheme
        }
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}