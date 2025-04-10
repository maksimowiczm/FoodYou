package com.maksimowiczm.foodyou.feature.goals

import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.feature.goals.ui.screen.CaloriesApp
import com.maksimowiczm.foodyou.feature.goals.ui.settings.GoalsSettingsScreen
import kotlinx.serialization.Serializable

@Serializable
data object GoalsSettings

@Serializable
data class GoalsScreen(val epochDay: Int)

fun NavGraphBuilder.goalsGraph(onGoalsSettingsBack: () -> Unit) {
    forwardBackwardComposable<GoalsSettings> {
        GoalsSettingsScreen(
            onBack = onGoalsSettingsBack
        )
    }
    crossfadeComposable<GoalsScreen> {
        val (epochDay) = it.toRoute<GoalsScreen>()

        CaloriesApp(
            outerAnimatedScope = this,
            epochDay = epochDay
        )
    }
}
