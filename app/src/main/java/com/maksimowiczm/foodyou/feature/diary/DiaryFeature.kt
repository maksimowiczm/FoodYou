package com.maksimowiczm.foodyou.feature.diary

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.addfood.AddFoodFeature
import com.maksimowiczm.foodyou.feature.addfood.AddFoodFeature.Companion.navigateToAddFood
import com.maksimowiczm.foodyou.feature.addfood.AddFoodFeature.Companion.popAddFood
import com.maksimowiczm.foodyou.feature.addfood.PortionFeature
import com.maksimowiczm.foodyou.feature.addfood.PortionFeature.Companion.navigateToPortion
import com.maksimowiczm.foodyou.feature.diary.data.DiaryRepository
import com.maksimowiczm.foodyou.feature.diary.ui.DiaryViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.caloriescard.buildCaloriesCard
import com.maksimowiczm.foodyou.feature.diary.ui.goalssettings.GoalsSettingsScreen
import com.maksimowiczm.foodyou.feature.diary.ui.goalssettings.GoalsSettingsViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.goalssettings.buildGoalsSettingsListItem
import com.maksimowiczm.foodyou.feature.diary.ui.mealscard.MealsCardViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.mealscard.buildMealsCard
import com.maksimowiczm.foodyou.feature.diary.ui.mealscreen.DiaryDayMealScreen
import com.maksimowiczm.foodyou.feature.diary.ui.mealscreen.DiaryDayMealViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.mealssettings.MealsSettingsScreen
import com.maksimowiczm.foodyou.feature.diary.ui.mealssettings.MealsSettingsViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.mealssettings.buildMealsSettingsListItem
import com.maksimowiczm.foodyou.feature.setup
import com.maksimowiczm.foodyou.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.navigation.forwardBackwardComposable
import kotlinx.serialization.Serializable
import org.koin.core.KoinApplication
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

abstract class DiaryFeature(
    diaryRepository: Module.() -> KoinDefinition<DiaryRepository>,
    private val addFoodFeature: AddFoodFeature
) : Feature.Koin,
    Feature.Home,
    Feature.Settings {
    private val diaryModule = module {
        viewModelOf(::DiaryViewModel)
        viewModelOf(::GoalsSettingsViewModel)
        viewModelOf(::MealsSettingsViewModel)
        viewModelOf(::MealsCardViewModel)
        viewModelOf(::DiaryDayMealViewModel)

        diaryRepository().bind()
    }

    final override fun KoinApplication.setup() {
        modules(diaryModule)

        setup(addFoodFeature)
    }

    final override fun NavGraphBuilder.homeGraph(navController: NavController) {
        with(addFoodFeature) {
            graph(
                navController = navController,
                props = AddFoodFeature.GraphProps(
                    onClose = { navController.popAddFood() }
                )
            )
        }

        crossfadeComposable<Meal> {
            val route = it.toRoute<Meal>()

            DiaryDayMealScreen(
                animatedVisibilityScope = this@crossfadeComposable,
                onProductAdd = {
                    navController.navigateToAddFood(
                        route = AddFoodFeature.Route(
                            mealId = route.mealId,
                            epochDay = route.epochDay
                        )
                    )
                },
                onEditEntry = { model ->
                    val id = model.measurementId
                        ?: error("Measurement ID is required to edit entry")

                    navController.navigateToPortion(
                        route = PortionFeature.Edit(
                            epochDay = route.epochDay,
                            mealId = route.mealId,
                            measurementId = id
                        )
                    )
                }
            )
        }
    }

    final override fun buildHomeFeatures(navController: NavController) = listOf(
        buildMealsCard(
            onMealClick = { epochDay, meal ->
                navController.navigateToMeal(
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
                navController.navigateToAddFood(
                    route = AddFoodFeature.Route(
                        epochDay = epochDay,
                        mealId = meal.id
                    ),
                    navOptions = navOptions {
                        launchSingleTop = true
                    }
                )
            }
        ),
        buildCaloriesCard(
            onClick = {
                navController.navigateToGoalsSettings(
                    navOptions = navOptions {
                        launchSingleTop = true
                    }
                )
            }
        )
    )

    final override fun NavGraphBuilder.settingsGraph(navController: NavController) {
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
                    navController.popBackStack(
                        route = MealsSettings,
                        inclusive = true
                    )
                }
            )
        }
    }

    final override fun buildSettingsFeatures(navController: NavController) = listOf(
        buildMealsSettingsListItem(navController),
        buildGoalsSettingsListItem(navController)
    )

    @Serializable
    data class Meal(val epochDay: Int, val mealId: Long)

    @Serializable
    data object GoalsSettings

    @Serializable
    data object MealsSettings

    companion object {
        fun NavController.navigateToGoalsSettings(navOptions: NavOptions? = null) {
            navigate(
                route = GoalsSettings,
                navOptions = navOptions
            )
        }

        fun NavController.navigateToMealsSettings(navOptions: NavOptions? = null) {
            navigate(
                route = MealsSettings,
                navOptions = navOptions
            )
        }

        fun NavController.navigateToMeal(route: Meal, navOptions: NavOptions? = null) {
            navigate(
                route = route,
                navOptions = navOptions
            )
        }
    }
}
