package com.maksimowiczm.foodyou.feature.diary

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.feature.diary.mealssettings.ui.MealsSettingsScreen
import com.maksimowiczm.foodyou.feature.diary.openfoodfactssettings.OpenFoodFactsSettingsScreen
import kotlinx.serialization.Serializable

@Serializable
data object MealsSettings

@Serializable
data object OpenFoodFactsSettings

fun NavGraphBuilder.diaryGraph(
    onMealsSettingsBack: () -> Unit,
    onOpenFoodFactsSettingsBack: () -> Unit
) {
    forwardBackwardComposable<MealsSettings> {
        MealsSettingsScreen(
            onBack = onMealsSettingsBack
        )
    }
    forwardBackwardComposable<OpenFoodFactsSettings> {
        OpenFoodFactsSettingsScreen(
            onBack = onOpenFoodFactsSettingsBack
        )
    }
}
