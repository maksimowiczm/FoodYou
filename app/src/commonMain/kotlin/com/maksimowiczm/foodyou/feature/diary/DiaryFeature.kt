package com.maksimowiczm.foodyou.feature.diary

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.SettingsFeature
import com.maksimowiczm.foodyou.feature.diary.data.DiaryRepository
import com.maksimowiczm.foodyou.feature.diary.data.MealRepository
import com.maksimowiczm.foodyou.feature.diary.domain.ObserveMealByDateUseCase
import com.maksimowiczm.foodyou.feature.diary.domain.ObserveMealsByDateUseCase
import com.maksimowiczm.foodyou.feature.diary.domain.ObserveMealsUseCase
import com.maksimowiczm.foodyou.feature.diary.ui.AddFoodToMealApp
import com.maksimowiczm.foodyou.feature.diary.ui.mealscard.MealsCardViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.mealscard.buildMealsCard
import com.maksimowiczm.foodyou.feature.diary.ui.mealscreen.MealScreenViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.mealssettings.MealsSettingsListItem
import com.maksimowiczm.foodyou.feature.diary.ui.mealssettings.MealsSettingsScreen
import com.maksimowiczm.foodyou.feature.diary.ui.mealssettings.MealsSettingsScreenViewModel
import com.maksimowiczm.foodyou.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.navigation.forwardBackwardComposable
import kotlinx.serialization.Serializable
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.binds
import org.koin.dsl.module

object DiaryFeature : Feature {
    override fun buildHomeFeatures(navController: NavController) = listOf(
        buildMealsCard(
            onMealClick = { epochDay, meal ->
                navController.navigate(
                    route = Meal(epochDay, meal.id),
                    navOptions = navOptions {
                        launchSingleTop = true
                    }
                )
            },
            onAddClick = { epochDay, meal ->
            }
        )
    )

    override fun buildSettingsFeatures(navController: NavController) = listOf(
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
        }
    )

    override fun declare(): KoinAppDeclaration = {
        modules(
            module {
                factoryOf(::DiaryRepository).binds(
                    arrayOf(
                        ObserveMealsByDateUseCase::class,
                        ObserveMealsUseCase::class,
                        ObserveMealByDateUseCase::class,
                        MealRepository::class
                    )
                )

                viewModelOf(::MealsCardViewModel)

                viewModelOf(::MealsSettingsScreenViewModel)

                viewModelOf(::MealScreenViewModel)
            }
        )
    }

    override fun NavGraphBuilder.graph(navController: NavController) {
        forwardBackwardComposable<MealsSettings> {
            MealsSettingsScreen(
                onBack = {
                    navController.popBackStack<MealsSettings>(
                        inclusive = true
                    )
                }
            )
        }
        crossfadeComposable<Meal> {
            val (epochDay, mealId) = it.toRoute<Meal>()

            AddFoodToMealApp(
                outerScope = this,
                outerOnBack = {
                    navController.popBackStack<Meal>(inclusive = true)
                },
                mealId = mealId,
                epochDay = epochDay
            )
        }
    }

    @Serializable
    private data object MealsSettings

    @Serializable
    private data class Meal(val epochDay: Int, val mealId: Long)
}
