package com.maksimowiczm.foodyou.feature.search

import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.HomeFeature
import com.maksimowiczm.foodyou.feature.search.data.OpenFoodFactsSettingsRepository
import com.maksimowiczm.foodyou.feature.search.data.SearchRepository
import com.maksimowiczm.foodyou.feature.search.domain.ObserveProductQueries
import com.maksimowiczm.foodyou.feature.search.domain.QueryProductsUseCase
import com.maksimowiczm.foodyou.feature.search.network.OpenFoodFactsRemoteMediatorFactory
import com.maksimowiczm.foodyou.feature.search.network.ProductRemoteMediatorFactory
import com.maksimowiczm.foodyou.feature.search.ui.SearchScreen
import com.maksimowiczm.foodyou.feature.search.ui.SearchViewModel
import com.maksimowiczm.foodyou.feature.search.ui.openfoodfactssettings.CountryFlag
import com.maksimowiczm.foodyou.feature.search.ui.openfoodfactssettings.OpenFoodFactsSettingsScreen
import com.maksimowiczm.foodyou.feature.search.ui.openfoodfactssettings.OpenFoodFactsSettingsViewModel
import com.maksimowiczm.foodyou.feature.search.ui.openfoodfactssettings.buildOpenFoodFactsSettingsListItem
import com.maksimowiczm.foodyou.feature.search.ui.openfoodfactssettings.flagCdnCountryFlag
import com.maksimowiczm.foodyou.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.navigation.forwardBackwardComposable
import kotlinx.serialization.Serializable
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

object SearchFeature : Feature {
    override fun declare(): KoinAppDeclaration = {
        modules(
            module {
                singleOf(::OpenFoodFactsRemoteMediatorFactory).bind<ProductRemoteMediatorFactory>()

                viewModelOf(::SearchViewModel)

                factory {
                    SearchRepository(
                        searchDatabase = get(),
                        productRemoteMediatorFactory = get()
                    )
                }.binds(
                    arrayOf(
                        QueryProductsUseCase::class,
                        ObserveProductQueries::class
                    )
                )

                factory { flagCdnCountryFlag }.bind<CountryFlag>()
                factoryOf(::OpenFoodFactsSettingsRepository)
                viewModelOf(::OpenFoodFactsSettingsViewModel)
            }
        )
    }

    @Serializable
    private data object Search

    @Serializable
    private data object FoodDatabaseSettings

    override fun NavGraphBuilder.graph(navController: NavController) {
        crossfadeComposable<Search> {
            SearchScreen(
                onBack = {
                    navController.popBackStack<Search>(
                        inclusive = true
                    )
                },
                onBarcodeScanner = {}
            ) {
                {
                    ListItem(
                        headlineContent = {
                            Text(
                                text = it?.name.toString()
                            )
                        }
                    )
                }
            }
        }
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

    override fun buildHomeFeatures(navController: NavController) = listOf(
        HomeFeature { animatedVisibilityScope, modifier, homeState ->
            Button(
                onClick = {
                    navController.navigate(Search)
                }
            ) {
                Text("Search")
            }
        }
    )

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
