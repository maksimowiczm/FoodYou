package com.maksimowiczm.foodyou.feature.diary

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.HomeFeature
import com.maksimowiczm.foodyou.feature.SettingsFeature
import com.maksimowiczm.foodyou.feature.diary.data.AddFoodRepository
import com.maksimowiczm.foodyou.feature.diary.data.AddFoodRepositoryImpl
import com.maksimowiczm.foodyou.feature.diary.data.DiaryRepository
import com.maksimowiczm.foodyou.feature.diary.data.DiaryRepositoryImpl
import com.maksimowiczm.foodyou.feature.diary.data.OpenFoodFactsSettingsRepository
import com.maksimowiczm.foodyou.feature.diary.data.OpenFoodFactsSettingsRepositoryImpl
import com.maksimowiczm.foodyou.feature.diary.data.ProductRepository
import com.maksimowiczm.foodyou.feature.diary.data.ProductRepositoryImpl
import com.maksimowiczm.foodyou.feature.diary.database.DiaryDatabase
import com.maksimowiczm.foodyou.feature.diary.network.OpenFoodFactsRemoteMediatorFactory
import com.maksimowiczm.foodyou.feature.diary.network.ProductRemoteMediatorFactory
import com.maksimowiczm.foodyou.feature.diary.ui.MealApp
import com.maksimowiczm.foodyou.feature.diary.ui.caloriescard.CaloriesCard
import com.maksimowiczm.foodyou.feature.diary.ui.caloriescard.CaloriesCardViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.caloriesscreen.CaloriesScreen
import com.maksimowiczm.foodyou.feature.diary.ui.caloriesscreen.CaloriesScreenViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.goalssettings.GoalsSettingsListItem
import com.maksimowiczm.foodyou.feature.diary.ui.goalssettings.GoalsSettingsScreen
import com.maksimowiczm.foodyou.feature.diary.ui.goalssettings.GoalsSettingsViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.meal.DiaryDayMealViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.mealscard.MealsCardViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.mealscard.buildMealsCard
import com.maksimowiczm.foodyou.feature.diary.ui.mealssettings.MealsSettingsListItem
import com.maksimowiczm.foodyou.feature.diary.ui.mealssettings.MealsSettingsScreen
import com.maksimowiczm.foodyou.feature.diary.ui.mealssettings.MealsSettingsScreenViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.measurement.CreateMeasurementViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.measurement.UpdateMeasurementViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.openfoodfactssettings.CountryFlag
import com.maksimowiczm.foodyou.feature.diary.ui.openfoodfactssettings.OpenFoodFactsSettingsScreen
import com.maksimowiczm.foodyou.feature.diary.ui.openfoodfactssettings.OpenFoodFactsSettingsViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.openfoodfactssettings.buildOpenFoodFactsSettingsListItem
import com.maksimowiczm.foodyou.feature.diary.ui.openfoodfactssettings.flagCdnCountryFlag
import com.maksimowiczm.foodyou.feature.diary.ui.product.create.CreateProductViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.product.update.UpdateProductViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.search.OpenFoodFactsSearchHintViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.search.SearchViewModel
import com.maksimowiczm.foodyou.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.navigation.forwardBackwardComposable
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
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
        HomeFeature { _, modifier, homeState ->
            CaloriesCard(
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

            MealApp(
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

            MealApp(
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
                animatedVisibilityScope = this@crossfadeComposable
            )
        }
    }

    override val module: Module = module {
        viewModelOf(::OpenFoodFactsSearchHintViewModel)
        viewModelOf(::OpenFoodFactsSettingsViewModel)
        factoryOf(::OpenFoodFactsSettingsRepositoryImpl).bind<OpenFoodFactsSettingsRepository>()
        singleOf(::OpenFoodFactsRemoteMediatorFactory).bind<ProductRemoteMediatorFactory>()
        factory { flagCdnCountryFlag }.bind<CountryFlag>()

        factory {
            AddFoodRepositoryImpl(
                addFoodDao = get(),
                productDao = get(),
                productRemoteMediatorFactory = get()
            )
        }.bind<AddFoodRepository>()
        factoryOf(::ProductRepositoryImpl).bind<ProductRepository>()

        factoryOf(::DiaryRepositoryImpl).bind<DiaryRepository>()

        viewModelOf(::GoalsSettingsViewModel)

        viewModelOf(::MealsSettingsScreenViewModel)

        viewModelOf(::SearchViewModel)
        viewModelOf(::CreateMeasurementViewModel)
        viewModelOf(::UpdateMeasurementViewModel)
        viewModelOf(::MealsCardViewModel)
        viewModelOf(::DiaryDayMealViewModel)
        viewModelOf(::CreateProductViewModel)
        viewModelOf(::UpdateProductViewModel)

        factory { get<DiaryDatabase>().addFoodDao() }
        factory { get<DiaryDatabase>().productDao() }
        factory { get<DiaryDatabase>().openFoodFactsDao() }

        viewModelOf(::CaloriesCardViewModel)
        viewModelOf(::CaloriesScreenViewModel)
    }
}
