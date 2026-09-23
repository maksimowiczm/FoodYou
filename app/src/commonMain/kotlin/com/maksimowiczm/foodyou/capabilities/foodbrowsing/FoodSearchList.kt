package com.maksimowiczm.foodyou.capabilities.foodbrowsing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.HistoryToggleOff
import androidx.compose.material.icons.outlined.NorthWest
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.favoritefood.FavoriteFoodListItem
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.fooddatacentral.FoodDataCentralErrorCard
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.fooddatacentral.FoodDataCentralListItem
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.openfoodfacts.OpenFoodFactsErrorCard
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.openfoodfacts.OpenFoodFactsListItem
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.userfood.UserProductListItem
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.userfood.UserRecipeListItem
import com.maksimowiczm.foodyou.common.RemoteData
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralApiError
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductId
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsApiError
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductId
import com.maksimowiczm.foodyou.search.domain.SearchResult
import com.maksimowiczm.foodyou.shared.ui.InteractionShapes
import com.maksimowiczm.foodyou.shared.ui.component.FoodListItemSkeleton
import com.maksimowiczm.foodyou.shared.ui.extension.add
import com.maksimowiczm.foodyou.shared.ui.extension.error
import com.maksimowiczm.foodyou.shared.ui.extension.rememberDebounceIsIdle
import com.maksimowiczm.foodyou.shared.ui.rememberInteractionAnimatedShape
import com.maksimowiczm.foodyou.userproduct.domain.UserProduct
import com.maksimowiczm.foodyou.userproduct.domain.UserProductId
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipe
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeId
import com.valentinilk.shimmer.Shimmer
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun FoodSearchList(
    selectedCollection: SearchCollection?,
    userFood: LazyPagingItems<SearchResult>,
    openFoodFacts: LazyPagingItems<OpenFoodFactsProduct>,
    foodDataCentral: LazyPagingItems<FoodDataCentralProduct>,
    favoriteFood: LazyPagingItems<RemoteData<Any>>,
    searchQuery: SearchQuery,
    contentPadding: PaddingValues,
    lazyListState: LazyListState,
    onFoodDataCentralProduct: (FoodDataCentralProductId, Quantity) -> Unit,
    onOpenFoodFactsProduct: (OpenFoodFactsProductId, Quantity) -> Unit,
    onUserProduct: (UserProductId, Quantity) -> Unit,
    onUserRecipe: (UserRecipeId, Quantity) -> Unit,
    shimmer: Shimmer,
    onFill: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
    history: List<String>? = null,
    showQuickResults: Boolean = true,
) {
    val density = LocalDensity.current
    var errorCardHeight by remember { mutableIntStateOf(0) }

    val isIdle =
        when (selectedCollection) {
            null ->
                userFood.rememberDebounceIsIdle() &&
                    openFoodFacts.rememberDebounceIsIdle() &&
                    foodDataCentral.rememberDebounceIsIdle()

            is SearchCollection.Favorite -> favoriteFood.rememberDebounceIsIdle()
            is SearchCollection.FoodDataCentral -> foodDataCentral.rememberDebounceIsIdle()
            is SearchCollection.OpenFoodFacts -> openFoodFacts.rememberDebounceIsIdle()
            is SearchCollection.UserFood -> userFood.rememberDebounceIsIdle()
        }

    Box(modifier) {
        LazyColumn(
            modifier = Modifier.imePadding(),
            state = lazyListState,
            contentPadding = contentPadding.add(top = density.run { errorCardHeight.toDp() }),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            when (selectedCollection) {
                null -> {
                    if (showQuickResults) {
                        val userFoodCount = userFood.itemCount.coerceAtMost(5)
                        val offCount = openFoodFacts.itemCount.coerceAtMost(5)
                        val fdcCount = foodDataCentral.itemCount.coerceAtMost(5)

                        val userFoodLoading =
                            userFood.loadState.refresh is LoadState.Loading ||
                                userFood.loadState.append is LoadState.Loading
                        val offLoading =
                            openFoodFacts.loadState.refresh is LoadState.Loading ||
                                openFoodFacts.loadState.append is LoadState.Loading
                        val fdcLoading =
                            foodDataCentral.loadState.refresh is LoadState.Loading ||
                                foodDataCentral.loadState.append is LoadState.Loading

                        items(
                            count = userFoodCount,
                            key =
                                userFood.itemKey {
                                    when (it) {
                                        is SearchResult.UserProduct -> it.id.toString()
                                        is SearchResult.UserRecipe -> it.id.toString()
                                    }
                                },
                        ) { i ->
                            val interactionSource = remember { MutableInteractionSource() }
                            val shape =
                                rememberItemShape(
                                    isFirst = i == 0,
                                    isLast =
                                        i == userFoodCount - 1 &&
                                            !userFoodLoading &&
                                            offCount == 0 &&
                                            !offLoading &&
                                            fdcCount == 0 &&
                                            !fdcLoading,
                                    interactionSource = interactionSource,
                                )

                            when (val product = userFood[i]) {
                                null ->
                                    FoodListItemSkeleton(
                                        shimmer = shimmer,
                                        modifier =
                                            Modifier.animateItem().padding(horizontal = 8.dp),
                                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                        shape = shape,
                                    )

                                is SearchResult.UserProduct ->
                                    UserProductListItem(
                                        product = product,
                                        onClick = {
                                            onUserProduct(UserProductId(product.id), it)
                                        },
                                        shimmer = shimmer,
                                        modifier =
                                            Modifier.animateItem().padding(horizontal = 8.dp),
                                        shape = shape,
                                        interactionSource = interactionSource,
                                    )

                                is SearchResult.UserRecipe ->
                                    UserRecipeListItem(
                                        recipe = product,
                                        onClick = {
                                            onUserRecipe(UserRecipeId(product.id), it)
                                        },
                                        shimmer = shimmer,
                                        interactionSource = interactionSource,
                                        shape = shape,
                                        modifier =
                                            Modifier.animateItem().padding(horizontal = 8.dp),
                                    )
                            }
                        }

                        if (userFoodLoading && userFoodCount < 5) {
                            items(3) { i ->
                                val shape =
                                    rememberItemShape(
                                        isFirst = i == 0 && userFoodCount == 0,
                                        isLast =
                                            i == 2 &&
                                                offCount == 0 &&
                                                !offLoading &&
                                                fdcCount == 0 &&
                                                !fdcLoading,
                                        interactionSource = remember { MutableInteractionSource() },
                                    )
                                FoodListItemSkeleton(
                                    shimmer = shimmer,
                                    modifier = Modifier.animateItem().padding(horizontal = 8.dp),
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    shape = shape,
                                )
                            }
                        }

                        items(
                            count = offCount,
                            key = openFoodFacts.itemKey { it.id.barcode },
                        ) { i ->
                            val interactionSource = remember { MutableInteractionSource() }
                            val shape =
                                rememberItemShape(
                                    isFirst = i == 0 && userFoodCount == 0 && !userFoodLoading,
                                    isLast =
                                        i == offCount - 1 &&
                                            !offLoading &&
                                            fdcCount == 0 &&
                                            !fdcLoading,
                                    interactionSource = interactionSource,
                                )

                            when (val product = openFoodFacts[i]) {
                                null ->
                                    FoodListItemSkeleton(
                                        shimmer = shimmer,
                                        modifier =
                                            Modifier.animateItem().padding(horizontal = 8.dp),
                                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                        shape = shape,
                                    )

                                else ->
                                    OpenFoodFactsListItem(
                                        food = product,
                                        onClick = { onOpenFoodFactsProduct(product.id, it) },
                                        shimmer = shimmer,
                                        interactionSource = interactionSource,
                                        shape = shape,
                                        modifier =
                                            Modifier.animateItem().padding(horizontal = 8.dp),
                                    )
                            }
                        }

                        if (offLoading && offCount < 5) {
                            items(3) { i ->
                                val shape =
                                    rememberItemShape(
                                        isFirst =
                                            i == 0 &&
                                                userFoodCount == 0 &&
                                                !userFoodLoading &&
                                                offCount == 0,
                                        isLast = i == 2 && fdcCount == 0 && !fdcLoading,
                                        interactionSource = remember { MutableInteractionSource() },
                                    )
                                FoodListItemSkeleton(
                                    shimmer = shimmer,
                                    modifier = Modifier.animateItem().padding(horizontal = 8.dp),
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    shape = shape,
                                )
                            }
                        }

                        items(
                            count = fdcCount,
                            key = foodDataCentral.itemKey { it.id.fdcId },
                        ) { i ->
                            val interactionSource = remember { MutableInteractionSource() }
                            val shape =
                                rememberItemShape(
                                    isFirst =
                                        i == 0 &&
                                            userFoodCount == 0 &&
                                            !userFoodLoading &&
                                            offCount == 0 &&
                                            !offLoading,
                                    isLast = i == fdcCount - 1 && !fdcLoading,
                                    interactionSource = interactionSource,
                                )

                            when (val product = foodDataCentral[i]) {
                                null ->
                                    FoodListItemSkeleton(
                                        shimmer = shimmer,
                                        modifier =
                                            Modifier.animateItem().padding(horizontal = 8.dp),
                                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                        shape = shape,
                                    )

                                else ->
                                    FoodDataCentralListItem(
                                        food = product,
                                        onClick = {
                                            onFoodDataCentralProduct(product.id, it)
                                        },
                                        interactionSource = interactionSource,
                                        shape = shape,
                                        modifier =
                                            Modifier.animateItem().padding(horizontal = 8.dp),
                                    )
                            }
                        }

                        if (fdcLoading && fdcCount < 5) {
                            items(3) { i ->
                                val shape =
                                    rememberItemShape(
                                        isFirst =
                                            i == 0 &&
                                                userFoodCount == 0 &&
                                                !userFoodLoading &&
                                                offCount == 0 &&
                                                !offLoading &&
                                                fdcCount == 0,
                                        isLast = i == 2,
                                        interactionSource = remember { MutableInteractionSource() },
                                    )
                                FoodListItemSkeleton(
                                    shimmer = shimmer,
                                    modifier = Modifier.animateItem().padding(horizontal = 8.dp),
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    shape = shape,
                                )
                            }
                        }
                    } else if (history != null) {
                        items(history, key = { "history_$it" }) { query ->
                            FoodSearchHistoryItem(
                                search = query,
                                onFill = { onFill(query) },
                                modifier = Modifier.clickable { onSearch(query) }.animateItem(),
                            )
                        }
                    }
                }

                is SearchCollection.UserFood ->
                    items(
                        count = userFood.itemCount,
                        key =
                            userFood.itemKey {
                                when (it) {
                                    is SearchResult.UserProduct -> it.id.toString()
                                    is SearchResult.UserRecipe -> it.id.toString()
                                }
                            },
                    ) { i ->
                        val interactionSource = remember { MutableInteractionSource() }
                        val isLoading =
                            userFood.loadState.refresh is LoadState.Loading ||
                                userFood.loadState.append is LoadState.Loading
                        val shape =
                            rememberItemShape(
                                isFirst = i == 0,
                                isLast = i == userFood.itemCount - 1 && !isLoading,
                                interactionSource = interactionSource,
                            )

                        when (val food = userFood[i]) {
                            null ->
                                FoodListItemSkeleton(
                                    shimmer = shimmer,
                                    modifier = Modifier.animateItem().padding(horizontal = 8.dp),
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    shape = shape,
                                )

                            is SearchResult.UserProduct ->
                                UserProductListItem(
                                    product = food,
                                    onClick = { onUserProduct(UserProductId(food.id), it) },
                                    shimmer = shimmer,
                                    modifier = Modifier.animateItem().padding(horizontal = 8.dp),
                                    shape = shape,
                                    interactionSource = interactionSource,
                                )

                            is SearchResult.UserRecipe ->
                                UserRecipeListItem(
                                    recipe = food,
                                    onClick = { onUserRecipe(UserRecipeId(food.id), it) },
                                    shimmer = shimmer,
                                    interactionSource = interactionSource,
                                    shape = shape,
                                    modifier = Modifier.animateItem().padding(horizontal = 8.dp),
                                )
                        }
                    }

                is SearchCollection.OpenFoodFacts ->
                    items(
                        count = openFoodFacts.itemCount,
                        key = openFoodFacts.itemKey { it.id.barcode },
                    ) { i ->
                        val interactionSource = remember { MutableInteractionSource() }
                        val isLoading =
                            openFoodFacts.loadState.refresh is LoadState.Loading ||
                                openFoodFacts.loadState.append is LoadState.Loading
                        val shape =
                            rememberItemShape(
                                isFirst = i == 0,
                                isLast = i == openFoodFacts.itemCount - 1 && !isLoading,
                                interactionSource = interactionSource,
                            )

                        when (val food = openFoodFacts[i]) {
                            null ->
                                FoodListItemSkeleton(
                                    shimmer = shimmer,
                                    modifier = Modifier.animateItem().padding(horizontal = 8.dp),
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    shape = shape,
                                )

                            else ->
                                OpenFoodFactsListItem(
                                    food = food,
                                    onClick = { onOpenFoodFactsProduct(food.id, it) },
                                    shimmer = shimmer,
                                    interactionSource = interactionSource,
                                    shape = shape,
                                    modifier = Modifier.animateItem().padding(horizontal = 8.dp),
                                )
                        }
                    }

                is SearchCollection.FoodDataCentral ->
                    items(
                        count = foodDataCentral.itemCount,
                        key = foodDataCentral.itemKey { it.id.fdcId },
                    ) { i ->
                        val interactionSource = remember { MutableInteractionSource() }
                        val isLoading =
                            foodDataCentral.loadState.refresh is LoadState.Loading ||
                                foodDataCentral.loadState.append is LoadState.Loading
                        val shape =
                            rememberItemShape(
                                isFirst = i == 0,
                                isLast = i == foodDataCentral.itemCount - 1 && !isLoading,
                                interactionSource = interactionSource,
                            )

                        when (val food = foodDataCentral[i]) {
                            null ->
                                FoodListItemSkeleton(
                                    shimmer = shimmer,
                                    modifier = Modifier.animateItem().padding(horizontal = 8.dp),
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    shape = shape,
                                )

                            else ->
                                FoodDataCentralListItem(
                                    food = food,
                                    onClick = { onFoodDataCentralProduct(food.id, it) },
                                    interactionSource = interactionSource,
                                    shape = shape,
                                    modifier = Modifier.animateItem().padding(horizontal = 8.dp),
                                )
                        }
                    }

                is SearchCollection.Favorite ->
                    items(favoriteFood.itemCount) { i ->
                        val interactionSource = remember { MutableInteractionSource() }
                        val isLoading =
                            favoriteFood.loadState.refresh is LoadState.Loading ||
                                favoriteFood.loadState.append is LoadState.Loading
                        val shape =
                            rememberItemShape(
                                isFirst = i == 0,
                                isLast = i == favoriteFood.itemCount - 1 && !isLoading,
                                interactionSource = interactionSource,
                            )

                        when (val food = favoriteFood[i]) {
                            null ->
                                FoodListItemSkeleton(
                                    shimmer = shimmer,
                                    modifier = Modifier.animateItem().padding(horizontal = 8.dp),
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    shape = shape,
                                )

                            else ->
                                FavoriteFoodListItem(
                                    food = food,
                                    shimmer = shimmer,
                                    interactionSource = interactionSource,
                                    shape = shape,
                                    onClick = {
                                        when (food) {
                                            is RemoteData.Success -> {
                                                when (val value = food.value) {
                                                    is UserProduct -> onUserProduct(value.id, it)

                                                    is UserRecipe -> onUserRecipe(value.id, it)

                                                    is OpenFoodFactsProduct ->
                                                        onOpenFoodFactsProduct(value.id, it)

                                                    is FoodDataCentralProduct ->
                                                        onFoodDataCentralProduct(value.id, it)
                                                }
                                            }

                                            is RemoteData.Error -> {
                                                food.partialValue?.let { value ->
                                                    when (value) {
                                                        is UserProduct ->
                                                            onUserProduct(value.id, it)

                                                        is UserRecipe -> onUserRecipe(value.id, it)

                                                        is OpenFoodFactsProduct ->
                                                            onOpenFoodFactsProduct(
                                                                value.id,
                                                                it,
                                                            )

                                                        is FoodDataCentralProduct ->
                                                            onFoodDataCentralProduct(
                                                                value.id,
                                                                it,
                                                            )
                                                    }
                                                }
                                            }

                                            is RemoteData.Loading -> {
                                                food.partialValue?.let { value ->
                                                    when (value) {
                                                        is UserProduct ->
                                                            onUserProduct(value.id, it)

                                                        is UserRecipe -> onUserRecipe(value.id, it)

                                                        is OpenFoodFactsProduct ->
                                                            onOpenFoodFactsProduct(
                                                                value.id,
                                                                it,
                                                            )

                                                        is FoodDataCentralProduct ->
                                                            onFoodDataCentralProduct(
                                                                value.id,
                                                                it,
                                                            )
                                                    }
                                                }
                                            }

                                            is RemoteData.NotFound -> Unit
                                        }
                                    },
                                    modifier = Modifier.animateItem().padding(horizontal = 8.dp),
                                    fallback = {
                                        FoodListItemSkeleton(
                                            shimmer = shimmer,
                                            modifier =
                                                Modifier.animateItem().padding(horizontal = 8.dp),
                                            containerColor =
                                                MaterialTheme.colorScheme.surfaceContainer,
                                            shape = shape,
                                        )
                                    },
                                )
                        }
                    }
            }

            // Common Loading State
            val loadState =
                when (selectedCollection) {
                    null ->
                        if (showQuickResults) {
                            if (
                                userFood.loadState.append is LoadState.Loading ||
                                    userFood.loadState.refresh is LoadState.Loading ||
                                    openFoodFacts.loadState.append is LoadState.Loading ||
                                    openFoodFacts.loadState.refresh is LoadState.Loading ||
                                    foodDataCentral.loadState.append is LoadState.Loading ||
                                    foodDataCentral.loadState.refresh is LoadState.Loading
                            ) {
                                LoadState.Loading
                            } else LoadState.NotLoading(true)
                        } else LoadState.NotLoading(true)

                    is SearchCollection.Favorite -> favoriteFood.loadState.append
                    is SearchCollection.FoodDataCentral -> foodDataCentral.loadState.append
                    is SearchCollection.OpenFoodFacts -> openFoodFacts.loadState.append
                    is SearchCollection.UserFood -> userFood.loadState.append
                }

            if (loadState is LoadState.Loading) {
                val itemCountBeforeLoading =
                    when (selectedCollection) {
                        null -> if (showQuickResults) 0 else history?.size ?: 0
                        is SearchCollection.Favorite -> favoriteFood.itemCount
                        is SearchCollection.FoodDataCentral -> foodDataCentral.itemCount
                        is SearchCollection.OpenFoodFacts -> openFoodFacts.itemCount
                        is SearchCollection.UserFood -> userFood.itemCount
                    }

                if (selectedCollection == null && !showQuickResults) {
                    items(10) { i ->
                        val isFirst = i == 0 && itemCountBeforeLoading == 0
                        val isLast = i == 9
                        val baseShape =
                            when {
                                isFirst && isLast -> MaterialTheme.shapes.extraLarge
                                isFirst ->
                                    MaterialTheme.shapes.extraSmall.copy(
                                        topStart = MaterialTheme.shapes.extraLarge.topStart,
                                        topEnd = MaterialTheme.shapes.extraLarge.topEnd,
                                    )

                                isLast ->
                                    MaterialTheme.shapes.extraSmall.copy(
                                        bottomStart = MaterialTheme.shapes.extraLarge.bottomStart,
                                        bottomEnd = MaterialTheme.shapes.extraLarge.bottomEnd,
                                    )

                                else -> MaterialTheme.shapes.extraSmall
                            }

                        FoodListItemSkeleton(
                            shimmer = shimmer,
                            modifier = Modifier.animateItem().padding(horizontal = 8.dp),
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            shape = baseShape,
                        )
                    }
                }
            }
        }

        // Error Cards
        Box(
            Modifier.padding(top = contentPadding.calculateTopPadding()).onSizeChanged {
                errorCardHeight = it.height
            }
        ) {
            when (selectedCollection) {
                is SearchCollection.OpenFoodFacts ->
                    when (val error = openFoodFacts.loadState.error) {
                        is OpenFoodFactsApiError ->
                            OpenFoodFactsErrorCard(
                                error = error,
                                modifier =
                                    Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp),
                            )

                        is Throwable ->
                            FoodSearchErrorCard(
                                message = error.message,
                                onRetry = openFoodFacts::retry,
                                modifier =
                                    Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp),
                            )
                    }

                is SearchCollection.FoodDataCentral ->
                    when (val error = foodDataCentral.loadState.error) {
                        is FoodDataCentralApiError ->
                            FoodDataCentralErrorCard(
                                error = error,
                                modifier =
                                    Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp),
                            )

                        is Throwable ->
                            FoodSearchErrorCard(
                                message = error.message,
                                onRetry = foodDataCentral::retry,
                                modifier =
                                    Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp),
                            )
                    }

                else -> Unit
            }
        }

        // Empty States
        if (isIdle) {
            val isSearchActive = selectedCollection != null || searchQuery !is SearchQuery.Blank
            val isEmpty =
                when (selectedCollection) {
                    null ->
                        if (isSearchActive)
                            userFood.itemCount == 0 &&
                                openFoodFacts.itemCount == 0 &&
                                foodDataCentral.itemCount == 0
                        else history.isNullOrEmpty()

                    is SearchCollection.Favorite -> favoriteFood.itemCount == 0
                    is SearchCollection.FoodDataCentral -> foodDataCentral.itemCount == 0
                    is SearchCollection.OpenFoodFacts -> openFoodFacts.itemCount == 0
                    is SearchCollection.UserFood -> userFood.itemCount == 0
                }

            if (isEmpty) {
                if (selectedCollection == null && !isSearchActive) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement =
                            Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.HistoryToggleOff,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = stringResource(Res.string.headline_no_recent_searches),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    Text(
                        text = stringResource(Res.string.neutral_no_food_found),
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        if (!isIdle) {
            ContainedLoadingIndicator(
                Modifier.align(Alignment.TopCenter)
                    .padding(top = contentPadding.calculateTopPadding())
            )
        }
    }
}

@Composable
private fun FoodSearchHistoryItem(
    search: String,
    onFill: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier,
        headlineContent = { Text(search) },
        leadingContent = { Icon(imageVector = Icons.Outlined.History, contentDescription = null) },
        trailingContent = {
            IconButton(onClick = onFill, shapes = IconButtonDefaults.shapes()) {
                Icon(
                    imageVector = Icons.Outlined.NorthWest,
                    contentDescription = stringResource(Res.string.action_insert_suggested_search),
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
    )
}

@Composable
private fun rememberItemShape(
    isFirst: Boolean,
    isLast: Boolean,
    interactionSource: InteractionSource,
): Shape {
    val baseShape =
        when {
            isFirst && isLast -> MaterialTheme.shapes.extraLarge
            isFirst ->
                MaterialTheme.shapes.extraSmall.copy(
                    topStart = MaterialTheme.shapes.extraLarge.topStart,
                    topEnd = MaterialTheme.shapes.extraLarge.topEnd,
                )
            isLast ->
                MaterialTheme.shapes.extraSmall.copy(
                    bottomStart = MaterialTheme.shapes.extraLarge.bottomStart,
                    bottomEnd = MaterialTheme.shapes.extraLarge.bottomEnd,
                )
            else -> MaterialTheme.shapes.extraSmall
        }
    return rememberInteractionAnimatedShape(
        shapes =
            InteractionShapes(
                shape = baseShape,
                pressedShape = MaterialTheme.shapes.extraLarge,
            ),
        interactionSource = interactionSource,
    )
}
