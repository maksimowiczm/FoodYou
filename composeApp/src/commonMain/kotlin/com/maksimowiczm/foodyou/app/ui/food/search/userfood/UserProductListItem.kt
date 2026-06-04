package com.maksimowiczm.foodyou.app.ui.food.search.userfood

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.component.Image
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.app.ui.common.utility.QuantityFormatter.stringResource
import com.maksimowiczm.foodyou.app.ui.common.utility.resolveBlob
import com.maksimowiczm.foodyou.app.ui.food.search.FoodSearchListItem
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.milliliters
import com.maksimowiczm.foodyou.common.fold
import com.maksimowiczm.foodyou.search.domain.SearchResult
import com.maksimowiczm.foodyou.userproduct.domain.UserProduct
import com.valentinilk.shimmer.Shimmer

@Composable
internal fun UserProductListItem(
    product: SearchResult.UserProduct,
    onClick: () -> Unit,
    shimmer: Shimmer,
    modifier: Modifier = Modifier,
) {
    val nameSelector = LocalFoodNameSelector.current

    val absoluteQuantity =
        when (product.isLiquid) {
            false -> AbsoluteQuantity.Weight(100.grams)
            true -> AbsoluteQuantity.Volume(100.milliliters)
        }

    val measurementFacts =
        remember(product, absoluteQuantity) {
            val factor =
                when (absoluteQuantity) {
                    is AbsoluteQuantity.Volume -> absoluteQuantity.volume.milliliters / 100.0
                    is AbsoluteQuantity.Weight -> absoluteQuantity.weight.grams / 100.0
                }

            product.nutritionFacts * factor
        }

    val measurementString =
        absoluteQuantity.stringResource(product.packageQuantity, product.servingQuantity).fold(
            onSuccess = { it }
        ) {
            absoluteQuantity.stringResource()
        }

    val headline =
        remember(product, nameSelector) {
            buildString {
                append(nameSelector.select(product.name))
                append(product.brand?.let { " ($it)" } ?: "")
            }
        }

    FoodSearchListItem(
        headline = headline,
        proteins = measurementFacts.proteins.value,
        carbohydrates = measurementFacts.carbohydrates.value,
        fats = measurementFacts.fats.value,
        energy = measurementFacts.energy.value,
        quantity = { Text(measurementString) },
        image =
            product.image?.let {
                @Composable { resolveBlob(it).Image(shimmer, Modifier.size(56.dp)) }
            },
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
internal fun UserProductListItem(
    product: UserProduct,
    onClick: () -> Unit,
    shimmer: Shimmer,
    modifier: Modifier = Modifier,
) {
    val nameSelector = LocalFoodNameSelector.current

    val absoluteQuantity =
        when (product.isLiquid) {
            false -> AbsoluteQuantity.Weight(100.grams)
            true -> AbsoluteQuantity.Volume(100.milliliters)
        }

    val measurementFacts =
        remember(product, absoluteQuantity) {
            val factor =
                when (absoluteQuantity) {
                    is AbsoluteQuantity.Volume -> absoluteQuantity.volume.milliliters / 100.0
                    is AbsoluteQuantity.Weight -> absoluteQuantity.weight.grams / 100.0
                }

            product.nutritionFacts * factor
        }

    val measurementString =
        absoluteQuantity.stringResource(product.packageQuantity, product.servingQuantity).fold(
            onSuccess = { it }
        ) {
            absoluteQuantity.stringResource()
        }

    val headline =
        remember(product, nameSelector) {
            buildString {
                append(nameSelector.select(product.name))
                append(product.brand?.let { " ($it)" } ?: "")
            }
        }

    FoodSearchListItem(
        headline = headline,
        proteins = measurementFacts.proteins.value,
        carbohydrates = measurementFacts.carbohydrates.value,
        fats = measurementFacts.fats.value,
        energy = measurementFacts.energy.value,
        quantity = { Text(measurementString) },
        image =
            product.image?.let {
                @Composable { resolveBlob(it).Image(shimmer, Modifier.size(56.dp)) }
            },
        onClick = onClick,
        modifier = modifier,
    )
}
