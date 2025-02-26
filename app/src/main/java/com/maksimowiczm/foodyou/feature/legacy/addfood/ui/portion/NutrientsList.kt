package com.maksimowiczm.foodyou.feature.legacy.addfood.ui.portion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.data.model.WeightUnit
import com.maksimowiczm.foodyou.ui.component.IndentedList
import com.maksimowiczm.foodyou.ui.preview.ProductPreviewParameterProvider
import com.maksimowiczm.foodyou.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.ui.res.stringResourceShort
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme

@Composable
fun NutrientsList(
    state: NutrientsListState,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    val startPadding = paddingValues.calculateStartPadding(LocalLayoutDirection.current)
    val endPadding = paddingValues.calculateEndPadding(LocalLayoutDirection.current)
    val topPadding = paddingValues.calculateTopPadding()
    val bottomPadding = paddingValues.calculateBottomPadding()

    Column(
        modifier = modifier.padding(top = topPadding, bottom = bottomPadding)
    ) {
        Column {
            Text(
                text = stringResource(R.string.neutral_all_values_per_x, ""),
                modifier = Modifier.padding(start = startPadding, end = endPadding)
            )

            Spacer(Modifier.width(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Spacer(
                        Modifier.width(
                            paddingValues.calculateStartPadding(
                                LocalLayoutDirection.current
                            )
                        )
                    )
                }

                itemsIndexed(state.filterOptions) { i, filterOption ->
                    FilterChip(
                        selected = i == state.selectedFilterIndex,
                        onClick = { state.selectedFilterIndex = i },
                        label = { Text(filterOption.stringResource(state.product.weightUnit)) }
                    )
                }

                item {
                    Spacer(
                        Modifier.width(
                            paddingValues.calculateEndPadding(LocalLayoutDirection.current)
                        )
                    )
                }
            }
        }

        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.bodyLarge
        ) {
            IndentedList(
                spacer = { Spacer(Modifier.width(24.dp)) },
                modifier = Modifier.padding(start = startPadding, end = endPadding)
            ) {
                item {
                    NutrientsListItem(
                        label = stringResource(R.string.unit_calories),
                        value =
                        "${state.nutrients.calories.formatClipZeros()} " +
                            stringResource(R.string.unit_kcal)
                    )
                }

                item {
                    HorizontalDivider()
                }

                item {
                    NutrientsListItem(
                        label = stringResource(R.string.nutriment_proteins),
                        value = state.nutrients.proteins.stringResource()
                    )
                }

                item {
                    HorizontalDivider()
                }

                item {
                    NutrientsListItem(
                        label = stringResource(R.string.nutriment_carbohydrates),
                        value = state.nutrients.carbohydrates.stringResource()
                    )
                }

                item(
                    level = 1
                ) {
                    NutrientsListItem(
                        label = stringResource(R.string.nutriment_sugars),
                        value = state.nutrients.sugars.stringResource()
                    )
                }

                item {
                    HorizontalDivider()
                }

                item {
                    NutrientsListItem(
                        label = stringResource(R.string.nutriment_fats),
                        value = state.nutrients.fats.stringResource()
                    )
                }

                item(
                    level = 1
                ) {
                    NutrientsListItem(
                        label = stringResource(R.string.nutriment_saturated_fats),
                        value = state.nutrients.saturatedFats.stringResource()
                    )
                }

                item {
                    HorizontalDivider()
                }

                item {
                    NutrientsListItem(
                        label = stringResource(R.string.nutriment_salt),
                        value = state.nutrients.salt.stringResource()
                    )
                }

                item(
                    level = 1
                ) {
                    NutrientsListItem(
                        label = stringResource(R.string.nutriment_sodium),
                        value = state.nutrients.sodium.stringResource()
                    )
                }

                item {
                    HorizontalDivider()
                }

                item {
                    NutrientsListItem(
                        label = stringResource(R.string.nutriment_fiber),
                        value = state.nutrients.fiber.stringResource()
                    )
                }
            }
        }
    }
}

@Composable
private fun NutrientsListItem(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value
            )
        }
    }
}

@Composable
private fun Float?.stringResource(): String {
    val clip = this?.formatClipZeros()

    return if (clip != null) {
        "$clip " + stringResource(R.string.unit_gram_short)
    } else {
        stringResource(R.string.not_available_short)
    }
}

@Composable
private fun WeightMeasurement.stringResource(weightUnit: WeightUnit) = when (this) {
    is WeightMeasurement.WeightUnit -> "${weight.formatClipZeros()} " +
        weightUnit.stringResourceShort()
    is WeightMeasurement.Package -> "${quantity.formatClipZeros()} x " +
        stringResource(R.string.product_package)
    is WeightMeasurement.Serving -> "${quantity.formatClipZeros()} x " +
        stringResource(R.string.product_serving)
}

@Preview(
    showBackground = true
)
@Composable
private fun NutrientsListPreview() {
    val product = ProductPreviewParameterProvider().values.first {
        it.packageWeight != null && it.servingWeight != null
    }

    FoodYouTheme {
        NutrientsList(
            state = rememberNutrientsListState(
                product = product
            ),
            paddingValues = PaddingValues(16.dp)
        )
    }
}
