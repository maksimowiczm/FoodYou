package com.maksimowiczm.foodyou.feature.addfood.ui.search

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.addfood.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.feature.addfood.ui.previewparameter.ProductWithWeightMeasurementPreviewParameter
import com.maksimowiczm.foodyou.ui.preview.SharedTransitionPreview

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.SearchBottomBar(
    measuredProducts: List<ProductWithWeightMeasurement>,
    searchState: SearchState,
    onCreateProduct: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    val calories by animateIntAsState(
        measuredProducts.sumOf { it.calories }
    )

    BottomAppBar(
        modifier = modifier,
        actions = {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // TODO
                    //  Something to fill the empty space

                    // Temporary solution TF :D
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = "$calories",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        modifier = Modifier.padding(start = 4.dp),
                        text = stringResource(R.string.unit_kcal),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Row {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip {
                                Text(stringResource(R.string.action_create_new_product))
                            }
                        },
                        state = rememberTooltipState()
                    ) {
                        FilledIconButton(
                            shape = MaterialTheme.shapes.medium,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                contentColor = MaterialTheme.colorScheme.secondary,
                                containerColor = MaterialTheme.colorScheme.onSecondary
                            ),
                            onClick = onCreateProduct
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.action_create_new_product)
                            )
                        }
                    }
                    FilledIconButton(
                        modifier = Modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState(SearchSharedContentKeys.SEARCH_BARCODE_SCANNER),
                            animatedVisibilityScope = animatedVisibilityScope
                        ),
                        shape = MaterialTheme.shapes.medium,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            contentColor = MaterialTheme.colorScheme.secondary,
                            containerColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        onClick = searchState::onBarcodeScannerClick
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_qr_code_scanner_24),
                            contentDescription = stringResource(R.string.action_scan_barcode)
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(SearchSharedContentKeys.SEARCH_SEARCH_VIEW),
                    animatedVisibilityScope = animatedVisibilityScope
                ),
                onClick = {
                    searchState.onSearchClick()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.action_search)
                )
            }
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewDynamicColors
@Composable
private fun SearchBottomBarPreview() {
    val data = ProductWithWeightMeasurementPreviewParameter().values.toList()

    SharedTransitionPreview {
        SearchBottomBar(
            measuredProducts = data.filter { it.measurementId != null },
            searchState = rememberSearchState(
                meal = Meal.Breakfast,
                initialData = data,
                onQuickRemove = {},
                onQuickAdd = { 0 },
                initialIsLoading = false,
                initialIsError = false
            ),
            onCreateProduct = {},
            animatedVisibilityScope = this
        )
    }
}
