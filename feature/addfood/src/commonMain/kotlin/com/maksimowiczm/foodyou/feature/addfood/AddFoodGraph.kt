package com.maksimowiczm.foodyou.feature.addfood

import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.feature.addfood.ui.AddFoodApp
import kotlinx.serialization.Serializable

@Serializable
data class AddFood(val mealId: Long, val epochDay: Long)

fun NavGraphBuilder.addFoodGraph(onBack: () -> Unit, onSwissFoodDatabase: () -> Unit) {
    crossfadeComposable<AddFood> {
        val (mealId, epochDay) = it.toRoute<AddFood>()

        AddFoodApp(
            onBack = onBack,
            onSwissFoodDatabase = onSwissFoodDatabase,
            mealId = mealId,
            epochDay = epochDay
        )
    }
}
