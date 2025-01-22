package com.maksimowiczm.foodyou.feature.addfood.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.addfood.ui.portion.PortionScreen
import com.maksimowiczm.foodyou.feature.addfood.ui.search.SearchScreen
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
sealed interface AddFoodRoute {

    @Serializable
    data class Search(
        val epochDay: Long,
        val meal: Meal
    ) : AddFoodRoute

    @Serializable
    data class CreatePortion(
        val productId: Long,
        val epochDay: Long,
        val meal: Meal
    ) : AddFoodRoute
}

fun NavGraphBuilder.addFoodGraph(
    searchOnCloseClick: () -> Unit,
    searchOnProductClick: (productId: Long, Meal, date: LocalDate) -> Unit,
    createOnSuccess: () -> Unit,
    createOnNavigateBack: () -> Unit
) {
    composable<AddFoodRoute.Search>(
        enterTransition = { fadeIn(tween()) + scaleIn(spring()) },
        popEnterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        val (epochDay, meal) = it.toRoute<AddFoodRoute.Search>()

        SearchScreen(
            onCloseClick = searchOnCloseClick,
            onProductClick = { productId ->
                searchOnProductClick(productId, meal, LocalDate.ofEpochDay(epochDay))
            }
        )
    }
    composable<AddFoodRoute.CreatePortion>(
        enterTransition = { fadeIn(tween()) + scaleIn(spring()) },
        exitTransition = { ExitTransition.None }
    ) {
        val (productId, epochDay, meal) = it.toRoute<AddFoodRoute.CreatePortion>()
        val date = LocalDate.ofEpochDay(epochDay)

        PortionScreen(
            productId = productId,
            date = date,
            meal = meal,
            onSuccess = createOnSuccess,
            onNavigateBack = createOnNavigateBack
        )
    }
}

fun <R : AddFoodRoute> NavController.navigateToAddFood(
    route: R,
    navOptions: NavOptions? = null
) {
    navigate(route, navOptions)
}
