package com.maksimowiczm.foodyou.app.ui.food

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import com.maksimowiczm.foodyou.common.domain.Language
import com.maksimowiczm.foodyou.food.domain.FoodName
import com.maksimowiczm.foodyou.food.domain.FoodNameSelector

val LocalFoodNameSelector: ProvidableCompositionLocal<FoodNameSelector> = compositionLocalOf {
    object : FoodNameSelector {
        override fun select(foodName: FoodName): String = foodName.english ?: foodName.fallback

        override fun select(): Language = Language.English
    }
}

@Composable
fun FoodNameSelectorProvider(selector: FoodNameSelector, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalFoodNameSelector provides selector, content = content)
}
