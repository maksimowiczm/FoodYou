package com.maksimowiczm.foodyou.app.ui.food.search

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.common.domain.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.Barcode
import com.maksimowiczm.foodyou.common.domain.FoodBrand
import com.maksimowiczm.foodyou.common.domain.FoodImage
import com.maksimowiczm.foodyou.common.domain.FoodName
import com.maksimowiczm.foodyou.common.domain.FoodNameSelector
import com.maksimowiczm.foodyou.common.domain.Grams
import com.maksimowiczm.foodyou.common.domain.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.Quantity
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.userfood.domain.UserFoodProduct
import com.maksimowiczm.foodyou.userfood.domain.UserFoodProductIdentity

@Immutable
sealed interface FoodIdentity {

    @Immutable data class OpenFoodFacts(val identity: OpenFoodFactsProductIdentity) : FoodIdentity

    @Immutable data class UserFood(val identity: UserFoodProductIdentity) : FoodIdentity

    @Immutable
    data class FoodDataCentral(val identity: FoodDataCentralProductIdentity) : FoodIdentity
}

@Immutable
sealed interface FoodSearchUiModel {
    val identity: FoodIdentity

    @Immutable data class Loading(override val identity: FoodIdentity) : FoodSearchUiModel

    @Immutable
    data class Loaded(
        override val identity: FoodIdentity,
        val name: FoodName,
        val brand: FoodBrand?,
        val barcode: Barcode?,
        val image: FoodImage?,
        val nutritionFacts: NutritionFacts,
        val servingQuantity: AbsoluteQuantity?,
        val packageQuantity: AbsoluteQuantity?,
        val isLiquid: Boolean,
        val suggestedQuantity: Quantity,
    ) : FoodSearchUiModel {
        fun localizedName(foodNameSelector: FoodNameSelector): String {
            val brandSuffix = brand?.let { " (${it.value})" } ?: ""
            return foodNameSelector.select(name) + brandSuffix
        }

        constructor(
            userFoodProduct: UserFoodProduct
        ) : this(
            identity = FoodIdentity.UserFood(userFoodProduct.identity),
            name = userFoodProduct.name,
            brand = userFoodProduct.brand,
            barcode = userFoodProduct.barcode,
            image = userFoodProduct.image,
            nutritionFacts = userFoodProduct.nutritionFacts,
            servingQuantity = userFoodProduct.servingQuantity,
            packageQuantity = userFoodProduct.packageQuantity,
            isLiquid = userFoodProduct.isLiquid,
            suggestedQuantity = AbsoluteQuantity.Weight(Grams(100.0)),
        )

        constructor(
            openFoodFactsProduct: OpenFoodFactsProduct
        ) : this(
            identity = FoodIdentity.OpenFoodFacts(openFoodFactsProduct.identity),
            name = openFoodFactsProduct.name,
            brand = openFoodFactsProduct.brand,
            barcode = openFoodFactsProduct.barcode,
            image = openFoodFactsProduct.image,
            nutritionFacts = openFoodFactsProduct.nutritionFacts,
            servingQuantity = openFoodFactsProduct.servingQuantity,
            packageQuantity = openFoodFactsProduct.packageQuantity,
            isLiquid = openFoodFactsProduct.isLiquid,
            suggestedQuantity = AbsoluteQuantity.Weight(Grams(100.0)),
        )

        constructor(
            foodDataCentralProduct: FoodDataCentralProduct
        ) : this(
            identity = FoodIdentity.FoodDataCentral(foodDataCentralProduct.identity),
            name = foodDataCentralProduct.name,
            brand = foodDataCentralProduct.brand,
            barcode = foodDataCentralProduct.barcode,
            image = foodDataCentralProduct.image,
            nutritionFacts = foodDataCentralProduct.nutritionFacts,
            servingQuantity = foodDataCentralProduct.servingQuantity,
            packageQuantity = foodDataCentralProduct.packageQuantity,
            isLiquid = foodDataCentralProduct.isLiquid,
            suggestedQuantity = AbsoluteQuantity.Weight(Grams(100.0)),
        )
    }

    companion object {
        fun comparator(nameSelector: FoodNameSelector) =
            Comparator<FoodSearchUiModel> { a, b ->
                val nameA =
                    when (a) {
                        is Loaded -> nameSelector.select(a.name)
                        is Loading -> return@Comparator Int.MAX_VALUE
                    }

                val nameB =
                    when (b) {
                        is Loaded -> nameSelector.select(b.name)
                        is Loading -> return@Comparator Int.MAX_VALUE
                    }

                val result = nameA.compareTo(nameB, ignoreCase = true)
                if (result == 0 && a.brand != null && b.brand != null)
                    a.brand.value.compareTo(b.brand.value, ignoreCase = true)
                else result
            }
    }
}
