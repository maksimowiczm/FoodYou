package com.maksimowiczm.foodyou.capabilities.theme

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.preferences.domain.NutrientsColors
import com.maksimowiczm.foodyou.preferences.domain.ThemePreference
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FoodYouTheme(content: @Composable () -> Unit) {
    val viewModel: ThemeViewModel = koinViewModel()
    val themeSettings by viewModel.themeSettings.collectAsStateWithLifecycle()
    FoodYouTheme(
        themeSettings = themeSettings,
        content = content,
    )
}

@Composable
fun PreviewFoodYouTheme(content: @Composable () -> Unit) {
    FoodYouTheme(themeSettings = null, content = content)
}

@Composable
internal expect fun FoodYouTheme(
    themeSettings: ThemePreference?,
    content: @Composable () -> Unit,
)

internal fun NutrientsPalette.applyColors(nutrientsColors: NutrientsColors?) =
    copy(
        proteinsOnSurfaceContainer =
            nutrientsColors?.proteins?.let(::Color) ?: proteinsOnSurfaceContainer,
        carbohydratesOnSurfaceContainer =
            nutrientsColors?.carbohydrates?.let(::Color) ?: carbohydratesOnSurfaceContainer,
        fatsOnSurfaceContainer = nutrientsColors?.fats?.let(::Color) ?: fatsOnSurfaceContainer,
    )

val MaterialDeepPurple = Color(0xFF6200EE)
