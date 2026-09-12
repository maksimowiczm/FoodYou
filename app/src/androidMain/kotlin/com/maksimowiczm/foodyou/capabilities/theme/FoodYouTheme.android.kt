package com.maksimowiczm.foodyou.capabilities.theme

import android.content.pm.ApplicationInfo
import android.os.Build
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import com.maksimowiczm.foodyou.device.domain.NutrientsColors
import com.maksimowiczm.foodyou.device.domain.Theme
import com.maksimowiczm.foodyou.device.domain.ThemeSettings
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.ktx.animateColorScheme
import com.materialkolor.rememberDynamicColorScheme

@Composable
internal actual fun FoodYouTheme(
    themeSettings: ThemeSettings?,
    nutrientsColors: NutrientsColors?,
    content: @Composable (() -> Unit),
) {
    FoodYouTheme(
        isDark = themeSettings?.isDark() ?: isSystemInDarkTheme(),
        theme = themeSettings?.theme ?: Theme.Default,
        nutrientsColors = nutrientsColors,
        content = content,
    )
}

@Composable
private fun FoodYouTheme(
    isDark: Boolean,
    theme: Theme,
    nutrientsColors: NutrientsColors?,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val view = LocalView.current

    LaunchedEffect(isDark) {
        if (isDark)
            view.windowInsetsController?.setSystemBarsAppearance(
                0,
                APPEARANCE_LIGHT_STATUS_BARS,
            )
        else
            view.windowInsetsController?.setSystemBarsAppearance(
                APPEARANCE_LIGHT_STATUS_BARS,
                APPEARANCE_LIGHT_STATUS_BARS,
            )
    }

    val isDynamic = remember(theme) { theme is Theme.Dynamic || theme is Theme.Default }

    val colorScheme =
        when {
            isDynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
                remember(isDark, context) {
                    if (isDark) dynamicDarkColorScheme(context)
                    else dynamicLightColorScheme(context)
                }
            theme is Theme.Custom -> theme.rememberColorScheme(isDark)
            else ->
                rememberDynamicColorScheme(
                    seedColor = MaterialDeepPurple,
                    isDark = isDark,
                    specVersion = ColorSpec.SpecVersion.SPEC_2025,
                    style = PaletteStyle.Expressive,
                )
        }

    val isDebuggable =
        remember(context) { context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0 }

    val animatedColorScheme =
        if (isDebuggable) colorScheme // Prevent Logger from being bloated with materialkolor
        else
            animateColorScheme(
                colorScheme = colorScheme,
                animationSpec = { MaterialTheme.motionScheme.slowEffectsSpec() },
            )

    val nutrientsPalette =
        remember(isDark, nutrientsColors) {
            val palette = if (isDark) DarkNutrientsPalette else LightNutrientsPalette
            palette.applyColors(nutrientsColors)
        }

    CompositionLocalProvider(LocalNutrientsPalette provides nutrientsPalette) {
        MaterialExpressiveTheme(colorScheme = animatedColorScheme, content = content)
    }
}
