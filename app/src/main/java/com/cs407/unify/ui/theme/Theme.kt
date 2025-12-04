package com.cs407.unify.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Custom theme extension to hold app-specific colors (like gradients)
// These are not part of the default Material3 color scheme.
data class CustomColors(
    val gradientTop: Color,
    val gradientBottom: Color
)

// CompositionLocal to provide access to CustomColors throughout the app
private val LocalCustomColors = staticCompositionLocalOf {
    CustomColors(
        gradientTop = Color.Unspecified,
        gradientBottom = Color.Unspecified
    )
}

// Default Material 3 dark color scheme for the app
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color(0xFF121212),
    surface = SurfaceBackgroundDark,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color.LightGray,
    outline = OutlineLight
)

// Default Material 3 light color scheme for the app
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color.White,
    surface = SurfaceBackground,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onSurfaceVariant = Color.Gray,
    outline = OutlineLight
)

// Light mode gradient values
val LightCustomColors = CustomColors(
    gradientTop = LightGradientTop,
    gradientBottom = LightGradientBottom
)

// Dark mode gradient values
val DarkCustomColors = CustomColors(
    gradientTop = DarkGradientTop,
    gradientBottom = DarkGradientBottom
)

// Singleton object to expose custom colors anywhere in the app
object AppTheme {
    val customColors: CustomColors
        @Composable
        @ReadOnlyComposable
        get() = LocalCustomColors.current
}

// Dark mode preference management
object ThemeManager {
    // User's preference (only used when system is in light mode)
    var userDarkModePreference = mutableStateOf(false)

    // System dark mode state
    var isSystemInDarkMode: Boolean = false

    /**
     * Determines if dark mode should be active
     * Rule: If system is in dark mode, always use dark mode
     *       If system is in light mode, use user's preference
     */
    fun shouldUseDarkMode(systemInDarkMode: Boolean): Boolean {
        isSystemInDarkMode = systemInDarkMode
        return if (systemInDarkMode) {
            true // Force dark mode when system is dark
        } else {
            userDarkModePreference.value // Use user preference when system is light
        }
    }

    /**
     * Check if user can toggle dark mode
     * Returns: false if system is in dark mode (locked to dark)
     *          true if system is in light mode (user can toggle)
     */
    fun canToggleDarkMode(): Boolean {
        return !isSystemInDarkMode
    }
}

/**
 * Top-level theme composable for the app.
 *
 * - Supports dark/light themes with user preference and system override
 * - Optionally uses dynamic colors (on Android 12+)
 * - Provides both Material3 colors and app-specific CustomColors
 */
@Composable
fun UnifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Detect system dark mode by default
    dynamicColor: Boolean = false,              // Disable dynamic colors to use custom gradients
    content: @Composable () -> Unit             // UI content wrapped in this theme
) {
    // Determine if we should use dark mode based on system and user preference
    val systemInDarkMode = isSystemInDarkTheme()
    val useDarkMode = ThemeManager.shouldUseDarkMode(systemInDarkMode)

    // Choose appropriate color scheme
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (useDarkMode) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Fallback to predefined dark or light schemes
        useDarkMode -> DarkColorScheme
        else -> LightColorScheme
    }

    // Select custom gradient colors depending on theme
    val currentCustomColors = if (useDarkMode) DarkCustomColors else LightCustomColors

    // Set status bar appearance (light/dark icons depending on theme)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkMode
        }
    }

    // Provide both MaterialTheme colors and our CustomColors to the app
    CompositionLocalProvider(LocalCustomColors provides currentCustomColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}