package com.maksimowiczm.foodyou.feature.legacy.product

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.data.ProductRepository
import com.maksimowiczm.foodyou.feature.legacy.product.ui.CreateProductDialog
import com.maksimowiczm.foodyou.feature.legacy.product.ui.update.UpdateProductDialog
import com.maksimowiczm.foodyou.ui.motion.crossfadeIn
import com.maksimowiczm.foodyou.ui.motion.crossfadeOut
import kotlinx.serialization.Serializable
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module

/**
 * ProductFeature is a feature that provides functionality for managing products.
 */
abstract class ProductFeature(
    productRepository: Module.() -> KoinDefinition<ProductRepository>,
    val settingsRoute: Any?
) {

    data class GraphProps(
        val createOnNavigateBack: () -> Unit,
        val createOnSuccess: (productId: Long, epochDay: Int, mealId: Long) -> Unit,
        val updateOnNavigateBack: () -> Unit,
        val updateOnSuccess: () -> Unit
    )

    final fun NavGraphBuilder.graph(navController: NavController, props: GraphProps) {
        val (createOnNavigateBack, createOnSuccess, updateOnNavigateBack, updateOnSuccess) = props
        composable<CreateProductDialog>(
            enterTransition = {
                crossfadeIn() + slideInVertically(
                    animationSpec = tween(
                        easing = LinearOutSlowInEasing
                    ),
                    initialOffsetY = { it }
                )
            },
            exitTransition = {
                crossfadeOut() + slideOutVertically(
                    animationSpec = tween(
                        easing = FastOutLinearInEasing
                    ),
                    targetOffsetY = { it }
                )
            }
        ) {
            val (epochDay, mealType) = it.toRoute<CreateProductDialog>()

            CreateProductDialog(
                onClose = createOnNavigateBack,
                onSuccess = { productId ->
                    createOnSuccess(productId, epochDay, mealType)
                }
            )
        }
        composable<UpdateProductDialog>(
            enterTransition = {
                crossfadeIn() + slideInVertically(
                    animationSpec = tween(
                        easing = LinearOutSlowInEasing
                    ),
                    initialOffsetY = { it }
                )
            },
            exitTransition = {
                crossfadeOut() + slideOutVertically(
                    animationSpec = tween(
                        easing = FastOutLinearInEasing
                    ),
                    targetOffsetY = { it }
                )
            }
        ) {
            UpdateProductDialog(
                onClose = updateOnNavigateBack,
                onSuccess = updateOnSuccess
            )
        }
    }

    sealed interface ProductsRoute

    @Serializable
    data class CreateProductDialog(val epochDay: Int, val mealId: Long) : ProductsRoute

    @Serializable
    data class UpdateProductDialog(val productId: Long) : ProductsRoute

    companion object {
        fun <R : ProductsRoute> NavController.navigateToProducts(
            route: R,
            navOptions: NavOptions? = null
        ) {
            navigate(route, navOptions)
        }

        inline fun <reified R : ProductsRoute> NavController.popProducts(
            inclusive: Boolean = true,
            saveState: Boolean = false
        ) {
            popBackStack<R>(
                inclusive = inclusive,
                saveState = saveState
            )
        }
    }
}
