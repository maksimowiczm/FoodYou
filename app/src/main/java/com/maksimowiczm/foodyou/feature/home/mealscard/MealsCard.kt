package com.maksimowiczm.foodyou.feature.home.mealscard

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.MealNavHost
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.card.buildMealsCard
import com.maksimowiczm.foodyou.navigation.crossfadeComposable
import kotlinx.serialization.Serializable

object MealsCard : Feature.Home {
    override fun build(navController: NavController) = buildMealsCard(
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
    )

    @Serializable
    private data class Meal(val epochDay: Int, val mealId: Long)

    @Serializable
    private data class MealAdd(val epochDay: Int, val mealId: Long)

    override fun NavGraphBuilder.graph(navController: NavController) {
        crossfadeComposable<Meal> {
            val (epochDay, mealId) = it.toRoute<Meal>()

            MealNavHost(
                outerScope = this@crossfadeComposable,
                mealId = mealId,
                epochDay = epochDay
            )
        }
        crossfadeComposable<MealAdd> {
            val (epochDay, mealId) = it.toRoute<MealAdd>()

            MealNavHost(
                outerScope = this@crossfadeComposable,
                mealId = mealId,
                epochDay = epochDay,
                skipToSearchScreen = true
            )
        }
    }
}
