package com.maksimowiczm.foodyou.app.ui.theme

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.business.opensource.domain.settings.NutrientsColors
import com.maksimowiczm.foodyou.app.business.opensource.domain.settings.ThemeSettings
import com.maksimowiczm.foodyou.app.ui.shared.theme.NutrientsPalette
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FoodYouTheme(content: @Composable () -> Unit) {
    val viewModel: ThemeSettingsViewModel = koinViewModel()
    val themeSettings by viewModel.themeSettings.collectAsStateWithLifecycle()
    val nutrientsColors by viewModel.nutrientsColors.collectAsStateWithLifecycle()

    FoodYouTheme(themeSettings, nutrientsColors, content)
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
