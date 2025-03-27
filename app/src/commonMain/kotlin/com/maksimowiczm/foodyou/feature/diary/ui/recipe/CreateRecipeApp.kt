package com.maksimowiczm.foodyou.feature.diary.ui.recipe

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.diary.ui.product.create.CreateProductDialog
import com.maksimowiczm.foodyou.feature.diary.ui.product.update.UpdateProductDialog
import com.maksimowiczm.foodyou.feature.diary.ui.search.SearchHome
import com.maksimowiczm.foodyou.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.ui.motion.crossfadeIn
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CreateRecipeApp(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val searchViewModel = koinViewModel<RecipeSearchViewModel>()
    val lazyListState = rememberLazyListState()

    NavHost(
        navController = navController,
        startDestination = CreateRecipeDialog,
        modifier = modifier
    ) {
        crossfadeComposable<CreateRecipeDialog> {
            CreateRecipeDialog(
                onClose = onClose,
                onAddIngredient = {
                    navController.navigate(
                        route = CreateRecipeSearch,
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                onProductClick = {
                    // TODO
                },
                onProductEdit = { id ->
                    navController.navigate(
                        route = EditProductDialog(id),
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                }
            )
        }
        crossfadeComposable<CreateRecipeSearch> {
            SearchHome(
                onProductClick = {},
                onBack = { navController.popBackStack<CreateRecipeSearch>(inclusive = true) },
                onCreateProduct = {
                    navController.navigate(
                        route = CreateProductDialog,
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                onCreateRecipe = {
                    navController.navigate(
                        route = CreateRecipeDialog,
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                onBarcodeScanner = {},
                viewModel = searchViewModel,
                lazyListState = lazyListState,
                searchHint = null
            )
        }
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
                slideOutVertically(
                    animationSpec = tween(
                        easing = FastOutLinearInEasing
                    ),
                    targetOffsetY = { it }
                ) + scaleOut(
                    targetScale = 0.8f,
                    animationSpec = tween(
                        easing = FastOutLinearInEasing
                    )
                )
            }
        ) {
            Surface(
                shadowElevation = 6.dp,
                shape = MaterialTheme.shapes.medium
            ) {
                CreateProductDialog(
                    onClose = { navController.popBackStack<CreateProductDialog>(inclusive = true) },
                    onSuccess = { productId ->
                        TODO()
                    }
                )
            }
        }
        composable<EditProductDialog>(
            enterTransition = {
                crossfadeIn() + slideInVertically(
                    animationSpec = tween(
                        easing = LinearOutSlowInEasing
                    ),
                    initialOffsetY = { it }
                )
            },
            exitTransition = {
                slideOutVertically(
                    animationSpec = tween(
                        easing = FastOutLinearInEasing
                    ),
                    targetOffsetY = { it }
                ) + scaleOut(
                    targetScale = 0.8f,
                    animationSpec = tween(
                        easing = FastOutLinearInEasing
                    )
                )
            }
        ) {
            val (productId) = it.toRoute<EditProductDialog>()

            Surface(
                shadowElevation = 6.dp,
                shape = MaterialTheme.shapes.medium
            ) {
                UpdateProductDialog(
                    onClose = { navController.popBackStack<EditProductDialog>(inclusive = true) },
                    onSuccess = { navController.popBackStack<EditProductDialog>(inclusive = true) },
                    viewModel = koinViewModel(
                        parameters = { parametersOf(productId) }
                    )
                )
            }
        }
    }
}

@Serializable
data object CreateRecipeDialog

@Serializable
data object CreateRecipeSearch

@Serializable
private data object CreateProductDialog

@Serializable
private data class EditProductDialog(val productId: Long)
