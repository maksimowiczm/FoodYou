package com.maksimowiczm.foodyou.feature.diary

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.feature.diary.mealssettings.ui.MealsSettingsScreen
import kotlinx.serialization.Serializable

@Serializable
data object MealsSettings

fun NavGraphBuilder.diaryGraph(onMealsSettingsBack: () -> Unit) {
    forwardBackwardComposable<MealsSettings> {
        MealsSettingsScreen(
            onBack = onMealsSettingsBack
        )
    }
}
