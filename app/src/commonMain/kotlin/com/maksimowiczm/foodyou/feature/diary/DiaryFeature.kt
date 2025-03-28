package com.maksimowiczm.foodyou.feature.diary

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.HomeFeature
import com.maksimowiczm.foodyou.feature.SettingsFeature
import com.maksimowiczm.foodyou.feature.diary.data.DiaryDayRepository
import com.maksimowiczm.foodyou.feature.diary.data.DiaryRepository
import com.maksimowiczm.foodyou.feature.diary.data.GoalsRepository
import com.maksimowiczm.foodyou.feature.diary.data.MealRepository
import com.maksimowiczm.foodyou.feature.diary.data.MeasurementRepository
import com.maksimowiczm.foodyou.feature.diary.data.OpenFoodFactsSettingsRepository
import com.maksimowiczm.foodyou.feature.diary.data.ProductRepository
import com.maksimowiczm.foodyou.feature.diary.data.ProductRepositoryImpl
import com.maksimowiczm.foodyou.feature.diary.data.SearchRepository
import com.maksimowiczm.foodyou.feature.diary.database.DiaryDatabase
import com.maksimowiczm.foodyou.feature.diary.network.OpenFoodFactsRemoteMediatorFactory
import com.maksimowiczm.foodyou.feature.diary.network.ProductRemoteMediatorFactory
import com.maksimowiczm.foodyou.feature.diary.ui.AddFoodToMealApp
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.CreateMeasurementViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.UpdateMeasurementViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.cases.DeleteProductCase
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.cases.ObserveProductCase
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.AddFoodSearchViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.cases.ObserveAddFoodSearchListItemCase
import com.maksimowiczm.foodyou.feature.diary.ui.caloriescard.CaloriesCard
import com.maksimowiczm.foodyou.feature.diary.ui.caloriescard.CaloriesCardViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.caloriesscreen.CaloriesScreen
import com.maksimowiczm.foodyou.feature.diary.ui.caloriesscreen.CaloriesScreenViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.goalssettings.GoalsSettingsListItem
import com.maksimowiczm.foodyou.feature.diary.ui.goalssettings.GoalsSettingsScreen
import com.maksimowiczm.foodyou.feature.diary.ui.goalssettings.GoalsSettingsViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.meal.DiaryDayMealViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.meal.cases.ObserveMealCase
import com.maksimowiczm.foodyou.feature.diary.ui.mealscard.MealsCardViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.mealscard.cases.ObserveMealsByDateCase
import com.maksimowiczm.foodyou.feature.diary.ui.mealscard.compose.buildMealsCard
import com.maksimowiczm.foodyou.feature.diary.ui.mealssettings.MealsSettingsListItem
import com.maksimowiczm.foodyou.feature.diary.ui.mealssettings.MealsSettingsScreen
import com.maksimowiczm.foodyou.feature.diary.ui.mealssettings.MealsSettingsScreenViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.openfoodfactshint.OpenFoodFactsSearchHintViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.openfoodfactssettings.CountryFlag
import com.maksimowiczm.foodyou.feature.diary.ui.openfoodfactssettings.OpenFoodFactsSettingsScreen
import com.maksimowiczm.foodyou.feature.diary.ui.openfoodfactssettings.OpenFoodFactsSettingsViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.openfoodfactssettings.buildOpenFoodFactsSettingsListItem
import com.maksimowiczm.foodyou.feature.diary.ui.openfoodfactssettings.flagCdnCountryFlag
import com.maksimowiczm.foodyou.feature.diary.ui.product.create.CreateProductViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.product.update.UpdateProductDialog
import com.maksimowiczm.foodyou.feature.diary.ui.product.update.UpdateProductViewModel
import com.maksimowiczm.foodyou.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.ui.motion.crossfadeIn
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

