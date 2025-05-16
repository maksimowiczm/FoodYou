package com.maksimowiczm.foodyou.feature.addfood.ui.search

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults
import androidx.compose.material3.TopSearchBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.ext.lambda
import com.maksimowiczm.foodyou.core.ui.component.BackHandler
import com.maksimowiczm.foodyou.core.ui.component.BarcodeScannerIconButton
import com.maksimowiczm.foodyou.core.ui.component.Scrim
import com.maksimowiczm.foodyou.feature.addfood.model.SearchFoodItem
import com.maksimowiczm.foodyou.feature.barcodescanner.FullScreenCameraBarcodeScanner
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SearchFoodScreen(
    onBack: () -> Unit,
    onProductAdd: () -> Unit,
    onRecipeAdd: () -> Unit,
    onFoodClick: (FoodId) -> Unit,
    state: SearchFoodScreenState,
    viewModel: SearchFoodViewModel,
    modifier: Modifier = Modifier
) {
    val pages = viewModel.pages.collectAsLazyPagingItems(viewModel.viewModelScope.coroutineContext)
    val recentQueries = viewModel.recentQueries.collectAsStateWithLifecycle().value

    SearchFoodScreen(
        state = state,
        pages = pages,
        recentQueries = recentQueries.map { it.query },
        onBack = onBack,
        onProductAdd = onProductAdd,
        onRecipeAdd = onRecipeAdd,
        onFoodClick = onFoodClick,
        onFoodToggle = { state, food ->
            when (state) {
                true -> viewModel.onQuickAdd(food)
                false -> viewModel.onQuickRemove(food)
            }
        },
        onSearch = viewModel::onSearch,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SearchFoodScreen(
    state: SearchFoodScreenState,
    pages: LazyPagingItems<SearchFoodItem>,
    recentQueries: List<String>,
    onBack: () -> Unit,
    onProductAdd: () -> Unit,
    onRecipeAdd: () -> Unit,
    onFoodClick: (FoodId) -> Unit,
    onFoodToggle: (Boolean, SearchFoodItem) -> Unit,
    onSearch: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var fabExpanded by rememberSaveable { mutableStateOf(false) }

    BackHandler(fabExpanded) { fabExpanded = false }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            Fab(
                expanded = fabExpanded,
                onExpandedChange = { expanded ->
                    fabExpanded = expanded
                },
                onRecipeAdd = {
                    onRecipeAdd()
                    fabExpanded = false
                },
                onProductAdd = {
                    onProductAdd()
                    fabExpanded = false
                }
            )
        }
    ) {
        Scrim(
            visible = fabExpanded,
            onDismiss = { fabExpanded = false },
            modifier = Modifier.fillMaxSize().zIndex(10f)
        )

        Content(
            state = state,
            pages = pages,
            recentQueries = recentQueries,
            onBack = onBack,
            onFoodClick = onFoodClick,
            onFoodToggle = onFoodToggle,
            onSearch = onSearch
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Fab(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onRecipeAdd: () -> Unit,
    onProductAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButtonMenu(
        expanded = expanded,
        button = {
            ToggleFloatingActionButton(
                checked = expanded,
                onCheckedChange = onExpandedChange,
                containerColor = ToggleFloatingActionButtonDefaults.containerColor(
                    initialColor = MaterialTheme.colorScheme.secondaryContainer,
                    finalColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                val rotation by remember {
                    derivedStateOf { checkedProgress * 45f }
                }

                val tintColor = lerp(
                    start = MaterialTheme.colorScheme.onSecondaryContainer,
                    stop = MaterialTheme.colorScheme.onSecondary,
                    fraction = checkedProgress
                )

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = if (expanded) {
                        stringResource(Res.string.action_close)
                    } else {
                        stringResource(Res.string.action_create)
                    },
                    tint = tintColor,
                    modifier = Modifier.graphicsLayer { rotationZ = rotation }
                )
            }
        },
        modifier = modifier
    ) {
        FloatingActionButtonMenuItem(
            onClick = onRecipeAdd,
            text = { Text(stringResource(Res.string.headline_recipe)) },
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_skillet),
                    contentDescription = null
                )
            },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
        FloatingActionButtonMenuItem(
            onClick = onProductAdd,
            icon = {
                Icon(
                    imageVector = Icons.Default.LunchDining,
                    contentDescription = null
                )
            },
            text = { Text(stringResource(Res.string.headline_product)) },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Content(
    state: SearchFoodScreenState,
    pages: LazyPagingItems<SearchFoodItem>,
    recentQueries: List<String>,
    onFoodClick: (FoodId) -> Unit,
    onFoodToggle: (Boolean, SearchFoodItem) -> Unit,
    onBack: () -> Unit,
    onSearch: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var showBarcodeScanner by rememberSaveable { mutableStateOf(false) }

    val inputField = @Composable {
        SearchBarDefaults.InputField(
            textFieldState = state.textFieldState,
            searchBarState = state.searchBarState,
            onSearch = coroutineScope.lambda<String> {
                onSearch(it)
                state.searchBarState.animateToCollapsed()
            },
            placeholder = { Text(stringResource(Res.string.action_search)) },
            leadingIcon = {
                IconButton(
                    onClick = coroutineScope.lambda {
                        if (state.searchBarState.currentValue == SearchBarValue.Expanded) {
                            state.searchBarState.animateToCollapsed()
                        } else {
                            onBack()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.action_go_back)
                    )
                }
            },
            trailingIcon = {
                if (state.textFieldState.text.isEmpty()) {
                    BarcodeScannerIconButton(
                        onClick = coroutineScope.lambda {
                            showBarcodeScanner = true
                            state.searchBarState.animateToCollapsed()
                        }
                    )
                } else {
                    IconButton(
                        onClick = {
                            state.textFieldState.clearText()
                            onSearch(null)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(Res.string.action_clear)
                        )
                    }
                }
            }
        )
    }

    if (showBarcodeScanner) {
        FullScreenCameraBarcodeScanner(
            onBarcodeScan = {
                state.textFieldState.setTextAndPlaceCursorAtEnd(it)
                onSearch(it)
                showBarcodeScanner = false
            },
            onClose = { showBarcodeScanner = false }
        )
    }

    ExpandedFullScreenSearchBar(
        state = state.searchBarState,
        inputField = inputField
    ) {
        ProductSearchBarSuggestions(
            recentQueries = recentQueries,
            onSearch = coroutineScope.lambda<String> {
                onSearch(it)
                state.textFieldState.setTextAndPlaceCursorAtEnd(it)
                state.searchBarState.animateToCollapsed()
            },
            onFill = {
                state.textFieldState.setTextAndPlaceCursorAtEnd(it)
            }
        )
    }

    Scaffold(
        topBar = {
            TopSearchBar(
                state = state.searchBarState,
                inputField = inputField
            )
        },
        modifier = modifier
    ) { paddingValues ->
        if (pages.itemCount == 0) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(Res.string.neutral_no_food_found),
                    style = MaterialTheme.typography.bodyLargeEmphasized
                )
            }
        }

        LazyColumn(
            modifier = Modifier.padding(horizontal = 8.dp),
            state = state.lazyListState,
            verticalArrangement = Arrangement.spacedBy(2.dp),
            contentPadding = paddingValues
        ) {
            items(
                count = pages.itemCount,
                key = pages.itemKey { it.uniqueId }
            ) { i ->
                val food = pages[i]

                val topStart = animateDpAsState(
                    targetValue = if (i == 0) 16.dp else 0.dp,
                    animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
                ).value.coerceAtLeast(0.dp)

                val topEnd = animateDpAsState(
                    targetValue = if (i == 0) 16.dp else 0.dp,
                    animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
                ).value.coerceAtLeast(0.dp)

                val bottomStart = animateDpAsState(
                    targetValue = if (i == pages.itemCount - 1) 16.dp else 0.dp,
                    animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
                ).value.coerceAtLeast(0.dp)

                val bottomEnd = animateDpAsState(
                    targetValue = if (i == pages.itemCount - 1) 16.dp else 0.dp,
                    animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
                ).value.coerceAtLeast(0.dp)

                var shape = RoundedCornerShape(topStart, topEnd, bottomStart, bottomEnd)

                if (food != null) {
                    SearchFoodListItem(
                        food = food,
                        onClick = { onFoodClick(food.foodId) },
                        onToggle = { onFoodToggle(it, food) },
                        modifier = Modifier.animateItem(
                            fadeInSpec = null,
                            placementSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
                            fadeOutSpec = null
                        ),
                        shape = shape
                    )
                }
            }

            // FAB spacer
            item {
                Spacer(Modifier.padding(vertical = 16.dp).height(72.dp))
            }
        }
    }
}

@Composable
private fun ProductSearchBarSuggestions(
    recentQueries: List<String>,
    onSearch: (String) -> Unit,
    onFill: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(recentQueries) { query ->
            ListItem(
                modifier = Modifier.clickable {
                    onSearch(query)
                },
                headlineContent = {
                    Text(query)
                },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent
                ),
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = stringResource(Res.string.action_search)
                    )
                },
                trailingContent = {
                    IconButton(
                        onClick = { onFill(query) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.NorthWest,
                            contentDescription = stringResource(
                                Res.string.action_insert_suggested_search
                            )
                        )
                    }
                }
            )
        }
    }
}
