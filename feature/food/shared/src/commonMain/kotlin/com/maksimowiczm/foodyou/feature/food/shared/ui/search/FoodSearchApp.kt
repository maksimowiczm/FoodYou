package com.maksimowiczm.foodyou.feature.food.shared.ui.search

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.paging.LoadState
import androidx.paging.compose.error
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.business.food.domain.FoodSearch
import com.maksimowiczm.foodyou.business.food.domain.remote.RemoteFoodException
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodId
import com.maksimowiczm.foodyou.business.shared.domain.measurement.Measurement
import com.maksimowiczm.foodyou.feature.food.shared.presentation.search.FoodFilter
import com.maksimowiczm.foodyou.feature.food.shared.presentation.search.FoodSearchUiState
import com.maksimowiczm.foodyou.feature.food.shared.presentation.search.FoodSearchViewModel
import com.maksimowiczm.foodyou.feature.shared.ui.FoodListItemSkeleton
import com.maksimowiczm.foodyou.shared.barcodescanner.FullScreenCameraBarcodeScanner
import com.maksimowiczm.foodyou.shared.ui.ext.add
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import foodyou.app.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodSearchApp(
    onFoodClick: (FoodSearch, Measurement) -> Unit,
    onUpdateUsdaApiKey: () -> Unit,
    modifier: Modifier = Modifier,
    excludedRecipe: FoodId.Recipe? = null,
) {
    val viewModel: FoodSearchViewModel = koinViewModel { parametersOf(excludedRecipe) }

    FoodSearchApp(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onSearch = viewModel::search,
        onSourceChange = viewModel::changeSource,
        onFoodClick = onFoodClick,
        onUpdateUsdaApiKey = onUpdateUsdaApiKey,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun FoodSearchApp(
    uiState: FoodSearchUiState,
    onSearch: (String?) -> Unit,
    onSourceChange: (FoodFilter.Source) -> Unit,
    onFoodClick: (FoodSearch, Measurement) -> Unit,
    onUpdateUsdaApiKey: () -> Unit,
    modifier: Modifier = Modifier,
    appState: FoodSearchAppState = rememberFoodSearchAppState(),
) {
    val coroutineScope = rememberCoroutineScope()
    val onSearch: (String?) -> Unit =
        remember(onSearch, appState, coroutineScope) {
            { query ->
                appState.searchTextFieldState.setTextAndPlaceCursorAtEnd(query ?: "")
                onSearch(query)
                coroutineScope.launch { appState.searchBarState.animateToCollapsed() }
            }
        }

    val pages = uiState.currentSourceState?.collectAsLazyPagingItems()
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
            )
        }

    FoodSearchView(
        appState = appState,
        uiState = uiState,
        onFill = { search -> appState.searchTextFieldState.setTextAndPlaceCursorAtEnd(search) },
        onSearch = onSearch,
        onSource = onSourceChange,
        inputField = searchInputField,
    )

    Scaffold(modifier) { paddingValues ->
        // Fix for searchbar issues on Android SDK 27 and below
        Box(Modifier.focusable().size(1.dp))

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
                    .onSizeChanged { topContentHeight = it.height }
                    .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SearchBar(
                state = appState.searchBarState,
                inputField = searchInputField,
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                colors =
                    SearchBarDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    ),
                shadowElevation = 2.dp,
            )

            if (uiState.sources.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                FoodSearchFilters(
                    uiState = uiState,
                    onSource = onSourceChange,
                    modifier = Modifier.height(32.dp + 8.dp + 32.dp).fillMaxWidth(),
                )
            }

            val error = pages?.loadState?.error as? RemoteFoodException

            when (val ex = error) {
                null -> Unit
                else ->
                    FoodSearchErrorCard(
                        error = ex,
                        onRetry = pages::retry,
                        onUsdaApiKey = onUpdateUsdaApiKey,
                        modifier =
                            Modifier.fillMaxWidth().padding(top = 8.dp).padding(horizontal = 16.dp),
                    )
            }
        }

        val paddingValues =
            paddingValues.add(
                top = LocalDensity.current.run { topContentHeight.toDp() },
                bottom = 56.dp + 32.dp,
            )

        Box(Modifier.fillMaxSize().zIndex(20f)) {
            if (pages?.itemCount == 0 && pages.loadState.append !is LoadState.Loading) {
                Text(
                    text = stringResource(Res.string.neutral_no_food_found),
                    modifier = Modifier.safeContentPadding().align(Alignment.Center),
                )
            }

            if (pages?.delayedLoadingState() == true) {
                ContainedLoadingIndicator(
                    modifier =
                        Modifier.align(Alignment.TopCenter)
                            .padding(top = paddingValues.calculateTopPadding())
                )
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = paddingValues) {
            if (pages != null) {
                items(
                    count = pages.itemCount,
                    key = pages.itemKey { (it.id to uiState.filter.source).toString() },
                ) { i ->
                    val food = pages[i]

                    when (food) {
                        null -> FoodListItemSkeleton(shimmer)
                        is FoodSearch.Product -> {
                            val measurement = food.defaultMeasurement
                            FoodSearchListItem(
                                food = food,
                                measurement = measurement,
                                onClick = { onFoodClick(food, measurement) },
                            )
                        }

                        is FoodSearch.Recipe -> {
                            val measurement = food.defaultMeasurement
                            FoodSearchListItem(
                                food = food,
                                measurement = measurement,
                                onClick = { onFoodClick(food, measurement) },
                                shimmer = shimmer,
                            )
                        }
                    }
                }

                if (pages.loadState.append is LoadState.Loading) {
                    items(10) { FoodListItemSkeleton(shimmer) }
                }
            }

            if (pages == null) {
                items(10) { FoodListItemSkeleton(shimmer) }
            }
        }
    }
}

object FoodSearchAppDefaults {

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun FloatingActionButton(
        fabExpanded: Boolean,
        onFabExpandedChange: (Boolean) -> Unit,
        onCreateRecipe: () -> Unit,
        onCreateProduct: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        FloatingActionButtonMenu(
            expanded = fabExpanded,
            modifier = modifier,
            button = {
                ToggleFloatingActionButton(
                    checked = fabExpanded,
                    onCheckedChange = onFabExpandedChange,
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
