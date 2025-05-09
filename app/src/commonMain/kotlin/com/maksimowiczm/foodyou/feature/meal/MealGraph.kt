package com.maksimowiczm.foodyou.feature.meal

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.feature.meal.ui.cardsettings.MealCardSettings
import com.maksimowiczm.foodyou.feature.meal.ui.settings.MealsSettingsScreen
import kotlinx.serialization.Serializable

@Serializable
data object MealsSettings

@Serializable
data object MealCardSettings

fun NavGraphBuilder.mealGraph(
    onMealsSettingsBack: () -> Unit,
    onMealsSettings: () -> Unit,
    onMealCardSettings: () -> Unit,
    onMealCardSettingsBack: () -> Unit
) {
    forwardBackwardComposable<MealsSettings> {
        MealsSettingsScreen(
            onBack = onMealsSettingsBack,
            onMealCardSettings = onMealCardSettings
        )
    }
    forwardBackwardComposable<MealCardSettings> {
        MealCardSettings(
            onMealsSettings = onMealsSettings,
            onBack = onMealCardSettingsBack
        )
    }
}
