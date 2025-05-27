package com.maksimowiczm.foodyou.ui.home

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.domain.model.MeasurementId
import com.maksimowiczm.foodyou.core.navigation.crossfadeComposable
import kotlinx.serialization.Serializable

@Serializable
data object Home

fun NavGraphBuilder.homeGraph(
    onSettings: () -> Unit,
    onAbout: () -> Unit,
    onEditMeasurement: (MeasurementId) -> Unit,
    onMealCardLongClick: () -> Unit,
    onMealCardAddClick: (epochDay: Int, mealId: Long) -> Unit,
    onGoalsCardClick: (epochDay: Int) -> Unit,
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
