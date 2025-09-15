package com.maksimowiczm.foodyou.navigation.graph.goals

import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.app.ui.goals.master.GoalsScreen
import com.maksimowiczm.foodyou.navigation.domain.GoalsMasterDestination
import com.maksimowiczm.foodyou.shared.compose.navigation.forwardBackwardComposable

fun NavGraphBuilder.goalsNavigationGraph(masterOnBack: () -> Unit) {
    forwardBackwardComposable<GoalsMasterDestination> {
        val (epochDay) = it.toRoute<GoalsMasterDestination>()

        GoalsScreen(onBack = masterOnBack, epochDay = epochDay)
    }
}
