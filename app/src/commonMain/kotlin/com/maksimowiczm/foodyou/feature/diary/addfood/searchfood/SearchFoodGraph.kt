package com.maksimowiczm.foodyou.feature.diary.addfood.searchfood

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.feature.diary.addfood.searchfood.ui.SearchFoodScreen
import com.maksimowiczm.foodyou.feature.diary.core.data.food.FoodId
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data object SearchFood

fun NavGraphBuilder.searchFoodGraph(
    mealId: Long,
    date: LocalDate,
    onBack: () -> Unit,
    onProductAdd: () -> Unit,
    onOpenFoodFactsSettings: () -> Unit,
    onFoodClick: (FoodId) -> Unit
) {
    crossfadeComposable<SearchFood> {
        SearchFoodScreen(
            mealId = mealId,
            date = date,
            onBack = onBack,
            onProductAdd = onProductAdd,
            onOpenFoodFactsSettings = onOpenFoodFactsSettings,
            onFoodClick = onFoodClick
        )
    }
}
