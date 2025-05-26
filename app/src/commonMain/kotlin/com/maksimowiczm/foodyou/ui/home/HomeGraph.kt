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
    onCaloriesCardClick: (epochDay: Int) -> Unit
) {
    crossfadeComposable<Home> {
        HomeScreen(
            animatedVisibilityScope = this,
            onSettings = onSettings,
            onAbout = onAbout,
            onEditMeasurement = onEditMeasurement,
            onMealCardAddClick = onMealCardAddClick,
            onMealCardLongClick = onMealCardLongClick,
            onCaloriesCardClick = onCaloriesCardClick
        )
    }
}
