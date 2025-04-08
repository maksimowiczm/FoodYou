package com.maksimowiczm.foodyou.feature.diary.addfood

import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.feature.diary.addfood.ui.AddFoodApp
import kotlinx.serialization.Serializable

@Serializable
data class AddFoodSearchFood(val mealId: Long, val epochDay: Int)

@Serializable
data class AddFoodMeal(val mealId: Long, val epochDay: Int)

fun NavGraphBuilder.addFoodGraph(onBack: () -> Unit, onOpenFoodFactsSettings: () -> Unit) {
    crossfadeComposable<AddFoodSearchFood> {
        val (mealId, epochDay) = it.toRoute<AddFoodSearchFood>()

        AddFoodApp(
            outerOnBack = onBack,
            outerAnimatedScope = this,
            onOpenFoodFactsSettings = onOpenFoodFactsSettings,
            mealId = mealId,
            epochDay = epochDay,
            skipToSearch = true
        )
    }
    crossfadeComposable<AddFoodMeal> {
        val (mealId, epochDay) = it.toRoute<AddFoodMeal>()

        AddFoodApp(
            outerOnBack = onBack,
            outerAnimatedScope = this,
            onOpenFoodFactsSettings = onOpenFoodFactsSettings,
            mealId = mealId,
            epochDay = epochDay,
            skipToSearch = false
        )
    }
}
