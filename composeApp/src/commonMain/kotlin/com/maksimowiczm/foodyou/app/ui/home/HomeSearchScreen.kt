package com.maksimowiczm.foodyou.app.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.HistoryToggleOff
import androidx.compose.material.icons.outlined.NorthWest
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.app.ui.common.component.FoodListItemSkeleton
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.common.extension.error
import com.maksimowiczm.foodyou.app.ui.common.extension.rememberDebounceIsIdle
import com.maksimowiczm.foodyou.app.ui.food.search.FoodDataCentralErrorCard
import com.maksimowiczm.foodyou.app.ui.food.search.FoodSearchErrorCard
import com.maksimowiczm.foodyou.app.ui.food.search.SearchCollection
import com.maksimowiczm.foodyou.app.ui.food.search.SearchViewModel
import com.maksimowiczm.foodyou.app.ui.food.search.favoritefood.FavoriteFoodListItem
import com.maksimowiczm.foodyou.app.ui.food.search.favoritefood.FavoriteFoodSearchViewModel
import com.maksimowiczm.foodyou.app.ui.food.search.fooddatacentral.FoodDataCentralListItem
import com.maksimowiczm.foodyou.app.ui.food.search.fooddatacentral.FoodDataCentralSearchViewModel
import com.maksimowiczm.foodyou.app.ui.food.search.openfoodfacts.OpenFoodFactsListItem
import com.maksimowiczm.foodyou.app.ui.food.search.openfoodfacts.OpenFoodFactsSearchViewModel
import com.maksimowiczm.foodyou.app.ui.food.search.userfood.UserFoodSearchViewModel
import com.maksimowiczm.foodyou.app.ui.food.search.userfood.UserProductListItem
import com.maksimowiczm.foodyou.common.RemoteData
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralApiError
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsApiError
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.search.domain.SearchResult
import com.maksimowiczm.foodyou.userproduct.domain.UserProduct
import com.maksimowiczm.foodyou.userproduct.domain.UserProductIdentity
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import foodyou.app.generated.resources.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun HomeSearchScreen(
    selectedCollection: SearchCollection?,
    contentPadding: PaddingValues,
    onFoodDataCentralProduct: (FoodDataCentralProductIdentity) -> Unit,
    onOpenFoodFactsProduct: (OpenFoodFactsProductIdentity) -> Unit,
    onUserProduct: (UserProductIdentity) -> Unit,
    onSearch: (String) -> Unit,
    onFill: (String) -> Unit,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
) {
    val searchViewModel: SearchViewModel = koinViewModel()
    val userFoodSearchViewModel: UserFoodSearchViewModel = koinViewModel()
    val openFoodFactsSearchViewModel: OpenFoodFactsSearchViewModel = koinViewModel()
    val foodDataCentralSearchViewModel: FoodDataCentralSearchViewModel = koinViewModel()
    val favoriteFoodSearchViewModel: FavoriteFoodSearchViewModel = koinViewModel()

    val searchQuery = searchViewModel.searchQuery.collectAsStateWithLifecycle().value
    val history = searchViewModel.searchHistory.collectAsStateWithLifecycle().value

    val userFood = userFoodSearchViewModel.pages.collectAsLazyPagingItems()
    val openFoodFacts = openFoodFactsSearchViewModel.pages.collectAsLazyPagingItems()
    val foodDataCentral = foodDataCentralSearchViewModel.pages.collectAsLazyPagingItems()
    val favoriteFood = favoriteFoodSearchViewModel.pages.collectAsLazyPagingItems()

    val isIdle =
        when (selectedCollection) {
            null ->
                userFood.rememberDebounceIsIdle() &&
                    openFoodFacts.rememberDebounceIsIdle() &&
                    foodDataCentral.rememberDebounceIsIdle()

            is SearchCollection.Favorite -> favoriteFood.rememberDebounceIsIdle()
            is SearchCollection.FoodDataCentral -> foodDataCentral.rememberDebounceIsIdle()
            is SearchCollection.OpenFoodFacts -> openFoodFacts.rememberDebounceIsIdle()
            is SearchCollection.UserFood -> userFood.rememberDebounceIsIdle()
        }

    val showQuickResults = remember { mutableStateOf(searchQuery !is SearchQuery.Blank) }
    LaunchedEffect(searchQuery) {
        if (searchQuery is SearchQuery.Blank) {
            showQuickResults.value = false
        } else {
            delay(100)
            showQuickResults.value = true
        }
    }

    HomeSearchScreen(
        selectedCollection = selectedCollection,
        history = history,
        userFood = userFood,
        openFoodFacts = openFoodFacts,
        foodDataCentral = foodDataCentral,
        favoriteFood = favoriteFood,
        isIdle = isIdle,
        showQuickResults = showQuickResults.value,
        searchQuery = searchQuery,
        contentPadding = contentPadding,
        lazyListState = lazyListState,
        onFoodDataCentralProduct = onFoodDataCentralProduct,
        onOpenFoodFactsProduct = onOpenFoodFactsProduct,
        onUserProduct = onUserProduct,
        onSearch = onSearch,
        onFill = onFill,
        modifier = modifier,
    )
}

