package com.maksimowiczm.foodyou.feature.home.mealscard

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.DiaryDayMealScreen
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.buildMealsCard
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
        }
    )

    // TODO: Make it private
    @Serializable
    data class Meal(val epochDay: Int, val mealId: Long)

    override fun NavGraphBuilder.graph(navController: NavController) {
        crossfadeComposable<Meal> {
            DiaryDayMealScreen(
                animatedVisibilityScope = this
            )
        }
    }
}
