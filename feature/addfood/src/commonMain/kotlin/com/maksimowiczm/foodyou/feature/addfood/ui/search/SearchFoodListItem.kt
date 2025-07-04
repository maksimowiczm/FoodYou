package com.maksimowiczm.foodyou.feature.addfood.ui.search

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.ui.ext.performToggle
import com.maksimowiczm.foodyou.core.ui.nutrition.FoodErrorListItem
import com.maksimowiczm.foodyou.core.ui.nutrition.FoodListItem
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.feature.addfood.model.SearchFoodItem
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun SearchFoodListItem(
    food: SearchFoodItem,
    onClick: () -> Unit,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape
) {
    val weight = food.weight
    val measurementString = food.measurementString
    val caloriesString = food.caloriesString
    if (weight == null || measurementString == null || caloriesString == null) {
        FoodErrorListItem(
            headline = food.headline,
            modifier = modifier
                .clip(shape)
                .clickable { onClick() }
        )
        return
    }

    val hapticFeedback = LocalHapticFeedback.current

    val verticalPadding by animateDpAsState(
        targetValue = if (food.isSelected) 8.dp else 0.dp,
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
    )

    val containerColor by animateColorAsState(
        targetValue = if (food.isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainer
        },
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()
    )

    val contentColor by animateColorAsState(
        targetValue = if (food.isSelected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()
    )

    val g = stringResource(Res.string.unit_gram_short)

    FoodListItem(
        name = { Text(food.headline) },
        proteins = {
            val proteins = (food.proteins * weight / 100f).formatClipZeros("%.1f")
            Text("$proteins $g")
        },
        carbohydrates = {
            val carbohydrates = (food.carbohydrates * weight / 100f).formatClipZeros("%.1f")
            Text("$carbohydrates $g")
        },
        fats = {
            val fats = (food.fats * weight / 100f).formatClipZeros("%.1f")
            Text("$fats $g")
        },
        calories = { Text(caloriesString) },
        measurement = { Text(measurementString) },
        modifier = modifier,
        onClick = onClick,
        trailingContent = {
            ToggleButton(
                checked = food.isSelected,
                onCheckedChange = {
                    hapticFeedback.performToggle(it)
                    onToggle(it)
                },
                colors = ToggleButtonDefaults.toggleButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    checkedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    checkedContainerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                if (food.isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null
                    )
                }
            }
        },
        containerColor = containerColor,
        contentColor = contentColor,
        shape = shape,
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = (12.dp + verticalPadding).coerceAtLeast(0.dp)
        )
    )
}

private val SearchFoodItem.measurementStringShort: String
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

            is Measurement.Milliliter -> "${value.formatClipZeros()} " +
                stringResource(Res.string.unit_milliliter_short)
        }
    }

private val SearchFoodItem.measurementString: String?
    @Composable get() {
        val short = measurementStringShort
        val weight = weight?.formatClipZeros() ?: return null
        val suffix = if (food.isLiquid) {
            stringResource(Res.string.unit_milliliter_short)
        } else {
            stringResource(Res.string.unit_gram_short)
        }

        return when (measurement) {
            is Measurement.Gram,
            is Measurement.Milliliter -> short

            is Measurement.Package,
            is Measurement.Serving ->
                "$short ($weight $suffix)"
        }
    }

private val SearchFoodItem.caloriesString: String?
    @Composable get() = weight?.let {
        val value = (it * calories / 100)
        "${value.roundToInt()} " + stringResource(Res.string.unit_kcal)
    }
