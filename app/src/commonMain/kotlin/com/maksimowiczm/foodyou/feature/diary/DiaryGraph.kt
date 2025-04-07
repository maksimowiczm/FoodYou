package com.maksimowiczm.foodyou.feature.diary

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.feature.diary.mealssettings.mealsSettingsGraph
import com.maksimowiczm.foodyou.feature.diary.openfoodfactssettings.openFoodFactsSettingsGraph

fun NavGraphBuilder.diaryGraph(
    onMealsSettingsBack: () -> Unit,
    onOpenFoodFactsSettingsBack: () -> Unit
) {
    mealsSettingsGraph(
        onMealsSettingsBack = onMealsSettingsBack
    )
    openFoodFactsSettingsGraph(
        onOpenFoodFactsSettingsBack = onOpenFoodFactsSettingsBack
    )
}
