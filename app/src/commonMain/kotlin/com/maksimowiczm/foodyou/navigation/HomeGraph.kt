package com.maksimowiczm.foodyou.navigation

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.ui.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object Home

fun NavGraphBuilder.homeGraph(
    onSettings: () -> Unit,
    onAbout: () -> Unit,
    onEditMeasurement: (Long) -> Unit,
    onMealCardLongClick: () -> Unit,
    onMealCardAddClick: (epochDay: Long, mealId: Long) -> Unit,
    onGoalsCardClick: (epochDay: Long) -> Unit,
    onGoalsCardLongClick: () -> Unit
) {
    crossfadeComposable<Home> {
        HomeScreen(
            onSettings = onSettings,
            onAbout = onAbout,
            onEditMeasurement = onEditMeasurement,
            onMealCardAddClick = onMealCardAddClick,
            onMealCardLongClick = onMealCardLongClick,
            onGoalsCardClick = onGoalsCardClick,
            onGoalsCardLongClick = onGoalsCardLongClick
        )
    }
}
