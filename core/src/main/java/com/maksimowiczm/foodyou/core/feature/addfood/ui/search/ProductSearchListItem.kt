package com.maksimowiczm.foodyou.core.feature.addfood.ui.search

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.core.feature.diary.ui.previewparameter.ProductWithWeightMeasurementPreviewParameter
import com.maksimowiczm.foodyou.core.feature.product.ui.res.stringResourceShort
import com.maksimowiczm.foodyou.core.ui.component.ToggleButton
import com.maksimowiczm.foodyou.core.ui.component.ToggleButtonDefaults
import com.maksimowiczm.foodyou.core.ui.modifier.horizontalDisplayCutoutPadding
import com.maksimowiczm.foodyou.core.ui.theme.FoodYouTheme
import com.maksimowiczm.foodyou.core.ui.toDp
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import kotlin.math.max

@Composable
fun ProductSearchListItem(
    model: ProductWithWeightMeasurement,
    onClick: () -> Unit,
    isChecked: Boolean,
    onCheckChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    colors: ProductSearchListItemColors = ProductSearchListItemDefaults.colors()
) {
    val containerColor =
        if (isChecked) colors.checkedContainerColor else colors.uncheckedContainerColor
    val contentColor = if (isChecked) colors.checkedContentColor else colors.uncheckedContentColor

    ListItem(
        headlineContent = {
            Text(
                text = model.product.name
            )
        },
        modifier = modifier
            .clickable { onClick() }
            .horizontalDisplayCutoutPadding(),
        overlineContent = {
            model.product.brand?.let {
                Text(
                    text = it
                )
            }
        },
        supportingContent = {
            SupportingTextLayout(
                measurementString = model.measurementString,
                measurementStringShort = model.measurementStringShort,
                caloriesString = model.caloriesString,
                modifier = Modifier.fillMaxWidth()
            )
        },
        trailingContent = {
            ToggleButton(
                checked = isChecked,
                onCheckChange = onCheckChange,
                colors = ToggleButtonDefaults.colors(
                    checkedColor = colors.checkedToggleButtonContainerColor,
                    checkedContentColor = colors.checkedToggleButtonContentColor,
                    uncheckedColor = colors.uncheckedToggleButtonContainerColor
                ),
                indication = LocalIndication.current
            ) {
                if (isChecked) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null
                    )
                }
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = containerColor,
            headlineColor = contentColor,
            overlineColor = contentColor,
            supportingColor = contentColor,
            trailingIconColor = contentColor
        )
    )
}

@Composable
private fun SupportingTextLayout(
    measurementString: String,
    measurementStringShort: String,
    caloriesString: String,
    modifier: Modifier = Modifier
) {
    val textStyle = LocalTextStyle.current
    val measurement = @Composable { Text(text = measurementString, maxLines = 1) }
    val measurementShort = @Composable { Text(text = measurementStringShort, maxLines = 1) }
    val calories = @Composable { Text(text = caloriesString, maxLines = 1) }
    val textMeasurer = rememberTextMeasurer()

    Layout(
        contents = listOf(
            measurement,
            measurementShort,
            calories
        ),
        modifier = modifier
    ) { (measurement, measurementShort, calories), constraints ->
        val measurementWidth = textMeasurer.measure(
            text = measurementString,
            style = textStyle
        ).size.width
        val measurementShortWidth = textMeasurer.measure(
            text = measurementStringShort,
            style = textStyle
        ).size.width
        val caloriesWidth = textMeasurer.measure(
            text = caloriesString,
            style = textStyle
        ).size.width

        if (constraints.maxWidth > measurementWidth + caloriesWidth) {
            val measurementPlaceable =
                measurement.first().measure(Constraints.fixedWidth(measurementWidth))
            val caloriesPlaceable = calories.first().measure(Constraints.fixedWidth(caloriesWidth))

            val height = max(measurementPlaceable.height, caloriesPlaceable.height)

            layout(constraints.maxWidth, height) {
                measurementPlaceable.placeRelative(0, 0)
                caloriesPlaceable.placeRelative(
                    constraints.maxWidth - caloriesPlaceable.width,
                    0
                )
            }
        } else if (constraints.maxWidth > measurementShortWidth + caloriesWidth) {
            val measurementShortPlaceable =
                measurementShort.first().measure(Constraints.fixedWidth(measurementShortWidth))
            val caloriesPlaceable = calories.first().measure(Constraints.fixedWidth(caloriesWidth))

            val height = max(measurementShortPlaceable.height, caloriesPlaceable.height)

            layout(constraints.maxWidth, height) {
                measurementShortPlaceable.placeRelative(0, 0)
                caloriesPlaceable.placeRelative(
                    constraints.maxWidth - caloriesPlaceable.width,
                    0
                )
            }
        } else if (constraints.maxWidth > measurementWidth) {
            val measurementPlaceable =
                measurement.first().measure(Constraints.fixedWidth(measurementWidth))

            val height = measurementPlaceable.height

            layout(constraints.maxWidth, height) {
                measurementPlaceable.placeRelative(0, 0)
            }
        } else {
            val measurementShortPlaceable =
                measurementShort.first().measure(Constraints.fixedWidth(measurementShortWidth))

            val height = measurementShortPlaceable.height

            layout(constraints.maxWidth, height) {
                measurementShortPlaceable.placeRelative(0, 0)
            }
        }
    }
}

