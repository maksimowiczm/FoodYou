package com.maksimowiczm.foodyou.app.ui.common.theme

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

@Immutable
data class NutrientsPalette(
    val proteinsOnSurfaceContainer: Color = Color.Unspecified,
    val carbohydratesOnSurfaceContainer: Color = Color.Unspecified,
    val fatsOnSurfaceContainer: Color = Color.Unspecified,
)

val DarkNutrientsPalette =
    NutrientsPalette(
        proteinsOnSurfaceContainer = Color(0XFF8FE0F7),
        carbohydratesOnSurfaceContainer = Color(0XFFA69AE2),
        fatsOnSurfaceContainer = Color(0XFFE8D291),
    )

val LightNutrientsPalette =
    NutrientsPalette(
        proteinsOnSurfaceContainer = Color(0xFF0A7694),
        carbohydratesOnSurfaceContainer = Color(0xFF6D5CD1),
        fatsOnSurfaceContainer = Color(0xFF876C1D),
    )

val LocalNutrientsPalette = staticCompositionLocalOf { NutrientsPalette() }
