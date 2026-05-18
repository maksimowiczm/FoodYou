package com.maksimowiczm.foodyou.app.ui.food.search.openfoodfacts

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.component.Image
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.app.ui.common.utility.QuantityFormatter.stringResource
import com.maksimowiczm.foodyou.app.ui.food.search.FoodSearchListItem
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.fold
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.valentinilk.shimmer.Shimmer

@Composable
internal fun OpenFoodFactsListItem(
    food: OpenFoodFactsProduct,
    onClick: () -> Unit,
    shimmer: Shimmer,
    modifier: Modifier = Modifier,
) {
    val nameSelector = LocalFoodNameSelector.current

    val absoluteQuantity = food.packageQuantity ?: AbsoluteQuantity.Weight(100.grams)

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
        absoluteQuantity.stringResource(food.packageQuantity, food.servingQuantity).fold(
            onSuccess = { it }
        ) {
            absoluteQuantity.stringResource()
        }

    val headline =
        remember(food, nameSelector) {
            buildString {
                append(nameSelector.select(food.name))
                append(food.brand?.let { " ($it)" } ?: "")
            }
        }

    FoodSearchListItem(
        headline = headline,
        proteins = measurementFacts.proteins.value,
        carbohydrates = measurementFacts.carbohydrates.value,
        fats = measurementFacts.fats.value,
        energy = measurementFacts.energy.value,
        quantity = { Text(measurementString) },
        image = food.image?.let { @Composable { it.Image(shimmer, Modifier.size(56.dp)) } },
        onClick = onClick,
        modifier = modifier,
    )
}
