package com.maksimowiczm.foodyou.feature.diary.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.feature.diary.ui.mealscreen.MealScreen
import com.maksimowiczm.foodyou.navigation.crossfadeComposable
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AddFoodToMealApp(
    outerScope: AnimatedVisibilityScope,
    outerOnBack: () -> Unit,
    mealId: Long,
    epochDay: Int,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    SharedTransitionLayout {
        CompositionLocalProvider(
            LocalMealSharedTransitionScope provides this
        ) {
            AppNavHost(
                outerScope = outerScope,
                outerOnBack = outerOnBack,
                mealId = mealId,
                epochDay = epochDay,
                modifier = modifier,
                navController = navController
            )
        }
    }
}

@Serializable
private data object MealHome

@Composable
private fun AppNavHost(
    outerScope: AnimatedVisibilityScope,
    outerOnBack: () -> Unit,
    mealId: Long,
    epochDay: Int,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val date = LocalDate.fromEpochDays(epochDay)

    NavHost(
        navController = navController,
        startDestination = MealHome,
        modifier = modifier
    ) {
        crossfadeComposable<MealHome>(
            exitTransition = { fadeOut() }
        ) {
            MealScreen(
                date = date,
                mealId = mealId,
                navigationScope = this,
                mealHeaderScope = outerScope,
                onProductAdd = {

                },
                onBarcodeScan = {

                },
                onEditEntry = {

                }
            )
        }
    }
}