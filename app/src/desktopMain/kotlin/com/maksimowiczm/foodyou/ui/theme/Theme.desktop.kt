package com.maksimowiczm.foodyou.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
actual fun FoodYouTheme(content: @Composable (() -> Unit)) {
    val nutrientsPalette = LightNutrientsPalette

    CompositionLocalProvider(
        LocalNutrientsPalette provides nutrientsPalette
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
