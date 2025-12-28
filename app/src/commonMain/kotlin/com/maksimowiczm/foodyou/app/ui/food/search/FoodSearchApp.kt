package com.maksimowiczm.foodyou.app.ui.food.search

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
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.app.ui.common.component.FoodListItemSkeleton
import com.maksimowiczm.foodyou.app.ui.common.component.FullScreenCameraBarcodeScanner
import com.maksimowiczm.foodyou.common.compose.extension.add
import com.maksimowiczm.foodyou.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.common.extension.error
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.food.domain.entity.RemoteFoodException
import com.maksimowiczm.foodyou.food.search.domain.FoodSearch
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import foodyou.app.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun FoodSearchApp(
    onFoodClick: (FoodSearch, Measurement) -> Unit,
    modifier: Modifier = Modifier,
    excludedRecipe: FoodId.Recipe? = null,
) {
    val viewModel: FoodSearchViewModel = koinViewModel { parametersOf(excludedRecipe) }

    FoodSearchApp(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onSearch = viewModel::search,
        onSourceChange = viewModel::changeSource,
        onFoodClick = onFoodClick,
        modifier = modifier,
    )
}

@Composable
private fun FoodSearchApp(
    uiState: FoodSearchUiState,
    onSearch: (String?) -> Unit,
    onSourceChange: (FoodFilter.Source) -> Unit,
    onFoodClick: (FoodSearch, Measurement) -> Unit,
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
                    onSource = {
                        onSourceChange(it)

                        if (it == uiState.filter.source) {
                            val listState = appState.listStates.state(it)
                            coroutineScope.launch { listState.animateScrollToItem(0) }
                        }
                    },
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

        if (pages?.itemCount == 0 && pages.loadState.append !is LoadState.Loading) {
            Box(Modifier.fillMaxSize()) {
                Text(
                    text = stringResource(Res.string.neutral_no_food_found),
                    modifier = Modifier.safeContentPadding().align(Alignment.Center),
                )
            }
        }

        if (pages?.delayedLoadingState() == true) {
            Box(Modifier.fillMaxSize().zIndex(20f)) {
                ContainedLoadingIndicator(
                    modifier =
                        Modifier.align(Alignment.TopCenter)
                            .padding(top = paddingValues.calculateTopPadding())
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues,
            state = appState.listStates.state(uiState.filter.source),
        ) {
            if (pages != null) {
                items(
                    count = pages.itemCount,
                    key = pages.itemKey { (it.id to uiState.filter.source).toString() },
                ) { i ->
                    val food = pages[i]

                    when (food) {
                        null -> FoodListItemSkeleton(shimmer)
                        is FoodSearch.Product -> {
                            val measurement = food.suggestedMeasurement
                            FoodSearchListItem(
                                food = food,
                                measurement = measurement,
                                onClick = { onFoodClick(food, measurement) },
                            )
                        }

                        is FoodSearch.Recipe -> {
                            val measurement = food.suggestedMeasurement
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

private fun ListStates.state(source: FoodFilter.Source) =
    when (source) {
        FoodFilter.Source.Recent -> recent
        FoodFilter.Source.YourFood -> yourFood
        FoodFilter.Source.TBCA -> tbca
    }
