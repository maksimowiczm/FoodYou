package com.maksimowiczm.foodyou.feature.recipe.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.core.ui.component.FoodListItemSkeleton
import com.maksimowiczm.foodyou.core.ui.ext.throwable
import com.maksimowiczm.foodyou.feature.addfood.ui.component.SearchScreen
import com.maksimowiczm.foodyou.feature.openfoodfacts.OpenFoodFactsErrorCard
import com.maksimowiczm.foodyou.feature.recipe.model.Ingredient
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import foodyou.app.generated.resources.*
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AddIngredientScreen(
    viewModel: RecipeViewModel,
    listState: LazyListState,
    onBarcodeScanner: () -> Unit,
    onProductClick: (productId: Long) -> Unit,
    onCreateProduct: () -> Unit,
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
        onProductClick = onProductClick,
        onCreateProduct = onCreateProduct,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
private fun AddIngredientScreen(
    pages: LazyPagingItems<Ingredient>,
    onBarcodeScanner: () -> Unit,
    listState: LazyListState,
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    onClear: () -> Unit,
    onProductClick: (productId: Long) -> Unit,
    onCreateProduct: () -> Unit,
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateProduct
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(Res.string.action_add)
                )
            }
        },
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
                count = pages.itemCount,
                key = pages.itemKey { it.product.id.id }
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
                        null -> FoodListItemSkeleton(shimmer)
                        else -> item.ListItem(
                            modifier = Modifier.clickable { onProductClick(item.product.id.id) }
                        )
                    }
                }
            }

            if (pages.loadState.append is androidx.paging.LoadState.Loading) {
                items(3) {
                    FoodListItemSkeleton(shimmer)
                }
            }
        }
    }
}
