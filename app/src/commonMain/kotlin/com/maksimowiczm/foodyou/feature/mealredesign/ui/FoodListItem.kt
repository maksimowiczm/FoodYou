package com.maksimowiczm.foodyou.feature.mealredesign.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.domain.model.FoodWithMeasurement
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.core.ui.theme.LocalNutrientsPalette
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.action_show_more
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
fun FoodListItem(
    foodWithMeasurement: FoodWithMeasurement,
    onMore: () -> Unit,
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
            onMore = onMore,
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
            trailingContent = {
                IconButton(
                    onClick = onMore
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(Res.string.action_show_more)
                    )
                }
            },
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FoodListErrorItem(headline: String, onMore: () -> Unit, modifier: Modifier = Modifier) {
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
        trailingContent = {
            IconButton(
                onClick = onMore,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(Res.string.action_show_more)
                )
            }
        },
        modifier = modifier,
        color = MaterialTheme.colorScheme.errorContainer
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun FoodListItem(
    headlineContent: @Composable () -> Unit,
    supportingContent: @Composable () -> Unit,
    trailingContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Surface(
        modifier = modifier,
        color = color
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
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

            trailingContent()
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
