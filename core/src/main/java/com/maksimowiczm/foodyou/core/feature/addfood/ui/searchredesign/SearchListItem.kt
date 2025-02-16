package com.maksimowiczm.foodyou.core.feature.addfood.ui.searchredesign

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.core.feature.product.data.model.Nutrients
import com.maksimowiczm.foodyou.core.feature.product.data.model.Product
import com.maksimowiczm.foodyou.core.feature.product.data.model.WeightUnit
import com.maksimowiczm.foodyou.core.feature.product.ui.res.stringResourceShort
import com.maksimowiczm.foodyou.core.ui.component.ToggleButton
import com.maksimowiczm.foodyou.core.ui.toDp
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun SearchListItem(
    viewModel: SearchViewModel.SearchListItemViewModel,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    colors: SearchListItemColors = SearchListItemDefaults.colors()
) {
    val product by viewModel.product.collectAsStateWithLifecycle()
    val measurement by viewModel.measurement.collectAsStateWithLifecycle()
    val isChecked by viewModel.isChecked.collectAsStateWithLifecycle()

    SearchListItem(
        product = product,
        measurement = measurement,
        isChecked = isChecked,
        onCheckChange = viewModel::onCheckChange,
        onClick = onClick,
        onLongClick = onLongClick,
        colors = colors
    )
}

@Composable
private fun SearchListItem(
    product: Product?,
    measurement: WeightMeasurement?,
    isChecked: Boolean,
    onCheckChange: (Boolean) -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    colors: SearchListItemColors = SearchListItemDefaults.colors()
) {
    val containerColor by animateColorAsState(
        targetValue = if (isChecked) colors.checkedContainerColor else colors.uncheckedContainerColor
    )
    val contentColor by animateColorAsState(
        targetValue = if (isChecked) colors.checkedContentColor else colors.uncheckedContentColor
    )

    ListItem(
        modifier = Modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick
        ),
        headlineContent = {
            product?.let {
                Text(it.name)
            } ?: Spacer(
                Modifier
                    .height(LocalTextStyle.current.toDp())
                    .width(200.dp)
                    .background(Color.Red)
            )
        },
        supportingContent = {
            // TODO
            //  this temporary is fix for
            //  java.lang.IllegalStateException: LookaheadDelegate has not been measured yet when measureResult is requested.
            if (product != null && measurement != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = measurement.measurementString(product.weightUnit),
                        maxLines = 1
                    )
                    Text(
                        text = product.nutrients.caloriesString,
                        maxLines = 1
                    )
                }

//                SupportingTextLayout(
//                    measurementString = measurement.measurementString(product.weightUnit),
//                    measurementStringShort = measurement.measurementStringShort(product.weightUnit),
//                    caloriesString = product.nutrients.caloriesString
//                )
            } else {
                Spacer(
                    Modifier
                        .height(LocalTextStyle.current.toDp())
                        .width(50.dp)
                        .background(Color.Red)
                )
            }
        },
        overlineContent = {
            product?.apply {
                if (brand != null) {
                    Text(brand)
                }
            } ?: Spacer(
                Modifier
                    .height(LocalTextStyle.current.toDp())
                    .width(100.dp)
                    .background(Color.Red)
            )
        },
        trailingContent = {
            ToggleButton(
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal)
                ),
                checked = isChecked,
                onCheckChange = onCheckChange,
                indication = LocalIndication.current
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null
                )
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

@Immutable
data class SearchListItemColors(
    val uncheckedContainerColor: Color,
    val uncheckedContentColor: Color,
    val uncheckedToggleButtonContainerColor: Color,
    val checkedContainerColor: Color,
    val checkedContentColor: Color,
    val checkedToggleButtonContainerColor: Color,
    val checkedToggleButtonContentColor: Color
)

object SearchListItemDefaults {
    @Composable
    fun colors(
        uncheckedContainerColor: Color = MaterialTheme.colorScheme.surface,
        uncheckedContentColor: Color = MaterialTheme.colorScheme.onSurface,
        uncheckedToggleButtonContainerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
        checkedContainerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
        checkedContentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
        checkedToggleButtonContainerColor: Color = MaterialTheme.colorScheme.surface,
        checkedToggleButtonContentColor: Color = MaterialTheme.colorScheme.onSurface
    ) = SearchListItemColors(
        uncheckedContainerColor = uncheckedContainerColor,
        uncheckedContentColor = uncheckedContentColor,
        uncheckedToggleButtonContainerColor = uncheckedToggleButtonContainerColor,
        checkedContainerColor = checkedContainerColor,
        checkedContentColor = checkedContentColor,
        checkedToggleButtonContainerColor = checkedToggleButtonContainerColor,
        checkedToggleButtonContentColor = checkedToggleButtonContentColor
    )
}

@Composable
private fun WeightMeasurement.measurementStringShort(
    weightUnit: WeightUnit
): String = when (this) {
    is WeightMeasurement.Package -> {
        val quantity = quantity.toInt()
        val packageString = pluralStringResource(R.plurals.product_package, 1)
        "%d %s".format(quantity, packageString)
    }

    is WeightMeasurement.Serving -> {
        val quantity = quantity.toInt()
        val servingString = pluralStringResource(R.plurals.product_serving, quantity)
        "%d %s".format(quantity, servingString)
    }

    is WeightMeasurement.WeightUnit -> {
        val quantity = weight
        val quantityString = if (quantity % 1 == 0f) {
            quantity.toInt().toString()
        } else {
            "%.2f".format(quantity).trimEnd { it == '0' || it == '.' }
        }

        "%s %s".format(
            quantityString,
            weightUnit.stringResourceShort()
        )
    }
}

@Composable
private fun WeightMeasurement.measurementString(
    weightUnit: WeightUnit
): String = when (this) {
    is WeightMeasurement.Package -> {
        val quantity = quantity.toInt()
        val packageString = pluralStringResource(R.plurals.product_package, 1)

        "%d %s (%.0f %s)".format(
            quantity,
            packageString,
            weight,
            weightUnit.stringResourceShort()
        )
    }

    is WeightMeasurement.Serving -> {
        val quantity = quantity.toInt()
        val servingString = pluralStringResource(
            R.plurals.product_serving,
            quantity
        )

        "%d %s (%.0f %s)".format(
            quantity,
            servingString,
            weight,
            weightUnit.stringResourceShort()
        )
    }

    is WeightMeasurement.WeightUnit -> {
        val quantity = weight
        val quantityString = if (quantity % 1 == 0f) {
            quantity.toInt().toString()
        } else {
            "%.2f".format(quantity).trimEnd('0').trimEnd { !it.isDigit() }
        }

        "%s %s".format(
            quantityString,
            weightUnit.stringResourceShort()
        )
    }
}

private val Nutrients.caloriesString: String
    @Composable get() = "${calories.roundToInt()} " + stringResource(R.string.unit_kcal)
