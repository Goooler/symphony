package io.github.zyrouge.symphony.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import io.github.zyrouge.symphony.ui.helpers.ViewContext

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
    BLACK,
}

enum class ColorSchemeMode {
    LIGHT,
    DARK,
    BLACK
}

@Composable
fun SymphonyTheme(
    context: ViewContext,
    content: @Composable () -> Unit,
) {
    val themeMode by context.symphony.settings.themeMode.collectAsState()
    val useMaterialYou by context.symphony.settings.useMaterialYou.collectAsState()
    val primaryColorName by context.symphony.settings.primaryColor.collectAsState()
    val fontName by context.symphony.settings.fontFamily.collectAsState()

    val colorSchemeMode = when (themeMode) {
        ThemeMode.SYSTEM -> if (isSystemInDarkTheme()) ColorSchemeMode.DARK else ColorSchemeMode.LIGHT
        ThemeMode.LIGHT -> ColorSchemeMode.LIGHT
        ThemeMode.DARK -> ColorSchemeMode.DARK
        ThemeMode.BLACK -> ColorSchemeMode.BLACK
    }

    val colorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && useMaterialYou) {
        val currentContext = LocalContext.current
        when (colorSchemeMode) {
            ColorSchemeMode.LIGHT -> dynamicLightColorScheme(currentContext)
            ColorSchemeMode.DARK -> dynamicDarkColorScheme(currentContext)
            ColorSchemeMode.BLACK -> ThemeColorSchemes.toBlackColorScheme(
                dynamicDarkColorScheme(currentContext)
            )
        }
    } else {
        val primaryColor = ThemeColors.resolvePrimaryColor(primaryColorName)
        when (colorSchemeMode) {
            ColorSchemeMode.LIGHT -> ThemeColorSchemes.createLightColorScheme(primaryColor)
            ColorSchemeMode.DARK -> ThemeColorSchemes.createDarkColorScheme(primaryColor)
            ColorSchemeMode.BLACK -> ThemeColorSchemes.createBlackColorScheme(primaryColor)
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        val activity = view.context as Activity
        SideEffect {
            activity.window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(activity.window, view)
                .isAppearanceLightStatusBars = colorSchemeMode == ColorSchemeMode.LIGHT
        }
    }

    val typography = SymphonyTypography.toTypography(
        SymphonyTypography.resolveFont(fontName)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
