package com.maksimowiczm.foodyou.feature.meal

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.feature.meal.ui.settings.MealsSettingsScreen
import kotlinx.serialization.Serializable

@Serializable
data object MealsSettings

fun NavGraphBuilder.mealGraph(onMealsSettingsBack: () -> Unit) {
    forwardBackwardComposable<MealsSettings> {
        MealsSettingsScreen(
            onBack = onMealsSettingsBack
        )
    }
}
