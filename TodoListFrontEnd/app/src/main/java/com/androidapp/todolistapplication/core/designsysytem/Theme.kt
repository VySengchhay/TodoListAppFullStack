package com.androidapp.todolistapplication.core.designsysytem

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val LightColors = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    primaryContainer = BlueContainerLight,
    onPrimaryContainer = OnBlueContainerLight,
    secondary = Teal,
    onSecondary = Color.White,
    secondaryContainer = TealContainerLight,
    onSecondaryContainer = OnTealContainerLight,
    tertiary = Amber,
    onTertiary = Color.White,
    tertiaryContainer = AmberContainerLight,
    onTertiaryContainer = OnAmberContainerLight,
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OutlineLight,
    surfaceContainerLow = SurfaceContainerLowLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    outline = OutlineLight
)

private val DarkColors = darkColorScheme(
    primary = BluePrimaryDark,
    onPrimary = Color.Black,
    primaryContainer = BlueContainerDark,
    onPrimaryContainer = OnBlueContainerDark,
    secondary = TealDark,
    onSecondary = Color.Black,
    secondaryContainer = TealContainerDark,
    onSecondaryContainer = OnTealContainerDark,
    tertiary = AmberDark,
    onTertiary = Color.Black,
    tertiaryContainer = AmberContainerDark,
    onTertiaryContainer = OnAmberContainerDark,
    error = ErrorRedDark,
    onError = Color.Black,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OutlineDark,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    outline = OutlineDark
)

private val AppTypography = Typography(
    headlineMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 28.sp, lineHeight = 36.sp),
    headlineSmall = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 32.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 22.sp, lineHeight = 28.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp)
)


@Composable
fun TodoListApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Off by default so the brand palette in Color.kt is what ships. Pass true to
    // opt into wallpaper-based colour on Android 12+.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        darkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}