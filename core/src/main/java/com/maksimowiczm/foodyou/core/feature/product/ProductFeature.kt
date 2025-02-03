package com.maksimowiczm.foodyou.core.feature.product

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.maksimowiczm.foodyou.core.feature.Feature
import com.maksimowiczm.foodyou.core.feature.product.data.DatabaseSettingsRepository
import com.maksimowiczm.foodyou.core.feature.product.data.DatabaseSettingsRepositoryImpl
import com.maksimowiczm.foodyou.core.feature.product.data.ProductRepository
import com.maksimowiczm.foodyou.core.feature.product.data.ProductRepositoryImpl
import com.maksimowiczm.foodyou.core.feature.product.network.RemoteProductDatabase
import com.maksimowiczm.foodyou.core.feature.product.network.openfoodfacts.OpenFoodFactsDatabase
import com.maksimowiczm.foodyou.core.feature.product.network.openfoodfacts.OpenFoodFactsNetworkDataSource
import com.maksimowiczm.foodyou.core.feature.product.ui.create.CreateProductViewModel
import com.maksimowiczm.foodyou.core.feature.product.ui.databasesettings.FoodDatabaseSettingsScreen
import com.maksimowiczm.foodyou.core.feature.product.ui.databasesettings.FoodDatabaseSettingsViewModel
import com.maksimowiczm.foodyou.core.feature.product.ui.databasesettings.buildFoodDatabaseSettingsListItem
import com.maksimowiczm.foodyou.core.feature.product.ui.databasesettings.country.CountryFlag
import com.maksimowiczm.foodyou.core.feature.product.ui.databasesettings.country.flagCdnCountryFlag
import com.maksimowiczm.foodyou.core.navigation.settingsComposable
import kotlinx.serialization.Serializable
import org.koin.core.KoinApplication
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

private val productsModule = module {
    factoryOf(::ProductRepositoryImpl).bind<ProductRepository>()

    viewModelOf(::CreateProductViewModel)

    factoryOf(::DatabaseSettingsRepositoryImpl).bind<DatabaseSettingsRepository>()

    viewModelOf(::FoodDatabaseSettingsViewModel)

    factory { flagCdnCountryFlag }.bind<CountryFlag>()

    factory {
        OpenFoodFactsDatabase(
            dataStore = get(),
            productDatabase = get(),
            openFoodFactsNetworkDataSource = OpenFoodFactsNetworkDataSource()
        )
    }.bind<RemoteProductDatabase>()
}

/**
 * ProductFeature is a feature that provides functionality for managing products.
 */
object ProductFeature : Feature.Koin, Feature.Settings {
    override fun KoinApplication.setup() {
        modules(productsModule)
    }

    override fun NavGraphBuilder.settingsGraph(navController: NavController) {
        settingsComposable<FoodDatabaseSettings> {
            FoodDatabaseSettingsScreen(
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
        buildFoodDatabaseSettingsListItem(navController)
    )

    @Serializable
    data object FoodDatabaseSettings

    fun NavController.navigateToFoodDatabaseSettings(
        navOptions: NavOptions? = null
    ) {
        navigate(
            route = FoodDatabaseSettings,
            navOptions = navOptions
        )
    }
}
