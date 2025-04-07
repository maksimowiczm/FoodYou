package com.maksimowiczm.foodyou.feature.diary.addfood.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.feature.diary.addfood.core.ui.LocalAddFoodSharedTransitionScope
import com.maksimowiczm.foodyou.feature.diary.addfood.searchfood.SearchFood
import com.maksimowiczm.foodyou.feature.diary.addfood.searchfood.searchFoodGraph
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun AddFoodApp(
    outerAnimatedScope: AnimatedContentScope,
    outerOnBack: () -> Unit,
    onOpenFoodFactsSettings: () -> Unit,
    mealId: Long,
    epochDay: Int,
    modifier: Modifier = Modifier
) {
    SharedTransitionScope {
        CompositionLocalProvider(
            LocalAddFoodSharedTransitionScope provides this
        ) {
            AddFoodNavHost(
                outerAnimatedScope = outerAnimatedScope,
                outerOnBack = outerOnBack,
                onOpenFoodFactsSettings = onOpenFoodFactsSettings,
                mealId = mealId,
                epochDay = epochDay,
                modifier = modifier
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun AddFoodNavHost(
    outerAnimatedScope: AnimatedContentScope,
    outerOnBack: () -> Unit,
    onOpenFoodFactsSettings: () -> Unit,
    mealId: Long,
    epochDay: Int,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val date = LocalDate.fromEpochDays(epochDay)

    NavHost(
        navController = navController,
        startDestination = SearchFood,
        modifier = modifier
    ) {
        searchFoodGraph(
            mealId = mealId,
            date = date,
            onBack = outerOnBack,
            onProductAdd = {
                // TODO
            },
            onOpenFoodFactsSettings = onOpenFoodFactsSettings,
            onFoodClick = {
                // TODO
            }
        )
    }
}
