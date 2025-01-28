package com.maksimowiczm.foodyou.feature.product.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.product.ui.ProductShareTransitionKeys
import com.maksimowiczm.foodyou.feature.product.ui.create.CreateProductScreen
import com.maksimowiczm.foodyou.ui.LocalSharedTransitionScope
import kotlinx.serialization.Serializable

@Serializable
sealed interface ProductsRoute {
    @Serializable
    data class CreateProduct(
        val epochDay: Long,
        val mealType: Meal
    ) : ProductsRoute
}

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.productsGraph(
    createOnNavigateBack: () -> Unit,
    createOnSuccess: (productId: Long, epochDay: Long, Meal) -> Unit
) {
    composable<ProductsRoute.CreateProduct> {
        val (epochDay, mealType) = it.toRoute<ProductsRoute.CreateProduct>()
        val sharedTransitionScope =
            LocalSharedTransitionScope.current ?: error("No SharedTransitionScope found")

        with(sharedTransitionScope) {
            CreateProductScreen(
                onNavigateBack = createOnNavigateBack,
                onSuccess = { productId ->
                    createOnSuccess(productId, epochDay, mealType)
                },
                modifier = Modifier
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(
                            ProductShareTransitionKeys.PRODUCT_CREATE_SCREEN
                        ),
                        animatedVisibilityScope = this@composable
                    )
                    .skipToLookaheadSize()
            )
        }
    }
}

fun <R : ProductsRoute> NavController.navigateToProducts(
    route: R,
    navOptions: NavOptions? = null
) {
    navigate(route, navOptions)
}
