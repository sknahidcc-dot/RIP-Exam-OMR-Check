package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AmoledDarkColorScheme = darkColorScheme(
  primary = CyanNeon,
  onPrimary = AmoledBlack,
  primaryContainer = CyanDark,
  onPrimaryContainer = CyanNeon,
  secondary = PurpleNeon,
  onSecondary = AmoledBlack,
  secondaryContainer = PurpleDark,
  onSecondaryContainer = PurpleNeon,
  tertiary = PinkNeon,
  onTertiary = AmoledBlack,
  background = AmoledBlack,
  onBackground = TextWhite,
  surface = DarkSurface,
  onSurface = TextWhite,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = TextMuted,
  surfaceContainer = DarkCardBg,
  surfaceContainerHigh = DarkSurfaceVariant,
  outline = DarkBorder,
  outlineVariant = DarkBorder,
  error = CoralRed,
  onError = TextWhite
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force Pure Dark / AMOLED Black theme as requested
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = AmoledBlack.toArgb()
        window.navigationBarColor = AmoledBlack.toArgb()
        val insetsController = WindowCompat.getInsetsController(window, view)
        insetsController.isAppearanceLightStatusBars = false
        insetsController.isAppearanceLightNavigationBars = false
      }
    }
  }

  MaterialTheme(
    colorScheme = AmoledDarkColorScheme,
    typography = Typography,
    content = content
  )
}
