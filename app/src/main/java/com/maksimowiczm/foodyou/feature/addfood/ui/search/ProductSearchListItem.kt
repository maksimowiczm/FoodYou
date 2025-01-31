package com.maksimowiczm.foodyou.feature.addfood.ui.search

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Constraints
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.feature.addfood.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.feature.addfood.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.addfood.ui.previewparameter.ProductSearchUiModelPreviewParameter
import com.maksimowiczm.foodyou.feature.product.ui.res.stringResourceShort
import com.maksimowiczm.foodyou.ui.component.ToggleButton
import com.maksimowiczm.foodyou.ui.component.ToggleButtonDefaults
import com.maksimowiczm.foodyou.ui.modifier.animateRotation
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import kotlin.math.max

@Composable
fun ProductSearchListItem(
    uiModel: ProductSearchUiModel,
    onClick: () -> Unit,
    onCheckChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    colors: ProductSearchListItemColors = ProductSearchListItemDefaults.colors()
) {
    val (model, isLoading, isChecked) = uiModel

    val containerColor by animateColorAsState(
        targetValue = if (isChecked || isLoading) colors.checkedContainerColor else colors.uncheckedContainerColor
    )
    val contentColor by animateColorAsState(
        targetValue = if (isChecked || isLoading) colors.checkedContentColor else colors.uncheckedContentColor
    )

    ListItem(
        headlineContent = {
            Text(
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.displayCutout.only(
                        WindowInsetsSides.Horizontal
                    )
                ),
                text = model.product.name
            )
        },
        modifier = modifier.clickable { onClick() },
        overlineContent = {
            model.product.brand?.let {
                Text(
                    modifier = Modifier.windowInsetsPadding(
                        WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal)
                    ),
                    text = it
                )
            }
        },
        supportingContent = {
            SupportingTextLayout(
                measurementString = model.measurementString,
                measurementStringShort = model.measurementStringShort,
                caloriesString = model.caloriesString,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(
                        WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal)
                    )
            )
        },
        trailingContent = {
            ToggleButton(
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal)
                ),
                checked = isChecked || isLoading,
                onCheckChange = onCheckChange,
                colors = ToggleButtonDefaults.colors(
                    checkedColor = colors.checkedToggleButtonContainerColor,
                    checkedContentColor = colors.checkedToggleButtonContentColor,
                    uncheckedColor = colors.uncheckedToggleButtonContainerColor
                ),
                indication = LocalIndication.current
            ) {
                if (isLoading) {
                    Icon(
                        modifier = Modifier.animateRotation(),
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null
                    )
                } else if (isChecked) {
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
        checkedToggleButtonContainerColor: Color = MaterialTheme.colorScheme.surface,
        checkedToggleButtonContentColor: Color = MaterialTheme.colorScheme.onSurface
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

@Preview
@Preview(
    fontScale = 2f
)
@Composable
private fun ProductSearchListItemPreview(
    @PreviewParameter(ProductSearchUiModelPreviewParameter::class) productSearchUiModel: ProductSearchUiModel
) {
    FoodYouTheme {
        ProductSearchListItem(
            uiModel = productSearchUiModel,
            onClick = {},
            onCheckChange = {}
        )
    }
}
