package com.maksimowiczm.foodyou.feature.diary.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.maksimowiczm.foodyou.feature.diary.data.model.Meal
import com.maksimowiczm.foodyou.feature.diary.ui.DiaryScreen
import kotlinx.serialization.Serializable

@Serializable
data object DiaryFeature

fun NavGraphBuilder.diaryGraph(
    onAddProductMeal: (Meal) -> Unit
) {
    composable<DiaryFeature> {
        DiaryScreen(
            onAddProductToMeal = onAddProductMeal
        )
    }
}

fun NavController.navigateToDiary(
    navOptions: NavOptions? = null
) {
    navigate(DiaryFeature, navOptions)
}
