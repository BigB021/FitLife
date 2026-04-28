package com.fitlife.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary           = Green80,
    onPrimary         = OnPrimary,
    primaryContainer  = Green40,
    onPrimaryContainer = Green80,

    secondary         = Steel80,
    onSecondary       = OnPrimary,
    secondaryContainer = Steel40,
    onSecondaryContainer = Steel80,

    tertiary          = Orange80,
    onTertiary        = OnPrimary,
    tertiaryContainer = Orange40,
    onTertiaryContainer = Orange80,

    background        = CharcoalDark,
    onBackground      = OnDark,

    surface           = CharcoalMid,
    onSurface         = OnDark,
    surfaceVariant    = CharcoalLight,
    onSurfaceVariant  = Steel80,

    outline           = SlateGray,
    error             = ErrorRed,
    onError           = OnPrimary,
)

private val LightColorScheme = lightColorScheme(
    primary           = Green40,
    onPrimary         = OnPrimary,
    primaryContainer  = Green80,
    onPrimaryContainer = OnLight,

    secondary         = Steel40,
    onSecondary       = OffWhite,
    secondaryContainer = Steel80,
    onSecondaryContainer = OnLight,

    tertiary          = Orange40,
    onTertiary        = OffWhite,
    tertiaryContainer = Orange80,
    onTertiaryContainer = OnLight,

    background        = OffWhite,
    onBackground      = OnLight,

    surface           = SoftWhite,
    onSurface         = OnLight,
    surfaceVariant    = LightSurface,
    onSurfaceVariant  = Steel40,

    outline           = LightSurface,
    error             = ErrorRed,
    onError           = OffWhite,
    errorContainer    = ErrorRedLight,
    onErrorContainer  = OnLight,
)

@Composable
fun FitLifeTheme(
    darkTheme: Boolean = true,
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

    // Status bar styling
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}