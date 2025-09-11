package com.maksimowiczm.foodyou.feature.food.shared.ui.search

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.business.opensource.domain.search.FoodSearch
import com.maksimowiczm.foodyou.app.ui.shared.utility.LocalEnergyFormatter
import com.maksimowiczm.foodyou.feature.shared.ui.FoodErrorListItem
import com.maksimowiczm.foodyou.feature.shared.ui.FoodListItem
import com.maksimowiczm.foodyou.feature.shared.ui.FoodListItemSkeleton
import com.maksimowiczm.foodyou.feature.shared.ui.stringResourceWithWeight
import com.maksimowiczm.foodyou.food.domain.entity.Recipe
import com.maksimowiczm.foodyou.food.domain.usecase.ObserveFoodUseCase
import com.maksimowiczm.foodyou.shared.compose.utility.formatClipZeros
import com.maksimowiczm.foodyou.shared.domain.measurement.Measurement
import com.valentinilk.shimmer.Shimmer
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.mapNotNull
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
internal fun FoodSearchListItem(
    food: FoodSearch.Product,
    measurement: Measurement,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val weight = food.weight(measurement)
    val factor = weight?.div(100)

    if (factor == null) {
        return FoodErrorListItem(
            headline = food.headline,
            errorMessage = stringResource(Res.string.error_measurement_error),
            modifier = modifier,
            onClick = onClick,
        )
    }

    val measurementFacts = food.nutritionFacts * factor
    val proteins = measurementFacts.proteins.value
    val carbohydrates = measurementFacts.carbohydrates.value
    val fats = measurementFacts.fats.value
    val energy = measurementFacts.energy.value
    val measurementString =
        measurement.stringResourceWithWeight(
            totalWeight = food.totalWeight,
            servingWeight = food.servingWeight,
            isLiquid = food.isLiquid,
        )

    if (
        proteins == null ||
            carbohydrates == null ||
            fats == null ||
            energy == null ||
            measurementString == null
    ) {
        return FoodErrorListItem(
            headline = food.headline,
            modifier = modifier,
            onClick = onClick,
            errorMessage = stringResource(Res.string.error_food_is_missing_required_fields),
        )
    }

    FoodSearchListItem(
        headline = food.headline,
        proteins = proteins,
        carbohydrates = carbohydrates,
        fats = fats,
        energy = energy,
        measurement = { Text(measurementString) },
        isRecipe = false,
        onClick = onClick,
        modifier = modifier,
    )
}

/** Recipe has to be lazy loaded, so we use [ObserveFoodUseCase] to observe the recipe. */
@Composable
internal fun FoodSearchListItem(
    food: FoodSearch.Recipe,
    measurement: Measurement,
    onClick: () -> Unit,
    shimmer: Shimmer,
    modifier: Modifier = Modifier,
) {
    val observeRecipeUseCase: ObserveFoodUseCase = koinInject()

    val recipe =
        observeRecipeUseCase
            .observe(food.id)
            .mapNotNull { it as? Recipe }
            .collectAsStateWithLifecycle(null)
            .value

    if (recipe == null) {
        return FoodListItemSkeleton(shimmer)
    }

    val factor = recipe.weight(measurement) / 100
    val measurementFacts = recipe.nutritionFacts * factor
    val proteins = measurementFacts.proteins.value
    val carbohydrates = measurementFacts.carbohydrates.value
    val fats = measurementFacts.fats.value
    val energy = measurementFacts.energy.value

    val measurementString =
        measurement.stringResourceWithWeight(
            totalWeight = recipe.totalWeight,
            servingWeight = recipe.servingWeight,
            isLiquid = recipe.isLiquid,
        )

    if (
        proteins == null ||
            carbohydrates == null ||
            fats == null ||
            energy == null ||
            measurementString == null
    ) {
        return FoodErrorListItem(
            headline = food.headline,
            modifier = modifier,
            onClick = onClick,
            errorMessage = stringResource(Res.string.error_food_is_missing_required_fields),
        )
    }

    FoodSearchListItem(
        headline = food.headline,
        proteins = proteins,
        carbohydrates = carbohydrates,
        fats = fats,
        energy = energy,
        measurement = { Text(measurementString) },
        isRecipe = true,
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
private fun FoodSearchListItem(
    headline: String,
    proteins: Double,
    carbohydrates: Double,
    fats: Double,
    energy: Double,
    measurement: @Composable () -> Unit,
    isRecipe: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val g = stringResource(Res.string.unit_gram_short)

    FoodListItem(
        name = { Text(text = headline) },
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
        calories = { Text(LocalEnergyFormatter.current.formatEnergy(energy.roundToInt())) },
        measurement = measurement,
        isRecipe = isRecipe,
        modifier = modifier,
        onClick = onClick,
    )
}
