package com.maksimowiczm.foodyou.feature.home.mealscard.ui.app

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.meal.DiaryDayMealScreen
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.measurement.CreateMeasurementViewModel
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.measurement.MeasurementScreen
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.measurement.UpdateMeasurementViewModel
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.search.SearchHome
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.search.SearchViewModel
import com.maksimowiczm.foodyou.feature.legacy.camera.ui.CameraBarcodeScannerScreen
import com.maksimowiczm.foodyou.feature.legacy.product.ui.create.CreateProductDialog
import com.maksimowiczm.foodyou.feature.legacy.product.ui.update.UpdateProductDialog
import com.maksimowiczm.foodyou.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.ui.motion.crossfadeIn
import com.maksimowiczm.foodyou.ui.motion.crossfadeOut
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
private data object MealHome

@Serializable
private data object Search

@Serializable
private data class CreateMeasurement(val productId: Long)

@Serializable
private data class EditMeasurement(val measurementId: Long)

@Serializable
private data object BarcodeScanner

@Serializable
private data object CreateProductDialog

@Serializable
private data class EditProductDialog(val productId: Long)

@Composable
fun MealNavHost(
    outerScope: AnimatedVisibilityScope,
    mealId: Long,
    epochDay: Int,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    skipToSearchScreen: Boolean = false
) {
    val date = LocalDate.fromEpochDays(epochDay)
    val searchViewModel = koinViewModel<SearchViewModel>(
        parameters = { parametersOf(date, mealId) }
    )

    val startDestination: Any = if (skipToSearchScreen) Search else MealHome

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        crossfadeComposable<MealHome> {
            DiaryDayMealScreen(
                date = date,
                mealId = mealId,
                mealHeaderScope = outerScope,
                onProductAdd = {
                    navController.navigate(
                        route = Search,
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                onEditEntry = {
                    navController.navigate(
                        route = EditMeasurement(it),
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                }
            )
        }
        crossfadeComposable<Search> {
            SearchHome(
                onProductClick = {
                    navController.navigate(
                        route = CreateMeasurement(it),
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                onBack = { navController.popBackStack<Search>(inclusive = true) },
                onCreateProduct = {
                    navController.navigate(
                        route = CreateProductDialog,
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                onBarcodeScanner = {
                    navController.navigate(
                        route = BarcodeScanner,
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                viewModel = searchViewModel
            )
        }
        crossfadeComposable<BarcodeScanner> {
            CameraBarcodeScannerScreen(
                onBarcodeScan = {
                    searchViewModel.onSearch(it)

                    navController.popBackStack<BarcodeScanner>(inclusive = true)
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        crossfadeComposable<CreateMeasurement> {
            val (productId) = it.toRoute<CreateMeasurement>()

            MeasurementScreen(
                onBack = { navController.popBackStack<CreateMeasurement>(inclusive = true) },
                onSuccess = { navController.popBackStack<CreateMeasurement>(inclusive = true) },
                onEditClick = {
                    navController.navigate(
                        route = EditProductDialog(productId),
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                onDelete = { navController.popBackStack<CreateMeasurement>(inclusive = true) },
                viewModel = koinViewModel<CreateMeasurementViewModel>(
                    parameters = { parametersOf(mealId, date, productId) }
                )
            )
        }
        crossfadeComposable<EditMeasurement> {
            val (measurementId) = it.toRoute<EditMeasurement>()

            MeasurementScreen(
                onBack = { navController.popBackStack<EditMeasurement>(inclusive = true) },
                onSuccess = { navController.popBackStack<EditMeasurement>(inclusive = true) },
                onEditClick = { productId ->
                    navController.navigate(
                        route = EditProductDialog(productId),
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                onDelete = { navController.popBackStack<EditMeasurement>(inclusive = true) },
                viewModel = koinViewModel<UpdateMeasurementViewModel>(
                    parameters = { parametersOf(measurementId) }
                )
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
                crossfadeOut() + slideOutVertically(
                    animationSpec = tween(
                        easing = FastOutLinearInEasing
                    ),
                    targetOffsetY = { it }
                )
            }
        ) {
            CreateProductDialog(
                onClose = { navController.popBackStack<CreateProductDialog>(inclusive = true) },
                onSuccess = { productId ->
                    navController.navigate(
                        route = CreateMeasurement(productId),
                        navOptions = navOptions {
                            launchSingleTop = true

                            popUpTo<Search> {
                                inclusive = false
                            }
                        }
                    )
                }
            )
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
                crossfadeOut() + slideOutVertically(
                    animationSpec = tween(
                        easing = FastOutLinearInEasing
                    ),
                    targetOffsetY = { it }
                )
            }
        ) {
            val (productId) = it.toRoute<EditProductDialog>()

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
