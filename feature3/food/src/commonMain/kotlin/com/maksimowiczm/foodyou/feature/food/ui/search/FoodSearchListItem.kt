package com.maksimowiczm.foodyou.feature.food.ui.search

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.feature.food.domain.FoodSearch
import com.maksimowiczm.foodyou.feature.food.domain.ObserveRecipeUseCase
import com.maksimowiczm.foodyou.feature.food.domain.weight
import com.maksimowiczm.foodyou.feature.food.ui.FoodErrorListItem
import com.maksimowiczm.foodyou.feature.food.ui.FoodListItem
import com.maksimowiczm.foodyou.feature.food.ui.FoodListItemSkeleton
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import com.maksimowiczm.foodyou.feature.measurement.ui.stringResource
import com.valentinilk.shimmer.Shimmer
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun FoodSearchListItem(
    food: FoodSearch.Product,
    measurement: Measurement,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val factor = measurement.weight(food)?.div(100f)

    if (factor == null) {
        return FoodErrorListItem(
            headline = food.headline,
            errorMessage = stringResource(Res.string.error_measurement_error),
            modifier = modifier,
            onClick = onClick
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
        onClick = onClick
    )
}

/**
 * Recipe has to be lazy loaded, so we use [ObserveRecipeUseCase] to observe the recipe.
 */
@Composable
internal fun FoodSearchListItem(
    food: FoodSearch.Recipe,
    measurement: Measurement,
    onClick: () -> Unit,
    shimmer: Shimmer,
    modifier: Modifier = Modifier,
    observeRecipeUseCase: ObserveRecipeUseCase = koinInject()
) {
    val recipe = observeRecipeUseCase(food.id).collectAsStateWithLifecycle(null).value

    if (recipe == null) {
        return FoodListItemSkeleton(shimmer)
    }

    val factor = measurement.weight(recipe) / 100f
    val measurementFacts = recipe.nutritionFacts * factor
    val proteins = measurementFacts.proteins.value
    val carbohydrates = measurementFacts.carbohydrates.value
    val fats = measurementFacts.fats.value
    val energy = measurementFacts.energy.value

    if (proteins == null || carbohydrates == null || fats == null || energy == null) {
        return FoodErrorListItem(
            headline = food.headline,
            modifier = modifier,
            onClick = onClick,
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

                is Measurement.Package -> recipe.totalWeight.let(measurement::weight)
                is Measurement.Serving -> recipe.servingWeight.let(measurement::weight)
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
        onClick = onClick
    )
}
