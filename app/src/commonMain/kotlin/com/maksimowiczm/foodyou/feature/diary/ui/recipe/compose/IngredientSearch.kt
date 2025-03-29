package com.maksimowiczm.foodyou.feature.diary.ui.recipe.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductQuery
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.compose.AddFoodSearchScreen
import com.maksimowiczm.foodyou.feature.diary.ui.barcodescanner.CameraBarcodeScannerScreen
import com.maksimowiczm.foodyou.feature.diary.ui.recipe.CreateRecipeViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.recipe.model.IngredientSearch
import com.maksimowiczm.foodyou.navigation.crossfadeComposable
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.action_clear
import foodyou.app.generated.resources.action_go_back
import foodyou.app.generated.resources.action_insert_suggested_search
import foodyou.app.generated.resources.action_scan_barcode
import foodyou.app.generated.resources.action_search
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Stable
class SearchState(
    val textFieldState: TextFieldState,
    val searchBarState: SearchBarState,
    val lazyListState: LazyListState
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberSearchState(): SearchState {
    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberSearchBarState(
        initialValue = SearchBarValue.Collapsed
    )
    val lazyListState = rememberLazyListState()

    return remember(
        textFieldState,
        searchBarState,
        lazyListState
    ) {
        SearchState(
            textFieldState = textFieldState,
            searchBarState = searchBarState,
            lazyListState = lazyListState
        )
    }
}

@Composable
fun IngredientSearch(
    onBack: () -> Unit,
    viewModel: CreateRecipeViewModel,
    modifier: Modifier = Modifier,
    state: SearchState = rememberSearchState()
) {
    val pages = viewModel.pages.collectAsLazyPagingItems(
        viewModel.viewModelScope.coroutineContext
    )

    val recentQueries by viewModel.recentQueries.collectAsStateWithLifecycle()

    val navController = rememberNavController()

    // Use NavHost to handle predictive back navigation
    NavHost(
        navController = navController,
        startDestination = "search"
    ) {
        crossfadeComposable("search") {
            IngredientSearch(
                state = state,
                pages = pages,
                recentQueries = recentQueries,
                onSearch = viewModel::onSearch,
                onBarcodeScanner = {
                    navController.navigate(
                        route = AddFoodSearchScreen.BarcodeScanner.route,
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                onBack = onBack,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IngredientSearch(
    state: SearchState,
    pages: LazyPagingItems<IngredientSearch>,
    recentQueries: List<ProductQuery>,
    onSearch: (String?) -> Unit,
    onBarcodeScanner: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    val inputField = @Composable {
        SearchBarDefaults.InputField(
            textFieldState = state.textFieldState,
            searchBarState = state.searchBarState,
            onSearch = {
                onSearch(it)
                coroutineScope.launch {
                    state.searchBarState.animateToCollapsed()
                }
            },
            placeholder = { Text(stringResource(Res.string.action_search)) },
            leadingIcon = {
                IconButton(
                    onClick = {
                        if (state.searchBarState.currentValue == SearchBarValue.Expanded) {
                            coroutineScope.launch {
                                state.searchBarState.animateToCollapsed()
                            }
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
                Row {
                    if (state.textFieldState.text.isNotEmpty()) {
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

                    IconButton(
                        onClick = onBarcodeScanner
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = stringResource(Res.string.action_scan_barcode)
                        )
                    }
                }
            }
        )
    }

    ExpandedFullScreenSearchBar(
        state = state.searchBarState,
        inputField = inputField
    ) {
        LazyColumn {
            items(recentQueries) { (query) ->
                ListItem(
                    modifier = Modifier.clickable {
                        onSearch(query)
                        state.textFieldState.setTextAndPlaceCursorAtEnd(query)
                        coroutineScope.launch {
                            state.searchBarState.animateToCollapsed()
                        }
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
                            onClick = {
                                state.textFieldState.setTextAndPlaceCursorAtEnd(query)
                            }
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

    Scaffold(
        modifier = modifier,
        topBar = {
            TopSearchBar(
                state = state.searchBarState,
                inputField = inputField
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues
        ) {
            items(
                count = pages.itemCount
            ) {
                val item = pages[it]
                if (item != null) {
                    Text(
                        text = item.toString()
                    )
                }
            }
        }
    }
}
