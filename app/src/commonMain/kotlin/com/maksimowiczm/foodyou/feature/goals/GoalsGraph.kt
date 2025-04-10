package com.maksimowiczm.foodyou.feature.goals

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.feature.goals.ui.GoalsSettingsScreen
import kotlinx.serialization.Serializable

@Serializable
data object GoalsSettings

fun NavGraphBuilder.goalsGraph(onGoalsSettingsBack: () -> Unit) {
    forwardBackwardComposable<GoalsSettings> {
        GoalsSettingsScreen(
            onBack = onGoalsSettingsBack
        )
    }
}
