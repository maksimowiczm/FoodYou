package com.maksimowiczm.foodyou.feature.meal.ui.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.domain.model.FoodWithMeasurement
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.core.ui.theme.LocalNutrientsPalette
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.error_measurement_error
import foodyou.app.generated.resources.product_package
import foodyou.app.generated.resources.product_serving
import foodyou.app.generated.resources.unit_gram_short
import foodyou.app.generated.resources.unit_kcal
import foodyou.app.generated.resources.x_times_y
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun FoodListItem(
    foodWithMeasurement: FoodWithMeasurement,
    color: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    val nutrientsPalette = LocalNutrientsPalette.current

    val proteinsString = foodWithMeasurement.proteins?.let {
        it.formatClipZeros() + " " + stringResource(Res.string.unit_gram_short)
    }

    val carbohydratesString = foodWithMeasurement.carbohydrates?.let {
        it.formatClipZeros() + " " + stringResource(Res.string.unit_gram_short)
    }

    val fatsString = foodWithMeasurement.fats?.let {
        it.formatClipZeros() + " " + stringResource(Res.string.unit_gram_short)
    }

    val caloriesString = foodWithMeasurement.caloriesString
    val measurementString = foodWithMeasurement.measurementString

    if (
        proteinsString == null ||
        carbohydratesString == null ||
        fatsString == null ||
        caloriesString == null ||
        measurementString == null
    ) {
        FoodListErrorItem(
            headline = foodWithMeasurement.food.headline,
            modifier = modifier
        )
    } else {
        FoodListItem(
            headlineContent = { Text(foodWithMeasurement.food.headline) },
            supportingContent = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = proteinsString,
                            color = nutrientsPalette.proteinsOnSurfaceContainer
                        )

                        Text(
                            text = carbohydratesString,
                            color = nutrientsPalette.carbohydratesOnSurfaceContainer
                        )

                        Text(
                            text = fatsString,
                            color = nutrientsPalette.fatsOnSurfaceContainer
                        )

                        Text(
                            text = caloriesString,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(text = measurementString)
                }
            },
            modifier = modifier,
            color = color,
            contentColor = contentColor
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun FoodListErrorItem(headline: String, modifier: Modifier = Modifier) {
    FoodListItem(
        headlineContent = {
            Text(
                text = headline,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        },
        supportingContent = {
            Text(
                text = stringResource(Res.string.error_measurement_error),
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        },
        modifier = modifier,
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun FoodListItem(
    headlineContent: @Composable () -> Unit,
    supportingContent: @Composable () -> Unit,
    color: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = color,
        contentColor = contentColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .padding(start = 16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.titleMediumEmphasized
            ) {
                headlineContent()
            }

            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.bodySmall
            ) {
                supportingContent()
            }
        }
    }
}

private val FoodWithMeasurement.measurementStringShort: String
    @Composable get() = with(measurement) {
        when (this) {
            is Measurement.Package -> stringResource(
                Res.string.x_times_y,
                quantity.formatClipZeros(),
                stringResource(Res.string.product_package)
            )

            is Measurement.Serving -> stringResource(
                Res.string.x_times_y,
                quantity.formatClipZeros(),
                stringResource(Res.string.product_serving)
            )

            is Measurement.Gram -> "${value.formatClipZeros()} " +
                stringResource(Res.string.unit_gram_short)
        }
    }

private val FoodWithMeasurement.measurementString: String?
    @Composable get() {
        val short = measurementStringShort
        val weight = weight?.formatClipZeros() ?: return null

        return when (measurement) {
            is Measurement.Gram -> short
            is Measurement.Package,
            is Measurement.Serving ->
                "$short ($weight ${stringResource(Res.string.unit_gram_short)})"
        }
    }

private val FoodWithMeasurement.caloriesString: String?
    @Composable get() = weight?.let {
        val calories = food.nutritionFacts.calories * it / 100f

        val value = (it * calories.value / 100f).roundToInt()
        "$value " + stringResource(Res.string.unit_kcal)
    }
