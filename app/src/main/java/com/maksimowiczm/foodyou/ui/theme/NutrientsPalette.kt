package com.maksimowiczm.foodyou.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class NutrientsPalette(
    val proteinsOnSurfaceContainer: Color = Color.Unspecified,
    val carbohydratesOnSurfaceContainer: Color = Color.Unspecified,
    val fatsOnSurfaceContainer: Color = Color.Unspecified
)

val DarkNutrientsPalette = NutrientsPalette(
    proteinsOnSurfaceContainer = Color(0XFF8FE0F7),
    carbohydratesOnSurfaceContainer = Color(0XFFA69AE2),
    fatsOnSurfaceContainer = Color(0XFFE8D291)
)

val LightNutrientsPalette = NutrientsPalette(
    proteinsOnSurfaceContainer = Color(0XFF0D94BA),
    carbohydratesOnSurfaceContainer = Color(0XFF8A7CDA),
    fatsOnSurfaceContainer = Color(0XFFA48423)
)

val LocalNutrientsPalette = staticCompositionLocalOf { NutrientsPalette() }
