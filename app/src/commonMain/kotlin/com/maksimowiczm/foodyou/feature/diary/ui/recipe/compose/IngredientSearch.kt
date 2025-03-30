package com.maksimowiczm.foodyou.feature.diary.ui.recipe.compose

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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductQuery
import com.maksimowiczm.foodyou.feature.diary.ui.barcodescanner.CameraBarcodeScannerScreen
import com.maksimowiczm.foodyou.feature.diary.ui.component.FoodDatabaseErrorCard
import com.maksimowiczm.foodyou.feature.diary.ui.component.ProductSearchBarSuggestions
import com.maksimowiczm.foodyou.feature.diary.ui.component.SearchModelListItem
import com.maksimowiczm.foodyou.feature.diary.ui.component.SearchModelListItemSkeleton
import com.maksimowiczm.foodyou.feature.diary.ui.component.SearchScreen
import com.maksimowiczm.foodyou.feature.diary.ui.openfoodfactshint.OpenFoodFactsSearchHint
import com.maksimowiczm.foodyou.feature.diary.ui.recipe.CreateRecipeViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.recipe.model.IngredientSearch
import com.maksimowiczm.foodyou.navigation.crossfadeComposable
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.action_add_food
import foodyou.app.generated.resources.neutral_no_products_found
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

private const val SEARCH_SCREEN = "search"
private const val BARCODE_SCANNER_SCREEN = "barcodeScanner"

@Composable
fun IngredientSearch(
    onBack: () -> Unit,
    onGoToOpenFoodFactsSettings: () -> Unit,
    viewModel: CreateRecipeViewModel,
    modifier: Modifier = Modifier,
    state: IngredientSearchState = rememberIngredientSearchState()
) {
    val pages = viewModel.pages.collectAsLazyPagingItems(
        viewModel.viewModelScope.coroutineContext
    )

    val recentQueries by viewModel.recentQueries.collectAsStateWithLifecycle()

    val navController = rememberNavController()

    // Use NavHost to handle predictive back navigation
    NavHost(
        navController = navController,
        startDestination = SEARCH_SCREEN
    ) {
        crossfadeComposable(SEARCH_SCREEN) {
            IngredientSearch(
                state = state,
                pages = pages,
                recentQueries = recentQueries,
                onSearch = viewModel::onSearch,
                onBarcodeScanner = {
                    navController.navigate(
                        route = BARCODE_SCANNER_SCREEN,
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                onBack = onBack,
                onGoToOpenFoodFactsSettings = onGoToOpenFoodFactsSettings,
                modifier = modifier
            )
        }
        crossfadeComposable(BARCODE_SCANNER_SCREEN) {
            CameraBarcodeScannerScreen(
                onBarcodeScan = {
                    viewModel.onSearch(it)
                    state.textFieldState.setTextAndPlaceCursorAtEnd(it)
                    navController.popBackStack(BARCODE_SCANNER_SCREEN, inclusive = true)
                },
                onClose = {
                    navController.popBackStack(BARCODE_SCANNER_SCREEN, inclusive = true)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IngredientSearch(
    state: IngredientSearchState,
    pages: LazyPagingItems<IngredientSearch>,
    recentQueries: List<ProductQuery>,
    onSearch: (String?) -> Unit,
    onBarcodeScanner: () -> Unit,
    onBack: () -> Unit,
    onGoToOpenFoodFactsSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val isEmpty by remember(pages.loadState) {
        derivedStateOf { pages.itemCount == 0 }
    }
    val shimmer = rememberShimmer(
        shimmerBounds = ShimmerBounds.Window
    )

    SearchScreen(
        pages = pages,
        onSearch = onSearch,
        onClear = { onSearch(null) },
        onBack = onBack,
        onBarcodeScanner = onBarcodeScanner,
        modifier = modifier,
        textFieldState = state.textFieldState,
        searchBarState = state.searchBarState,
        coroutineScope = coroutineScope,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // TODO
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(Res.string.action_add_food)
                )
            }
        },
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
                    SearchModelListItemSkeleton(shimmer = shimmer)
                }
            }

            items(
                count = pages.itemCount
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
                        SearchModelListItemSkeleton(shimmer = shimmer)
                    } else {
                        SearchModelListItem(
                            name = target.name,
                            brand = target.brand,
                            calories = target.calories,
                            proteins = target.proteins,
                            carbohydrates = target.carbohydrates,
                            fats = target.fats,
                            weight = target.weightMeasurement.getWeight(
                                target.packageWeight,
                                target.servingWeight
                            ),
                            measurement = target.weightMeasurement,
                            modifier = Modifier.animateItem(
                                fadeInSpec = null,
                                fadeOutSpec = null
                            ),
                            onClick = {
                                // TODO
                            },
                            trailingContent = {
                                if (target.selected) {
                                    // TODO
                                    Text("TODO")
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
                    SearchModelListItemSkeleton(shimmer = shimmer)
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
