package com.maksimowiczm.foodyou.feature.diary.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductWithMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.ui.res.stringResourceShort
import com.maksimowiczm.foodyou.ui.theme.LocalNutrientsPalette
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProductWithMeasurement.ListItem(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    colors: ListItemColors = ListItemDefaults.colors()
) {
    ListItem(
        headlineContent = { Text(product.name) },
        modifier = modifier.then(
            if (onClick == null) Modifier else Modifier.clickable { onClick() }
        ),
        overlineContent = { product.brand?.let { Text(it) } },
        supportingContent = {
            Column {
                NutrientsRow(
                    proteins = proteins.roundToInt(),
                    carbohydrates = carbohydrates.roundToInt(),
                    fats = fats.roundToInt(),
                    modifier = Modifier.fillMaxWidth()
                )

                MeasurementSummary(
                    measurementString = measurementString,
                    measurementStringShort = measurementStringShort,
                    caloriesString = caloriesString,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        trailingContent = trailingContent,
        colors = colors
    )
}

@Composable
private fun NutrientsRow(
    proteins: Int,
    carbohydrates: Int,
    fats: Int,
    modifier: Modifier = Modifier
) {
    val nutrientsPalette = LocalNutrientsPalette.current

    CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.labelMedium
    ) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = proteins.toString() + " " + stringResource(Res.string.unit_gram_short),
                color = nutrientsPalette.proteinsOnSurfaceContainer
            )

            Text(
                text = carbohydrates.toString() + " " + stringResource(Res.string.unit_gram_short),
                color = nutrientsPalette.carbohydratesOnSurfaceContainer
            )

            Text(
                text = fats.toString() + " " + stringResource(Res.string.unit_gram_short),
                color = nutrientsPalette.fatsOnSurfaceContainer
            )
        }
    }
}

val ProductWithMeasurement.measurementStringShort: String
    @Composable get() = when (val measurement = measurement) {
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

val ProductWithMeasurement.measurementString: String
    @Composable get() {
        val short = measurementStringShort

        if (measurement is WeightMeasurement.WeightUnit) {
            return short
        }

        val grams = measurement.weight

        return "$short (${grams.formatClipZeros()} ${product.weightUnit.stringResourceShort()})"
    }

val ProductWithMeasurement.caloriesString: String
    @Composable get() = "${calories.roundToInt()} " + stringResource(Res.string.unit_kcal)
