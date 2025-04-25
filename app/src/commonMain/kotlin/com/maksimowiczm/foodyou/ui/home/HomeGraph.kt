package com.maksimowiczm.foodyou.ui.home

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.navigation.crossfadeComposable
import kotlinx.serialization.Serializable

@Serializable
data object Home

fun NavGraphBuilder.homeGraph(
    onSettings: () -> Unit,
    onAbout: () -> Unit,
    onMealCardClick: (epochDay: Int, mealId: Long) -> Unit,
    onMealCardAddClick: (epochDay: Int, mealId: Long) -> Unit,
    onCaloriesCardClick: (epochDay: Int) -> Unit
) {
    crossfadeComposable<Home> {
        HomeScreen(
            animatedVisibilityScope = this,
            onSettings = onSettings,
            onAbout = onAbout,
            onMealCardClick = onMealCardClick,
            onMealCardAddClick = onMealCardAddClick,
            onCaloriesCardClick = onCaloriesCardClick
        )
    }
}
