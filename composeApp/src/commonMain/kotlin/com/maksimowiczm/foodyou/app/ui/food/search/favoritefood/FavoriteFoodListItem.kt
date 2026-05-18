package com.maksimowiczm.foodyou.app.ui.food.search.favoritefood

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.component.Image
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.app.ui.common.utility.QuantityFormatter.stringResource
import com.maksimowiczm.foodyou.app.ui.common.utility.resolveBlob
import com.maksimowiczm.foodyou.app.ui.food.search.FoodSearchListItem
import com.maksimowiczm.foodyou.common.RemoteData
import com.maksimowiczm.foodyou.common.domain.FileUri
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.fold
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.userproduct.domain.UserProduct
import com.valentinilk.shimmer.Shimmer

@Composable
internal fun FavoriteFoodListItem(
    food: RemoteData<Any>,
    shimmer: Shimmer,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fallback: @Composable () -> Unit,
) {
    val name = food.name() ?: return fallback()
    val nutritionFacts = food.nutritionFacts() ?: return fallback()

    val image = food.image()
    val packageQuantity = food.packageQuantity()
    val servingQuantity = food.servingQuantity()

    val nameSelector = LocalFoodNameSelector.current

    val absoluteQuantity = packageQuantity ?: AbsoluteQuantity.Weight(100.grams)

    val measurementFacts =
        remember(absoluteQuantity, nutritionFacts) {
            val factor =
                when (absoluteQuantity) {
                    is AbsoluteQuantity.Volume -> absoluteQuantity.volume.milliliters / 100.0
                    is AbsoluteQuantity.Weight -> absoluteQuantity.weight.grams / 100.0
                }

            nutritionFacts * factor
        }

    val measurementString =
        absoluteQuantity
            .stringResource(packageQuantity, servingQuantity)
            .fold(onSuccess = { it }, onError = { absoluteQuantity.stringResource() })

    FoodSearchListItem(
        headline = nameSelector.select(name),
        proteins = measurementFacts.proteins.value,
        carbohydrates = measurementFacts.carbohydrates.value,
        fats = measurementFacts.fats.value,
        energy = measurementFacts.energy.value,
        quantity = { Text(measurementString) },
        image = image?.let { @Composable { it.Image(shimmer, Modifier.Companion.size(56.dp)) } },
        onClick = onClick,
        modifier = modifier,
    )
}

private fun Any.name(): FoodName =
    when (this) {
        is UserProduct -> name
        is OpenFoodFactsProduct -> name
        is FoodDataCentralProduct -> FoodName(english = name, fallback = name)
        else -> error("Unknown type ${this::class}")
    }

private fun RemoteData<Any>.name(): FoodName? =
    when (this) {
        is RemoteData.Success -> value.name()
        is RemoteData.Error -> partialValue?.name()
        is RemoteData.Loading -> partialValue?.name()
        is RemoteData.NotFound -> null
    }

private fun Any.packageQuantity(): AbsoluteQuantity? =
    when (this) {
        is UserProduct -> packageQuantity
        is OpenFoodFactsProduct -> packageQuantity
        is FoodDataCentralProduct -> packageQuantity
        else -> error("Unknown type ${this::class}")
    }

private fun RemoteData<Any>.packageQuantity(): AbsoluteQuantity? =
    when (this) {
        is RemoteData.Success -> value.packageQuantity()
        is RemoteData.Error -> partialValue?.packageQuantity()
        is RemoteData.Loading -> partialValue?.packageQuantity()
        is RemoteData.NotFound -> null
    }

private fun Any.servingQuantity(): AbsoluteQuantity? =
    when (this) {
        is UserProduct -> servingQuantity
        is OpenFoodFactsProduct -> servingQuantity
        is FoodDataCentralProduct -> servingQuantity
        else -> error("Unknown type ${this::class}")
    }

private fun RemoteData<Any>.servingQuantity(): AbsoluteQuantity? =
    when (this) {
        is RemoteData.Success -> value.servingQuantity()
        is RemoteData.Error -> partialValue?.servingQuantity()
        is RemoteData.Loading -> partialValue?.servingQuantity()
        is RemoteData.NotFound -> null
    }

private fun Any.nutritionFacts(): NutritionFacts =
    when (this) {
        is UserProduct -> nutritionFacts
        is OpenFoodFactsProduct -> nutritionFacts
        is FoodDataCentralProduct -> nutritionFacts
        else -> error("Unknown type ${this::class}")
    }

private fun RemoteData<Any>.nutritionFacts(): NutritionFacts? =
    when (this) {
        is RemoteData.Success -> value.nutritionFacts()
        is RemoteData.Error -> partialValue?.nutritionFacts()
        is RemoteData.Loading -> partialValue?.nutritionFacts()
        is RemoteData.NotFound -> null
    }

@Composable
private fun Any.image(): FileUri? =
    when (this) {
        is UserProduct -> image?.let { resolveBlob(it) }
        is OpenFoodFactsProduct -> image
        is FoodDataCentralProduct -> null
        else -> error("Unknown type ${this::class}")
    }

@Composable
private fun RemoteData<Any>.image(): FileUri? =
    when (this) {
        is RemoteData.Success -> value.image()
        is RemoteData.Error -> partialValue?.image()
        is RemoteData.Loading -> partialValue?.image()
        is RemoteData.NotFound -> null
    }
