package com.maksimowiczm.foodyou.app.ui.food.search

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.component.FoodListItem
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalEnergyFormatter
import com.maksimowiczm.foodyou.app.ui.common.utility.formatClipZeros
import com.maksimowiczm.foodyou.app.ui.common.utility.stringResource
import com.maksimowiczm.foodyou.app.ui.food.LocalFoodNameSelector
import com.maksimowiczm.foodyou.app.ui.food.Thumbnail
import com.maksimowiczm.foodyou.common.domain.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.Grams
import com.maksimowiczm.foodyou.common.domain.Milliliters
import com.maksimowiczm.foodyou.common.fold
import com.maksimowiczm.foodyou.food.search.domain.QuantityCalculator
import com.maksimowiczm.foodyou.food.search.domain.SearchableFoodDto
import com.valentinilk.shimmer.Shimmer
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FoodSearchListItem(
    food: SearchableFoodDto,
    onClick: () -> Unit,
    shimmer: Shimmer,
    modifier: Modifier = Modifier,
) {
    val nameSelector = LocalFoodNameSelector.current
    val image = food.image?.let { @Composable { it.Thumbnail(shimmer, Modifier.size(56.dp)) } }

    // Calculate quantity for 100 ml or 100 g
    val absoluteQuantity =
        QuantityCalculator.calculateAbsoluteQuantity(food)
            .fold(
                onSuccess = { it },
                onError = { error ->
                    if (food.isLiquid) AbsoluteQuantity.Volume(Milliliters(100.0))
                    else AbsoluteQuantity.Weight(Grams(100.0))
                },
            )

    val factor =
        when (absoluteQuantity) {
            is AbsoluteQuantity.Volume -> absoluteQuantity.volume.milliliters / 100.0
            is AbsoluteQuantity.Weight -> absoluteQuantity.weight.grams / 100.0
        }

    val measurementFacts = food.nutritionFacts * factor
    val proteins = measurementFacts.proteins.value
    val carbohydrates = measurementFacts.carbohydrates.value
    val fats = measurementFacts.fats.value
    val energy = measurementFacts.energy.value
    val measurementString =
        food.suggestedQuantity.stringResource(food.packageQuantity, food.servingQuantity)
            ?: absoluteQuantity.stringResource()

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
    proteins: Double?,
    carbohydrates: Double?,
    fats: Double?,
    energy: Double?,
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
            val text = proteins?.formatClipZeros() ?: "?"
            Text("$text $g")
        },
        carbohydrates = {
            val text = carbohydrates?.formatClipZeros() ?: "?"
            Text("$text $g")
        },
        fats = {
            val text = fats?.formatClipZeros() ?: "?"
            Text("$text $g")
        },
        energy = {
            val text =
                energy?.let { LocalEnergyFormatter.current.formatEnergy(it.roundToInt()) }
                    ?: ("? " + LocalEnergyFormatter.current.suffix())
            Text(text)
        },
        quantity = quantity,
        isRecipe = isRecipe,
        modifier = modifier,
        onClick = onClick,
    )
}
