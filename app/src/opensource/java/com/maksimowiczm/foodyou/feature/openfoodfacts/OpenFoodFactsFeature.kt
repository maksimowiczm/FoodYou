package com.maksimowiczm.foodyou.feature.openfoodfacts

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.addfood.network.ProductRemoteMediatorFactory
import com.maksimowiczm.foodyou.feature.openfoodfacts.data.OpenFoodFactsSettingsRepository
import com.maksimowiczm.foodyou.feature.openfoodfacts.data.OpenFoodFactsSettingsRepositoryImpl
import com.maksimowiczm.foodyou.feature.openfoodfacts.data.ProductRepositoryImpl
import com.maksimowiczm.foodyou.feature.openfoodfacts.network.OpenFoodFactsRemoteMediatorFactory
import com.maksimowiczm.foodyou.feature.openfoodfacts.ui.OpenFoodFactsSettingsScreen
import com.maksimowiczm.foodyou.feature.openfoodfacts.ui.OpenFoodFactsSettingsViewModel
import com.maksimowiczm.foodyou.feature.openfoodfacts.ui.buildOpenFoodFactsSettingsListItem
import com.maksimowiczm.foodyou.feature.openfoodfacts.ui.country.CountryFlag
import com.maksimowiczm.foodyou.feature.openfoodfacts.ui.country.flagCdnCountryFlag
import com.maksimowiczm.foodyou.feature.product.ProductFeature
import com.maksimowiczm.foodyou.navigation.forwardBackwardComposable
import kotlinx.serialization.Serializable
import org.koin.core.KoinApplication
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Feature for Open Food Facts. It requires the [ProductFeature] to be enabled.
 */
object OpenFoodFactsFeature : Feature.Settings, ProductFeature(
    productRepository = { factoryOf(::ProductRepositoryImpl) },
    settingsRoute = FoodDatabaseSettings
) {
    private val module = module {
        viewModelOf(::OpenFoodFactsSettingsViewModel)
        factoryOf(::OpenFoodFactsSettingsRepositoryImpl).bind<OpenFoodFactsSettingsRepository>()

        singleOf(::OpenFoodFactsRemoteMediatorFactory).bind<ProductRemoteMediatorFactory>()

        factory { flagCdnCountryFlag }.bind<CountryFlag>()
    }

    override fun KoinApplication.configure() {
        modules(module)
    }

    @Serializable
    data object FoodDatabaseSettings

    override fun NavGraphBuilder.settingsGraph(navController: NavController) {
        forwardBackwardComposable<FoodDatabaseSettings> {
            OpenFoodFactsSettingsScreen(
                onBack = {
                    navController.popBackStack(
                        route = FoodDatabaseSettings,
                        inclusive = true
                    )
                }
            )
        }
    }

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
        )
    )
}
