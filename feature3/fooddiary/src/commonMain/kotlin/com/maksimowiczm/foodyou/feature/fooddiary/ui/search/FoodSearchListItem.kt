package com.maksimowiczm.foodyou.feature.fooddiary.ui.search

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.feature.fooddiary.domain.Food
import com.maksimowiczm.foodyou.feature.fooddiary.ui.FoodErrorListItem
import com.maksimowiczm.foodyou.feature.fooddiary.ui.FoodListItem
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import com.maksimowiczm.foodyou.feature.measurement.ui.stringResource
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun FoodSearchListItem(
    food: Food,
    measurement: Measurement,
    onClick: () -> Unit,
    shape: Shape,
    modifier: Modifier = Modifier
) {
    val factor = when (measurement) {
        is Measurement.Gram,
        is Measurement.Milliliter -> 1f

        is Measurement.Package -> food.totalWeight?.let { totalWeight ->
            measurement.weight(totalWeight) / 100f
        }

        is Measurement.Serving -> food.servingWeight?.let { servingWeight ->
            measurement.weight(servingWeight) / 100f
        }
    }

    if (factor == null) {
        return FoodErrorListItem(
            headline = food.headline,
            errorMessage = stringResource(Res.string.error_measurement_error),
            modifier = modifier,
            onClick = onClick,
            shape = shape
        )
    }

    val measurementFacts = food.nutritionFacts * factor
    val proteins = measurementFacts.proteins.value
    val carbohydrates = measurementFacts.carbohydrates.value
    val fats = measurementFacts.fats.value
    val energy = measurementFacts.energy.value

    if (proteins == null || carbohydrates == null || fats == null || energy == null) {
        return FoodErrorListItem(
            headline = food.headline,
            modifier = modifier,
            onClick = onClick,
            shape = shape,
            errorMessage = stringResource(Res.string.error_food_is_missing_required_fields)
        )
    }

    val g = stringResource(Res.string.unit_gram_short)

    FoodListItem(
        name = {
            Text(text = food.headline)
        },
        proteins = {
            val text = proteins.formatClipZeros()
            Text("$text $g")
        },
        carbohydrates = {
            val text = carbohydrates.formatClipZeros()
            Text("$text $g")
        },
        fats = {
            val text = fats.formatClipZeros()
            Text("$text $g")
        },
        calories = {
            val kcal = stringResource(Res.string.unit_kcal)
            val text = energy.formatClipZeros("%.0f")
            Text("$text $kcal")
        },
        measurement = {
            val weight = when (measurement) {
                is Measurement.Gram,
                is Measurement.Milliliter -> null

                is Measurement.Package -> food.totalWeight?.let(measurement::weight)
                is Measurement.Serving -> food.servingWeight?.let(measurement::weight)
            }

            val text = buildString {
                append(measurement.stringResource())
                if (weight != null) {
                    append(" (${weight.formatClipZeros()} $g)")
                }
            }

            Text(text)
        },
        modifier = modifier,
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = shape
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AnimatedCheckIcon(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
    strokeWidth: Dp = 2.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
    )

    Canvas(modifier = modifier.size(100.dp)) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Define check mark path
        val checkPath = Path().apply {
            moveTo(canvasWidth * 0.2f, canvasHeight * 0.5f)
            lineTo(canvasWidth * 0.4f, canvasHeight * 0.7f)
            lineTo(canvasWidth * 0.8f, canvasHeight * 0.3f)
        }

        // Only draw if there's progress to show
        if (animatedProgress > 0f) {
            // Calculate path length for animation
            val pathMeasure = PathMeasure()
            pathMeasure.setPath(checkPath, false)
            val pathLength = pathMeasure.length

            // Draw the animated path
            drawPath(
                path = checkPath,
                color = color,
                style = Stroke(
                    width = strokeWidth.toPx(),
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(
                            pathLength * animatedProgress,
                            pathLength
                        )
                    )
                )
            )
        }
    }
}
