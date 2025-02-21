package com.maksimowiczm.foodyou.core.feature.openfoodfacts

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.maksimowiczm.foodyou.core.feature.Feature
import com.maksimowiczm.foodyou.core.feature.openfoodfacts.data.OpenFoodFactsSettingsRepository
import com.maksimowiczm.foodyou.core.feature.openfoodfacts.data.OpenFoodFactsSettingsRepositoryImpl
import com.maksimowiczm.foodyou.core.feature.openfoodfacts.network.OpenFoodFactsRemoteMediatorFactory
import com.maksimowiczm.foodyou.core.feature.openfoodfacts.ui.OpenFoodFactsSettingsScreen
import com.maksimowiczm.foodyou.core.feature.openfoodfacts.ui.OpenFoodFactsSettingsViewModel
import com.maksimowiczm.foodyou.core.feature.openfoodfacts.ui.buildOpenFoodFactsSettingsListItem
import com.maksimowiczm.foodyou.core.feature.openfoodfacts.ui.country.CountryFlag
import com.maksimowiczm.foodyou.core.feature.openfoodfacts.ui.country.flagCdnCountryFlag
import com.maksimowiczm.foodyou.core.feature.product.ProductFeature
import com.maksimowiczm.foodyou.core.feature.product.network.ProductRemoteMediatorFactory
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import kotlinx.serialization.Serializable
import org.koin.core.KoinApplication
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

private val openFoodFactsModule = module {
    viewModelOf(::OpenFoodFactsSettingsViewModel)
    factoryOf(::OpenFoodFactsSettingsRepositoryImpl).bind<OpenFoodFactsSettingsRepository>()

    singleOf(::OpenFoodFactsRemoteMediatorFactory).bind<ProductRemoteMediatorFactory>()

    factory { flagCdnCountryFlag }.bind<CountryFlag>()
}

/**
 * Feature for Open Food Facts. It requires the [ProductFeature] to be enabled.
 */
object OpenFoodFactsFeature : Feature.Koin, Feature.Settings {
    override fun KoinApplication.setup() {
        modules(openFoodFactsModule)
    }

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
        buildOpenFoodFactsSettingsListItem(navController)
    )

    @Serializable
    data object FoodDatabaseSettings

    fun NavController.navigateToOpenFoodFactsSettings(navOptions: NavOptions? = null) {
        navigate(
            route = FoodDatabaseSettings,
            navOptions = navOptions
        )
    }
}
