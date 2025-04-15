package com.maksimowiczm.foodyou.feature.recipe.ui

import androidx.compose.animation.core.snap
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.paging.compose.collectAsLazyPagingItems
import com.maksimowiczm.foodyou.core.domain.model.Product
import com.maksimowiczm.foodyou.core.navigation.CrossFadeComposableDefaults
import com.maksimowiczm.foodyou.core.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.feature.barcodescanner.CameraBarcodeScannerScreen
import com.maksimowiczm.foodyou.feature.measurement.MeasurementScreen
import com.maksimowiczm.foodyou.feature.product.CreateProduct
import com.maksimowiczm.foodyou.feature.product.UpdateProduct
import com.maksimowiczm.foodyou.feature.product.productGraph
import com.maksimowiczm.foodyou.feature.recipe.model.Ingredient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun RecipeApp(
    onBack: () -> Unit,
    onCreate: (recipeId: Long) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    recipeId: Long = -1,
    viewModel: RecipeViewModel = koinViewModel(
        parameters = { parametersOf(recipeId) }
    )
) {
    val onBack: () -> Unit = {
        if (navController.currentBackStack.value.size == 2) {
            onBack()
        } else {
            navController.popBackStack<CreateRecipe>(inclusive = true)
        }
    }

    val recipeState by viewModel.state.collectAsStateWithLifecycle()
    val searchListState = rememberLazyListState()

    val onCreate by rememberUpdatedState(onCreate)
    LaunchedEffect(viewModel) {
        viewModel.createState.collectLatest {
            when (it) {
                is CreateState.Created -> onCreate(it.recipeId)
                CreateState.CreatingRecipe,
                CreateState.Nothing -> Unit
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = CreateRecipe,
        modifier = modifier
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
                onClose = onBack,
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
                onRemoveIngredient = remember(viewModel) { viewModel::onRemoveIngredient }
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
            val pages = viewModel.pages.collectAsLazyPagingItems(
                viewModel.viewModelScope.coroutineContext
            )
            val recentQueries by viewModel.recentQueries.collectAsStateWithLifecycle()

            val textFieldState = rememberTextFieldState()

            LaunchedEffect(viewModel) {
                viewModel.searchQuery.collectLatest {
                    when (it) {
                        null -> textFieldState.clearText()
                        else -> textFieldState.setTextAndPlaceCursorAtEnd(it)
                    }
                }
            }

            AddIngredientScreen(
                pages = pages,
                recentQueries = recentQueries,
                onBarcodeScanner = {
                    navController.navigate(BarcodeScanner) {
                        launchSingleTop = true
                    }
                },
                listState = searchListState,
                textFieldState = textFieldState,
                onSearch = remember(viewModel) { viewModel::onSearch },
                onClear = remember(viewModel) { { viewModel.onSearch(null) } },
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

            val food = run {
                viewModel.observeMeasurableFood(productId).collectAsStateWithLifecycle(null).value
            } ?: return@crossfadeComposable

            MeasurementScreen(
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
        crossfadeComposable<UpdateIngredientMeasurement> {
            val (index) = it.toRoute<UpdateIngredientMeasurement>()

            val ingredient = recipeState.ingredients.getOrNull(index) ?: return@crossfadeComposable

            val food = run {
                viewModel
                    .observeMeasurableFood(ingredient.productId)
                    .collectAsStateWithLifecycle(null).value
            } ?: return@crossfadeComposable

            UpdateIngredientMeasurementScreen(
                food = food,
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
                    navController.navigate(UpdateProduct(ingredient.productId)) {
                        launchSingleTop = true
                    }
                },
                onDeleteFood = {
                    navController.popBackStack<UpdateIngredientMeasurement>(inclusive = true)
                    viewModel.onRemoveIngredient(index)
                    viewModel.onProductDelete(ingredient.productId)
                }
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
