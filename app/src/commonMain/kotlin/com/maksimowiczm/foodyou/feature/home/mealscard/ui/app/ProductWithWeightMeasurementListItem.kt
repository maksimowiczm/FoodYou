package com.maksimowiczm.foodyou.feature.home.mealscard.ui.app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import com.maksimowiczm.foodyou.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.ui.res.stringResourceShort
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import kotlin.math.max
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProductWithWeightMeasurement.ListItem(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    colors: ListItemColors = ListItemDefaults.colors()
) {
    androidx.compose.material3.ListItem(
        headlineContent = {
            Text(
                text = product.name
            )
        },
        modifier = modifier.then(
            if (onClick == null) Modifier else Modifier.clickable { onClick() }
        ),
        overlineContent = {
            product.brand?.let {
                Text(
                    text = it
                )
            }
        },
        supportingContent = {
            SupportingTextLayout(
                measurementString = measurementString,
                measurementStringShort = measurementStringShort,
                caloriesString = caloriesString,
                modifier = Modifier.fillMaxWidth()
            )
        },
        trailingContent = trailingContent,
        colors = colors
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

val ProductWithWeightMeasurement.measurementStringShort: String
    @Composable get() = when (measurement) {
        is WeightMeasurement.Package -> stringResource(
            Res.string.x_times_y,
            measurement.quantity.formatClipZeros(),
            stringResource(Res.string.product_package)
        )

        is WeightMeasurement.Serving -> stringResource(
            Res.string.x_times_y,
            measurement.quantity.formatClipZeros(),
            stringResource(Res.string.product_serving)
        )

        is WeightMeasurement.WeightUnit -> measurement.weight.formatClipZeros(".2f") + " " +
            product.weightUnit.stringResourceShort()
    }

val ProductWithWeightMeasurement.measurementString: String
    @Composable get() {
        val short = measurementStringShort

        if (measurement is WeightMeasurement.WeightUnit) {
            return short
        }

        val grams = measurement.weight

        return "$short (${grams.formatClipZeros()} ${product.weightUnit.stringResourceShort()})"
    }

val ProductWithWeightMeasurement.caloriesString: String
    @Composable get() = "$calories " + stringResource(Res.string.unit_kcal)
