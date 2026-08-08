package com.maksimowiczm.foodyou.features.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.FoodSearchList
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.SearchCollection
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.SearchViewModel
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.favoritefood.FavoriteFoodSearchExtension
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.fooddatacentral.FoodDataCentralSearchExtension
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.openfoodfacts.OpenFoodFactsSearchExtension
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.userfood.UserFoodSearchExtension
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.userproduct.domain.UserProductIdentity
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeIdentity
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun HomeSearchScreen(
    selectedCollection: SearchCollection?,
    contentPadding: PaddingValues,
    onFoodDataCentralProduct: (FoodDataCentralProductIdentity, Quantity) -> Unit,
    onOpenFoodFactsProduct: (OpenFoodFactsProductIdentity, Quantity) -> Unit,
    onUserProduct: (UserProductIdentity, Quantity) -> Unit,
    onUserRecipe: (UserRecipeIdentity, Quantity) -> Unit,
    onSearch: (String) -> Unit,
    onFill: (String) -> Unit,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
) {
    val viewModel: SearchViewModel = koinViewModel()

    val searchQuery = viewModel.searchQuery.collectAsStateWithLifecycle().value
    val history = viewModel.searchHistory.collectAsStateWithLifecycle().value

    val userFood = viewModel.extension<UserFoodSearchExtension>().pages.collectAsLazyPagingItems()
    val openFoodFacts =
        viewModel.extension<OpenFoodFactsSearchExtension>().pages.collectAsLazyPagingItems()
    val foodDataCentral =
        viewModel.extension<FoodDataCentralSearchExtension>().pages.collectAsLazyPagingItems()
    val favoriteFood =
        viewModel.extension<FavoriteFoodSearchExtension>().pages.collectAsLazyPagingItems()

    val showQuickResults = remember { mutableStateOf(searchQuery !is SearchQuery.Blank) }
    LaunchedEffect(searchQuery) {
        if (searchQuery is SearchQuery.Blank) {
            showQuickResults.value = false
        } else {
            delay(100.milliseconds)
            showQuickResults.value = true
        }
    }

    FoodSearchList(
        selectedCollection = selectedCollection,
        userFood = userFood,
        openFoodFacts = openFoodFacts,
        foodDataCentral = foodDataCentral,
        favoriteFood = favoriteFood,
        searchQuery = searchQuery,
        contentPadding = contentPadding,
        lazyListState = lazyListState,
        onFoodDataCentralProduct = onFoodDataCentralProduct,
        onOpenFoodFactsProduct = onOpenFoodFactsProduct,
        onUserProduct = onUserProduct,
        onUserRecipe = onUserRecipe,
        shimmer = rememberShimmer(ShimmerBounds.View),
        modifier = modifier,
        history = history,
        showQuickResults = showQuickResults.value,
        onSearch = onSearch,
        onFill = onFill,
    )
}