data class ProductSearchListItemColors(
    val uncheckedContainerColor: Color,
    val uncheckedContentColor: Color,
    val uncheckedToggleButtonContainerColor: Color,
    val checkedContainerColor: Color,
    val checkedContentColor: Color,
    val checkedToggleButtonContainerColor: Color,
    val checkedToggleButtonContentColor: Color
)

object ProductSearchListItemDefaults {
    @Composable
    fun colors(
        uncheckedContainerColor: Color = MaterialTheme.colorScheme.surface,
        uncheckedContentColor: Color = MaterialTheme.colorScheme.onSurface,
        uncheckedToggleButtonContainerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
        checkedContainerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
        checkedContentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
        checkedToggleButtonContainerColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
        checkedToggleButtonContentColor: Color = MaterialTheme.colorScheme.onTertiaryContainer
    ) = ProductSearchListItemColors(
        uncheckedContainerColor = uncheckedContainerColor,
        uncheckedContentColor = uncheckedContentColor,
        uncheckedToggleButtonContainerColor = uncheckedToggleButtonContainerColor,
        checkedContainerColor = checkedContainerColor,
        checkedContentColor = checkedContentColor,
        checkedToggleButtonContainerColor = checkedToggleButtonContainerColor,
        checkedToggleButtonContentColor = checkedToggleButtonContentColor
    )
}

val ProductWithWeightMeasurement.measurementStringShort: String
    @Composable get() = when (measurement) {
        is WeightMeasurement.Package -> {
            val quantity = measurement.quantity.toInt()
            val packageString = pluralStringResource(R.plurals.product_package, 1)
            "%d %s".format(quantity, packageString)
        }

        is WeightMeasurement.Serving -> {
            val quantity = measurement.quantity.toInt()
            val servingString = pluralStringResource(R.plurals.product_serving, quantity)
            "%d %s".format(quantity, servingString)
        }

        is WeightMeasurement.WeightUnit -> {
            val quantity = measurement.weight
            val quantityString = if (quantity % 1 == 0f) {
                quantity.toInt().toString()
            } else {
                "%.2f".format(quantity).trimEnd { it == '0' || it == '.' }
            }

            "%s %s".format(
                quantityString,
                product.weightUnit.stringResourceShort()
            )
        }
    }

val ProductWithWeightMeasurement.measurementString: String
    @Composable get() = when (measurement) {
        is WeightMeasurement.Package -> {
            val quantity = measurement.quantity.toInt()
            val packageString = pluralStringResource(R.plurals.product_package, 1)

            "%d %s (%.0f %s)".format(
                quantity,
                packageString,
                measurement.weight,
                product.weightUnit.stringResourceShort()
            )
        }

        is WeightMeasurement.Serving -> {
            val quantity = measurement.quantity.toInt()
            val servingString = pluralStringResource(
                R.plurals.product_serving,
                quantity
            )

            "%d %s (%.0f %s)".format(
                quantity,
                servingString,
                measurement.weight,
                product.weightUnit.stringResourceShort()
            )
        }

        is WeightMeasurement.WeightUnit -> {
            val quantity = measurement.weight
            val quantityString = if (quantity % 1 == 0f) {
                quantity.toInt().toString()
            } else {
                "%.2f".format(quantity).trimEnd { it == '0' || it == '.' }
            }

            "%s %s".format(
                quantityString,
                product.weightUnit.stringResourceShort()
            )
        }
    }

val ProductWithWeightMeasurement.caloriesString: String
    @Composable get() = "$calories " + stringResource(R.string.unit_kcal)

@Composable
fun ProductSearchListItemSkeleton(
    modifier: Modifier = Modifier,
    shimmer: Shimmer = rememberShimmer(
        shimmerBounds = ShimmerBounds.Window
    )
) {
    ListItem(
        headlineContent = {
            Column {
                Spacer(Modifier.height(2.dp))
                Spacer(
                    Modifier
                        .shimmer(shimmer)
                        .height(LocalTextStyle.current.toDp() - 4.dp)
                        .width(200.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                )
                Spacer(Modifier.height(2.dp))
            }
        },
        overlineContent = {
            Spacer(
                Modifier
                    .shimmer(shimmer)
                    .height(LocalTextStyle.current.toDp())
                    .width(100.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )
        },
        supportingContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(
                    Modifier
                        .shimmer(shimmer)
                        .height(LocalTextStyle.current.toDp())
                        .width(125.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                )
                Spacer(
                    Modifier
                        .shimmer(shimmer)
                        .height(LocalTextStyle.current.toDp())
                        .width(75.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                )
            }
        },
        trailingContent = {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Spacer(
                    Modifier
                        .shimmer(shimmer)
                        .size(24.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                )
            }
        },
        modifier = modifier.horizontalDisplayCutoutPadding()
    )
}

@Preview
@Composable
private fun ProductSearchListItemSkeletonPreview() {
    FoodYouTheme {
        ProductSearchListItemSkeleton()
    }
}

@Preview
@Composable
private fun ProductSearchListItemPreview() {
    FoodYouTheme {
        ProductSearchListItem(
            model = ProductWithWeightMeasurementPreviewParameter().values.first(),
            onClick = {},
            onCheckChange = {},
            isChecked = true
        )
    }
}

@Preview
@Preview(
    fontScale = 2f
)
@Composable
private fun ProductSearchListItemPreview(
    @PreviewParameter(ProductWithWeightMeasurementPreviewParameter::class) model:
    ProductWithWeightMeasurement
) {
    FoodYouTheme {
        ProductSearchListItem(
            model = model,
            onClick = {},
            onCheckChange = {},
            isChecked = false
        )
    }
}
