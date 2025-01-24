package com.maksimowiczm.foodyou.feature.product.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.product.ui.create.CreateProductScreen
import com.maksimowiczm.foodyou.navigation.foodYouComposable
import kotlinx.serialization.Serializable

@Serializable
sealed interface ProductsRoute {
    @Serializable
    data class CreateProduct(
        val epochDay: Long,
        val mealType: Meal
    ) : ProductsRoute
}

fun NavGraphBuilder.productsGraph(
    createOnNavigateBack: () -> Unit,
    createOnSuccess: (productId: Long, epochDay: Long, Meal) -> Unit
) {
    foodYouComposable<ProductsRoute.CreateProduct> {
        val (epochDay, mealType) = it.toRoute<ProductsRoute.CreateProduct>()
        CreateProductScreen(
            onNavigateBack = createOnNavigateBack,
            onSuccess = { productId ->
                createOnSuccess(productId, epochDay, mealType)
            }
        )
    }
}

fun <R : ProductsRoute> NavController.navigateToProducts(
    route: R,
    navOptions: NavOptions? = null
) {
    navigate(route, navOptions)
}
