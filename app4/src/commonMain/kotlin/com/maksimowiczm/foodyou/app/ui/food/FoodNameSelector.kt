package com.maksimowiczm.foodyou.app.ui.food

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import com.maksimowiczm.foodyou.food.domain.FoodNameSelector

val LocalFoodNameSelector: ProvidableCompositionLocal<FoodNameSelector> = compositionLocalOf {
    FoodNameSelector { it.english ?: it.fallback }
}

@Composable
fun FoodNameSelectorProvider(selector: FoodNameSelector, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalFoodNameSelector provides selector, content = content)
}
