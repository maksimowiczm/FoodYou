package com.maksimowiczm.foodyou.feature.food.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.animateFloatingActionButton
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
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.error
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.preferences.getBlocking
import com.maksimowiczm.foodyou.core.preferences.setBlocking
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.core.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.BackHandler
import com.maksimowiczm.foodyou.core.ui.ext.add
import com.maksimowiczm.foodyou.feature.barcodescanner.FullScreenCameraBarcodeScanner
import com.maksimowiczm.foodyou.feature.food.domain.FoodSearch
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource
import com.maksimowiczm.foodyou.feature.food.preferences.UseOpenFoodFacts
import com.maksimowiczm.foodyou.feature.food.ui.FoodListItemSkeleton
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import foodyou.app.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
// Search bar focus fix causes this
@Composable
@Suppress("ktlint:compose:multiple-emitters-check")
internal fun FoodSearchApp(
    onFoodClick: (FoodSearch, Measurement) -> Unit,
    onCreateProduct: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    viewModel: FoodSearchViewModel = koinViewModel(),
    useOpenFoodFactsPreference: UseOpenFoodFacts = userPreference()
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val pages = viewModel.pages.collectAsLazyPagingItems()
    val source = viewModel.source.collectAsStateWithLifecycle().value

    // No sure if this is good idea, but it stops scroll from glitching when switching between sources
    LaunchedEffect(source) {
        listState.stopScroll()
    }

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

    Box(Modifier.focusable().size(1.dp))
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

    BackHandler(
        enabled = source == FoodSource.Type.OpenFoodFacts
    ) {
        viewModel.setSource(FoodSource.Type.User)
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateProduct,
                modifier = Modifier.animateFloatingActionButton(
                    visible = !animatedVisibilityScope.transition.isRunning,
                    alignment = Alignment.BottomEnd
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null
                )
            }
        }
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

            when (source) {
                FoodSource.Type.User -> {
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
                                OpenFoodFactsState.Loading -> viewModel.setSource(
                                    FoodSource.Type.OpenFoodFacts
                                )

                                OpenFoodFactsState.PrivacyPolicyRequested ->
                                    showOpenFoodFactsPrivacyDialog = true
                            }
                        },
                        state = openFoodFactsState,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                FoodSource.Type.OpenFoodFacts -> BrowsingOpenFoodFactsCard(
                    onClose = {
                        viewModel.setSource(FoodSource.Type.User)
                    },
                    modifier = Modifier
                        .padding(
                            top = contentPadding.calculateTopPadding()
                        )
                        .padding(horizontal = 16.dp)
                        .zIndex(10f)
                )

                // TODO
                FoodSource.Type.USDA -> Unit
            }

            val error = pages.loadState.error
            if (pages.loadState.hasError) {
                ErrorCard(
                    message = error?.message ?: stringResource(Res.string.error_unknown_error),
                    onRetry = pages::retry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }

        SearchList(
            pages = pages,
            onFoodClick = onFoodClick,
            contentPadding = contentPadding.add(
                top = LocalDensity.current.run { searchBarHeight.toDp() },
                bottom = 56.dp + 32.dp // FAB
            ),
            listState = listState,
            modifier = Modifier.fillMaxSize()
        )
    }
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

@Composable
private fun ErrorCard(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    var showDetails by rememberSaveable { mutableStateOf(false) }

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {
            Text(
                text = stringResource(Res.string.neutral_an_error_occurred),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.neutral_open_food_facts_error),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                TextButton(
                    onClick = {
                        showDetails = !showDetails
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text(stringResource(Res.string.action_show_details))
                }

                FilledTonalButton(
                    onClick = {
                        onRetry()
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        contentColor = MaterialTheme.colorScheme.errorContainer,
                        containerColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text(stringResource(Res.string.action_retry))
                }
            }
            Spacer(Modifier.height(8.dp))

            AnimatedVisibility(showDetails) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SearchList(
    pages: LazyPagingItems<FoodSearch>,
    onFoodClick: (FoodSearch, Measurement) -> Unit,
    contentPadding: PaddingValues,
    listState: LazyListState,
    modifier: Modifier = Modifier
) = Box(modifier) {
    if (pages.itemCount == 0) {
        Text(
            text = stringResource(Res.string.neutral_no_food_found),
            modifier = Modifier
                .safeContentPadding()
                .align(Alignment.Center)
        )
    }

    if (pages.loadState.refresh is LoadState.Loading ||
        pages.loadState.append is LoadState.Loading
    ) {
        ContainedLoadingIndicator(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = contentPadding.calculateTopPadding())
                .zIndex(20f)
        )
    }

    val shimmer = rememberShimmer(ShimmerBounds.View)
    LazyColumn(
        state = listState,
        contentPadding = contentPadding
    ) {
        if (pages.loadState.refresh is LoadState.Loading) {
            items(10) {
                FoodListItemSkeleton(shimmer)
            }
        }

        items(
            count = pages.itemCount,
            key = pages.itemKey { it.id.toString() }
        ) { i ->
            val food = pages[i]

            if (food == null) {
                FoodListItemSkeleton(shimmer)
            } else {
                val measurement = food.defaultMeasurement

                FoodSearchListItem(
                    food = food,
                    measurement = measurement,
                    onClick = { onFoodClick(food, measurement) }
                )
            }
        }

        if (pages.loadState.append is LoadState.Loading) {
            items(3) {
                FoodListItemSkeleton(shimmer)
            }
        }
    }
}
