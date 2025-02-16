package com.maksimowiczm.foodyou.core.feature.diary

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.maksimowiczm.foodyou.core.feature.Feature
import com.maksimowiczm.foodyou.core.feature.addfood.navigation.addFoodGraph
import com.maksimowiczm.foodyou.core.feature.diary.data.DiaryRepository
import com.maksimowiczm.foodyou.core.feature.diary.data.DiaryRepositoryImpl
import com.maksimowiczm.foodyou.core.feature.diary.ui.DiaryViewModel
import com.maksimowiczm.foodyou.core.feature.diary.ui.caloriescard.buildCaloriesCard
import com.maksimowiczm.foodyou.core.feature.diary.ui.goalssettings.GoalsSettingsScreen
import com.maksimowiczm.foodyou.core.feature.diary.ui.goalssettings.GoalsSettingsViewModel
import com.maksimowiczm.foodyou.core.feature.diary.ui.goalssettings.buildGoalsSettingsListItem
import com.maksimowiczm.foodyou.core.feature.diary.ui.mealscard.MealsCardViewModel
import com.maksimowiczm.foodyou.core.feature.diary.ui.mealscard.buildMealsCard
import com.maksimowiczm.foodyou.core.feature.diary.ui.mealssettings.MealsSettingsScreen
import com.maksimowiczm.foodyou.core.feature.diary.ui.mealssettings.MealsSettingsViewModel
import com.maksimowiczm.foodyou.core.feature.diary.ui.mealssettings.buildMealsSettingsListItem
import com.maksimowiczm.foodyou.core.feature.product.ProductFeature.navigateToFoodDatabaseSettings
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import kotlinx.serialization.Serializable
import org.koin.core.KoinApplication
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

private val diaryModule = module {
    viewModelOf(::DiaryViewModel)
    viewModelOf(::GoalsSettingsViewModel)
    viewModelOf(::MealsSettingsViewModel)
    viewModelOf(::MealsCardViewModel)

    factoryOf(::DiaryRepositoryImpl).bind(DiaryRepository::class)
}

object DiaryFeature : Feature.Koin, Feature.Home, Feature.Settings {
    override fun KoinApplication.setup() {
        modules(diaryModule)
    }

    override fun NavGraphBuilder.homeGraph(navController: NavController) {
        addFoodGraph(
            onClose = {
                navController.popBackStack()
            },
            onSearchSettings = {
                navController.navigateToFoodDatabaseSettings()
            }
        )
    }

    override fun buildHomeFeatures(navController: NavController) = listOf(
        buildMealsCard(navController),
        buildCaloriesCard(navController)
    )

    override fun NavGraphBuilder.settingsGraph(navController: NavController) {
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

    override fun buildSettingsFeatures(navController: NavController) = listOf(
        buildMealsSettingsListItem(navController),
        buildGoalsSettingsListItem(navController)
    )

    @Serializable
    data object GoalsSettings

    @Serializable
    data object MealsSettings

    fun NavController.navigateToGoalsSettings(
        navOptions: NavOptions? = null
    ) {
        navigate(
            route = GoalsSettings,
            navOptions = navOptions
        )
    }

    fun NavController.navigateToMealsSettings(
        navOptions: NavOptions? = null
    ) {
        navigate(
            route = MealsSettings,
            navOptions = navOptions
        )
    }
}
