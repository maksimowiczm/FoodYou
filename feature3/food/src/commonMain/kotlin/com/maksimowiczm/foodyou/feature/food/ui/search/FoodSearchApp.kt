package com.maksimowiczm.foodyou.feature.food.ui.search

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.preferences.getBlocking
import com.maksimowiczm.foodyou.core.preferences.setBlocking
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.core.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.ext.add
import com.maksimowiczm.foodyou.feature.barcodescanner.FullScreenCameraBarcodeScanner
import com.maksimowiczm.foodyou.feature.food.domain.FoodSearch
import com.maksimowiczm.foodyou.feature.food.preferences.UseOpenFoodFacts
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.data.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import foodyou.app.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FoodSearchApp(
    onFoodClick: (FoodSearch, Measurement) -> Unit,
    onCreateProduct: () -> Unit,
    onOpenFoodFactsProduct: (OpenFoodFactsProduct) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    viewModel: FoodSearchViewModel = koinViewModel(),
    useOpenFoodFactsPreference: UseOpenFoodFacts = userPreference()
) {
    val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()
    val backstack = navController.currentBackStackEntryFlow
        .collectAsStateWithLifecycle(null).value
    val state = backstack?.destination?.route?.let(FoodSearchState::valueOf)

    val localPages = viewModel.localPages.collectAsLazyPagingItems()
    val useOpenFoodFacts = useOpenFoodFactsPreference
        .collectAsStateWithLifecycle(useOpenFoodFactsPreference.getBlocking()).value
    val openFoodFactsPages = viewModel.openFoodFactsPages.collectAsLazyPagingItems()
    val openFoodFactsCount = viewModel.openFoodFactsProductCount.collectAsStateWithLifecycle().value

    var showBarcodeScanner by rememberSaveable { mutableStateOf(false) }
    val searchState = rememberSearchBarState()
    val searchTextFieldState = rememberTextFieldState()
    val inputField = @Composable {
        SearchBarInputField(
            searchBarState = searchState,
            textFieldState = searchTextFieldState,
            onSearch = {
                viewModel.search(it)
                coroutineScope.launch {
                    searchState.animateToCollapsed()
                }
            },
            onBarcodeScanner = { showBarcodeScanner = true }
        )
    }

    ExpandedFullScreenSearchBar(
        state = searchState,
        inputField = inputField
    ) {
    }

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

    var showOpenFoodFactsPrivacyDialog by rememberSaveable { mutableStateOf(false) }
    if (showOpenFoodFactsPrivacyDialog) {
        OpenFoodFactsPrivacyDialog(
            onDismissRequest = { showOpenFoodFactsPrivacyDialog = false },
            onConfirm = {
                showOpenFoodFactsPrivacyDialog = false
                useOpenFoodFactsPreference.setBlocking(true)
            }
        )
    }

    Scaffold(
        modifier = modifier
    ) { paddingValues ->

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

            when (state) {
                FoodSearchState.Local -> {
                    val openFoodFactsState by remember(
                        useOpenFoodFacts,
                        openFoodFactsCount,
                        openFoodFactsPages.loadState
                    ) {
                        val loadState = openFoodFactsPages.loadState

                        derivedStateOf {
                            when {
                                !useOpenFoodFacts -> OpenFoodFactsState.PrivacyPolicyRequested
                                loadState.refresh is LoadState.Loading ||
                                    loadState.append is LoadState.Loading ||
                                    loadState.prepend is LoadState.Loading ->
                                    OpenFoodFactsState.Loading

                                loadState.hasError -> OpenFoodFactsState.Error
                                else -> OpenFoodFactsState.Loaded(openFoodFactsCount)
                            }
                        }
                    }

                    OpenFoodFactsCard(
                        onClick = {
                            when (openFoodFactsState) {
                                OpenFoodFactsState.Error,
                                is OpenFoodFactsState.Loaded,
                                OpenFoodFactsState.Loading -> navController.navigate(
                                    FoodSearchState.OpenFoodFacts.name
                                ) {
                                    launchSingleTop = true
                                }

                                OpenFoodFactsState.PrivacyPolicyRequested ->
                                    showOpenFoodFactsPrivacyDialog =
                                        true
                            }
                        },
                        state = openFoodFactsState,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                FoodSearchState.OpenFoodFacts -> BrowsingOpenFoodFactsCard(
                    onClose = {
                        navController.popBackStack(
                            route = FoodSearchState.OpenFoodFacts.name,
                            inclusive = true
                        )
                    },
                    modifier = Modifier
                        .padding(top = contentPadding.calculateTopPadding())
                        .padding(horizontal = 16.dp)
                        .zIndex(10f)
                )

                null -> Unit
            }
        }

        NavHost(
            navController = navController,
            startDestination = FoodSearchState.Local.name
        ) {
            forwardBackwardComposable(FoodSearchState.Local.name) {
                LocalSearchList(
                    pages = localPages,
                    onFoodClick = onFoodClick,
                    onCreateProduct = onCreateProduct,
                    contentPadding = contentPadding.add(
                        top = LocalDensity.current.run { searchBarHeight.toDp() }
                    ),
                    fabVisible =
                    !this.transition.isRunning && !animatedVisibilityScope.transition.isRunning,
                    modifier = Modifier.fillMaxSize()
                )
            }
            forwardBackwardComposable(FoodSearchState.OpenFoodFacts.name) {
                OpenFoodFactsSearchList(
                    pages = openFoodFactsPages,
                    contentPadding = contentPadding.add(
                        top = LocalDensity.current.run { searchBarHeight.toDp() }
                    ),
                    onProductClick = onOpenFoodFactsProduct,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

private enum class FoodSearchState(name: String) {
    Local("Local"),
    OpenFoodFacts("Open Food Facts")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarInputField(
    textFieldState: TextFieldState,
    searchBarState: SearchBarState,
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
        }
    )
}
