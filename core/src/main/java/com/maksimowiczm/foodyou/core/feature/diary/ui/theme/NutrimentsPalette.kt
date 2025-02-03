package com.maksimowiczm.foodyou.core.feature.diary.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class NutrimentsPalette(
    val caloriesOnSurfaceContainer: Color = Color.Unspecified,
    val proteinsOnSurfaceContainer: Color = Color.Unspecified,
    val carbohydratesOnSurfaceContainer: Color = Color.Unspecified,
    val fatsOnSurfaceContainer: Color = Color.Unspecified
)

val DarkNutrimentsPalette = NutrimentsPalette(
    caloriesOnSurfaceContainer = Color(0XFFEA90F3),
    proteinsOnSurfaceContainer = Color(0XFF8FE0F7),
    carbohydratesOnSurfaceContainer = Color(0XFFA69AE2),
    fatsOnSurfaceContainer = Color(0XFFE8D291)
)

val LightNutrimentsPalette = NutrimentsPalette(
    caloriesOnSurfaceContainer = Color(0XFFDB3EEA),
    proteinsOnSurfaceContainer = Color(0XFF0D94BA),
    carbohydratesOnSurfaceContainer = Color(0XFF8A7CDA),
    fatsOnSurfaceContainer = Color(0XFFA48423)
)

val LocalNutrimentsPalette = staticCompositionLocalOf { NutrimentsPalette() }
