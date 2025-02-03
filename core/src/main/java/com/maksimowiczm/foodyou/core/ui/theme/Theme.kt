package com.maksimowiczm.foodyou.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import com.maksimowiczm.foodyou.core.feature.diary.ui.theme.DarkDiaryPalette
import com.maksimowiczm.foodyou.core.feature.diary.ui.theme.DarkNutrimentsPalette
import com.maksimowiczm.foodyou.core.feature.diary.ui.theme.LightDiaryPalette
import com.maksimowiczm.foodyou.core.feature.diary.ui.theme.LightNutrimentsPalette
import com.maksimowiczm.foodyou.core.feature.diary.ui.theme.LocalDiaryPalette
import com.maksimowiczm.foodyou.core.feature.diary.ui.theme.LocalNutrimentsPalette

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun FoodYouTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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

    val diaryPalette = if (darkTheme) DarkDiaryPalette else LightDiaryPalette
    val nutrimentsPalette = if (darkTheme) DarkNutrimentsPalette else LightNutrimentsPalette

    CompositionLocalProvider(
        LocalDiaryPalette provides diaryPalette,
        LocalNutrimentsPalette provides nutrimentsPalette
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
