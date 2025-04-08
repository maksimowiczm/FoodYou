package com.maksimowiczm.foodyou.feature.diary

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.feature.diary.addfood.addFoodGraph
import com.maksimowiczm.foodyou.feature.diary.mealssettings.mealsSettingsGraph
import com.maksimowiczm.foodyou.feature.diary.openfoodfacts.openFoodFactsSettingsGraph

fun NavGraphBuilder.diaryGraph(
    onAddFoodBack: () -> Unit,
    onMealsSettingsBack: () -> Unit,
    onOpenFoodFactsSettings: () -> Unit,
    onOpenFoodFactsSettingsBack: () -> Unit
) {
    addFoodGraph(
        onBack = onAddFoodBack,
        onOpenFoodFactsSettings = onOpenFoodFactsSettings
    )
    mealsSettingsGraph(
        onMealsSettingsBack = onMealsSettingsBack
    )
    openFoodFactsSettingsGraph(
        onOpenFoodFactsSettingsBack = onOpenFoodFactsSettingsBack
    )
}
