package com.maksimowiczm.foodyou.app.ui.food.search.fooddatacentral

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.app.ui.common.utility.QuantityFormatter.stringResource
import com.maksimowiczm.foodyou.app.ui.food.search.FoodSearchListItem
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.PackageQuantity
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.ServingQuantity
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.expect
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct

@Composable
internal fun FoodDataCentralListItem(
    food: FoodDataCentralProduct,
    onClick: (Quantity) -> Unit,
    modifier: Modifier = Modifier,
    preferredQuantity: Quantity =
        remember(food.servingQuantity, food.packageQuantity) {
            when {
                food.servingQuantity != null -> ServingQuantity(1.0)
                food.packageQuantity != null -> PackageQuantity(1.0)
                else -> AbsoluteQuantity.Weight(100.grams)
            }
        },
) {
    val factor =
        remember(preferredQuantity, food.packageQuantity, food.servingQuantity) {
            when (preferredQuantity) {
                is AbsoluteQuantity.Volume -> preferredQuantity.volume.milliliters / 100.0
                is AbsoluteQuantity.Weight -> preferredQuantity.weight.grams / 100.0
                is PackageQuantity ->
                    when (food.packageQuantity) {
                        is AbsoluteQuantity.Volume ->
                            food.packageQuantity.volume.milliliters / 100.0
                        is AbsoluteQuantity.Weight -> food.packageQuantity.weight.grams / 100.0
                        null -> error("Unreachable")
                    }
                is ServingQuantity ->
                    when (food.servingQuantity) {
                        is AbsoluteQuantity.Volume ->
                            food.servingQuantity.volume.milliliters / 100.0
                        is AbsoluteQuantity.Weight -> food.servingQuantity.weight.grams / 100.0
                        null -> error("Unreachable")
                    }
            }
        }

    val measurementFacts = remember(food.nutritionFacts, factor) { food.nutritionFacts * factor }

    val measurementString =
        preferredQuantity
            .stringResource(food.packageQuantity, food.servingQuantity)
            .expect("PreferredQuantity string can't be null")

    FoodSearchListItem(
        headline = food.headline,
        proteins = measurementFacts.proteins.value,
        carbohydrates = measurementFacts.carbohydrates.value,
        fats = measurementFacts.fats.value,
        energy = measurementFacts.energy.value,
        quantity = { Text(measurementString) },
        image = null,
        onClick = { onClick(preferredQuantity) },
        modifier = modifier,
    )
}
