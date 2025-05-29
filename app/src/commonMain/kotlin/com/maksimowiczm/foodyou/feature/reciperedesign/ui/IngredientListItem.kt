package com.maksimowiczm.foodyou.feature.reciperedesign.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.ui.component.FoodErrorListItem
import com.maksimowiczm.foodyou.core.ui.component.FoodListItem
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.feature.reciperedesign.domain.Ingredient
import com.maksimowiczm.foodyou.feature.reciperedesign.domain.IngredientSearchItem
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun IngredientListItem(
    ingredient: IngredientSearchItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Transparent,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    shape: Shape = RectangleShape
) {
    val weight = ingredient.weight
    val measurementString = ingredient.measurementString
    val caloriesString = ingredient.caloriesString

    if (weight == null || measurementString == null || caloriesString == null) {
        FoodErrorListItem(
            headline = ingredient.headline,
            modifier = modifier
        )
        return
    }

    FoodListItem(
        name = { Text(ingredient.headline) },
        proteins = {
            val proteins = (ingredient.proteins * weight / 100f).formatClipZeros("%.1f")
            Text("$proteins " + stringResource(Res.string.unit_gram_short))
        },
        carbohydrates = {
            val carbohydrates = (ingredient.carbohydrates * weight / 100f).formatClipZeros("%.1f")
            Text("$carbohydrates " + stringResource(Res.string.unit_gram_short))
        },
        fats = {
            val fats = (ingredient.fats * weight / 100f).formatClipZeros("%.1f")
            Text("$fats " + stringResource(Res.string.unit_gram_short))
        },
        calories = { Text(caloriesString) },
        measurement = { Text(measurementString) },
        modifier = modifier,
        onClick = onClick,
        containerColor = containerColor,
        contentColor = contentColor,
        shape = shape
    )
}

private val IngredientSearchItem.measurementStringShort: String
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

private val IngredientSearchItem.measurementString: String?
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

private val IngredientSearchItem.caloriesString: String?
    @Composable get() = weight?.let {
        val value = (it * calories / 100).roundToInt()
        "$value " + stringResource(Res.string.unit_kcal)
    }

@Composable
internal fun IngredientListItem(
    ingredient: Ingredient,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Transparent,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    shape: Shape = RectangleShape
) {
    val weight = ingredient.weight
    val proteins = ingredient.proteins
    val carbohydrates = ingredient.carbohydrates
    val fats = ingredient.fats
    val caloriesString = ingredient.caloriesString
    val measurementString = ingredient.measurementString

    if (
        weight == null ||
        proteins == null ||
        carbohydrates == null ||
        fats == null ||
        caloriesString == null ||
        measurementString == null
    ) {
        FoodErrorListItem(
            headline = ingredient.food.headline,
            modifier = modifier
        )
        return
    }

    val g = stringResource(Res.string.unit_gram_short)

    FoodListItem(
        name = { Text(ingredient.food.headline) },
        proteins = {
            val proteins = (proteins * weight / 100f).formatClipZeros("%.1f")
            Text("$proteins $g")
        },
        carbohydrates = {
            val carbohydrates = (carbohydrates * weight / 100f).formatClipZeros("%.1f")
            Text("$carbohydrates $g")
        },
        fats = {
            val fats = (fats * weight / 100f).formatClipZeros("%.1f")
            Text("$fats $g")
        },
        calories = { Text(caloriesString) },
        measurement = { Text(measurementString) },
        modifier = modifier,
        onClick = onClick,
        containerColor = containerColor,
        contentColor = contentColor,
        shape = shape
    )
}

private val Ingredient.measurementStringShort: String
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

private val Ingredient.measurementString: String?
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

private val Ingredient.caloriesString: String?
    @Composable get() = weight?.let {
        val value = (it * food.nutritionFacts.calories.value / 100).roundToInt()
        "$value " + stringResource(Res.string.unit_kcal)
    }
