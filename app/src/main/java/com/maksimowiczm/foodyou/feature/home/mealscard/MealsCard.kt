package com.maksimowiczm.foodyou.feature.home.mealscard

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.MealApp
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.barcodescanner.BarcodeScannerScreen
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.search.SearchHintBuilder
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.card.buildMealsCard
import com.maksimowiczm.foodyou.navigation.crossfadeComposable
import kotlinx.serialization.Serializable

class MealsCard(
    private val searchHintBuilder: SearchHintBuilder,
    private val barcodeScannerScreen: BarcodeScannerScreen
) : Feature.Home {
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

            MealApp(
                outerScope = this@crossfadeComposable,
                outerOnBack = { navController.popBackStack<Meal>(inclusive = true) },
                mealId = mealId,
                epochDay = epochDay,
                barcodeScannerScreen = barcodeScannerScreen,
                searchHint = searchHintBuilder.build(navController)
            )
        }
        crossfadeComposable<MealAdd> {
            val (epochDay, mealId) = it.toRoute<MealAdd>()

            MealApp(
                outerScope = this@crossfadeComposable,
                outerOnBack = { navController.popBackStack<MealAdd>(inclusive = true) },
                mealId = mealId,
                epochDay = epochDay,
                barcodeScannerScreen = barcodeScannerScreen,
                searchHint = searchHintBuilder.build(navController),
                skipToSearchScreen = true
            )
        }
        with(searchHintBuilder) {
            graph(navController)
        }
    }
}
