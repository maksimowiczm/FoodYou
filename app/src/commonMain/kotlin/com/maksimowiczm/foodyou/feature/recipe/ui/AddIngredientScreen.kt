package com.maksimowiczm.foodyou.feature.recipe.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.core.domain.model.SearchQuery
import com.maksimowiczm.foodyou.core.ui.component.FoodListItemSkeleton
import com.maksimowiczm.foodyou.feature.addfood.ui.component.ProductSearchBarSuggestions
import com.maksimowiczm.foodyou.feature.addfood.ui.component.SearchScreen
import com.maksimowiczm.foodyou.feature.recipe.model.Ingredient
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import foodyou.app.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
internal fun AddIngredientScreen(
    pages: LazyPagingItems<Ingredient>,
    recentQueries: List<SearchQuery>,
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
    val coroutineScope = rememberCoroutineScope()
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
        fullScreenSearchBarContent = {
            ProductSearchBarSuggestions(
                recentQueries = recentQueries.map { it.query },
                onSearch = {
                    onSearch(it)
                    textFieldState.setTextAndPlaceCursorAtEnd(it)
                    coroutineScope.launch {
                        searchBarState.animateToCollapsed()
                    }
                },
                onFill = {
                    textFieldState.setTextAndPlaceCursorAtEnd(it)
                    onSearch(it)
                }
            )
        },
        errorCard = {},
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
