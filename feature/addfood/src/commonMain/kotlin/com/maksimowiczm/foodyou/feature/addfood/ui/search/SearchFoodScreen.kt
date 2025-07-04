package com.maksimowiczm.foodyou.feature.addfood.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ext.lambda
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.ui.component.BackHandler
import com.maksimowiczm.foodyou.core.ui.component.BarcodeScannerIconButton
import com.maksimowiczm.foodyou.core.ui.component.Scrim
import com.maksimowiczm.foodyou.core.ui.component.StatusBarProtection
import com.maksimowiczm.foodyou.feature.addfood.model.SearchFoodItem
import com.maksimowiczm.foodyou.feature.barcodescanner.FullScreenCameraBarcodeScanner
import com.maksimowiczm.foodyou.feature.swissfoodcompositiondatabase.SwissFoodCompositionDatabaseHintCard
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SearchFoodScreen(
    onBack: () -> Unit,
    onProductAdd: () -> Unit,
    onRecipeAdd: () -> Unit,
    onFoodClick: (FoodId) -> Unit,
    onSwissFoodDatabase: () -> Unit,
    state: SearchFoodScreenState,
    viewModel: SearchFoodViewModel,
    modifier: Modifier = Modifier
) {
    val foods = viewModel.foods.collectAsStateWithLifecycle().value
    val recentQueries = viewModel.recentQueries.collectAsStateWithLifecycle().value

    SearchFoodScreen(
        state = state,
        foods = foods,
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
        onSwissFoodDatabase = onSwissFoodDatabase,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SearchFoodScreen(
    state: SearchFoodScreenState,
    foods: List<SearchFoodItem>?,
    recentQueries: List<String>,
    onBack: () -> Unit,
    onProductAdd: () -> Unit,
    onRecipeAdd: () -> Unit,
    onFoodClick: (FoodId) -> Unit,
    onFoodToggle: (Boolean, SearchFoodItem) -> Unit,
    onSearch: (String?) -> Unit,
    onSwissFoodDatabase: () -> Unit,
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
        Content(
            state = state,
            foods = foods,
            recentQueries = recentQueries,
            onBack = onBack,
            onFoodClick = onFoodClick,
            onFoodToggle = onFoodToggle,
            onSearch = onSearch,
            onSwissFoodDatabase = onSwissFoodDatabase
        )

        Scrim(
            visible = fabExpanded,
            onDismiss = { fabExpanded = false },
            modifier = Modifier
                .fillMaxSize()
                .zIndex(100f)
        )

        AnimatedVisibility(
            visible = state.lazyListState.canScrollBackward,
            enter = fadeIn(tween(50)),
            exit = fadeOut(tween(100))
        ) {
            StatusBarProtection(
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            )
        }
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
        // For whatever reason FloatingActionButtonMenu has default padding which makes scaffold fab
        // position to be broken exactly by 16.dp
        modifier = modifier.offset(x = 16.dp, y = 16.dp)
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
    foods: List<SearchFoodItem>?,
    recentQueries: List<String>,
    onFoodClick: (FoodId) -> Unit,
    onFoodToggle: (Boolean, SearchFoodItem) -> Unit,
    onBack: () -> Unit,
    onSearch: (String?) -> Unit,
    onSwissFoodDatabase: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var showBarcodeScanner by rememberSaveable { mutableStateOf(false) }

    val showLoadingIndicator = remember(foods) {
        foods == null
    }

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

    FullScreenSearchBar(
        state = state,
        recentQueries = recentQueries,
        onSearch = onSearch,
        inputField = inputField
    )

    Scaffold(
        topBar = {
            TopSearchBar(
                state = state.searchBarState,
                inputField = inputField
            )
        },
        modifier = modifier
    ) { paddingValues ->
        if (foods?.isEmpty() == true) {
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

        AnimatedVisibility(
            visible = showLoadingIndicator,
            modifier = Modifier
                .zIndex(5f)
                .fillMaxWidth()
                .padding(top = paddingValues.calculateTopPadding()),
            enter = expandVertically(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator(Modifier)
            }
        }

        if (foods != null) {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 8.dp),
                state = state.lazyListState,
                contentPadding = paddingValues
            ) {
                item {
                    SwissFoodCompositionDatabaseHintCard(
                        onAdd = onSwissFoodDatabase,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                itemsIndexed(
                    items = foods,
                    key = { _, food -> food.uniqueId }
                ) { i, food ->
                    val topStart = animateDpAsState(
                        targetValue = if (i == 0) 16.dp else 0.dp,
                        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
                    ).value.coerceAtLeast(0.dp)

                    val topEnd = animateDpAsState(
                        targetValue = if (i == 0) 16.dp else 0.dp,
                        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
                    ).value.coerceAtLeast(0.dp)

                    val bottomStart = animateDpAsState(
                        targetValue = if (i == foods.size - 1) 16.dp else 0.dp,
                        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
                    ).value.coerceAtLeast(0.dp)

                    val bottomEnd = animateDpAsState(
                        targetValue = if (i == foods.size - 1) 16.dp else 0.dp,
                        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
                    ).value.coerceAtLeast(0.dp)

                    var shape = RoundedCornerShape(topStart, topEnd, bottomStart, bottomEnd)

                    SearchFoodListItem(
                        food = food,
                        onClick = { onFoodClick(food.food.id) },
                        onToggle = { onFoodToggle(it, food) },
                        shape = shape
                    )

                    if (i < foods.size - 1) {
                        Spacer(Modifier.height(2.dp))
                    }
                }

                // FAB spacer
                item {
                    Spacer(Modifier.padding(vertical = 16.dp).height(72.dp))
                }
            }
        }
    }
}
