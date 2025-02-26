package com.maksimowiczm.foodyou.feature.home.mealscard.ui.app

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
    mealId: Long,
    epochDay: Int,
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
                mealId = mealId,
                epochDay = epochDay,
                modifier = modifier,
                navController = navController,
                skipToSearchScreen = skipToSearchScreen
            )
        }
    }
}
