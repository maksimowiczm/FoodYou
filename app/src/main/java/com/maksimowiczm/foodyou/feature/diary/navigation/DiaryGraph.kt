package com.maksimowiczm.foodyou.feature.diary.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.diary.ui.DiaryScreen
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data object DiaryFeature

fun NavGraphBuilder.diaryGraph(
    onAddProductToMeal: (Meal, LocalDate) -> Unit
) {
    composable<DiaryFeature>(
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        DiaryScreen(
            onAddProductToMeal = onAddProductToMeal
        )
    }
}

fun NavController.navigateToDiary(
    navOptions: NavOptions? = null
) {
    navigate(DiaryFeature, navOptions)
}
