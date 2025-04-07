package com.maksimowiczm.foodyou.feature.diary.addfood

import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.feature.diary.addfood.ui.AddFoodApp
import kotlinx.serialization.Serializable

@Serializable
data class AddFoodSearchFood(val mealId: Long, val epochDay: Int)

fun NavGraphBuilder.addFoodGraph(
    onAddFoodSearchBack: () -> Unit,
    onOpenFoodFactsSettings: () -> Unit
) {
    crossfadeComposable<AddFoodSearchFood> {
        val (mealId, epochDay) = it.toRoute<AddFoodSearchFood>()

        AddFoodApp(
            outerAnimatedScope = this,
            outerOnBack = onAddFoodSearchBack,
            onOpenFoodFactsSettings = onOpenFoodFactsSettings,
            mealId = mealId,
            epochDay = epochDay
        )
    }
}
