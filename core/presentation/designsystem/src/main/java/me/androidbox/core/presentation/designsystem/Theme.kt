package me.androidbox.core.presentation.designsystem

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val DarkColorScheme = darkColorScheme(
    primary = BusbyGreen,
    background = BusbyBlack,
    surface = BusbyDarkGray,
    secondary = BusbyWhite,
    tertiary = BusbyWhite,
    primaryContainer = BusbyGreen30,
    onPrimary = BusbyBlack,
    onBackground = BusbyWhite,
    onSurface = BusbyWhite,
    onSurfaceVariant = BusbyGray
)

val LightColorScheme = darkColorScheme(
    primary = BusbyGreenLight,
    background = BusbyBlackLight,
    surface = BusbyDarkGrayLight,
    secondary = BusbyWhiteLight,
    tertiary = BusbyWhiteLight,
    primaryContainer = BusbyGreen30Light,
    onPrimary = BusbyBlackLight,
    onBackground = BusbyWhiteLight,
    onSurface = BusbyWhiteLight,
    onSurfaceVariant = BusbyGrayLight
)

@Composable
fun BusbyRunnerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
   /* val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }*/

    val colorScheme = if(darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }


    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}