package com.maksimowiczm.foodyou.app.ui.common.theme

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import com.maksimowiczm.foodyou.device.domain.NutrientsColors
import com.maksimowiczm.foodyou.device.domain.Theme
import com.maksimowiczm.foodyou.device.domain.ThemeSettings
import com.materialkolor.ktx.animateColorScheme
import com.materialkolor.rememberDynamicColorScheme

@Composable
internal actual fun FoodYouTheme(
    themeSettings: ThemeSettings?,
    nutrientsColors: NutrientsColors?,
    content: @Composable (() -> Unit),
) {
    if (themeSettings == null) {
        FoodYouTheme(
            isDark = isSystemInDarkTheme(),
            theme = Theme.Default,
            nutrientsColors = nutrientsColors,
            content = content,
        )
    } else {
        FoodYouTheme(
            isDark = themeSettings.isDark(),
            theme = themeSettings.theme,
            nutrientsColors = nutrientsColors,
            content = content,
        )
    }
}

@Composable
private fun FoodYouTheme(
    isDark: Boolean,
    theme: Theme,
    nutrientsColors: NutrientsColors?,
    content: @Composable () -> Unit,
) {
    val view = LocalView.current

    LaunchedEffect(isDark) {
        if (Build.VERSION.SDK_INT >= 30) {
            if (isDark) {
                view.windowInsetsController?.setSystemBarsAppearance(
                    0,
                    APPEARANCE_LIGHT_STATUS_BARS,
                )
            } else {
                view.windowInsetsController?.setSystemBarsAppearance(
                    APPEARANCE_LIGHT_STATUS_BARS,
                    APPEARANCE_LIGHT_STATUS_BARS,
                )
            }
        } else
            @Suppress("DEPRECATION")
            {
                val window = (view.context as Activity).window
                val flags = window.decorView.systemUiVisibility

                if (isDark) {
                    window.decorView.systemUiVisibility =
                        flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                } else {
                    window.decorView.systemUiVisibility =
                        flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            }
    }

    val colorScheme =
        when (theme) {
            is Theme.Dynamic if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) -> {
                val context = LocalContext.current
                @SuppressLint("NewApi")
                if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            is Theme.Default if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) -> {
                val context = LocalContext.current
                @SuppressLint("NewApi")
                if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            is Theme.Custom -> theme.rememberColorScheme(isDark)
            else -> rememberDynamicColorScheme(seedColor = MaterialDeepPurple, isDark = isDark)
        }

    val animatedColorScheme =
        animateColorScheme(
            colorScheme = colorScheme,
            animationSpec = { MaterialTheme.motionScheme.slowEffectsSpec() },
        )

    val nutrientsPalette = if (isDark) DarkNutrientsPalette else LightNutrientsPalette

    CompositionLocalProvider(
        LocalNutrientsPalette provides nutrientsPalette.applyColors(nutrientsColors)
    ) {
        MaterialTheme(colorScheme = animatedColorScheme, content = content)
    }
}
