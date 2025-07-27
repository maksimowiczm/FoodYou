package com.maksimowiczm.foodyou.feature.food.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.NorthWest
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.error
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.core.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.ext.add
import com.maksimowiczm.foodyou.feature.barcodescanner.FullScreenCameraBarcodeScanner
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.FoodSearch
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
    modifier: Modifier = Modifier
) {
    val viewModel: FoodSearchViewModel = koinViewModel {
        parametersOf(excludedFood)
    }
    val coroutineScope = rememberCoroutineScope()

    val uiState = viewModel.state.collectAsStateWithLifecycle().value

    if (uiState == null) {
        return
    }

    val appState = rememberFoodSearchAppState()

    FoodSearchApp(
        appState = appState,
        uiState = uiState,
        onSearch = { query ->
            appState.searchTextFieldState.setTextAndPlaceCursorAtEnd(query ?: "")
            viewModel.search(query)
            coroutineScope.launch {
                appState.searchBarState.animateToCollapsed()
            }
        },
        onSourceChange = viewModel::changeSource,
        onFoodClick = onFoodClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun FoodSearchApp(
    appState: FoodSearchAppState,
    uiState: FoodSearchUiState,
    onSearch: (String?) -> Unit,
    onSourceChange: (FoodFilter.Source) -> Unit,
    onFoodClick: (FoodSearch, Measurement) -> Unit,
    modifier: Modifier = Modifier
) {
    val pages = uiState.currentSourceState.collectAsLazyPagingItems()
    val shimmer = rememberShimmer(ShimmerBounds.View)

    FullScreenCameraBarcodeScanner(
        visible = appState.showBarcodeScanner,
        onBarcodeScan = onSearch,
        onClose = { appState.showBarcodeScanner = false }
    )

    val searchInputField = @Composable {
        FoodSearchBarInputField(
            searchBarState = appState.searchBarState,
            textFieldState = appState.searchTextFieldState,
            onSearch = onSearch,
            onBarcodeScanner = { appState.showBarcodeScanner = true }
        )
    }

    FoodSearchView(
        appState = appState,
        uiState = uiState,
        onFill = { search ->
            appState.searchTextFieldState.setTextAndPlaceCursorAtEnd(search)
        },
        onSearch = onSearch,
        onSource = onSourceChange,
        inputField = searchInputField
    )

    Scaffold(modifier) { paddingValues ->
        // Fix for searchbar issues on Android SDK 27 and below
        Box(Modifier.focusable().size(1.dp))

        var topContentHeight by remember { mutableIntStateOf(0) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(10f)
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
                .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                .padding(top = paddingValues.calculateTopPadding())
                .onSizeChanged { topContentHeight = it.height }
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SearchBar(
                state = appState.searchBarState,
                inputField = searchInputField,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                colors = SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                ),
                shadowElevation = 2.dp
            )

            FoodSearchFilters(
                uiState = uiState,
                onSource = onSourceChange,
                modifier = Modifier
                    .height(32.dp + 8.dp + 32.dp)
                    .fillMaxWidth()
            )

            when (val ex = pages.loadState.error) {
                null -> Unit

                is USDAException -> UsdaErrorCard(
                    error = ex,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                else -> ErrorCard(
                    message = ex.message ?: stringResource(Res.string.error_unknown_error),
                    onRetry = pages::retry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }

        val paddingValues = paddingValues.add(
            top = LocalDensity.current.run { topContentHeight.toDp() },
            bottom = 56.dp + 32.dp
        )

        Box(Modifier.fillMaxSize().zIndex(20f)) {
            if (pages.itemCount == 0 && pages.loadState.append !is LoadState.Loading) {
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
                        .padding(top = paddingValues.calculateTopPadding())
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues
        ) {
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
                items(10) {
                    FoodListItemSkeleton(shimmer)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FoodSearchBarInputField(
    searchBarState: SearchBarState,
    textFieldState: TextFieldState,
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
                    IconButton(onBarcodeScanner) {
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
        }
    )
}

@Composable
private fun FoodSearchFilters(
    uiState: FoodSearchUiState,
    onSource: (FoodFilter.Source) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp)
) {
    val filters = uiState.sources.filterValues { state ->
        state.shouldShowFilter
    }

    LazyHorizontalStaggeredGrid(
        rows = StaggeredGridCells.Fixed(2),
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalItemSpacing = 8.dp
    ) {
        items(filters.toList()) { (source, state) ->
            val pages = state.collectAsLazyPagingItems()
            val isLoading = pages.delayedLoadingState()
            val hasError = pages.loadState.hasError
            val selected = uiState.filter.source == source

            val colors = if (hasError) {
                FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    labelColor = MaterialTheme.colorScheme.onErrorContainer,
                    iconColor = MaterialTheme.colorScheme.onErrorContainer,
                    selectedContainerColor = MaterialTheme.colorScheme.error,
                    selectedLabelColor = MaterialTheme.colorScheme.onError,
                    selectedTrailingIconColor = MaterialTheme.colorScheme.onError
                )
            } else {
                FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            }

            val border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = selected,
                borderColor = if (hasError || selected) {
                    Color.Transparent
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                }
            )

            FilterChip(
                selected = selected,
                onClick = { onSource(source) },
                label = {
                    Text(source.stringResource())
                },
                leadingIcon = {
                    source.Icon(Modifier.size(FilterChipDefaults.IconSize))
                },
                trailingIcon = {
                    if (hasError) {
                        Icon(
                            imageVector = Icons.Outlined.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    } else {
                        if (state.remoteEnabled != RemoteStatus.LocalOnly && isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(FilterChipDefaults.IconSize),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = state.count.toString(),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                colors = colors,
                border = border
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FoodSearchView(
    appState: FoodSearchAppState,
    uiState: FoodSearchUiState,
    onFill: (String) -> Unit,
    onSearch: (String?) -> Unit,
    onSource: (FoodFilter.Source) -> Unit,
    inputField: @Composable () -> Unit
) {
    val filters = uiState.sources.filterValues { state ->
        state.shouldShowFilter
    }

    ExpandedFullScreenSearchBar(
        state = appState.searchBarState,
        inputField = inputField
    ) {
        LazyColumn {
            items(uiState.recentSearches.take(3)) {
                FoodSearchItem(
                    search = it,
                    onFill = { onFill(it) },
                    modifier = Modifier.clickable { onSearch(it) }
                )
            }

            item {
                if (uiState.recentSearches.isNotEmpty()) {
                    HorizontalDivider()
                }

                Column(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.headline_filters),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        items(filters.toList()) { (source, _) ->
                            DatabaseFilterIconButton(
                                selected = uiState.filter.source == source,
                                onClick = { onSource(source) },
                                logo = { source.Icon() },
                                label = {
                                    Text(
                                        text = source.stringResource(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            )
                        }
                    }
                }

                HorizontalDivider()
            }

            items(uiState.recentSearches.drop(3)) {
                FoodSearchItem(
                    search = it,
                    onFill = { onFill(it) },
                    modifier = Modifier.clickable { onSearch(it) }
                )
            }
        }
    }
}

@Composable
private fun FoodSearchItem(search: String, onFill: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        modifier = modifier,
        headlineContent = { Text(search) },
        leadingContent = {
            Icon(
                imageVector = Icons.Outlined.History,
                contentDescription = null
            )
        },
        trailingContent = {
            IconButton(onFill) {
                Icon(
                    imageVector = Icons.Outlined.NorthWest,
                    contentDescription = stringResource(Res.string.action_insert_suggested_search)
                )
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
private fun DatabaseFilterIconButton(
    selected: Boolean,
    onClick: () -> Unit,
    logo: @Composable () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    val containerColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    )

    val labelColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    )

    Column(
        modifier = modifier
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = interactionSource
            )
            .widthIn(
                min = 64.dp,
                max = 96.dp
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            onClick = onClick,
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = containerColor,
            contentColor = contentColor,
            interactionSource = interactionSource
        ) {
            Box(
                modifier = Modifier.padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                logo()
            }
        }
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.labelMedium,
            LocalContentColor provides labelColor
        ) {
            label()
        }
    }
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

@Composable
@OptIn(FlowPreview::class)
private fun <T : Any> LazyPagingItems<T>.delayedLoadingState(timeout: Long = 100L): Boolean {
    var isLoading by remember { mutableStateOf(false) }
    LaunchedEffect(this) {
        snapshotFlow {
            loadState.refresh is LoadState.Loading || loadState.append is LoadState.Loading
        }
            .debounce(timeout)
            .collectLatest { isLoading = it }
    }

    return isLoading
}
