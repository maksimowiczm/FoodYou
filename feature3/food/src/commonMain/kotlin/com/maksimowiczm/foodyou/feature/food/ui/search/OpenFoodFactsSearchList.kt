package com.maksimowiczm.foodyou.feature.food.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.error
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.core.ui.ext.add
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.feature.food.domain.domainFacts
import com.maksimowiczm.foodyou.feature.food.domain.headline
import com.maksimowiczm.foodyou.feature.food.ui.FoodListItem
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.data.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import com.maksimowiczm.foodyou.feature.measurement.ui.stringResource
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun OpenFoodFactsSearchList(
    pages: LazyPagingItems<OpenFoodFactsProduct>,
    contentPadding: PaddingValues,
    onProductClick: (OpenFoodFactsProduct) -> Unit,
    modifier: Modifier = Modifier
) {
    var errorCardHeight by remember { mutableIntStateOf(0) }

    Box(modifier) {
        if (pages.itemCount == 0) {
            Text(
                text = stringResource(Res.string.neutral_no_food_found),
                modifier = Modifier
                    .safeContentPadding()
                    .align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier
                .zIndex(10f)
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(
                    top = contentPadding.calculateTopPadding()
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val error = pages.loadState.error
            if (pages.loadState.hasError) {
                ErrorCard(
                    message = error?.message ?: stringResource(Res.string.error_unknown_error),
                    onRetry = pages::retry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .onSizeChanged { errorCardHeight = it.height }
                        .padding(bottom = 8.dp)
                )
            }

            if (
                pages.loadState.refresh is LoadState.Loading ||
                pages.loadState.append is LoadState.Loading
            ) {
                ContainedLoadingIndicator(
                    modifier = Modifier
                        .padding(
                            top = contentPadding.calculateTopPadding()
                        )
                        .zIndex(10f)
                )
            }
        }

        LazyColumn(
            contentPadding = contentPadding.add(
                top = if (pages.loadState.hasError) {
                    LocalDensity.current.run { errorCardHeight.toDp() }
                } else {
                    0.dp
                }
            )
        ) {
            items(
                count = pages.itemCount,
                key = pages.itemKey { it.id }
            ) {
                val product = pages[it]

                if (product != null) {
                    OpenFoodFactsSearchListItem(
                        product = product,
                        onClick = { onProductClick(product) }
                    )
                }
            }
        }
    }
}

@Composable
private fun OpenFoodFactsSearchListItem(
    product: OpenFoodFactsProduct,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val facts = product.domainFacts

    val g = stringResource(Res.string.unit_gram_short)

    val measurement = product.defaultMeasurement

    val factor = when (measurement) {
        is Measurement.Gram,
        is Measurement.Milliliter -> 1f

        is Measurement.Package -> product.packageWeight!! / 100f
        is Measurement.Serving -> product.servingWeight!! / 100f
    }

    val measurementFacts = facts * factor

    FoodListItem(
        name = {
            Text(text = product.headline)
        },
        proteins = {
            val proteins = measurementFacts.proteins.value
            if (proteins != null) {
                Text("${proteins.formatClipZeros()} $g")
            }
        },
        carbohydrates = {
            val carbohydrates = measurementFacts.carbohydrates.value
            if (carbohydrates != null) {
                Text("${carbohydrates.formatClipZeros()} $g")
            }
        },
        fats = {
            val fats = measurementFacts.fats.value
            if (fats != null) {
                Text("${fats.formatClipZeros()} $g")
            }
        },
        calories = {
            val energy = measurementFacts.energy.value
            val kcal = stringResource(Res.string.unit_kcal)
            if (energy != null) {
                Text("${energy.formatClipZeros("%.0f")} $kcal")
            }
        },
        measurement = {
            val g = stringResource(Res.string.unit_gram_short)
            val text = buildString {
                append(measurement.stringResource())
                val weight = product.weight(measurement)

                if (weight != null) {
                    append(" (${weight.formatClipZeros()} $g)")
                }
            }

            Text(text)
        },
        modifier = modifier,
        onClick = onClick
    )
}

private val OpenFoodFactsProduct.defaultMeasurement: Measurement
    get() = when {
        servingWeight != null -> Measurement.Serving(1f)
        packageWeight != null -> Measurement.Package(1f)
        else -> Measurement.Gram(100f)
    }

private fun OpenFoodFactsProduct.weight(measurement: Measurement): Float? = when (measurement) {
    is Measurement.Gram,
    is Measurement.Milliliter -> null

    is Measurement.Package -> packageWeight
    is Measurement.Serving -> servingWeight
}

@Composable
private fun ErrorCard(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    var showDetails by rememberSaveable { mutableStateOf(false) }

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {
            Text(
                text = stringResource(Res.string.neutral_an_error_occurred),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.neutral_open_food_facts_error),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                TextButton(
                    onClick = {
                        showDetails = !showDetails
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text(stringResource(Res.string.action_show_details))
                }

                FilledTonalButton(
                    onClick = {
                        onRetry()
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        contentColor = MaterialTheme.colorScheme.errorContainer,
                        containerColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text(stringResource(Res.string.action_retry))
                }
            }
            Spacer(Modifier.height(8.dp))

            AnimatedVisibility(showDetails) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}
