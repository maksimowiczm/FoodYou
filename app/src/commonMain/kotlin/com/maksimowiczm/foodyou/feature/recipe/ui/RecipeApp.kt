package com.maksimowiczm.foodyou.feature.recipe.ui

import androidx.compose.animation.core.snap
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.model.Product
import com.maksimowiczm.foodyou.core.navigation.CrossFadeComposableDefaults
import com.maksimowiczm.foodyou.core.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.feature.barcodescanner.CameraBarcodeScannerScreen
import com.maksimowiczm.foodyou.feature.measurement.MeasurementScreen
import com.maksimowiczm.foodyou.feature.product.CreateProduct
import com.maksimowiczm.foodyou.feature.product.UpdateProduct
import com.maksimowiczm.foodyou.feature.product.productGraph
import com.maksimowiczm.foodyou.feature.recipe.model.Ingredient
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun RecipeApp(onBack: () -> Unit, modifier: Modifier = Modifier) {
    RecipeNavHost(
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
private fun RecipeNavHost(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val viewModel = koinViewModel<RecipeViewModel>()
    val recipeState by viewModel.state.collectAsStateWithLifecycle()
    val searchListState = rememberLazyListState()

    NavHost(
        navController = navController,
        startDestination = CreateRecipe
    ) {
        crossfadeComposable<CreateRecipe> {
            RecipeFormScreen(
                state = recipeState,
                onNameChange = remember(viewModel) { viewModel::onNameChange },
                onServingsChange = remember(viewModel) { viewModel::onServingsChange },
                onAddIngredient = {
                    navController.navigate(AddIngredient) {
                        launchSingleTop = true
                    }
                },
                onClose = {
                    if (navController.currentBackStack.value.size == 2) {
                        onBack()
                    } else {
                        navController.popBackStack<CreateRecipe>(inclusive = true)
                    }
                },
                onCreate = remember(viewModel) { viewModel::onCreate },
                onEditProduct = {
                    navController.navigate(UpdateProduct(it)) {
                        launchSingleTop = true
                    }
                },
                onEditIngredient = {
                    val index = recipeState.ingredients.indexOf(it)

                    navController.navigate(UpdateIngredientMeasurement(index)) {
                        launchSingleTop = true
                    }
                },
                onRemoveIngredient = remember(viewModel) { viewModel::onRemoveIngredient },
                modifier = modifier
            )
        }
        crossfadeComposable<AddIngredient>(
            popEnterTransition = {
                if (initialState.destination.hasRoute<CreateProduct>()) {
                    fadeIn(snap())
                } else {
                    CrossFadeComposableDefaults.enterTransition()
                }
            }
        ) {
            AddIngredientScreen(
                viewModel = viewModel,
                listState = searchListState,
                onBarcodeScanner = {
                    navController.navigate(BarcodeScanner) {
                        launchSingleTop = true
                    }
                },
                onProductClick = {
                    navController.navigate(MeasureIngredient(it))
                },
                onCreateProduct = {
                    navController.navigate(CreateProduct) {
                        launchSingleTop = true
                    }
                },
                onBack = {
                    navController.popBackStack<AddIngredient>(inclusive = true)
                }
            )
        }
        crossfadeComposable<BarcodeScanner> {
            CameraBarcodeScannerScreen(
                onBarcodeScan = {
                    viewModel.onSearch(it)
                    navController.popBackStack<BarcodeScanner>(inclusive = true)
                },
                onClose = {
                    navController.popBackStack<BarcodeScanner>(inclusive = true)
                }
            )
        }
        crossfadeComposable<MeasureIngredient> {
            val (productId) = it.toRoute<MeasureIngredient>()

            val food by viewModel.observeMeasurableFood(productId).collectAsStateWithLifecycle(null)

            when (val food = food) {
                null -> Unit
                else -> MeasurementScreen(
                    food = food,
                    selectedMeasurement = null,
                    onBack = {
                        navController.popBackStack<MeasureIngredient>(inclusive = true)
                    },
                    onMeasurement = {
                        viewModel.onAddIngredient(
                            Ingredient(
                                product = food.food as Product,
                                measurement = it
                            )
                        )
                        navController.navigate(CreateRecipe) {
                            popUpTo(CreateRecipe) {
                                inclusive = false
                            }

                            launchSingleTop = true
                        }
                    },
                    onEditFood = {
                        navController.navigate(UpdateProduct(productId)) {
                            launchSingleTop = true
                        }
                    },
                    onDeleteFood = {
                        viewModel.onProductDelete(productId)
                        navController.popBackStack<MeasureIngredient>(inclusive = true)
                    }
                )
            }
        }
        crossfadeComposable<UpdateIngredientMeasurement> {
            val (index) = it.toRoute<UpdateIngredientMeasurement>()

            val ingredient = recipeState.ingredients.getOrNull(index)
                ?: return@crossfadeComposable

            UpdateIngredientMeasurementScreen(
                ingredient = ingredient,
                onBack = {
                    navController.popBackStack<UpdateIngredientMeasurement>(inclusive = true)
                },
                onMeasurement = {
                    viewModel.onUpdateIngredient(
                        index = index,
                        ingredient = ingredient.copy(
                            measurement = it
                        )
                    )
                    navController.navigate(CreateRecipe) {
                        popUpTo(CreateRecipe) {
                            inclusive = false
                        }

                        launchSingleTop = true
                    }
                },
                onEditFood = {
                    navController.navigate(UpdateProduct(ingredient.product.id.id)) {
                        launchSingleTop = true
                    }
                },
                onDeleteFood = {
                    navController.popBackStack<UpdateIngredientMeasurement>(inclusive = true)
                    viewModel.onRemoveIngredient(ingredient)
                    viewModel.onProductDelete(ingredient.product.id.id)
                },
                viewModel = viewModel
            )
        }
        productGraph(
            onCreateClose = {
                navController.popBackStack<CreateProduct>(inclusive = true)
            },
            onCreateProduct = {
                navController.navigate(MeasureIngredient(it)) {
                    launchSingleTop = true
                }
            },
            onUpdateClose = {
                navController.popBackStack<UpdateProduct>(inclusive = true)
            },
            onUpdateProduct = {
                navController.popBackStack<UpdateProduct>(inclusive = true)
            }
        )
    }
}

@Serializable
private data object CreateRecipe

@Serializable
private data object AddIngredient

@Serializable
private data object BarcodeScanner

@Serializable
private data class MeasureIngredient(val productId: Long)

@Serializable
private data class UpdateIngredientMeasurement(val index: Int)
