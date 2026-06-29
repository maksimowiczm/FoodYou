package com.maksimowiczm.foodyou.app.ui.food.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.HistoryToggleOff
import androidx.compose.material.icons.outlined.NorthWest
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.app.ui.common.barcodescanner.FullScreenCameraBarcodeScanner
import com.maksimowiczm.foodyou.app.ui.common.component.FoodListItemSkeleton
import com.maksimowiczm.foodyou.app.ui.common.component.StatusBarProtection
import com.maksimowiczm.foodyou.app.ui.common.component.StatusBarProtectionDefaults.rememberScrollConnection
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.common.extension.error
import com.maksimowiczm.foodyou.app.ui.common.extension.horizontal
import com.maksimowiczm.foodyou.app.ui.common.extension.plus
import com.maksimowiczm.foodyou.app.ui.common.extension.rememberDebounceIsIdle
import com.maksimowiczm.foodyou.app.ui.common.extension.toDp
import com.maksimowiczm.foodyou.app.ui.common.saveable.jsonSaver
import com.maksimowiczm.foodyou.app.ui.food.search.FoodSearchErrorCard
import com.maksimowiczm.foodyou.app.ui.food.search.SearchCollection
import com.maksimowiczm.foodyou.app.ui.food.search.SearchFilters
import com.maksimowiczm.foodyou.app.ui.food.search.SearchViewModel
import com.maksimowiczm.foodyou.app.ui.food.search.favoritefood.FavoriteFoodListItem
import com.maksimowiczm.foodyou.app.ui.food.search.favoritefood.FavoriteFoodSearchExtension
import com.maksimowiczm.foodyou.app.ui.food.search.fooddatacentral.FoodDataCentralListItem
import com.maksimowiczm.foodyou.app.ui.food.search.fooddatacentral.FoodDataCentralSearchExtension
import com.maksimowiczm.foodyou.app.ui.food.search.openfoodfacts.OpenFoodFactsListItem
import com.maksimowiczm.foodyou.app.ui.food.search.openfoodfacts.OpenFoodFactsSearchExtension
import com.maksimowiczm.foodyou.app.ui.food.search.rememberCollectionFilters
import com.maksimowiczm.foodyou.app.ui.food.search.userfood.UserFoodSearchExtension
import com.maksimowiczm.foodyou.app.ui.food.search.userfood.UserProductListItem
import com.maksimowiczm.foodyou.app.ui.food.search.userfood.UserRecipeListItem
import com.maksimowiczm.foodyou.app.ui.fooddatacentral.FoodDataCentralErrorCard
import com.maksimowiczm.foodyou.common.RemoteData
import com.maksimowiczm.foodyou.common.domain.food.Quantity
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
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipe
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeIdentity
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import foodyou.app.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun IngredientSearchScreen(
    recipeIdentity: UserRecipeIdentity?,
    onBack: () -> Unit,
    onFoodDataCentralProduct: (FoodDataCentralProductIdentity, Quantity) -> Unit,
    onOpenFoodFactsProduct: (OpenFoodFactsProductIdentity, Quantity) -> Unit,
    onUserProduct: (UserProductIdentity, Quantity) -> Unit,
    onUserRecipe: (UserRecipeIdentity, Quantity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val searchViewModel: SearchViewModel = koinViewModel { parametersOf(recipeIdentity) }

    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val shimmer = rememberShimmer(ShimmerBounds.View)

    val offset = rememberSaveable { mutableFloatStateOf(0f) }
    val topBarHeight = density.run { 64.dp.toPx() }
    val scrollConnection = rememberScrollConnection { offset.floatValue -= it.y }

    val textFieldState = rememberTextFieldState()

    val filterChipsHeight = remember { mutableFloatStateOf(0f) }
    val collections = rememberCollectionFilters()
    var selectedCollection by
        rememberSaveable(stateSaver = jsonSaver()) { mutableStateOf<SearchCollection?>(null) }
    val lazyListState = rememberLazyListState()

    val searchQuery = searchViewModel.searchQuery.collectAsStateWithLifecycle().value

    val openFoodFactsExtension = searchViewModel.extension<OpenFoodFactsSearchExtension>()
    val foodDataCentralExtension = searchViewModel.extension<FoodDataCentralSearchExtension>()
    val userFoodExtension = searchViewModel.extension<UserFoodSearchExtension>()
    val favoriteFoodExtension = searchViewModel.extension<FavoriteFoodSearchExtension>()

    LaunchedEffect(selectedCollection) {
        when (val collection = selectedCollection) {
            is SearchCollection.FoodDataCentral ->
                foodDataCentralExtension.dataTypes(collection.dataTypes)

            is SearchCollection.OpenFoodFacts -> openFoodFactsExtension.version(collection.version)

            else -> Unit
        }
    }

    val showBarcodeScanner = rememberSaveable { mutableStateOf(false) }
    FullScreenCameraBarcodeScanner(
        visible = showBarcodeScanner.value,
        onClose = { showBarcodeScanner.value = false },
        onBarcodeScan = {
            showBarcodeScanner.value = false
            searchViewModel.search(it)
            textFieldState.setTextAndPlaceCursorAtEnd(it)
        },
    )

    val searchBarState = rememberSearchBarState()
    val searchInputField =
        @Composable {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = searchBarState,
                onSearch = {
                    scope.launch { searchBarState.animateToCollapsed() }
                    searchViewModel.search(it)
                    textFieldState.setTextAndPlaceCursorAtEnd(it)
                },
                placeholder = {
                    Text(
                        text = stringResource(Res.string.headline_search_ingredients),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                },
                leadingIcon = {
                    IconButton(
                        onClick = {
                            if (searchBarState.targetValue == SearchBarValue.Expanded)
                                scope.launch { searchBarState.animateToCollapsed() }
                            else onBack()
                        },
                        shapes = IconButtonDefaults.shapes(),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = stringResource(Res.string.action_go_back),
                        )
                    }
                },
                trailingIcon = {
                    Row {
                        if (textFieldState.text.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    textFieldState.clearText()
                                    searchViewModel.search(null)
                                },
                                shapes = IconButtonDefaults.shapes(),
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Clear,
                                    contentDescription = stringResource(Res.string.action_clear),
                                )
                            }
                        }
                        IconButton(
                            onClick = { showBarcodeScanner.value = true },
                            shapes = IconButtonDefaults.shapes(),
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_barcode_scanner),
                                contentDescription = stringResource(Res.string.action_scan_barcode),
                            )
                        }
                    }
                },
            )
        }

    Scaffold(
        modifier = modifier.nestedScroll(scrollConnection),
        topBar = {
            AppBarWithSearch(
                state = searchBarState,
                inputField = searchInputField,
                colors =
                    SearchBarDefaults.appBarWithSearchColors(
                        appBarContainerColor = Color.Transparent
                    ),
            )
        },
    ) { contentPadding ->
        SearchList(
            selectedCollection = selectedCollection,
            userFood = userFoodExtension.pages.collectAsLazyPagingItems(),
            openFoodFacts = openFoodFactsExtension.pages.collectAsLazyPagingItems(),
            foodDataCentral = foodDataCentralExtension.pages.collectAsLazyPagingItems(),
            favoriteFood = favoriteFoodExtension.pages.collectAsLazyPagingItems(),
            searchQuery = searchQuery,
            contentPadding =
                contentPadding.add(top = density.run { filterChipsHeight.floatValue.toDp() }),
            lazyListState = lazyListState,
            onFoodDataCentralProduct = onFoodDataCentralProduct,
            onOpenFoodFactsProduct = onOpenFoodFactsProduct,
            onUserProduct = onUserProduct,
            onUserRecipe = onUserRecipe,
            shimmer = shimmer,
            modifier = Modifier.fillMaxSize(),
        )

        Box(
            Modifier.fillMaxWidth()
                .padding(top = contentPadding.calculateTopPadding())
                .graphicsLayer { filterChipsHeight.floatValue = size.height }
        ) {
            SearchFilters(
                collections = collections,
                selected = selectedCollection,
                onCollection = { selectedCollection = it },
                contentPadding =
                    PaddingValues(horizontal = 16.dp) +
                        WindowInsets.statusBars.asPaddingValues().horizontal() +
                        WindowInsets.displayCutout.asPaddingValues().horizontal(),
            )
        }

        StatusBarProtection { (offset.value / topBarHeight).coerceIn(0f, 1f) }
    }

    ExpandedFullScreenSearchBar(state = searchBarState, inputField = searchInputField) {
        SearchHints(
            history = searchViewModel.searchHistory.collectAsStateWithLifecycle().value,
            onSearch = {
                scope.launch { searchBarState.animateToCollapsed() }
                searchViewModel.search(it)
                textFieldState.setTextAndPlaceCursorAtEnd(it)
            },
            onFill = { textFieldState.setTextAndPlaceCursorAtEnd(it) },
            shimmer = shimmer,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun SearchList(
    selectedCollection: SearchCollection?,
    userFood: LazyPagingItems<SearchResult>,
    openFoodFacts: LazyPagingItems<OpenFoodFactsProduct>,
    foodDataCentral: LazyPagingItems<FoodDataCentralProduct>,
    favoriteFood: LazyPagingItems<RemoteData<Any>>,
    searchQuery: SearchQuery,
    contentPadding: PaddingValues,
    lazyListState: LazyListState,
    onFoodDataCentralProduct: (FoodDataCentralProductIdentity, Quantity) -> Unit,
    onOpenFoodFactsProduct: (OpenFoodFactsProductIdentity, Quantity) -> Unit,
    onUserProduct: (UserProductIdentity, Quantity) -> Unit,
    onUserRecipe: (UserRecipeIdentity, Quantity) -> Unit,
    shimmer: Shimmer,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    var errorCardHeight by remember { mutableIntStateOf(0) }

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

    Box(modifier) {
        LazyColumn(
            modifier = Modifier.imePadding(),
            state = lazyListState,
            contentPadding = contentPadding.add(top = density.run { errorCardHeight.toDp() }),
        ) {
            when (selectedCollection) {
                null -> {
                    items(
                        count = userFood.itemCount.coerceAtMost(5),
                        key =
                            userFood.itemKey {
                                when (it) {
                                    is SearchResult.UserProduct -> it.id.toString()
                                    is SearchResult.UserRecipe -> it.id.toString()
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
                                    onClick = {
                                        onUserProduct(UserProductIdentity(product.id), it)
                                    },
                                    shimmer = shimmer,
                                    modifier = Modifier.animateItem(),
                                )

                            is SearchResult.UserRecipe ->
                                UserRecipeListItem(
                                    recipe = product,
                                    onClick = { onUserRecipe(UserRecipeIdentity(product.id), it) },
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
                                    onClick = { onFoodDataCentralProduct(product.identity, it) },
                                    modifier = Modifier.animateItem(),
                                )
                        }
                    }
                }

                is SearchCollection.UserFood ->
                    items(
                        count = userFood.itemCount,
                        key =
                            userFood.itemKey {
                                when (it) {
                                    is SearchResult.UserProduct -> it.id.toString()
                                    is SearchResult.UserRecipe -> it.id.toString()
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
                                    onClick = { onUserProduct(UserProductIdentity(food.id), it) },
                                    shimmer = shimmer,
                                    modifier = Modifier.animateItem(),
                                )

                            is SearchResult.UserRecipe ->
                                UserRecipeListItem(
                                    recipe = food,
                                    onClick = { onUserRecipe(UserRecipeIdentity(food.id), it) },
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
                                    onClick = { onOpenFoodFactsProduct(food.identity, it) },
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
                                    onClick = { onFoodDataCentralProduct(food.identity, it) },
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
                                                    is UserProduct ->
                                                        onUserProduct(value.identity, it)

                                                    is UserRecipe ->
                                                        onUserRecipe(value.identity, it)

                                                    is OpenFoodFactsProduct ->
                                                        onOpenFoodFactsProduct(value.identity, it)

                                                    is FoodDataCentralProduct ->
                                                        onFoodDataCentralProduct(value.identity, it)
                                                }
                                            }

                                            is RemoteData.Error -> {
                                                food.partialValue?.let { value ->
                                                    when (value) {
                                                        is UserProduct ->
                                                            onUserProduct(value.identity, it)

                                                        is UserRecipe ->
                                                            onUserRecipe(value.identity, it)

                                                        is OpenFoodFactsProduct ->
                                                            onOpenFoodFactsProduct(
                                                                value.identity,
                                                                it,
                                                            )

                                                        is FoodDataCentralProduct ->
                                                            onFoodDataCentralProduct(
                                                                value.identity,
                                                                it,
                                                            )
                                                    }
                                                }
                                            }

                                            is RemoteData.Loading -> {
                                                food.partialValue?.let { value ->
                                                    when (value) {
                                                        is UserProduct ->
                                                            onUserProduct(value.identity, it)

                                                        is UserRecipe ->
                                                            onUserRecipe(value.identity, it)

                                                        is OpenFoodFactsProduct ->
                                                            onOpenFoodFactsProduct(
                                                                value.identity,
                                                                it,
                                                            )

                                                        is FoodDataCentralProduct ->
                                                            onFoodDataCentralProduct(
                                                                value.identity,
                                                                it,
                                                            )
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
                        userFood.itemCount == 0 &&
                            openFoodFacts.itemCount == 0 &&
                            foodDataCentral.itemCount == 0

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
private fun SearchHints(
    history: List<String>?,
    onSearch: (String) -> Unit,
    onFill: (String) -> Unit,
    shimmer: Shimmer,
    modifier: Modifier = Modifier,
) {
    val history = history ?: List<String?>(5) { null }

    LazyColumn(modifier) {
        itemsIndexed(history, key = { i, _ -> i }) { _, query ->
            if (query == null) {
                ListItem(
                    modifier = Modifier.animateItem(),
                    headlineContent = {
                        val extraWidth = rememberSaveable { ((0..100).random().toFloat() / 100f) }

                        Spacer(
                            Modifier.shimmer(shimmer)
                                .height(LocalTextStyle.current.toDp())
                                .width(100.dp + extraWidth * 100.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        )
                    },
                    leadingContent = {
                        Icon(imageVector = Icons.Outlined.History, contentDescription = null)
                    },
                    trailingContent = {
                        Icon(imageVector = Icons.Outlined.NorthWest, contentDescription = null)
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                )
            } else {
                ListItem(
                    modifier = Modifier.clickable { onSearch(query) }.animateItem(),
                    headlineContent = { Text(query) },
                    leadingContent = {
                        Icon(imageVector = Icons.Outlined.History, contentDescription = null)
                    },
                    trailingContent = {
                        IconButton(
                            onClick = { onFill(query) },
                            shapes = IconButtonDefaults.shapes(),
                            modifier = Modifier.offset(x = 12.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.NorthWest,
                                contentDescription =
                                    stringResource(Res.string.action_insert_suggested_search),
                            )
                        }
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                )
            }
        }
    }
}
