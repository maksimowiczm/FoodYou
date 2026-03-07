package com.maksimowiczm.foodyou.app.ui.food.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.component.FullScreenCameraBarcodeScanner
import com.maksimowiczm.foodyou.app.ui.common.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.food.search.favoritefood.FavoriteFoodSearchApp
import com.maksimowiczm.foodyou.app.ui.food.search.favoritefood.FavoriteFoodSearchViewModel
import com.maksimowiczm.foodyou.app.ui.food.search.fooddatacentral.FoodDataCentralSearchApp
import com.maksimowiczm.foodyou.app.ui.food.search.fooddatacentral.FoodDataCentralSearchViewModel
import com.maksimowiczm.foodyou.app.ui.food.search.openfoodfacts.OpenFoodFactsSearchApp
import com.maksimowiczm.foodyou.app.ui.food.search.openfoodfacts.OpenFoodFactsSearchViewModel
import com.maksimowiczm.foodyou.app.ui.food.search.userfood.UserFoodSearchApp
import com.maksimowiczm.foodyou.app.ui.food.search.userfood.UserFoodSearchViewModel
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.foodsearch.domain.SearchQuery
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.userfood.domain.product.UserProduct
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductIdentity
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import foodyou.app.generated.resources.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun FoodSearchApp(
    onFoodDataCentralProduct: (FoodDataCentralProductIdentity) -> Unit,
    onOpenFoodFactsProduct: (OpenFoodFactsProductIdentity) -> Unit,
    onUserProduct: (UserProductIdentity) -> Unit,
    query: String?,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()

    val viewModel: FoodSearchViewModel = koinViewModel { parametersOf(query) }
    val recentSearches by viewModel.searchHistory.collectAsStateWithLifecycle()
    val appState: FoodSearchAppState =
        rememberFoodSearchAppState(searchTextFieldState = rememberTextFieldState(query ?: ""))

    val onSearch: (String?) -> Unit = { query ->
        appState.searchTextFieldState.setTextAndPlaceCursorAtEnd(query ?: "")
        viewModel.search(query)
        scope.launch { appState.searchBarState.animateToCollapsed() }
    }

    val userFoodSearchViewModel: UserFoodSearchViewModel = koinViewModel()
    val openFoodFactsSearchViewModel: OpenFoodFactsSearchViewModel = koinViewModel()
    val foodDataCentralSearchViewModel: FoodDataCentralSearchViewModel = koinViewModel()
    val favoriteFoodSearchViewModel: FavoriteFoodSearchViewModel = koinViewModel()

    LaunchedCollectWithLifecycle(viewModel.searchQuery) {
        userFoodSearchViewModel.search(it)
        openFoodFactsSearchViewModel.search(it)
        foodDataCentralSearchViewModel.search(it)
        favoriteFoodSearchViewModel.search(it)
    }

    // Switch food source when there aren't any food available
    LaunchedCollectWithLifecycle(viewModel.searchQuery) { query ->
        // Don't switch when the query is blank
        if (query is SearchQuery.Blank) {
            return@LaunchedCollectWithLifecycle
        }

        // Don't switch when already using remote source
        when (appState.foodSource) {
            FoodSource.Favorite,
            FoodSource.YourFood -> Unit

            FoodSource.OpenFoodFacts,
            FoodSource.USDA -> return@LaunchedCollectWithLifecycle
        }

        // Give it a little time to load
        delay(150)

        when {
            favoriteFoodSearchViewModel.count.first().positive() ->
                appState.foodSource = FoodSource.Favorite

            openFoodFactsSearchViewModel.count.first().positive() ->
                appState.foodSource = FoodSource.OpenFoodFacts

            foodDataCentralSearchViewModel.count.first().positive() ->
                appState.foodSource = FoodSource.USDA
        }
    }

    FoodSearchApp(
        appState = appState,
        recentSearches = recentSearches,
        onSearch = onSearch,
        onBack = onBack,
        onFoodDataCentralProduct = onFoodDataCentralProduct,
        onOpenFoodFactsProduct = onOpenFoodFactsProduct,
        onUserProduct = onUserProduct,
        modifier = modifier,
    )
}

