package com.maksimowiczm.foodyou.feature.settings.openfoodfactssettings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.search.OpenFoodFactsSearchHint
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.search.SearchHint
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.search.SearchHintBuilder
import com.maksimowiczm.foodyou.feature.settings.openfoodfactssettings.ui.OpenFoodFactsSettingsScreen
import com.maksimowiczm.foodyou.feature.settings.openfoodfactssettings.ui.buildOpenFoodFactsSettingsListItem
import com.maksimowiczm.foodyou.navigation.forwardBackwardComposable
import kotlinx.serialization.Serializable

object OpenFoodFactsSettings : Feature.Settings, SearchHintBuilder {
    override fun buildSettingsFeatures(navController: NavController) =
        buildOpenFoodFactsSettingsListItem(
            onClick = {
                navController.navigate(
                    route = FoodDatabaseSettings,
                    navOptions = navOptions {
                        launchSingleTop = true
                    }
                )
            }
        )

    @Serializable
    private data object FoodDatabaseSettings

    override fun NavGraphBuilder.graph(navController: NavController) {
        forwardBackwardComposable<FoodDatabaseSettings> {
            OpenFoodFactsSettingsScreen(
                onBack = {
                    navController.popBackStack<FoodDatabaseSettings>(
                        inclusive = true
                    )
                }
            )
        }
    }

    override fun build(navController: NavController) = SearchHint { modifier ->
        OpenFoodFactsSearchHint(
            onGoToSettings = {
                navController.navigate(
                    route = FoodDatabaseSettings,
                    navOptions = navOptions {
                        launchSingleTop = true
                    }
                )
            },
            modifier = modifier
        )
    }
}
