package com.maksimowiczm.foodyou.app.ui.common.theme

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.device.domain.NutrientsColors
import com.maksimowiczm.foodyou.device.domain.ThemeSettings
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FoodYouTheme(content: @Composable () -> Unit) {
    val viewModel: ThemeViewModel = koinViewModel()
    val themeSettings by viewModel.themeSettings.collectAsStateWithLifecycle()
    val nutrientsColors by viewModel.nutrientsColors.collectAsStateWithLifecycle()
    FoodYouTheme(
        themeSettings = themeSettings,
        nutrientsColors = nutrientsColors,
        content = content,
    )
}

@Composable
fun PreviewFoodYouTheme(content: @Composable () -> Unit) {
    FoodYouTheme(themeSettings = null, nutrientsColors = null, content = content)
}

@Composable
internal expect fun FoodYouTheme(
    themeSettings: ThemeSettings?,
    nutrientsColors: NutrientsColors?,
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
