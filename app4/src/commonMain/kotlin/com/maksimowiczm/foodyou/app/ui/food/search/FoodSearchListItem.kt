package com.maksimowiczm.foodyou.app.ui.food.search

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.component.FoodErrorListItem
import com.maksimowiczm.foodyou.app.ui.common.component.FoodListItem
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalEnergyFormatter
import com.maksimowiczm.foodyou.app.ui.common.utility.formatClipZeros
import com.maksimowiczm.foodyou.app.ui.common.utility.stringResource
import com.maksimowiczm.foodyou.app.ui.food.LocalFoodNameSelector
import com.maksimowiczm.foodyou.app.ui.food.Thumbnail
import com.maksimowiczm.foodyou.common.domain.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.Quantity
import com.maksimowiczm.foodyou.common.expect
import com.maksimowiczm.foodyou.common.onError
import com.maksimowiczm.foodyou.food.search.domain.QuantityCalculator
import com.maksimowiczm.foodyou.food.search.domain.SearchableFoodDto
import com.valentinilk.shimmer.Shimmer
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FoodSearchListItem(
    food: SearchableFoodDto,
    quantity: Quantity,
    onClick: () -> Unit,
    shimmer: Shimmer,
    modifier: Modifier = Modifier,
) {
    val nameSelector = LocalFoodNameSelector.current
    val image = food.image?.let { @Composable { it.Thumbnail(shimmer, Modifier.size(56.dp)) } }

    val weight =
        QuantityCalculator.calculateAbsoluteQuantity(food, quantity)
            .onError {
                return FoodErrorListItem(
                    headline = food.localizedName(nameSelector),
                    image = image,
                    errorMessage = stringResource(Res.string.error_measurement_error),
                    modifier = modifier,
                    onClick = onClick,
                )
            }
            .expect("Quantity calculation should succeed")

    val factor =
        when (weight) {
            is AbsoluteQuantity.Volume -> weight.volume.milliliters / 100.0
            is AbsoluteQuantity.Weight -> weight.weight.grams / 100.0
        }

    val measurementFacts = food.nutritionFacts * factor
    val proteins = measurementFacts.proteins.value
    val carbohydrates = measurementFacts.carbohydrates.value
    val fats = measurementFacts.fats.value
    val energy = measurementFacts.energy.value
    val measurementString = quantity.stringResource(food.packageQuantity, food.servingQuantity)

    if (
        proteins == null ||
            carbohydrates == null ||
            fats == null ||
            energy == null ||
            measurementString == null
    ) {
        return FoodErrorListItem(
            headline = food.localizedName(nameSelector),
            image = image,
            modifier = modifier,
            onClick = onClick,
            errorMessage = stringResource(Res.string.error_food_is_missing_required_fields),
        )
    }

    FoodSearchListItem(
        headline = food.localizedName(nameSelector),
        proteins = proteins,
        carbohydrates = carbohydrates,
        fats = fats,
        energy = energy,
        quantity = { Text(measurementString) },
        image = image,
        isRecipe = false,
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
    quantity: @Composable () -> Unit,
    image: @Composable (() -> Unit)?,
    isRecipe: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val g = stringResource(Res.string.unit_gram_short)

    FoodListItem(
        name = { Text(text = headline) },
        image = image,
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
        energy = { Text(LocalEnergyFormatter.current.formatEnergy(energy.roundToInt())) },
        quantity = quantity,
        isRecipe = isRecipe,
        modifier = modifier,
        onClick = onClick,
    )
}
