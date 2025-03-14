package com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.measurement

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.data.model.Product
import com.maksimowiczm.foodyou.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.ui.component.MultiColorProgressIndicator
import com.maksimowiczm.foodyou.ui.component.MultiColorProgressIndicatorItem
import com.maksimowiczm.foodyou.ui.preview.ProductPreviewParameterProvider
import com.maksimowiczm.foodyou.ui.preview.WeightMeasurementPreviewParameter
import com.maksimowiczm.foodyou.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.ui.res.stringResourceShort
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import com.maksimowiczm.foodyou.ui.theme.LocalNutrientsPalette
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

@Composable
fun MacroGraph(product: Product, measurement: WeightMeasurement, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // In quantity x package.
        // Fractions are "impossible" to translate correctly.

        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.bodySmall
        ) {
            Box(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                when (measurement) {
                    is WeightMeasurement.Package -> Text(
                        text = stringResource(
                            Res.string.in_x_times_y,
                            measurement.quantity.formatClipZeros(),
                            stringResource(Res.string.product_package)
                        )
                    )

                    is WeightMeasurement.Serving -> Text(
                        text = stringResource(
                            Res.string.in_x_times_y,
                            measurement.quantity.formatClipZeros(),
                            stringResource(Res.string.product_serving)
                        )
                    )

                    is WeightMeasurement.WeightUnit -> Text(
                        text = stringResource(
                            Res.string.in_x_weight_unit,
                            measurement.weight.formatClipZeros(),
                            product.weightUnit.stringResourceShort()
                        )
                    )
                }
            }
        }

        Text(
            text = "${product.nutrients.calories(measurement.weight).roundToInt()} " +
                stringResource(Res.string.unit_kcal),
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(8.dp))

        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.bodyMedium
        ) {
            NutrimentPieChart(
                product = product,
                weight = measurement.weight
            )
        }
    }
}

@Composable
private fun NutrimentPieChart(product: Product, weight: Float, modifier: Modifier = Modifier) {
    val nutrientsPalette = LocalNutrientsPalette.current

    val sum = product.nutrients.proteins + product.nutrients.carbohydrates + product.nutrients.fats

    Column(
        modifier = modifier
    ) {
        MultiColorProgressIndicator(
            items = listOf(
                MultiColorProgressIndicatorItem(
                    progress = product.nutrients.proteins / sum,
                    color = nutrientsPalette.proteinsOnSurfaceContainer
                ),
                MultiColorProgressIndicatorItem(
                    progress = product.nutrients.carbohydrates / sum,
                    color = nutrientsPalette.carbohydratesOnSurfaceContainer
                ),
                MultiColorProgressIndicatorItem(
                    progress = product.nutrients.fats / sum,
                    color = nutrientsPalette.fatsOnSurfaceContainer
                )
            ),
            modifier = Modifier
                .height(16.dp)
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
        )

        Spacer(Modifier.height(8.dp))

        Column {
            NutrimentLegendItem(
                color = nutrientsPalette.proteinsOnSurfaceContainer,
                text = stringResource(Res.string.nutriment_proteins),
                value = (product.nutrients.proteins * weight / 100).formatClipZeros()
            )

            NutrimentLegendItem(
                color = nutrientsPalette.carbohydratesOnSurfaceContainer,
                text = stringResource(Res.string.nutriment_carbohydrates),
                value = (product.nutrients.carbohydrates * weight / 100).formatClipZeros()
            )

            NutrimentLegendItem(
                color = nutrientsPalette.fatsOnSurfaceContainer,
                text = stringResource(Res.string.nutriment_fats),
                value = (product.nutrients.fats * weight / 100).formatClipZeros()
            )
        }
    }
}

@Composable
private fun NutrimentLegendItem(
    color: Color,
    text: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(
            modifier = Modifier
                .size(16.dp)
                .clip(MaterialTheme.shapes.extraSmall)
        ) {
            drawRect(
                color = color
            )
        }

        Spacer(Modifier.width(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = text
            )

            Text(
                stringResource(Res.string.en_dash)
            )

            Text(
                text = "$value " + stringResource(Res.string.unit_gram_short)
            )
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun MacroGraphPreview(
    @PreviewParameter(WeightMeasurementPreviewParameter::class) measurement: WeightMeasurement
) {
    FoodYouTheme {
        MacroGraph(
            product = ProductPreviewParameterProvider().values.first {
                it.packageWeight != null && it.servingWeight != null
            },
            measurement = measurement
        )
    }
}
