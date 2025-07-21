package com.maksimowiczm.foodyou.feature.food.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
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
import com.maksimowiczm.foodyou.core.ui.ext.add
import com.maksimowiczm.foodyou.feature.barcodescanner.FullScreenCameraBarcodeScanner
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.FoodSearch
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource
import com.maksimowiczm.foodyou.feature.food.preferences.UseOpenFoodFacts
import com.maksimowiczm.foodyou.feature.food.preferences.UseUSDA
import com.maksimowiczm.foodyou.feature.food.ui.FoodListItemSkeleton
import com.maksimowiczm.foodyou.feature.food.ui.UsdaErrorCard
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import com.maksimowiczm.foodyou.feature.usda.USDAException
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import foodyou.app.generated.resources.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
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
    modifier: Modifier = Modifier,
    viewModel: FoodSearchViewModel = koinViewModel {
        parametersOf(excludedFood)
    },
    useOpenFoodFactsPreference: UseOpenFoodFacts = userPreference(),
    useUSDAPreference: UseUSDA = userPreference()
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val pages = viewModel.pages.collectAsLazyPagingItems()
    val source = viewModel.source.collectAsStateWithLifecycle().value

    // Not sure if this is good idea, but it stops scroll from glitching when switching between sources
    LaunchedEffect(source) {
        listState.stopScroll()
    }

    val useOpenFoodFacts = useOpenFoodFactsPreference
        .collectAsStateWithLifecycle(useOpenFoodFactsPreference.getBlocking()).value
    val openFoodFactsPages = viewModel.openFoodFactsPages.collectAsLazyPagingItems()
    val openFoodFactsCount = viewModel.openFoodFactsFoodCount.collectAsStateWithLifecycle().value
    LaunchedEffect(useOpenFoodFacts, source) {
        if (!useOpenFoodFacts && source == FoodSource.Type.OpenFoodFacts) {
            viewModel.setSource(FoodSource.Type.User)
        }
    }

    // val localPages = viewModel.localPages.collectAsLazyPagingItems()
    val localCount = viewModel.localFoodCount.collectAsStateWithLifecycle().value

    val useUSDA =
        useUSDAPreference.collectAsStateWithLifecycle(useUSDAPreference.getBlocking()).value
    val usdaPages = viewModel.usdaPages.collectAsLazyPagingItems()
    val usdaCount = viewModel.usdaFoodCount.collectAsStateWithLifecycle().value
    LaunchedEffect(useUSDA, source) {
        if (!useUSDA && source == FoodSource.Type.USDA) {
            viewModel.setSource(FoodSource.Type.User)
        }
    }

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

    // Observe even when not expanded, it's small and user won't have to wait for it
    val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()
    ExpandedFullScreenSearchBar(
        state = searchState,
        inputField = inputField
    ) {
        FoodSearchView(
            availableSources = listOfNotNull(
                FoodSource.Type.User,
                if (useOpenFoodFacts) FoodSource.Type.OpenFoodFacts else null,
                if (useUSDA) FoodSource.Type.USDA else null
            ),
            source = source,
            recentSearches = recentSearches,
            onSource = viewModel::setSource,
            onFill = { searchTextFieldState.setTextAndPlaceCursorAtEnd(it) },
            onSearch = {
                viewModel.search(it)
                searchTextFieldState.setTextAndPlaceCursorAtEnd(it)
                coroutineScope.launch {
                    searchState.animateToCollapsed()
                }
            }
        )
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

    var showUSDAPrivacyDialog by rememberSaveable { mutableStateOf(false) }
    if (showUSDAPrivacyDialog) {
        USDAPrivacyDialog(
            onDismissRequest = { showUSDAPrivacyDialog = false },
            onConfirm = {
                showUSDAPrivacyDialog = false
                useUSDAPreference.setBlocking(true)
            }
        )
    }

    val filters = @Composable {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DatabaseFilterChip(
                state = remember(localCount) {
                    DatabaseFilterChipState.Loaded(localCount)
                },
                selected = source == FoodSource.Type.User,
                onClick = { viewModel.setSource(FoodSource.Type.User) },
                logo = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        modifier = Modifier.height(18.dp)
                    )
                },
                label = { Text(stringResource(Res.string.headline_your_food)) }
            )

            val offIsLoading = openFoodFactsPages.delayedLoadingState()
            val openFoodFactsError = openFoodFactsPages.loadState.error
            DatabaseFilterChip(
                state = remember(
                    openFoodFactsCount,
                    useOpenFoodFacts,
                    offIsLoading,
                    openFoodFactsError
                ) {
                    if (openFoodFactsError != null) {
                        DatabaseFilterChipState.Error
                    } else if (!useOpenFoodFacts) {
                        DatabaseFilterChipState.ActionRequired
                    } else if (offIsLoading) {
                        DatabaseFilterChipState.Loading
                    } else {
                        DatabaseFilterChipState.Loaded(openFoodFactsCount)
                    }
                },
                selected = source == FoodSource.Type.OpenFoodFacts,
                onClick = {
                    if (useOpenFoodFacts) {
                        viewModel.setSource(FoodSource.Type.OpenFoodFacts)
                    } else {
                        showOpenFoodFactsPrivacyDialog = true
                    }
                },
                logo = {
                    Image(
                        painter = painterResource(Res.drawable.openfoodfacts_logo),
                        contentDescription = null,
                        modifier = Modifier.height(DatabaseFilterChipDefaults.logoSize)
                    )
                },
                label = { Text(stringResource(Res.string.headline_open_food_facts)) }
            )

            val usdaIsLoading = usdaPages.delayedLoadingState()
            val usdaError = usdaPages.loadState.error
            DatabaseFilterChip(
                state = remember(usdaCount, useUSDA, usdaIsLoading, usdaError) {
                    if (usdaError != null) {
                        DatabaseFilterChipState.Error
                    } else if (!useUSDA) {
                        DatabaseFilterChipState.ActionRequired
                    } else if (usdaIsLoading) {
                        DatabaseFilterChipState.Loading
                    } else {
                        DatabaseFilterChipState.Loaded(usdaCount)
                    }
                },
                selected = source == FoodSource.Type.USDA,
                onClick = {
                    if (useUSDA) {
                        viewModel.setSource(FoodSource.Type.USDA)
                    } else {
                        showUSDAPrivacyDialog = true
                    }
                },
                logo = {
                    Image(
                        painter = painterResource(Res.drawable.usda_logo),
                        contentDescription = null,
                        modifier = Modifier.height(DatabaseFilterChipDefaults.logoSize)
                    )
                },
                label = { Text(stringResource(Res.string.headline_food_data_central_usda)) }
            )
        }
    }

    Scaffold(modifier) { paddingValues ->

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

            val error = pages.loadState.error

            when (val ex = pages.loadState.error) {
                null -> Unit
                is USDAException -> UsdaErrorCard(
                    error = ex,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                else -> ErrorCard(
                    message = error?.message ?: stringResource(Res.string.error_unknown_error),
                    onRetry = pages::retry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            filters()
        }

        SearchList(
            pages = pages,
            onFoodClick = onFoodClick,
            contentPadding = contentPadding.add(
                top = LocalDensity.current.run { searchBarHeight.toDp() },
                bottom = 56.dp + 32.dp // FAB
            ),
            listState = listState,
            excludes = excludedFood != null,
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
                text = stringResource(Res.string.neutral_remote_database_error),
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class, FlowPreview::class)
@Composable
private fun SearchList(
    pages: LazyPagingItems<FoodSearch>,
    onFoodClick: (FoodSearch, Measurement) -> Unit,
    contentPadding: PaddingValues,
    listState: LazyListState,
    excludes: Boolean,
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

    if (pages.delayedLoadingState()) {
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
        if (excludes) {
            item {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(
                                Res.string.description_recipe_missing_ingredients
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

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

            when (food) {
                null -> FoodListItemSkeleton(shimmer)
                is FoodSearch.Product -> {
                    val measurement = food.defaultMeasurement
                    FoodSearchListItem(
                        food = food,
                        measurement = measurement,
                        onClick = { onFoodClick(food, measurement) }
                    )
                }

                is FoodSearch.Recipe -> {
                    val measurement = food.defaultMeasurement

                    FoodSearchListItem(
                        food = food,
                        measurement = measurement,
                        onClick = { onFoodClick(food, measurement) },
                        shimmer = shimmer
                    )
                }
            }
        }

        if (pages.loadState.append is LoadState.Loading) {
            items(3) {
                FoodListItemSkeleton(shimmer)
            }
        }
    }
}

@Composable
@OptIn(FlowPreview::class)
private fun <T : Any> LazyPagingItems<T>.delayedLoadingState(): Boolean {
    var isLoading by remember { mutableStateOf(false) }
    LaunchedEffect(this) {
        snapshotFlow {
            loadState.refresh is LoadState.Loading || loadState.append is LoadState.Loading
        }
            .debounce(100L)
            .collectLatest { isLoading = it }
    }

    return isLoading
}
