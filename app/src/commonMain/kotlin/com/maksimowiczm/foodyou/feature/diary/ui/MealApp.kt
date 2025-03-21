package com.maksimowiczm.foodyou.feature.diary.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MealApp(
    outerScope: AnimatedVisibilityScope,
    outerOnBack: () -> Unit,
    mealId: Long,
    epochDay: Int,
    onGoToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    skipToSearchScreen: Boolean = false
) {
    SharedTransitionLayout {
        CompositionLocalProvider(
            LocalMealSharedTransitionScope provides this
        ) {
            MealNavHost(
                outerScope = outerScope,
                outerOnBack = outerOnBack,
                mealId = mealId,
                epochDay = epochDay,
                onGoToSettings = onGoToSettings,
                modifier = modifier,
                navController = navController,
                skipToSearchScreen = skipToSearchScreen
            )
        }
    }
}
