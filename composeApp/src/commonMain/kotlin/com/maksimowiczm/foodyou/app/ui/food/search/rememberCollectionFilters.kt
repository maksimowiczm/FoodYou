package com.maksimowiczm.foodyou.app.ui.food.search

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.maksimowiczm.foodyou.app.ui.food.search.favoritefood.FavoriteFoodSearchExtension
import com.maksimowiczm.foodyou.app.ui.food.search.fooddatacentral.FoodDataCentralSearchExtension
import com.maksimowiczm.foodyou.app.ui.food.search.openfoodfacts.OpenFoodFactsSearchExtension
import com.maksimowiczm.foodyou.app.ui.food.search.userfood.UserFoodSearchExtension
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun rememberCollectionFilters(): List<CollectionFilter> {
    val viewModel: SearchViewModel = koinViewModel()

    val favorite = run {
        val source = viewModel.extension<FavoriteFoodSearchExtension>()
        rememberCollectionFilter(
            collection = SearchCollection.Favorite(),
            count = source.count.collectAsStateWithLifecycle().value,
            pages = source.pages.collectAsLazyPagingItems(),
        )
    }

    val userFood = run {
        val source = viewModel.extension<UserFoodSearchExtension>()
        rememberCollectionFilter(
            collection = SearchCollection.UserFood(),
            count = source.count.collectAsStateWithLifecycle().value,
            pages = source.pages.collectAsLazyPagingItems(),
        )
    }

    val openFoodFacts = run {
        val source = viewModel.extension<OpenFoodFactsSearchExtension>()
        if (!source.shouldShowFilter.collectAsStateWithLifecycle().value) return@run null
        rememberCollectionFilter(
            collection = SearchCollection.OpenFoodFacts(),
            count = source.count.collectAsStateWithLifecycle().value,
            pages = source.pages.collectAsLazyPagingItems(),
        )
    }

    val foodDataCentral = run {
        val source = viewModel.extension<FoodDataCentralSearchExtension>()
        if (!source.shouldShowFilter.collectAsStateWithLifecycle().value) return@run null
        rememberCollectionFilter(
            collection = SearchCollection.FoodDataCentral(),
            count = source.count.collectAsStateWithLifecycle().value,
            pages = source.pages.collectAsLazyPagingItems(),
        )
    }

    return remember(favorite, userFood, openFoodFacts, foodDataCentral) {
        listOfNotNull(favorite, userFood, openFoodFacts, foodDataCentral)
    }
}
