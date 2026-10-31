package com.maksimowiczm.foodyou.capabilities.foodbrowsing.swissfoodcomposition

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.FoodSearchListItem
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.expect
import com.maksimowiczm.foodyou.shared.ui.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.shared.ui.utility.QuantityFormatter.stringResource
import com.maksimowiczm.foodyou.swissfoodcomposition.domain.SwissFoodCompositionProduct

@Composable
fun SwissFoodCompositionListItem(
    food: SwissFoodCompositionProduct,
    onClick: (Quantity) -> Unit,
    interactionSource: MutableInteractionSource,
    shape: Shape,
    modifier: Modifier = Modifier,
    preferredQuantity: AbsoluteQuantity.Weight = AbsoluteQuantity.Weight(100.grams),
) {
    val factor = remember(preferredQuantity) { preferredQuantity.weight.grams / 100.0 }

    val energyKcal = food.energyKcal?.value?.times(factor)
    val protein = food.protein?.value?.times(factor)
    val carbohydrates = food.carbohydratesAvailable?.value?.times(factor)
    val fats = food.fatTotal?.value?.times(factor)

    val measurementString =
        preferredQuantity
            .stringResource(null, null)
            .expect("PreferredQuantity string can't be null")

    FoodSearchListItem(
        headline = LocalFoodNameSelector.current.select(food.name),
        proteins = protein?.value,
        carbohydrates = carbohydrates?.value,
        fats = fats?.value,
        energy = energyKcal?.value,
        quantity = { Text(measurementString) },
        image = null,
        onClick = { onClick(preferredQuantity) },
        interactionSource = interactionSource,
        shape = shape,
        modifier = modifier,
    )
}
