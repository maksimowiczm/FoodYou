package com.maksimowiczm.foodyou.features.food.search.userfood

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.PackageQuantity
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.ServingQuantity
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.milliliters
import com.maksimowiczm.foodyou.common.expect
import com.maksimowiczm.foodyou.features.food.search.FoodSearchListItem
import com.maksimowiczm.foodyou.search.domain.SearchResult
import com.maksimowiczm.foodyou.shared.ui.component.Image
import com.maksimowiczm.foodyou.shared.ui.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.shared.ui.utility.QuantityFormatter.stringResource
import com.maksimowiczm.foodyou.shared.ui.utility.headline
import com.maksimowiczm.foodyou.shared.ui.utility.resolveBlob
import com.valentinilk.shimmer.Shimmer

@Composable
internal fun UserProductListItem(
    product: SearchResult.UserProduct,
    onClick: (Quantity) -> Unit,
    shimmer: Shimmer,
    modifier: Modifier = Modifier,
    preferredQuantity: Quantity =
        remember(product.servingQuantity, product.packageQuantity, product.isLiquid) {
            when {
                product.servingQuantity != null -> ServingQuantity(1.0)
                product.packageQuantity != null -> PackageQuantity(1.0)
                product.isLiquid -> AbsoluteQuantity.Volume(100.milliliters)
                else -> AbsoluteQuantity.Weight(100.grams)
            }
        },
) {
    val factor =
        remember(preferredQuantity, product.packageQuantity, product.servingQuantity) {
            when (preferredQuantity) {
                is AbsoluteQuantity.Volume -> preferredQuantity.volume.milliliters / 100.0
                is AbsoluteQuantity.Weight -> preferredQuantity.weight.grams / 100.0
                is PackageQuantity ->
                    when (val packageQuantity = product.packageQuantity) {
                        is AbsoluteQuantity.Volume -> packageQuantity.volume.milliliters / 100.0
                        is AbsoluteQuantity.Weight -> packageQuantity.weight.grams / 100.0
                        null -> error("Unreachable")
                    }
                is ServingQuantity ->
                    when (val servingQuantity = product.servingQuantity) {
                        is AbsoluteQuantity.Volume -> servingQuantity.volume.milliliters / 100.0
                        is AbsoluteQuantity.Weight -> servingQuantity.weight.grams / 100.0
                        null -> error("Unreachable")
                    }
            }
        }

    val measurementFacts =
        remember(product.nutritionFacts, factor) { product.nutritionFacts * factor }

    val measurementString =
        preferredQuantity
            .stringResource(product.packageQuantity, product.servingQuantity)
            .expect("PreferredQuantity string can't be null")

    FoodSearchListItem(
        headline = product.headline(LocalFoodNameSelector.current),
        proteins = measurementFacts.proteins.value,
        carbohydrates = measurementFacts.carbohydrates.value,
        fats = measurementFacts.fats.value,
        energy = measurementFacts.energy.value,
        quantity = { Text(measurementString) },
        image =
            product.image?.let {
                @Composable { resolveBlob(it).Image(shimmer, Modifier.size(56.dp)) }
            },
        onClick = { onClick(preferredQuantity) },
        modifier = modifier,
    )
}
