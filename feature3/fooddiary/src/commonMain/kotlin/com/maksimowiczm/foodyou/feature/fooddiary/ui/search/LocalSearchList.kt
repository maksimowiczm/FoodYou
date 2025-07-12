package com.maksimowiczm.foodyou.feature.fooddiary.ui.search

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.core.ui.ext.add
import com.maksimowiczm.foodyou.feature.fooddiary.domain.Food
import com.maksimowiczm.foodyou.feature.fooddiary.domain.FoodMeasurement
import com.maksimowiczm.foodyou.feature.fooddiary.ui.search.openfoodfacts.OpenFoodFactsCard
import com.maksimowiczm.foodyou.feature.fooddiary.ui.search.openfoodfacts.OpenFoodFactsState
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun AnimatedVisibilityScope.LocalSearchList(
    pages: LazyPagingItems<Food>,
    measurements: List<FoodMeasurement>,
    onCreateProduct: () -> Unit,
    onMeasurement: (Food, Measurement) -> Unit,
    onDeleteMeasurement: (measurementId: Long) -> Unit,
    onFoodClick: (Food, Measurement) -> Unit,
    useOpenFoodFacts: Boolean,
    openFoodFactsCount: Int,
    openFoodFactsLoadState: CombinedLoadStates,
    onOpenFoodFactsPrivacyDialog: () -> Unit,
    onOpenFoodFacts: () -> Unit,
    contentPadding: PaddingValues,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) = Box(modifier) {
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
                visible =
                !this@LocalSearchList.transition.isRunning &&
                    !animatedVisibilityScope.transition.isRunning,
                alignment = Alignment.BottomEnd
            )
    ) {
        Icon(
            imageVector = Icons.Outlined.Add,
            contentDescription = null
        )
    }

    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        contentPadding = contentPadding.add(
            bottom = 56.dp + 32.dp // FAB
        )
    ) {
        item {
            val cardState by remember(
                useOpenFoodFacts,
                openFoodFactsCount,
                openFoodFactsLoadState
            ) {
                derivedStateOf {
                    when {
                        !useOpenFoodFacts -> OpenFoodFactsState.PrivacyPolicyRequested
                        openFoodFactsLoadState.refresh is LoadState.Loading ||
                            openFoodFactsLoadState.append is LoadState.Loading ||
                            openFoodFactsLoadState.prepend is LoadState.Loading ->
                            OpenFoodFactsState.Loading

                        openFoodFactsLoadState.hasError -> OpenFoodFactsState.Error
                        else -> OpenFoodFactsState.Loaded(openFoodFactsCount)
                    }
                }
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
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(
            count = pages.itemCount,
            key = pages.itemKey { it.id.toString() }
        ) { i ->
            val food = pages[i]

            if (food != null) {
                val measurements = remember(measurements) {
                    measurements
                        .filter { it.foodId == food.id }
                        .map {
                            Triple<Measurement, Long?, Boolean>(
                                it.measurement,
                                it.measurementId,
                                true
                            )
                        }
                        .ifEmpty {
                            listOf(Triple(food.defaultMeasurement, null, false))
                        }
                }

                Column(
                    modifier = Modifier.graphicsLayer {
                        clip = true
                        this.shape = shape
                    },
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    measurements.forEachIndexed { index, (measurement, measurementId, isChecked) ->
                        val key = if (index == 0) {
                            index.toLong()
                        } else {
                            measurementId!!
                        }

                        key(key) {
                            val topStart = animateDpAsState(
                                targetValue = if (i == 0 && index == 0) 16.dp else 0.dp,
                                animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
                            ).value.coerceAtLeast(0.dp)

                            val topEnd = animateDpAsState(
                                targetValue = if (i == 0 && index == 0) 16.dp else 0.dp,
                                animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
                            ).value.coerceAtLeast(0.dp)

                            val bottomStart = animateDpAsState(
                                targetValue = if (
                                    i == pages.itemCount - 1 &&
                                    index == measurements.lastIndex
                                ) {
                                    16.dp
                                } else {
                                    0.dp
                                },
                                animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
                            ).value.coerceAtLeast(0.dp)

                            val bottomEnd = animateDpAsState(
                                targetValue = if (
                                    i == pages.itemCount - 1 &&
                                    index == measurements.lastIndex
                                ) {
                                    16.dp
                                } else {
                                    0.dp
                                },
                                animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
                            ).value.coerceAtLeast(0.dp)

                            val shape = RoundedCornerShape(topStart, topEnd, bottomStart, bottomEnd)

                            FoodSearchListItem(
                                food = food,
                                measurement = measurement,
                                checked = isChecked,
                                onClick = { onFoodClick(food, measurement) },
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        onMeasurement(food, measurement)
                                    } else if (measurementId != null) {
                                        onDeleteMeasurement(measurementId)
                                    }
                                },
                                shape = shape
                            )
                        }
                    }
                }

                Spacer(Modifier.height(2.dp))
            }
        }
    }
}