object DiaryFeature : Feature {
    override fun buildHomeFeatures(navController: NavController) = listOf(
        buildMealsCard(
            onMealClick = { epochDay, meal ->
                navController.navigate(
                    route = Meal(
                        epochDay = epochDay,
                        mealId = meal.id
                    ),
                    navOptions = navOptions {
                        launchSingleTop = true
                    }
                )
            },
            onAddClick = { epochDay, meal ->
                navController.navigate(
                    route = MealAdd(
                        epochDay = epochDay,
                        mealId = meal.id
                    ),
                    navOptions = navOptions {
                        launchSingleTop = true
                    }
                )
            }
        ),
        HomeFeature { animatedVisibilityScope, modifier, homeState ->
            CaloriesCard(
                animatedVisibilityScope = animatedVisibilityScope,
                homeState = homeState,
                modifier = modifier,
                onClick = {
                    navController.navigate(
                        route = CaloriesDetails(
                            epochDay = homeState.selectedDate.toEpochDays()
                        ),
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                }
            )
        }
    )

    override fun buildSettingsFeatures(navController: NavController) = listOf(
        buildOpenFoodFactsSettingsListItem(
            onClick = {
                navController.navigate(
                    route = FoodDatabaseSettings,
                    navOptions = navOptions {
                        launchSingleTop = true
                    }
                )
            }
        ),
        SettingsFeature { modifier ->
            MealsSettingsListItem(
                onClick = {
                    navController.navigate(
                        route = MealsSettings,
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                modifier = modifier
            )
        },
        SettingsFeature { modifier ->
            GoalsSettingsListItem(
                onGoalsClick = {
                    navController.navigate(
                        route = GoalsSettings,
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                modifier = modifier
            )
        }
    )

    @Serializable
    private data object GoalsSettings

    @Serializable
    private data object MealsSettings

    @Serializable
    private data object FoodDatabaseSettings

    @Serializable
    private data class Meal(val epochDay: Int, val mealId: Long)

    @Serializable
    private data class MealAdd(val epochDay: Int, val mealId: Long)

    @Serializable
    private data class CaloriesDetails(val epochDay: Int)

    @Serializable
    private data class EditProductDialog(val productId: Long)

    override fun NavGraphBuilder.graph(navController: NavController) {
        forwardBackwardComposable<GoalsSettings> {
            GoalsSettingsScreen(
                onBack = {
                    navController.popBackStack(
                        route = GoalsSettings,
                        inclusive = true
                    )
                }
            )
        }

        forwardBackwardComposable<MealsSettings> {
            MealsSettingsScreen(
                onBack = {
                    navController.popBackStack<MealsSettings>(
                        inclusive = true
                    )
                }
            )
        }

        forwardBackwardComposable<FoodDatabaseSettings> {
            OpenFoodFactsSettingsScreen(
                onBack = {
                    navController.popBackStack<FoodDatabaseSettings>(
                        inclusive = true
                    )
                }
            )
        }

        crossfadeComposable<Meal> {
            val (epochDay, mealId) = it.toRoute<Meal>()

            AddFoodToMealApp(
                outerScope = this@crossfadeComposable,
                outerOnBack = { navController.popBackStack<Meal>(inclusive = true) },
                mealId = mealId,
                epochDay = epochDay,
                onGoToSettings = {
                    navController.navigate(
                        route = FoodDatabaseSettings,
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                }
            )
        }
        crossfadeComposable<MealAdd> {
            val (epochDay, mealId) = it.toRoute<MealAdd>()

            AddFoodToMealApp(
                outerScope = this@crossfadeComposable,
                outerOnBack = { navController.popBackStack<MealAdd>(inclusive = true) },
                mealId = mealId,
                epochDay = epochDay,
                skipToSearchScreen = true,
                onGoToSettings = {
                    navController.navigate(
                        route = FoodDatabaseSettings,
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                }
            )
        }

        crossfadeComposable<CaloriesDetails> {
            val (epochDay) = it.toRoute<CaloriesDetails>()
            val date = LocalDate.fromEpochDays(epochDay)

            CaloriesScreen(
                date = date,
                animatedVisibilityScope = this@crossfadeComposable,
                onProductClick = {
                    navController.navigate(
                        route = EditProductDialog(
                            productId = it.id
                        ),
                        navOptions = navOptions {
                            launchSingleTop = true
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

    override val module: Module = module {
        viewModelOf(::OpenFoodFactsSearchHintViewModel)
        viewModelOf(::OpenFoodFactsSettingsViewModel)
        factoryOf(::OpenFoodFactsSettingsRepository)
        singleOf(::OpenFoodFactsRemoteMediatorFactory).bind<ProductRemoteMediatorFactory>()
        factory { flagCdnCountryFlag }.bind<CountryFlag>()

        factory {
            DiaryRepository(
                addFoodDao = get(),
                productDao = get(),
                productRemoteMediatorFactory = get(),
                dataStore = get()
            )
        }.binds(
            arrayOf(
                GoalsRepository::class,
                MealRepository::class,
                MeasurementRepository::class,
                DiaryDayRepository::class,
                SearchRepository::class
            )
        )

        factoryOf(::ProductRepositoryImpl).bind<ProductRepository>()

        viewModelOf(::GoalsSettingsViewModel)

        viewModelOf(::MealsSettingsScreenViewModel)

        viewModelOf(::DiaryDayMealViewModel)
        viewModelOf(::CreateProductViewModel)
        viewModelOf(::UpdateProductViewModel)

        factory { get<DiaryDatabase>().addFoodDao() }
        factory { get<DiaryDatabase>().productDao() }
        factory { get<DiaryDatabase>().openFoodFactsDao() }

        viewModelOf(::CaloriesCardViewModel)
        viewModelOf(::CaloriesScreenViewModel)

        // AddFoodSearch
        factoryOf(::ObserveAddFoodSearchListItemCase)
        viewModelOf(::AddFoodSearchViewModel)

        // Meal
        factoryOf(::ObserveMealCase)

        // AddFoodProduct
        factoryOf(::ObserveProductCase)
        factoryOf(::DeleteProductCase)
        viewModelOf(::CreateMeasurementViewModel)
        viewModelOf(::UpdateMeasurementViewModel)

        // MealsCard
        factoryOf(::ObserveMealsByDateCase)
        viewModelOf(::MealsCardViewModel)
    }
}