@Composable
private fun HomeSearchScreen(
    selectedCollection: SearchCollection?,
    history: List<String>?,
    userFood: LazyPagingItems<SearchResult>,
    openFoodFacts: LazyPagingItems<OpenFoodFactsProduct>,
    foodDataCentral: LazyPagingItems<FoodDataCentralProduct>,
    favoriteFood: LazyPagingItems<RemoteData<Any>>,
    isIdle: Boolean,
    showQuickResults: Boolean,
    searchQuery: SearchQuery,
    contentPadding: PaddingValues,
    lazyListState: LazyListState,
    onFoodDataCentralProduct: (FoodDataCentralProductIdentity) -> Unit,
    onOpenFoodFactsProduct: (OpenFoodFactsProductIdentity) -> Unit,
    onUserProduct: (UserProductIdentity) -> Unit,
    onSearch: (String) -> Unit,
    onFill: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shimmer = rememberShimmer(ShimmerBounds.View)
    val density = LocalDensity.current
    var errorCardHeight by remember { mutableIntStateOf(0) }

    Box(modifier) {
        LazyColumn(
            modifier = Modifier.imePadding(),
            state = lazyListState,
            contentPadding = contentPadding.add(top = density.run { errorCardHeight.toDp() }),
        ) {
            when (selectedCollection) {
                null ->
                    if (showQuickResults) {
                        items(
                            count = userFood.itemCount.coerceAtMost(5),
                            key =
                                userFood.itemKey {
                                    when (it) {
                                        is SearchResult.UserProduct -> it.identity.id.toString()
                                    }
                                },
                        ) { i ->
                            when (val product = userFood[i]) {
                                null ->
                                    FoodListItemSkeleton(
                                        shimmer = shimmer,
                                        modifier = Modifier.animateItem(),
                                    )

                                is SearchResult.UserProduct ->
                                    UserProductListItem(
                                        product = product,
                                        onClick = { onUserProduct(product.identity) },
                                        shimmer = shimmer,
                                        modifier = Modifier.animateItem(),
                                    )
                            }
                        }

                        items(
                            count = openFoodFacts.itemCount.coerceAtMost(5),
                            key = openFoodFacts.itemKey { it.identity.barcode },
                        ) { i ->
                            when (val product = openFoodFacts[i]) {
                                null ->
                                    FoodListItemSkeleton(
                                        shimmer = shimmer,
                                        modifier = Modifier.animateItem(),
                                    )

                                else ->
                                    OpenFoodFactsListItem(
                                        food = product,
                                        onClick = { onOpenFoodFactsProduct(product.identity) },
                                        shimmer = shimmer,
                                        modifier = Modifier.animateItem(),
                                    )
                            }
                        }

                        items(
                            count = foodDataCentral.itemCount.coerceAtMost(5),
                            key = foodDataCentral.itemKey { it.identity.fdcId },
                        ) { i ->
                            when (val product = foodDataCentral[i]) {
                                null ->
                                    FoodListItemSkeleton(
                                        shimmer = shimmer,
                                        modifier = Modifier.animateItem(),
                                    )

                                else ->
                                    FoodDataCentralListItem(
                                        food = product,
                                        onClick = { onFoodDataCentralProduct(product.identity) },
                                        modifier = Modifier.animateItem(),
                                    )
                            }
                        }
                    } else if (history != null) {
                        items(history, key = { "history_$it" }) { query ->
                            FoodSearchHistoryItem(
                                search = query,
                                onFill = { onFill(query) },
                                modifier = Modifier.clickable { onSearch(query) }.animateItem(),
                            )
                        }
                    }

                is SearchCollection.UserFood ->
                    items(
                        count = userFood.itemCount,
                        key =
                            userFood.itemKey {
                                when (it) {
                                    is SearchResult.UserProduct -> it.identity.id.toString()
                                }
                            },
                    ) { i ->
                        when (val food = userFood[i]) {
                            null ->
                                FoodListItemSkeleton(
                                    shimmer = shimmer,
                                    modifier = Modifier.animateItem(),
                                )

                            is SearchResult.UserProduct ->
                                UserProductListItem(
                                    product = food,
                                    onClick = { onUserProduct(food.identity) },
                                    shimmer = shimmer,
                                    modifier = Modifier.animateItem(),
                                )
                        }
                    }

                is SearchCollection.OpenFoodFacts ->
                    items(
                        count = openFoodFacts.itemCount,
                        key = openFoodFacts.itemKey { it.identity.barcode },
                    ) { i ->
                        when (val food = openFoodFacts[i]) {
                            null ->
                                FoodListItemSkeleton(
                                    shimmer = shimmer,
                                    modifier = Modifier.animateItem(),
                                )

                            else ->
                                OpenFoodFactsListItem(
                                    food = food,
                                    onClick = { onOpenFoodFactsProduct(food.identity) },
                                    shimmer = shimmer,
                                    modifier = Modifier.animateItem(),
                                )
                        }
                    }

                is SearchCollection.FoodDataCentral ->
                    items(
                        count = foodDataCentral.itemCount,
                        key = foodDataCentral.itemKey { it.identity.fdcId },
                    ) { i ->
                        when (val food = foodDataCentral[i]) {
                            null ->
                                FoodListItemSkeleton(
                                    shimmer = shimmer,
                                    modifier = Modifier.animateItem(),
                                )

                            else ->
                                FoodDataCentralListItem(
                                    food = food,
                                    onClick = { onFoodDataCentralProduct(food.identity) },
                                    modifier = Modifier.animateItem(),
                                )
                        }
                    }

                is SearchCollection.Favorite ->
                    items(favoriteFood.itemCount) { i ->
                        when (val food = favoriteFood[i]) {
                            null ->
                                FoodListItemSkeleton(
                                    shimmer = shimmer,
                                    modifier = Modifier.animateItem(),
                                )

                            else ->
                                FavoriteFoodListItem(
                                    food = food,
                                    shimmer = shimmer,
                                    onClick = {
                                        when (food) {
                                            is RemoteData.Success -> {
                                                when (val value = food.value) {
                                                    is UserProduct -> onUserProduct(value.identity)
                                                    is OpenFoodFactsProduct ->
                                                        onOpenFoodFactsProduct(value.identity)

                                                    is FoodDataCentralProduct ->
                                                        onFoodDataCentralProduct(value.identity)
                                                }
                                            }

                                            is RemoteData.Error -> {
                                                food.partialValue?.let { value ->
                                                    when (value) {
                                                        is UserProduct ->
                                                            onUserProduct(value.identity)

                                                        is OpenFoodFactsProduct ->
                                                            onOpenFoodFactsProduct(value.identity)

                                                        is FoodDataCentralProduct ->
                                                            onFoodDataCentralProduct(value.identity)
                                                    }
                                                }
                                            }

                                            is RemoteData.Loading -> {
                                                food.partialValue?.let { value ->
                                                    when (value) {
                                                        is UserProduct ->
                                                            onUserProduct(value.identity)

                                                        is OpenFoodFactsProduct ->
                                                            onOpenFoodFactsProduct(value.identity)

                                                        is FoodDataCentralProduct ->
                                                            onFoodDataCentralProduct(value.identity)
                                                    }
                                                }
                                            }

                                            is RemoteData.NotFound -> Unit
                                        }
                                    },
                                    modifier = Modifier.animateItem(),
                                    fallback = {
                                        FoodListItemSkeleton(
                                            shimmer = shimmer,
                                            modifier = Modifier.animateItem(),
                                        )
                                    },
                                )
                        }
                    }
            }

            // Common Loading State
            val loadState =
                when (selectedCollection) {
                    null ->
                        if (showQuickResults) {
                            if (
                                userFood.loadState.append is LoadState.Loading ||
                                    userFood.loadState.refresh is LoadState.Loading ||
                                    openFoodFacts.loadState.append is LoadState.Loading ||
                                    openFoodFacts.loadState.refresh is LoadState.Loading ||
                                    foodDataCentral.loadState.append is LoadState.Loading ||
                                    foodDataCentral.loadState.refresh is LoadState.Loading
                            ) {
                                LoadState.Loading
                            } else LoadState.NotLoading(true)
                        } else LoadState.NotLoading(true)

                    is SearchCollection.Favorite -> favoriteFood.loadState.append
                    is SearchCollection.FoodDataCentral -> foodDataCentral.loadState.append
                    is SearchCollection.OpenFoodFacts -> openFoodFacts.loadState.append
                    is SearchCollection.UserFood -> userFood.loadState.append
                }

            if (loadState is LoadState.Loading) {
                items(10) { FoodListItemSkeleton(shimmer) }
            }
        }

        // Error Cards
        Box(
            Modifier.padding(top = contentPadding.calculateTopPadding()).onSizeChanged {
                errorCardHeight = it.height
            }
        ) {
            when (selectedCollection) {
                is SearchCollection.OpenFoodFacts ->
                    when (val error = openFoodFacts.loadState.error) {
                        is OpenFoodFactsApiError.RateLimitExceeded ->
                            FoodSearchErrorCard(
                                message =
                                    stringResource(Res.string.error_open_food_facts_rate_limit),
                                onRetry = openFoodFacts::retry,
                                modifier =
                                    Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp),
                            )

                        is Throwable ->
                            FoodSearchErrorCard(
                                message = error.message,
                                onRetry = openFoodFacts::retry,
                                modifier =
                                    Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp),
                            )
                    }

                is SearchCollection.FoodDataCentral ->
                    when (val error = foodDataCentral.loadState.error) {
                        is FoodDataCentralApiError ->
                            FoodDataCentralErrorCard(
                                error = error,
                                modifier =
                                    Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp),
                            )

                        is Throwable ->
                            FoodSearchErrorCard(
                                message = error.message,
                                onRetry = foodDataCentral::retry,
                                modifier =
                                    Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp),
                            )
                    }

                else -> Unit
            }
        }

        // Empty States
        if (isIdle) {
            val isSearchActive = selectedCollection != null || searchQuery !is SearchQuery.Blank
            val isEmpty =
                when (selectedCollection) {
                    null ->
                        if (isSearchActive)
                            userFood.itemCount == 0 &&
                                openFoodFacts.itemCount == 0 &&
                                foodDataCentral.itemCount == 0
                        else history.isNullOrEmpty()

                    is SearchCollection.Favorite -> favoriteFood.itemCount == 0
                    is SearchCollection.FoodDataCentral -> foodDataCentral.itemCount == 0
                    is SearchCollection.OpenFoodFacts -> openFoodFacts.itemCount == 0
                    is SearchCollection.UserFood -> userFood.itemCount == 0
                }

            if (isEmpty) {
                if (selectedCollection == null && !isSearchActive) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement =
                            Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.HistoryToggleOff,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = stringResource(Res.string.headline_no_recent_searches),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    Text(
                        text = stringResource(Res.string.neutral_no_food_found),
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        if (!isIdle) {
            ContainedLoadingIndicator(
                Modifier.align(Alignment.TopCenter)
                    .padding(top = contentPadding.calculateTopPadding())
            )
        }
    }
}

@Composable
private fun FoodSearchHistoryItem(
    search: String,
    onFill: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier,
        headlineContent = { Text(search) },
        leadingContent = { Icon(imageVector = Icons.Outlined.History, contentDescription = null) },
        trailingContent = {
            IconButton(onFill) {
                Icon(
                    imageVector = Icons.Outlined.NorthWest,
                    contentDescription = stringResource(Res.string.action_insert_suggested_search),
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
    )
}
