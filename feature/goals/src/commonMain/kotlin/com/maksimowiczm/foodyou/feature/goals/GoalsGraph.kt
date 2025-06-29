package com.maksimowiczm.foodyou.feature.goals

import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.feature.goals.ui.cardsettings.GoalsCardSettings
import com.maksimowiczm.foodyou.feature.goals.ui.screen.CaloriesScreen
import com.maksimowiczm.foodyou.feature.goals.ui.settings.GoalsSettingsScreen
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data object GoalsSettings

@Serializable
data class GoalsScreen(val epochDay: Long)

@Serializable
data object GoalsCardSettings

fun NavGraphBuilder.goalsGraph(
    onGoalsSettings: () -> Unit,
    onGoalsSettingsBack: () -> Unit,
    onGoalsCardSettingsBack: () -> Unit,
    onEditFood: (FoodId) -> Unit
) {
    forwardBackwardComposable<GoalsSettings> {
        GoalsSettingsScreen(
            onBack = onGoalsSettingsBack
        )
    }
    crossfadeComposable<GoalsScreen> {
        val (epochDay) = it.toRoute<GoalsScreen>()

        val date = LocalDate.fromEpochDays(epochDay)

        CaloriesScreen(
            date = date,
            animatedVisibilityScope = this,
            onFoodClick = onEditFood
        )
    }
    forwardBackwardComposable<GoalsCardSettings> {
        GoalsCardSettings(
            onBack = onGoalsCardSettingsBack,
            onGoalsSettings = onGoalsSettings
        )
    }
}
