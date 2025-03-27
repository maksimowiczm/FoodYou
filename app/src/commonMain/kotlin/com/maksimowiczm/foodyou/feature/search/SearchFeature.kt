package com.maksimowiczm.foodyou.feature.search

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.HomeFeature
import com.maksimowiczm.foodyou.feature.search.data.SearchRepository
import com.maksimowiczm.foodyou.feature.search.domain.QueryProductsUseCase
import com.maksimowiczm.foodyou.feature.search.network.OpenFoodFactsRemoteMediatorFactory
import com.maksimowiczm.foodyou.feature.search.network.ProductRemoteMediatorFactory
import com.maksimowiczm.foodyou.feature.search.ui.SearchScreen
import com.maksimowiczm.foodyou.feature.search.ui.SearchViewModel
import com.maksimowiczm.foodyou.navigation.crossfadeComposable
import kotlinx.serialization.Serializable
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
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
                }.bind<QueryProductsUseCase>()
            }
        )
    }

    @Serializable
    private data object Search

    override fun NavGraphBuilder.graph(navController: NavController) {
        crossfadeComposable<Search> {
            SearchScreen()
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
}
