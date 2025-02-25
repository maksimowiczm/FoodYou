package com.maksimowiczm.foodyou.feature.product

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
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.NavigationFeature
import com.maksimowiczm.foodyou.feature.product.data.ProductRepository
import com.maksimowiczm.foodyou.feature.product.ui.crud.create.CreateProductScreen
import com.maksimowiczm.foodyou.feature.product.ui.crud.create.CreateProductViewModel
import com.maksimowiczm.foodyou.feature.product.ui.crud.update.UpdateProductScreen
import com.maksimowiczm.foodyou.feature.product.ui.crud.update.UpdateProductViewModel
import com.maksimowiczm.foodyou.ui.motion.crossfadeIn
import com.maksimowiczm.foodyou.ui.motion.crossfadeOut
import kotlinx.serialization.Serializable
import org.koin.core.KoinApplication
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * ProductFeature is a feature that provides functionality for managing products.
 */
abstract class ProductFeature(
    productRepository: Module.() -> KoinDefinition<ProductRepository>,
    val settingsRoute: Any?
) : Feature.Koin,
    NavigationFeature<ProductFeature.GraphProps> {
    private val module = module {
        viewModelOf(::CreateProductViewModel)
        viewModelOf(::UpdateProductViewModel)

        productRepository().bind()
    }

    final override fun KoinApplication.setup() {
        modules(module)

        configure()
    }

    abstract fun KoinApplication.configure()

    data class GraphProps(
        val createOnNavigateBack: () -> Unit,
        val createOnSuccess: (productId: Long, epochDay: Int, mealId: Long) -> Unit,
        val updateOnNavigateBack: () -> Unit,
        val updateOnSuccess: () -> Unit
    )

    final override fun NavGraphBuilder.graph(navController: NavController, props: GraphProps) {
        val (createOnNavigateBack, createOnSuccess, updateOnNavigateBack, updateOnSuccess) = props
        composable<CreateProduct>(
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
            val (epochDay, mealType) = it.toRoute<CreateProduct>()

            CreateProductScreen(
                onNavigateBack = createOnNavigateBack,
                onSuccess = { productId ->
                    createOnSuccess(productId, epochDay, mealType)
                }
            )
        }
        composable<UpdateProduct>(
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
            UpdateProductScreen(
                onNavigateBack = updateOnNavigateBack,
                onSuccess = updateOnSuccess
            )
        }
    }

    sealed interface ProductsRoute

    @Serializable
    data class CreateProduct(val epochDay: Int, val mealId: Long) : ProductsRoute

    @Serializable
    data class UpdateProduct(val productId: Long) : ProductsRoute

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
