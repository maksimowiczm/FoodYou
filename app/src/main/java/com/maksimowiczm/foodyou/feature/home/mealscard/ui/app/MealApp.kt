package com.maksimowiczm.foodyou.feature.home.mealscard.ui.app

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.barcodescanner.BarcodeScannerScreen
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.search.SearchHint

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MealApp(
    outerScope: AnimatedVisibilityScope,
    outerOnBack: () -> Unit,
    mealId: Long,
    epochDay: Int,
    barcodeScannerScreen: BarcodeScannerScreen,
    searchHint: SearchHint,
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
                barcodeScannerScreen = barcodeScannerScreen,
                searchHint = searchHint,
                modifier = modifier,
                navController = navController,
                skipToSearchScreen = skipToSearchScreen
            )
        }
    }
}
