package com.maksimowiczm.foodyou.capabilities.foodbrowsing.openfoodfacts

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.request.CachePolicy
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.FoodSearchListItem
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.PackageQuantity
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.ServingQuantity
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.expect
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.shared.ui.component.Image
import com.maksimowiczm.foodyou.shared.ui.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.shared.ui.utility.LocalUIFeatureFlags
import com.maksimowiczm.foodyou.shared.ui.utility.QuantityFormatter.stringResource
import com.maksimowiczm.foodyou.shared.ui.utility.headline
import com.valentinilk.shimmer.Shimmer

@Composable
fun OpenFoodFactsListItem(
    food: OpenFoodFactsProduct,
    onClick: (Quantity) -> Unit,
    shimmer: Shimmer,
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
                    when (val packageQuantity = food.packageQuantity) {
                        is AbsoluteQuantity.Volume -> packageQuantity.volume.milliliters / 100.0
                        is AbsoluteQuantity.Weight -> packageQuantity.weight.grams / 100.0
                        null -> error("Unreachable")
                    }
                is ServingQuantity ->
                    when (val servingQuantity = food.servingQuantity) {
                        is AbsoluteQuantity.Volume -> servingQuantity.volume.milliliters / 100.0
                        is AbsoluteQuantity.Weight -> servingQuantity.weight.grams / 100.0
                        null -> error("Unreachable")
                    }
            }
        }

    val measurementFacts = remember(food.nutritionFacts, factor) { food.nutritionFacts * factor }

    val measurementString =
        preferredQuantity
            .stringResource(food.packageQuantity, food.servingQuantity)
            .expect("PreferredQuantity string can't be null")

    val downloadImages = LocalUIFeatureFlags.current.downloadOpenFoodFactsSearchImages

    FoodSearchListItem(
        headline = food.headline(LocalFoodNameSelector.current),
        proteins = measurementFacts.proteins.value,
        carbohydrates = measurementFacts.carbohydrates.value,
        fats = measurementFacts.fats.value,
        energy = measurementFacts.energy.value,
        quantity = { Text(measurementString) },
        image =
            food.image?.let {
                @Composable {
                    it.Image(
                        shimmer = shimmer,
                        modifier = Modifier.size(56.dp),
                        networkCachePolicy =
                            if (downloadImages) CachePolicy.ENABLED else CachePolicy.DISABLED,
                    )
                }
            },
        onClick = { onClick(preferredQuantity) },
        modifier = modifier,
    )
}
