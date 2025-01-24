package com.maksimowiczm.foodyou.feature.addfood.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.addfood.ui.portion.PortionScreen
import com.maksimowiczm.foodyou.feature.addfood.ui.search.SearchScreen
import com.maksimowiczm.foodyou.navigation.foodYouComposable
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

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.addFoodGraph(
    sharedTransitionScope: SharedTransitionScope,
    searchOnCloseClick: () -> Unit,
    searchOnProductClick: (productId: Long, Meal, date: LocalDate) -> Unit,
    searchOnCreateProduct: (Meal, date: LocalDate) -> Unit,
    createOnSuccess: () -> Unit,
    createOnNavigateBack: () -> Unit
) {
    foodYouComposable<AddFoodRoute.Search> {
        val (epochDay, meal) = it.toRoute<AddFoodRoute.Search>()

        with(sharedTransitionScope) {
            SearchScreen(
                onCloseClick = searchOnCloseClick,
                onProductClick = { productId ->
                    searchOnProductClick(productId, meal, LocalDate.ofEpochDay(epochDay))
                },
                onCreateProduct = {
                    searchOnCreateProduct(meal, LocalDate.ofEpochDay(epochDay))
                }
            )
        }
    }
    foodYouComposable<AddFoodRoute.CreatePortion> {
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
