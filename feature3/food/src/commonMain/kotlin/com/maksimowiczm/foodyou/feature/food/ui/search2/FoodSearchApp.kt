package com.maksimowiczm.foodyou.feature.food.ui.search2

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.core.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.feature.barcodescanner.FullScreenCameraBarcodeScanner
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.FoodSearch
import com.maksimowiczm.foodyou.feature.food.preferences.UseOpenFoodFacts
import com.maksimowiczm.foodyou.feature.food.preferences.UseUSDA
import com.maksimowiczm.foodyou.feature.food.ui.search.FoodSearchView
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import foodyou.app.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FoodSearchApp(
    onFoodClick: (FoodSearch, Measurement) -> Unit,
    excludedFood: FoodId.Recipe?,
    modifier: Modifier = Modifier
) {
    val viewModel: FoodSearchViewModel = koinViewModel {
        parametersOf(excludedFood)
    }
    val useOpenFoodFactsPreference = userPreference<UseOpenFoodFacts>()
    val useUSDAPreference = userPreference<UseUSDA>()
    val coroutineScope = rememberCoroutineScope()

    val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()
    val pages = viewModel.pages.collectAsLazyPagingItems()
    val useOpenFoodFacts = useOpenFoodFactsPreference.collectAsStateWithLifecycle(false).value
    val useUSDA = useUSDAPreference.collectAsStateWithLifecycle(false).value

    val searchState = rememberSearchBarState()
    val searchTextFieldState = rememberTextFieldState()
    var showBarcodeScanner by rememberSaveable { mutableStateOf(false) }

    val searchInputField = @Composable {
        FoodSearchBarInputField(
            searchBarState = searchState,
            textFieldState = searchTextFieldState,
            filter = filter,
            onSearch = {
                searchTextFieldState.setTextAndPlaceCursorAtEnd(it ?: "")
                viewModel.search(it)
                coroutineScope.launch {
                    searchState.animateToCollapsed()
                }
            },
            onBarcodeScanner = { showBarcodeScanner = true }
        )
    }

    FoodSearchView(
        availableSources = listOfNotNull(
            FoodFilter.Source.YourFood,
            if (useOpenFoodFacts) FoodFilter.Source.OpenFoodFacts else null,
            if (useUSDA) FoodFilter.Source.USDA else null
        ),
        searchState = searchState,
        filter = filter,
        recentSearches = recentSearches,
        onFill = { searchTextFieldState.setTextAndPlaceCursorAtEnd(it) },
        onSearch = {
            searchTextFieldState.setTextAndPlaceCursorAtEnd(it ?: "")
            viewModel.search(it)
            coroutineScope.launch {
                searchState.animateToCollapsed()
            }
        },
        onSource = viewModel::setSource,
        inputField = searchInputField
    )

    FullScreenCameraBarcodeScanner(
        visible = showBarcodeScanner,
        onBarcodeScan = {
            viewModel.search(it)
            searchTextFieldState.setTextAndPlaceCursorAtEnd(it)
            showBarcodeScanner = false
            coroutineScope.launch {
                searchState.animateToCollapsed()
            }
        },
        onClose = { showBarcodeScanner = false }
    )

    FoodSearchApp(
        searchState = searchState,
        pages = pages,
        inputField = searchInputField,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FoodSearchBarInputField(
    searchBarState: SearchBarState,
    textFieldState: TextFieldState,
    filter: FoodFilter,
    onSearch: (String?) -> Unit,
    onBarcodeScanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    SearchBarDefaults.InputField(
        textFieldState = textFieldState,
        searchBarState = searchBarState,
        onSearch = onSearch,
        modifier = modifier,
        placeholder = { Text(stringResource(Res.string.action_search)) },
        leadingIcon = {
            if (searchBarState.targetValue == SearchBarValue.Expanded) {
                ArrowBackIconButton(
                    onClick = {
                        coroutineScope.launch {
                            searchBarState.animateToCollapsed()
                        }
                    }
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null
                )
            }
        },
        trailingIcon = {
            Row {
                if (textFieldState.text.isEmpty()) {
                    IconButton(
                        onClick = onBarcodeScanner
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_barcode_scanner),
                            contentDescription = stringResource(Res.string.action_scan_barcode)
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            textFieldState.setTextAndPlaceCursorAtEnd("")
                            if (searchBarState.targetValue == SearchBarValue.Collapsed) {
                                onSearch(null)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Clear,
                            contentDescription = stringResource(Res.string.action_clear)
                        )
                    }
                }
                IconButton(
                    onClick = {
                        // TODO
                    }
                ) {
                    BadgedBox(
                        badge = {
                            if (filter.filterCount > 0) {
                                Badge { Text(filter.filterCount.toString()) }
                            }
                        },
                        content = {
                            Icon(
                                imageVector = Icons.Outlined.FilterAlt,
                                contentDescription = stringResource(Res.string.action_filter_foods)
                            )
                        }
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FoodSearchView(
    availableSources: List<FoodFilter.Source>,
    searchState: SearchBarState,
    filter: FoodFilter,
    recentSearches: List<String>,
    onFill: (String) -> Unit,
    onSearch: (String?) -> Unit,
    onSource: (FoodFilter.Source) -> Unit,
    inputField: @Composable () -> Unit
) {
    ExpandedFullScreenSearchBar(
        state = searchState,
        inputField = inputField
    ) {
        FoodSearchView(
            availableSources = availableSources,
            source = filter.source,
            recentSearches = recentSearches,
            onSource = onSource,
            onFill = onFill,
            onSearch = onSearch
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FoodSearchApp(
    searchState: SearchBarState,
    pages: LazyPagingItems<FoodSearch>,
    inputField: @Composable () -> Unit,
    modifier: Modifier = Modifier
) = Scaffold(modifier) { paddingValues ->
    Box(Modifier.focusable().size(1.dp))

    val layoutDirection = LocalLayoutDirection.current
    val contentPadding = PaddingValues(
        start = paddingValues.calculateStartPadding(layoutDirection),
        end = paddingValues.calculateEndPadding(layoutDirection),
        bottom = paddingValues.calculateBottomPadding()
    )

    var searchBarHeight by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(10f)
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
            .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
            .onSizeChanged { searchBarHeight = it.height }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SearchBar(
            state = searchState,
            inputField = inputField,
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            colors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
            ),
            shadowElevation = 2.dp
        )
    }
}
