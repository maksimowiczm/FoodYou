package com.maksimowiczm.foodyou.feature.addfood.ui.search

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.model.Product
import com.maksimowiczm.foodyou.core.model.SearchQuery
import com.maksimowiczm.foodyou.core.ui.component.FoodListItemSkeleton
import com.maksimowiczm.foodyou.core.ui.component.MeasurementSummary
import com.maksimowiczm.foodyou.core.ui.component.NutrientsRow
import com.maksimowiczm.foodyou.core.ui.component.ToggleButton
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.feature.addfood.domain.SearchFoodItem
import com.maksimowiczm.foodyou.feature.addfood.ui.component.ProductSearchBarSuggestions
import com.maksimowiczm.foodyou.feature.addfood.ui.component.SearchScreen
import com.maksimowiczm.foodyou.feature.openfoodfacts.OpenFoodFactsErrorCard
import com.maksimowiczm.foodyou.feature.openfoodfacts.OpenFoodFactsSearchHint
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import kotlin.math.roundToInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SearchFoodScreen(
    onBack: () -> Unit,
    onProductAdd: () -> Unit,
    onOpenFoodFactsSettings: () -> Unit,
    onFoodClick: (FoodId) -> Unit,
    onBarcodeScanner: () -> Unit,
    viewModel: SearchFoodViewModel,
    modifier: Modifier = Modifier,
    state: SearchFoodScreenState = rememberSearchFoodScreenState()
) {
    val pages = viewModel.pages.collectAsLazyPagingItems()
    val recentQueries by viewModel.recentQueries.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.searchQuery.collectLatest {
            when (it) {
                null -> state.textFieldState.clearText()
                else -> state.textFieldState.setTextAndPlaceCursorAtEnd(it)
            }
        }
    }

    SearchFoodScreen(
        pages = pages,
        recentQueries = recentQueries,
        onBack = onBack,
        onSearch = remember(viewModel) { viewModel::onSearch },
        onSearchClear = remember(viewModel) { { viewModel.onSearch(null) } },
        onBarcodeScanner = onBarcodeScanner,
        onProductAdd = onProductAdd,
        onOpenFoodFactsSettings = onOpenFoodFactsSettings,
        onFoodClick = onFoodClick,
        onFoodToggle = remember(viewModel) {
            { state, food ->
                when (state) {
                    true -> viewModel.onQuickAdd(food)
                    false -> viewModel.onQuickRemove(food)
                }
            }
        },
        modifier = modifier,
        state = state
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalAnimationApi::class
)
@Composable
private fun SearchFoodScreen(
    pages: LazyPagingItems<SearchFoodItem>,
    recentQueries: List<SearchQuery>,
    onBack: () -> Unit,
    onSearch: (String) -> Unit,
    onSearchClear: () -> Unit,
    onBarcodeScanner: () -> Unit,
    onProductAdd: () -> Unit,
    onOpenFoodFactsSettings: () -> Unit,
    onFoodClick: (FoodId) -> Unit,
    onFoodToggle: (Boolean, SearchFoodItem) -> Unit,
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    state: SearchFoodScreenState = rememberSearchFoodScreenState(),
    shimmer: Shimmer = rememberShimmer(ShimmerBounds.Window)
) {
    val isEmpty by remember(pages.loadState) {
        derivedStateOf { pages.itemCount == 0 }
    }

    val showEmptyLabel by remember(pages.loadState) {
        derivedStateOf {
            isEmpty &&
                pages.loadState.append !is LoadState.Loading &&
                pages.loadState.refresh !is LoadState.Loading
        }
    }

    val fab = @Composable {
        FloatingActionButton(
            onClick = onProductAdd
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(Res.string.action_add_food)
            )
        }
    }

    val fullScreenContent = @Composable {
        ProductSearchBarSuggestions(
            recentQueries = recentQueries.map { it.query },
            onSearch = {
                onSearch(it)
                state.textFieldState.setTextAndPlaceCursorAtEnd(it)
                coroutineScope.launch {
                    state.searchBarState.animateToCollapsed()
                }
            },
            onFill = {
                state.textFieldState.setTextAndPlaceCursorAtEnd(it)
                onSearch(it)
            }
        )
    }

    Box(
        modifier = modifier
    ) {
        SearchScreen(
            pages = pages,
            onSearch = onSearch,
            onClear = onSearchClear,
            onBack = onBack,
            onBarcodeScanner = onBarcodeScanner,
            textFieldState = state.textFieldState,
            searchBarState = state.searchBarState,
            coroutineScope = coroutineScope,
            topBar = null,
            floatingActionButton = fab,
            fullScreenSearchBarContent = fullScreenContent,
            errorCard = {
                val error by remember(pages.loadState) {
                    derivedStateOf { pages.throwable }
                }

                error?.let {
                    OpenFoodFactsErrorCard(
                        throwable = it,
                        onRetry = remember(pages) { pages::refresh },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            },
            hintCard = {
                OpenFoodFactsSearchHint(
                    onGoToSettings = onOpenFoodFactsSettings,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        ) { paddingValues ->
            if (showEmptyLabel) {
                Text(
                    text = stringResource(Res.string.neutral_no_products_found),
                    modifier = Modifier
                        .safeContentPadding()
                        .align(Alignment.Center)
                )
            }

            LazyColumn(
                contentPadding = paddingValues,
                state = state.lazyListState
            ) {
                if (!showEmptyLabel && pages.loadState.refresh == LoadState.Loading) {
                    items(
                        count = 100,
                        key = { "skeleton-refresh-$it" }
                    ) {
                        ListItemSkeleton(shimmer)
                    }
                }

                items(
                    count = pages.itemCount,
                    key = pages.itemKey { it.uniqueId }
                ) {
                    val item = pages[it]
                    val transition = updateTransition(item)

                    transition.Crossfade(
                        contentKey = { it != null },
                        modifier = Modifier.animateItem(
                            fadeInSpec = null,
                            fadeOutSpec = null
                        )
                    ) { item ->
                        when (item) {
                            null -> ListItemSkeleton(shimmer)
                            else -> item.ListItem(
                                onToggle = { onFoodToggle(it, item) },
                                modifier = Modifier.clickable {
                                    onFoodClick(item.food.id)
                                }
                            )
                        }
                    }
                }

                if (pages.loadState.append == LoadState.Loading) {
                    items(
                        count = 3,
                        key = { "skeleton-append-$it" }
                    ) {
                        ListItemSkeleton(shimmer)
                    }
                }

                // FAB spacer
                item {
                    Spacer(Modifier.height(8.dp))
                    Spacer(Modifier.height(56.dp))
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SearchFoodItem.ListItem(onToggle: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val weight = weight
    val measurementString = measurementString
    val caloriesString = caloriesString
    if (weight == null || measurementString == null || caloriesString == null) {
        // TODO handle broken weight
        return
    }

    ListItem(
        headlineContent = { Text(food.name) },
        modifier = modifier,
        overlineContent = food.brand?.let { { Text(it) } },
        supportingContent = {
            Column {
                val proteins = (food.nutrients.proteins.value * weight / 100f).roundToInt()
                val carbohydrates =
                    (food.nutrients.carbohydrates.value * weight / 100f).roundToInt()
                val fats = (food.nutrients.fats.value * weight / 100f).roundToInt()

                NutrientsRow(
                    proteins = proteins,
                    carbohydrates = carbohydrates,
                    fats = fats,
                    modifier = Modifier.fillMaxWidth()
                )
                MeasurementSummary(
                    measurementString = measurementString,
                    measurementStringShort = measurementStringShort,
                    caloriesString = caloriesString,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        trailingContent = {
            ToggleButton(
                checked = isSelected,
                onCheckedChange = onToggle
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null
                    )
                }
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = containerColor,
            headlineColor = contentColor,
            overlineColor = contentColor,
            supportingColor = contentColor,
            trailingIconColor = contentColor
        )
    )
}

@Composable
private fun ListItemSkeleton(shimmer: Shimmer, modifier: Modifier = Modifier) {
    FoodListItemSkeleton(
        shimmer = shimmer,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Spacer(
                Modifier
                    .shimmer(shimmer)
                    .size(24.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )
        }
    }
}

private val SearchFoodItem.measurementStringShort: String
    @Composable get() = with(measurement) {
        when (this) {
            is Measurement.Package -> stringResource(
                Res.string.x_times_y,
                quantity.formatClipZeros(),
                stringResource(Res.string.product_package)
            )

            is Measurement.Serving -> stringResource(
                Res.string.x_times_y,
                quantity.formatClipZeros(),
                stringResource(Res.string.product_serving)
            )

            is Measurement.Gram -> "${value.formatClipZeros()} " +
                stringResource(Res.string.unit_gram_short)
        }
    }

private val SearchFoodItem.measurementString: String?
    @Composable get() {
        val short = measurementStringShort
        val weight = weight?.formatClipZeros() ?: return null

        return when (food) {
            is Product -> when (measurement) {
                is Measurement.Gram -> short
                is Measurement.Package,
                is Measurement.Serving ->
                    "$short ($weight ${stringResource(Res.string.unit_gram_short)})"
            }
        }
    }

private val SearchFoodItem.caloriesString: String?
    @Composable get() = weight?.let {
        val value = (it * food.nutrients.calories.value / 100).roundToInt()
        "$value " + stringResource(Res.string.unit_kcal)
    }

private val LazyPagingItems<SearchFoodItem>.throwable: Throwable?
    get() {
        when (val state = loadState.refresh) {
            is LoadState.Error -> return state.error
            else -> null
        }

        when (val state = loadState.append) {
            is LoadState.Error -> return state.error
            else -> null
        }

        when (val state = loadState.prepend) {
            is LoadState.Error -> return state.error
            else -> null
        }

        return null
    }
