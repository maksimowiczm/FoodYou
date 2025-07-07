package com.maksimowiczm.foodyou.feature.fooddiary.ui.search

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.feature.fooddiary.data.Food
import com.maksimowiczm.foodyou.feature.fooddiary.ui.search.openfoodfacts.OpenFoodFactsCard
import com.maksimowiczm.foodyou.feature.fooddiary.ui.search.openfoodfacts.OpenFoodFactsState
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.neutral_no_food_found
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun AnimatedVisibilityScope.LocalSearchList(
    pages: LazyPagingItems<Food>,
    onCreateProduct: () -> Unit,
    useOpenFoodFacts: Boolean,
    openFoodFactsCount: Int,
    openFoodFactsLoadState: CombinedLoadStates,
    onOpenFoodFactsPrivacyDialog: () -> Unit,
    onOpenFoodFacts: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        if (pages.itemCount == 0) {
            Text(
                text = stringResource(Res.string.neutral_no_food_found),
                modifier = Modifier
                    .safeContentPadding()
                    .align(Alignment.Center)
            )
        }

        FloatingActionButton(
            onClick = onCreateProduct,
            modifier = Modifier
                .zIndex(20f)
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .windowInsetsPadding(WindowInsets.systemBars)
                .animateFloatingActionButton(
                    visible = !this@LocalSearchList.transition.isRunning,
                    alignment = Alignment.BottomEnd
                )
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null
            )
        }

        LazyColumn(
            contentPadding = contentPadding
        ) {
            item {
                val cardState = remember(
                    useOpenFoodFacts,
                    openFoodFactsCount,
                    openFoodFactsLoadState
                ) {
                    if (!useOpenFoodFacts) {
                        return@remember OpenFoodFactsState.PrivacyPolicyRequested
                    }

                    if (openFoodFactsLoadState.refresh is LoadState.Loading ||
                        openFoodFactsLoadState.append is LoadState.Loading ||
                        openFoodFactsLoadState.prepend is LoadState.Loading
                    ) {
                        return@remember OpenFoodFactsState.Loading
                    }

                    if (openFoodFactsLoadState.hasError) {
                        return@remember OpenFoodFactsState.Error
                    }

                    OpenFoodFactsState.Loaded(openFoodFactsCount)
                }

                OpenFoodFactsCard(
                    onClick = {
                        if (!useOpenFoodFacts) {
                            onOpenFoodFactsPrivacyDialog()
                        } else {
                            onOpenFoodFacts()
                        }
                    },
                    state = cardState,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp)
                )
            }

            items(
                count = pages.itemCount,
                key = pages.itemKey { it.productId ?: 0L }
            ) {
                val food = pages[it]
                if (food != null) {
                    FoodSearchListItem(
                        food = food
                    )
                }
            }
        }
    }
}
