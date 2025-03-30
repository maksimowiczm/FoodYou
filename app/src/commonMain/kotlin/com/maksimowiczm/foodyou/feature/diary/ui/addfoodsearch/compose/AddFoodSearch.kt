package com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.compose

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementId
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductQuery
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.AddFoodSearchViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.model.AddFoodSearchListItem
import com.maksimowiczm.foodyou.feature.diary.ui.barcodescanner.CameraBarcodeScannerScreen
import com.maksimowiczm.foodyou.feature.diary.ui.component.FoodDatabaseErrorCard
import com.maksimowiczm.foodyou.feature.diary.ui.component.ProductSearchBarSuggestions
import com.maksimowiczm.foodyou.feature.diary.ui.openfoodfactshint.OpenFoodFactsSearchHint
import com.maksimowiczm.foodyou.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.ui.ext.performToggle
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import foodyou.app.generated.resources.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

enum class AddFoodSearchScreen(val route: String) {
    List("list"),
    BarcodeScanner("barcodeScanner")
}

@Composable
fun AddFoodSearch(
    mealId: Long,
    date: LocalDate,
    onBack: () -> Unit,
    onProductClick: (Long) -> Unit,
    onCreateProduct: () -> Unit,
    onGoToOpenFoodFactsSettings: () -> Unit,
    modifier: Modifier = Modifier,
    initialScreen: AddFoodSearchScreen = AddFoodSearchScreen.List,
    viewModel: AddFoodSearchViewModel = koinViewModel(
        parameters = { parametersOf(mealId, date) }
    ),
    state: AddFoodSearchState = rememberAddFoodSearchState()
) {
    val pages = viewModel.pages.collectAsLazyPagingItems(
        viewModel.viewModelScope.coroutineContext
    )

    val recentQueries by viewModel.recentQueries.collectAsStateWithLifecycle()

    val navController = rememberNavController()

    // Use NavHost to handle predictive back navigation
    NavHost(
        navController = navController,
        startDestination = initialScreen.route
    ) {
        crossfadeComposable(AddFoodSearchScreen.List.route) {
            SearchScreen(
                state = state,
                pages = pages,
                onQuickAdd = viewModel::onQuickAdd,
                onQuickRemove = viewModel::onQuickRemove,
                onProductClick = onProductClick,
                onCreateProduct = onCreateProduct,
                recentQueries = recentQueries,
                onSearch = viewModel::onSearch,
                onClear = { viewModel.onSearch(null) },
                onBack = onBack,
                onGoToOpenFoodFactsSettings = onGoToOpenFoodFactsSettings,
                onBarcodeScanner = {
                    navController.navigate(
                        route = AddFoodSearchScreen.BarcodeScanner.route,
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                modifier = modifier
            )
        }
        crossfadeComposable(AddFoodSearchScreen.BarcodeScanner.route) {
            CameraBarcodeScannerScreen(
                onBarcodeScan = {
                    viewModel.onSearch(it)
                    state.textFieldState.setTextAndPlaceCursorAtEnd(it)
                    navController.popBackStack(
                        AddFoodSearchScreen.BarcodeScanner.route,
                        inclusive = true
                    )
                },
                onClose = {
                    navController.navigate(
                        route = AddFoodSearchScreen.List.route,
                        navOptions = navOptions {
                            launchSingleTop = true

                            popUpTo(AddFoodSearchScreen.BarcodeScanner.route) {
                                inclusive = true
                            }
                        }
                    )
                    navController.popBackStack(
                        AddFoodSearchScreen.BarcodeScanner.route,
                        inclusive = true
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SearchScreen(
    state: AddFoodSearchState,
    pages: LazyPagingItems<AddFoodSearchListItem>,
    onQuickAdd: (AddFoodSearchListItem) -> Unit,
    onQuickRemove: (MeasurementId) -> Unit,
    onProductClick: (Long) -> Unit,
    onCreateProduct: () -> Unit,
    recentQueries: List<ProductQuery>,
    onSearch: (String) -> Unit,
    onClear: () -> Unit,
    onBack: () -> Unit,
    onGoToOpenFoodFactsSettings: () -> Unit,
    onBarcodeScanner: () -> Unit,
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    val hapticFeedback = LocalHapticFeedback.current
    val shimmer = rememberShimmer(
        shimmerBounds = ShimmerBounds.Window
    )

    val isEmpty by remember(pages.loadState) {
        derivedStateOf { pages.itemCount == 0 }
    }

    // TODO
    //  Replace with animatedVisibilityScope.transition.isRunning is working as intended (by me)
    //  with predictive back
    var showFab by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(200)
        showFab = true
    }
    val fab = @Composable {
        FloatingActionButton(
            onClick = onCreateProduct,
            modifier = Modifier.animateFloatingActionButton(
                visible = showFab,
                alignment = Alignment.BottomEnd
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(Res.string.action_create_new_product)
            )
        }
    }

    com.maksimowiczm.foodyou.feature.diary.ui.component.SearchScreen(
        pages = pages,
        onSearch = onSearch,
        onClear = onClear,
        onBack = onBack,
        onBarcodeScanner = onBarcodeScanner,
        modifier = modifier,
        textFieldState = state.textFieldState,
        searchBarState = state.searchBarState,
        floatingActionButton = { fab() },
        hintCard = {
            OpenFoodFactsSearchHint(
                onGoToSettings = onGoToOpenFoodFactsSettings,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        },
        errorCard = {
            FoodDatabaseErrorCard(
                onRetry = pages::retry,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        },
        fullScreenSearchBarContent = {
            ProductSearchBarSuggestions(
                recentQueries = recentQueries,
                onSearch = {
                    onSearch(it.query)
                    state.textFieldState.setTextAndPlaceCursorAtEnd(it.query)
                    coroutineScope.launch {
                        state.searchBarState.animateToCollapsed()
                    }
                },
                onFill = {
                    state.textFieldState.setTextAndPlaceCursorAtEnd(it.query)
                }
            )
        }
    ) { paddingValues ->
        if (isEmpty && pages.loadState.append != LoadState.Loading) {
            Text(
                text = stringResource(Res.string.neutral_no_products_found),
                modifier = Modifier
                    .safeContentPadding()
                    .align(Alignment.Center)
            )
        }

        LazyColumn(
            state = state.lazyListState,
            contentPadding = paddingValues
        ) {
            if (pages.loadState.refresh == LoadState.Loading && isEmpty) {
                items(
                    count = 100,
                    key = { "skeleton-refresh-$it" }
                ) {
                    AddFoodSearchListItemSkeleton(shimmer = shimmer)
                }
            }

            items(
                count = pages.itemCount,
                key = pages.itemKey { it.listId }
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
                        AddFoodSearchListItemSkeleton(shimmer = shimmer)
                    } else {
                        target.AddFoodSearchListItem(
                            onClick = {
                                when (target.id) {
                                    is FoodId.Product -> onProductClick(target.id.productId)
                                    is FoodId.Recipe -> TODO()
                                }
                            },
                            onToggle = {
                                hapticFeedback.performToggle(it)

                                if (target.measurementId != null) {
                                    onQuickRemove(target.measurementId)
                                } else {
                                    onQuickAdd(target)
                                }
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
                    AddFoodSearchListItemSkeleton(shimmer = shimmer)
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
