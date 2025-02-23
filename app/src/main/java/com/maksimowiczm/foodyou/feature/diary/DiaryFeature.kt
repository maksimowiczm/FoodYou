package com.maksimowiczm.foodyou.feature.diary

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.addfood.navigation.AddFoodFeature
import com.maksimowiczm.foodyou.feature.addfood.navigation.addFoodGraph
import com.maksimowiczm.foodyou.feature.addfood.navigation.navigateToAddFood
import com.maksimowiczm.foodyou.feature.diary.data.DiaryRepository
import com.maksimowiczm.foodyou.feature.diary.ui.DiaryViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.caloriescard.buildCaloriesCard
import com.maksimowiczm.foodyou.feature.diary.ui.goalssettings.GoalsSettingsScreen
import com.maksimowiczm.foodyou.feature.diary.ui.goalssettings.GoalsSettingsViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.goalssettings.buildGoalsSettingsListItem
import com.maksimowiczm.foodyou.feature.diary.ui.mealscard.MealsCardViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.mealscard.buildMealsCard
import com.maksimowiczm.foodyou.feature.diary.ui.mealssettings.MealsSettingsScreen
import com.maksimowiczm.foodyou.feature.diary.ui.mealssettings.MealsSettingsViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.mealssettings.buildMealsSettingsListItem
import com.maksimowiczm.foodyou.feature.product.ProductFeature
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
    private val productFeature: ProductFeature
) : Feature.Koin,
    Feature.Home,
    Feature.Settings {
    private val diaryModule = module {
        viewModelOf(::DiaryViewModel)
        viewModelOf(::GoalsSettingsViewModel)
        viewModelOf(::MealsSettingsViewModel)
        viewModelOf(::MealsCardViewModel)

        diaryRepository().bind()
    }

    final override fun KoinApplication.setup() {
        modules(diaryModule)
    }

    final override fun NavGraphBuilder.homeGraph(navController: NavController) {
        val onSearchSettings = productFeature.settingsRoute?.let {
            {
                navController.navigate(
                    route = it,
                    navOptions = navOptions {
                        launchSingleTop = true
                    }
                )
            }
        }

        addFoodGraph(
            onClose = {
                navController.popBackStack()
            },
            onSearchSettings = onSearchSettings
        )
    }

    final override fun buildHomeFeatures(navController: NavController) = listOf(
        buildMealsCard(
            onAddProduct = { epochDay, meal ->
                navController.navigateToAddFood(
                    route = AddFoodFeature(
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

    companion object {

        @Serializable
        data object GoalsSettings

        @Serializable
        data object MealsSettings

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
    }
}
