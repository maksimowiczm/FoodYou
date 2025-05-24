package com.maksimowiczm.foodyou.feature.addfood

import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.feature.addfood.ui.AddFoodApp
import kotlinx.serialization.Serializable

@Serializable
data class AddFoodSearchFood(val mealId: Long, val epochDay: Int)

@Serializable
data class AddFoodMeal(val mealId: Long, val epochDay: Int)

fun NavGraphBuilder.addFoodGraph(onBack: () -> Unit) {
    crossfadeComposable<AddFoodSearchFood> {
        val (mealId, epochDay) = it.toRoute<AddFoodSearchFood>()

        AddFoodApp(
            outerOnBack = onBack,
            outerAnimatedScope = this,
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
            mealId = mealId,
            epochDay = epochDay,
            skipToSearch = false
        )
    }
}
