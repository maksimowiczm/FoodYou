package com.maksimowiczm.foodyou.feature.recipe.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.maksimowiczm.foodyou.core.ui.component.FoodListItemSkeleton
import com.maksimowiczm.foodyou.core.ui.ext.throwable
import com.maksimowiczm.foodyou.feature.addfood.ui.component.SearchScreen
import com.maksimowiczm.foodyou.feature.openfoodfacts.OpenFoodFactsErrorCard
import com.maksimowiczm.foodyou.feature.recipe.model.Ingredient
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun AddIngredientScreen(
    viewModel: RecipeViewModel,
    listState: LazyListState,
    onBarcodeScanner: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pages = viewModel.pages.collectAsLazyPagingItems(
        viewModel.viewModelScope.coroutineContext
    )

    val textFieldState = rememberTextFieldState()

    LaunchedEffect(viewModel) {
        viewModel.searchQuery.collectLatest {
            when (it) {
                null -> textFieldState.clearText()
                else -> textFieldState.setTextAndPlaceCursorAtEnd(it)
            }
        }
    }

    AddIngredientScreen(
        pages = pages,
        onBarcodeScanner = onBarcodeScanner,
        modifier = modifier,
        listState = listState,
        textFieldState = textFieldState,
        onSearch = remember(viewModel) { viewModel::onSearch },
        onClear = remember(viewModel) { { viewModel.onSearch(null) } },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddIngredientScreen(
    pages: LazyPagingItems<Ingredient>,
    onBarcodeScanner: () -> Unit,
    listState: LazyListState,
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    onClear: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val searchBarState = rememberSearchBarState()
    val shimmer = rememberShimmer(ShimmerBounds.View)

    SearchScreen(
        pages = pages,
        onSearch = onSearch,
        onClear = onClear,
        onBack = onBack,
        onBarcodeScanner = onBarcodeScanner,
        textFieldState = textFieldState,
        searchBarState = searchBarState,
        topBar = null,
        floatingActionButton = {},
        fullScreenSearchBarContent = {},
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
        hintCard = {},
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            state = listState
        ) {
            items(
                count = pages.itemCount
            ) {
                val ingredient = pages[it]

                Text(ingredient.toString())
            }

            if (pages.loadState.append is androidx.paging.LoadState.Loading) {
                items(3) {
                    FoodListItemSkeleton(shimmer)
                }
            }
        }
    }
}
