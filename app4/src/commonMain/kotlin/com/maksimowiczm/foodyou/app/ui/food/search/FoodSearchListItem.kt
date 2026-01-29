package com.maksimowiczm.foodyou.app.ui.food.search

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.component.FoodListItem
import com.maksimowiczm.foodyou.app.ui.common.component.Image
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalEnergyFormatter
import com.maksimowiczm.foodyou.app.ui.common.utility.QuantityFormatter.stringResource
import com.maksimowiczm.foodyou.app.ui.common.utility.formatClipZeros
import com.maksimowiczm.foodyou.app.ui.food.LocalFoodNameSelector
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.Grams
import com.maksimowiczm.foodyou.common.domain.food.Milliliters
import com.maksimowiczm.foodyou.common.domain.food.QuantityCalculator
import com.maksimowiczm.foodyou.common.fold
import com.valentinilk.shimmer.Shimmer
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FoodSearchListItem(
    food: FoodSearchUiModel.Loaded,
    onClick: () -> Unit,
    shimmer: Shimmer,
    modifier: Modifier = Modifier,
) {
    val nameSelector = LocalFoodNameSelector.current
    val image = food.image?.let { @Composable { it.Image(shimmer, Modifier.size(56.dp)) } }

    // Calculate quantity for 100 ml or 100 g
    val absoluteQuantity =
        remember(food) {
            QuantityCalculator.calculateAbsoluteQuantity(
                    food.suggestedQuantity,
                    food.packageQuantity,
                    food.servingQuantity,
                )
                .fold(
                    onSuccess = { it },
                    onError = {
                        if (food.isLiquid) AbsoluteQuantity.Volume(Milliliters(100.0))
                        else AbsoluteQuantity.Weight(Grams(100.0))
                    },
                )
        }

    val measurementFacts =
        remember(food, absoluteQuantity) {
            val factor =
                when (absoluteQuantity) {
                    is AbsoluteQuantity.Volume -> absoluteQuantity.volume.milliliters / 100.0
                    is AbsoluteQuantity.Weight -> absoluteQuantity.weight.grams / 100.0
                }

            food.nutritionFacts * factor
        }

    val measurementString =
        food.suggestedQuantity
            .stringResource(food.packageQuantity, food.servingQuantity)
            .fold(onSuccess = { it }, onError = { absoluteQuantity.stringResource() })

    FoodSearchListItem(
        headline = food.localizedName(nameSelector),
        proteins = measurementFacts.proteins.value,
        carbohydrates = measurementFacts.carbohydrates.value,
        fats = measurementFacts.fats.value,
        energy = measurementFacts.energy.value,
        quantity = { Text(measurementString) },
        image = image,
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val g = stringResource(Res.string.unit_gram_short)

    FoodListItem(
        headline = { Text(text = headline) },
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
            val text = LocalEnergyFormatter.current.formatEnergy(energy?.roundToInt())
            Text(text)
        },
        quantity = quantity,
        modifier = modifier,
        onClick = onClick,
    )
}
