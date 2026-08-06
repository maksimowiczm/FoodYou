package com.maksimowiczm.foodyou.features.food.search.favoritefood

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.common.RemoteData
import com.maksimowiczm.foodyou.common.domain.FileUri
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.PackageQuantity
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.ServingQuantity
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.expect
import com.maksimowiczm.foodyou.features.food.search.FoodSearchListItem
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.shared.ui.component.Image
import com.maksimowiczm.foodyou.shared.ui.utility.FoodNameSelector
import com.maksimowiczm.foodyou.shared.ui.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.shared.ui.utility.QuantityFormatter.stringResource
import com.maksimowiczm.foodyou.shared.ui.utility.resolveBlob
import com.maksimowiczm.foodyou.userproduct.domain.UserProduct
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipe
import com.valentinilk.shimmer.Shimmer
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun FavoriteFoodListItem(
    food: RemoteData<Any>,
    shimmer: Shimmer,
    onClick: (Quantity) -> Unit,
    modifier: Modifier = Modifier,
    fallback: @Composable () -> Unit,
    preferredQuantity: Quantity =
        remember(food) {
            val sq = food.servingQuantity()
            val pq = food.packageQuantity()
            when {
                sq != null -> ServingQuantity(1.0)
                pq != null -> PackageQuantity(1.0)
                else -> AbsoluteQuantity.Weight(100.grams)
            }
        },
) {
    val nameSelector = LocalFoodNameSelector.current
    val headline = food.headline(nameSelector) ?: return fallback()
    val nutritionFacts = food.nutritionFacts() ?: return fallback()

    val image = food.image()
    val packageQuantity = food.packageQuantity()
    val servingQuantity = food.servingQuantity()
    val isRecipe = food.isRecipe()

    val factor =
        remember(preferredQuantity, packageQuantity, servingQuantity) {
            when (preferredQuantity) {
                is AbsoluteQuantity.Volume -> preferredQuantity.volume.milliliters / 100.0
                is AbsoluteQuantity.Weight -> preferredQuantity.weight.grams / 100.0
                is PackageQuantity ->
                    when (packageQuantity) {
                        is AbsoluteQuantity.Volume -> packageQuantity.volume.milliliters / 100.0
                        is AbsoluteQuantity.Weight -> packageQuantity.weight.grams / 100.0
                        null -> error("Unreachable")
                    }
                is ServingQuantity ->
                    when (servingQuantity) {
                        is AbsoluteQuantity.Volume -> servingQuantity.volume.milliliters / 100.0
                        is AbsoluteQuantity.Weight -> servingQuantity.weight.grams / 100.0
                        null -> error("Unreachable")
                    }
            }
        }

    val measurementFacts = remember(nutritionFacts, factor) { nutritionFacts * factor }

    val measurementString =
        preferredQuantity
            .stringResource(packageQuantity, servingQuantity)
            .expect("PreferredQuantity string can't be null")

    FoodSearchListItem(
        headline = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(headline)
                if (isRecipe) {
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(Res.drawable.ic_skillet_filled),
                        contentDescription = null,
                    )
                }
            }
        },
        proteins = measurementFacts.proteins.value,
        carbohydrates = measurementFacts.carbohydrates.value,
        fats = measurementFacts.fats.value,
        energy = measurementFacts.energy.value,
        quantity = { Text(measurementString) },
        image = image?.let { @Composable { it.Image(shimmer, Modifier.size(56.dp)) } },
        onClick = { onClick(preferredQuantity) },
        modifier = modifier,
    )
}

private fun Any.name(): FoodName =
    when (this) {
        is UserProduct -> name
        is UserRecipe -> name
        is OpenFoodFactsProduct -> name
        is FoodDataCentralProduct -> FoodName(english = name, fallback = name)
        else -> error("Unknown type ${this::class}")
    }

private fun Any.brand(): String? =
    when (this) {
        is UserProduct -> brand
        is UserRecipe -> null
        is OpenFoodFactsProduct -> brand
        is FoodDataCentralProduct -> brand
        else -> error("Unknown type ${this::class}")
    }

private fun Any.headline(nameSelector: FoodNameSelector): String {
    val name = nameSelector.select(name())
    val brand = brand()
    return if (brand != null) "$name ($brand)" else name
}

private fun RemoteData<Any>.headline(nameSelector: FoodNameSelector): String? =
    when (this) {
        is RemoteData.Success -> value.headline(nameSelector)
        is RemoteData.Error -> partialValue?.headline(nameSelector)
        is RemoteData.Loading -> partialValue?.headline(nameSelector)
        is RemoteData.NotFound -> null
    }

private fun Any.isRecipe(): Boolean = this is UserRecipe

private fun RemoteData<Any>.isRecipe(): Boolean =
    when (this) {
        is RemoteData.Success -> value.isRecipe()
        is RemoteData.Error -> partialValue?.isRecipe() ?: false
        is RemoteData.Loading -> partialValue?.isRecipe() ?: false
        is RemoteData.NotFound -> false
    }

private fun Any.packageQuantity(): AbsoluteQuantity? =
    when (this) {
        is UserProduct -> packageQuantity
        is UserRecipe -> AbsoluteQuantity.Weight(totalWeight)
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
        is UserRecipe -> AbsoluteQuantity.Weight(servingWeight)
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
        is UserRecipe -> nutritionFacts
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
        is UserRecipe -> image?.let { resolveBlob(it) }
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