@Composable
private fun FoodSearchApp(
    appState: FoodSearchAppState,
    recentSearches: List<String>,
    onSearch: (String?) -> Unit,
    onBack: (() -> Unit)?,
    onFoodDataCentralProduct: (FoodDataCentralProductIdentity) -> Unit,
    onOpenFoodFactsProduct: (OpenFoodFactsProductIdentity) -> Unit,
    onUserProduct: (UserProductIdentity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val shimmer = rememberShimmer(ShimmerBounds.View)

    FullScreenCameraBarcodeScanner(
        visible = appState.showBarcodeScanner,
        onBarcodeScan = {
            appState.showBarcodeScanner = false
            onSearch(it)
        },
        onClose = { appState.showBarcodeScanner = false },
    )

    val searchInputField =
        @Composable {
            FoodSearchBarInputField(
                searchBarState = appState.searchBarState,
                textFieldState = appState.searchTextFieldState,
                onSearch = onSearch,
                onBarcodeScanner = { appState.showBarcodeScanner = true },
                onBack = onBack,
            )
        }

    val filters = run {
        val openFoodFactsSearchViewModel: OpenFoodFactsSearchViewModel = koinViewModel()
        val foodDataCentralSearchViewModel: FoodDataCentralSearchViewModel = koinViewModel()

        val usda =
            foodDataCentralSearchViewModel.shouldShowFilter.collectAsStateWithLifecycle().value
        val off = openFoodFactsSearchViewModel.shouldShowFilter.collectAsStateWithLifecycle().value

        remember(usda, off) {
            listOfNotNull(
                FoodSource.Favorite,
                FoodSource.YourFood,
                FoodSource.OpenFoodFacts.takeIf { off },
                FoodSource.USDA.takeIf { usda },
            )
        }
    }

    FoodSearchView(
        searchBarState = appState.searchBarState,
        recentSearches = recentSearches,
        selectedFilter = appState.foodSource,
        filters = filters,
        onFill = { search -> appState.searchTextFieldState.setTextAndPlaceCursorAtEnd(search) },
        onSearch = onSearch,
        onSource = { appState.foodSource = it },
        inputField = searchInputField,
    )

    Scaffold(modifier) { paddingValues ->
        var topContentHeight by remember { mutableIntStateOf(0) }

        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .zIndex(10f)
                    .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
                    .windowInsetsPadding(
                        WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal)
                    )
                    .padding(top = paddingValues.calculateTopPadding())
                    .onSizeChanged { topContentHeight = it.height },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SearchBar(
                state = appState.searchBarState,
                inputField = searchInputField,
                modifier = Modifier.padding(horizontal = 16.dp).padding(top = 8.dp).fillMaxWidth(),
                colors =
                    SearchBarDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    ),
                shadowElevation = 2.dp,
            )

            Spacer(Modifier.height(8.dp))
            FoodSearchFilters(
                selectedSource = appState.foodSource,
                onSource = {
                    if (it == appState.foodSource) {
                        scope.launch { appState.listStates.state(it).animateScrollToItem(0) }
                    }
                    appState.foodSource = it
                },
                modifier = Modifier.height(32.dp + 8.dp + 32.dp).fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
        }

        val contentPadding =
            paddingValues.add(
                top = LocalDensity.current.run { topContentHeight.toDp() },
                bottom = 56.dp + 32.dp,
            )

        when (appState.foodSource) {
            FoodSource.Favorite ->
                FavoriteFoodSearchApp(
                    shimmer = shimmer,
                    contentPadding = contentPadding,
                    lazyListState = appState.listStates.favorite,
                    onClick = {
                        when (it) {
                            is UserProduct -> onUserProduct(it.identity)
                            is OpenFoodFactsProduct -> onOpenFoodFactsProduct(it.identity)
                            is FoodDataCentralProduct -> onFoodDataCentralProduct(it.identity)
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                )

            FoodSource.YourFood ->
                UserFoodSearchApp(
                    shimmer = shimmer,
                    contentPadding = contentPadding,
                    lazyListState = appState.listStates.yourFood,
                    onUserProduct = { onUserProduct(it.identity) },
                    modifier = Modifier.fillMaxSize(),
                )

            FoodSource.OpenFoodFacts ->
                OpenFoodFactsSearchApp(
                    shimmer = shimmer,
                    contentPadding = contentPadding,
                    lazyListState = appState.listStates.openFoodFacts,
                    onClick = { onOpenFoodFactsProduct(it.identity) },
                    modifier = Modifier.fillMaxSize(),
                )

            FoodSource.USDA ->
                FoodDataCentralSearchApp(
                    shimmer = shimmer,
                    contentPadding = contentPadding,
                    lazyListState = appState.listStates.usda,
                    onClick = { onFoodDataCentralProduct(it.identity) },
                    modifier = Modifier.fillMaxSize(),
                )
        }
    }
}

object FoodSearchAppDefaults {

    // FAB menu with options to create a recipe or product
    @Composable
    fun FloatingActionButton(
        fabExpanded: Boolean,
        onFabExpandedChange: (Boolean) -> Unit,
        onCreateRecipe: () -> Unit,
        onCreateProduct: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val colorScheme = MaterialTheme.colorScheme

        FloatingActionButtonMenu(
            expanded = fabExpanded,
            modifier = modifier,
            button = {
                ToggleFloatingActionButton(
                    checked = fabExpanded,
                    onCheckedChange = onFabExpandedChange,
                    containerColor = {
                        lerp(
                            start = colorScheme.secondaryContainer,
                            stop = colorScheme.secondary,
                            fraction = it,
                        )
                    },
                ) {
                    val rotation by remember { derivedStateOf { checkedProgress * 45f } }

                    val tintColor =
                        lerp(
                            start = MaterialTheme.colorScheme.onSecondaryContainer,
                            stop = MaterialTheme.colorScheme.onSecondary,
                            fraction = checkedProgress,
                        )

                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription =
                            if (fabExpanded) {
                                stringResource(Res.string.action_close)
                            } else {
                                stringResource(Res.string.action_create)
                            },
                        tint = tintColor,
                        modifier = Modifier.graphicsLayer { rotationZ = rotation },
                    )
                }
            },
        ) {
            FloatingActionButtonMenuItem(
                modifier = Modifier,
                onClick = {
                    onCreateRecipe()
                    onFabExpandedChange(false)
                },
                icon = { Icon(painterResource(Res.drawable.ic_skillet_filled), null) },
                text = { Text(stringResource(Res.string.headline_recipe)) },
            )
            FloatingActionButtonMenuItem(
                modifier = Modifier,
                onClick = {
                    onCreateProduct()
                    onFabExpandedChange(false)
                },
                icon = { Icon(Icons.Filled.LunchDining, null) },
                text = { Text(stringResource(Res.string.headline_product)) },
            )
        }
    }
}

private fun ListStates.state(source: FoodSource) =
    when (source) {
        FoodSource.YourFood -> yourFood
        FoodSource.OpenFoodFacts -> openFoodFacts
        FoodSource.USDA -> usda
        FoodSource.Favorite -> favorite
    }

private fun Int?.positive(): Boolean = this != null && this > 0
