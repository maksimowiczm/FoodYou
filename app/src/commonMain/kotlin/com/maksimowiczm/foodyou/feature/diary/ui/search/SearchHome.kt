package com.maksimowiczm.foodyou.feature.diary.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.RamenDining
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.feature.diary.data.model.DiaryEntry
import com.maksimowiczm.foodyou.feature.diary.data.model.DiaryEntrySuggestion
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductQuery
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductWithMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.ui.component.BackHandler
import com.maksimowiczm.foodyou.ui.component.FloatingActionButtonWithActions
import com.maksimowiczm.foodyou.ui.ext.performToggle
import com.maksimowiczm.foodyou.ui.modifier.horizontalDisplayCutoutPadding
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import foodyou.app.generated.resources.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchHome(
    onProductClick: (productId: Long) -> Unit,
    onBack: () -> Unit,
    onCreateProduct: () -> Unit,
    onCreateRecipe: () -> Unit,
    onBarcodeScanner: () -> Unit,
    viewModel: SearchViewModel,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    searchHint: (@Composable () -> Unit)? = null
) {
    val productsWithMeasurements = viewModel.pages.collectAsLazyPagingItems(
        viewModel.viewModelScope.coroutineContext
    )
    val recentQueries by viewModel.recentQueries.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()

    SearchHome(
        pages = productsWithMeasurements,
        recentQueries = recentQueries,
        query = query,
        onProductClick = onProductClick,
        onQuickAdd = viewModel::onQuickAdd,
        onQuickRemove = viewModel::onQuickRemove,
        onSearch = viewModel::onSearch,
        onClearSearch = { viewModel.onSearch(null) },
        onBack = onBack,
        onCreateProduct = onCreateProduct,
        onCreateRecipe = onCreateRecipe,
        onBarcodeScanner = onBarcodeScanner,
        modifier = modifier,
        lazyListState = lazyListState,
        searchHint = searchHint
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SearchHome(
    pages: LazyPagingItems<ProductWithMeasurement>,
    recentQueries: List<ProductQuery>,
    query: String?,
    onProductClick: (productId: Long) -> Unit,
    onQuickAdd: (productId: Long, measurement: WeightMeasurement) -> Unit,
    onQuickRemove: (measurementId: Long) -> Unit,
    onSearch: (query: String) -> Unit,
    onClearSearch: () -> Unit,
    onBack: () -> Unit,
    onCreateProduct: () -> Unit,
    onCreateRecipe: () -> Unit,
    onBarcodeScanner: () -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    searchHint: (@Composable () -> Unit)? = null
) {
    val hapticFeedback = LocalHapticFeedback.current

    val isEmpty by remember(pages.loadState) {
        derivedStateOf { pages.itemCount == 0 }
    }
    val isLoading by remember(pages.loadState) {
        derivedStateOf {
            pages.loadState.refresh == LoadState.Loading ||
                pages.loadState.append == LoadState.Loading
        }
    }
    val hasError by remember(pages.loadState) {
        derivedStateOf { pages.loadState.hasError }
    }

    val topBar = @Composable {
        SearchTopBar(
            state = rememberSearchTopBarState(
                query = query,
                recentQueries = recentQueries
            ),
            onBarcodeScanner = onBarcodeScanner,
            onSearch = onSearch,
            onClearSearch = onClearSearch,
            onBack = onBack
        )
    }

    var subSearchBarHeight by remember { mutableIntStateOf(0) }
    val subSearchBar = @Composable {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { subSearchBarHeight = it.height },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DraggableVisibility(
                initialValue = if (hasError) CardState.VISIBLE else CardState.HIDDEN_END
            ) {
                FoodDatabaseErrorCard(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .padding(bottom = 8.dp),
                    onRetry = pages::retry
                )
            }
            searchHint?.let {
                DraggableVisibility {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .padding(bottom = 8.dp)
                    ) {
                        searchHint()
                    }
                }
            }
        }
    }

    val shimmer = rememberShimmer(
        shimmerBounds = ShimmerBounds.Window
    )

    var expanded by rememberSaveable { mutableStateOf(false) }
    BackHandler(
        enabled = expanded,
        onBack = { expanded = false }
    )
    val scrimAlpha by animateFloatAsState(
        targetValue = if (expanded) .5f else 0f
    )

    Box(
        modifier = modifier
    ) {
        SearchHomeFloatingActionButton(
            expanded = expanded,
            onExpandChange = { expanded = it },
            onCreateProduct = onCreateProduct,
            onCreateRecipe = onCreateRecipe,
            modifier = Modifier.zIndex(2f)
        )

        if (expanded) {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f)
                    .graphicsLayer {
                        alpha = scrimAlpha
                    }
                    .pointerInput(Unit) {
                        detectTapGestures {
                            expanded = false
                        }
                    }
                    .background(Color.Black)
            )
        }

        Scaffold(
            topBar = topBar
        ) { paddingValues ->
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .consumeWindowInsets(paddingValues)
                        .fillMaxWidth()
                        .zIndex(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    subSearchBar()

                    AnimatedVisibility(
                        visible = isLoading
                    ) {
                        LoadingIndicator()
                    }
                }

                if (isEmpty && pages.loadState.append != LoadState.Loading) {
                    Text(
                        text = stringResource(Res.string.neutral_no_products_found),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                LazyColumn(
                    contentPadding = paddingValues,
                    state = lazyListState
                ) {
                    item {
                        Spacer(
                            Modifier.height(
                                LocalDensity.current.run {
                                    subSearchBarHeight.toDp()
                                }
                            )
                        )
                    }

                    if (pages.loadState.refresh == LoadState.Loading && isEmpty) {
                        items(
                            count = 100,
                            key = { "skeleton-refresh-$it" }
                        ) {
                            ProductSearchListItemSkeleton(shimmer = shimmer)
                        }
                    }

                    items(
                        count = pages.itemCount,
                        key = pages.itemKey {
                            return@itemKey when (it) {
                                is DiaryEntry -> "${it.product.id} ${it.entryId}"
                                is DiaryEntrySuggestion -> "${it.product.id}"
                                else -> {
                                    error("Unknown item type $it")
                                }
                            }
                        }
                    ) {
                        Crossfade(
                            targetState = pages[it],
                            // Do only placement animation
                            modifier = Modifier.animateItem(
                                fadeInSpec = null,
                                fadeOutSpec = null
                            )
                        ) { target ->
                            if (target == null) {
                                ProductSearchListItemSkeleton(
                                    shimmer = shimmer
                                )
                            } else {
                                ProductSearchListItem(
                                    model = target,
                                    isChecked = target is DiaryEntry,
                                    onCheckChange = { newState ->
                                        hapticFeedback.performToggle(newState)

                                        when (target) {
                                            is DiaryEntry -> onQuickRemove(target.entryId)

                                            is DiaryEntrySuggestion ->
                                                onQuickAdd(target.product.id, target.measurement)
                                        }
                                    },
                                    onClick = { onProductClick(target.product.id) }
                                )
                            }
                        }
                    }

                    if (pages.loadState.append == LoadState.Loading) {
                        items(
                            count = 3,
                            key = { "skeleton-append-$it" }
                        ) {
                            ProductSearchListItemSkeleton(shimmer = shimmer)
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
}

@Composable
private fun DraggableVisibility(
    modifier: Modifier = Modifier,
    initialValue: CardState = CardState.VISIBLE,
    content: @Composable () -> Unit
) {
    val anchoredDraggableState = rememberSaveable(
        initialValue,
        saver = AnchoredDraggableState.Saver()
    ) {
        AnchoredDraggableState(
            initialValue = initialValue
        )
    }

    val density = LocalDensity.current

    BoxWithConstraints {
        SideEffect {
            with(density) {
                val draggableAnchors = DraggableAnchors {
                    CardState.HIDDEN_END at -maxWidth.toPx()
                    CardState.VISIBLE at 0f
                    CardState.HIDDEN_START at maxWidth.toPx()
                }

                anchoredDraggableState.updateAnchors(draggableAnchors)
            }
        }

        AnimatedVisibility(
            visible = anchoredDraggableState.settledValue == CardState.VISIBLE,
            modifier = modifier
                .horizontalDisplayCutoutPadding()
                .fillMaxWidth()
                .anchoredDraggable(
                    state = anchoredDraggableState,
                    orientation = Orientation.Horizontal
                )
                .offset {
                    IntOffset(
                        x = anchoredDraggableState.requireOffset().fastRoundToInt(),
                        y = 0
                    )
                }
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SearchHomeFloatingActionButton(
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onCreateProduct: () -> Unit,
    onCreateRecipe: () -> Unit,
    modifier: Modifier = Modifier
) {
    // TODO
    //  Replace with animatedVisibilityScope.transition.isRunning is working as intended (by me)
    //  with predictive back
    var showFab by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(200)
        showFab = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(bottom = 16.dp, end = 16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButtonWithActions(
            expanded = expanded,
            actions = listOf(
                {
                    Surface(
                        onClick = {
                            onCreateRecipe()
                            onExpandChange(false)
                        },
                        modifier = Modifier.semantics { role = Role.Button },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(Res.string.headline_recipe),
                                style = MaterialTheme.typography.titleSmall
                            )
                            Icon(
                                imageVector = Icons.Default.RamenDining,
                                contentDescription = null
                            )
                        }
                    }
                },
                {
                    Surface(
                        onClick = {
                            onCreateProduct()
                            onExpandChange(false)
                        },
                        modifier = Modifier.semantics { role = Role.Button },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(Res.string.headline_product),
                                style = MaterialTheme.typography.titleSmall

                            )
                            Icon(
                                imageVector = Icons.Default.LunchDining,
                                contentDescription = null
                            )
                        }
                    }
                }
            ),
            fab = {
                FloatingActionButton(
                    onClick = { onExpandChange(!expanded) },
                    modifier = Modifier.animateFloatingActionButton(
                        visible = showFab,
                        alignment = Alignment.BottomEnd
                    )
                ) {
                    val rotation by animateFloatAsState(
                        targetValue = if (expanded) 45f else 0f
                    )

                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = if (expanded) {
                            stringResource(Res.string.action_close)
                        } else {
                            stringResource(Res.string.action_create_new_product)
                        },
                        modifier = Modifier.graphicsLayer {
                            rotationZ = rotation
                        }
                    )
                }
            }
        )
    }
}

private enum class CardState {
    HIDDEN_START,
    VISIBLE,
    HIDDEN_END
}
