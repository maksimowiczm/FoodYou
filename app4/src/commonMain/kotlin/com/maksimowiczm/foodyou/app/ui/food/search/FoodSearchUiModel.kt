package com.maksimowiczm.foodyou.app.ui.food.search

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.account.domain.FavoriteFoodIdentity
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.Barcode
import com.maksimowiczm.foodyou.common.domain.food.FoodBrand
import com.maksimowiczm.foodyou.common.domain.food.FoodImage
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodNameSelector
import com.maksimowiczm.foodyou.common.domain.food.Grams
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
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

    companion object {
        fun from(identity: FavoriteFoodIdentity.OpenFoodFacts) =
            OpenFoodFacts(OpenFoodFactsProductIdentity(identity.barcode))

        fun from(identity: FavoriteFoodIdentity.UserFoodProduct, localAccountId: LocalAccountId) =
            UserFood(UserFoodProductIdentity(identity.id, localAccountId))

        fun from(identity: FavoriteFoodIdentity.FoodDataCentral) =
            FoodDataCentral(FoodDataCentralProductIdentity(identity.fdcId))
    }
}

@Immutable
sealed interface FoodSearchUiModel {
    val identity: FoodIdentity

    @Immutable data class Loading(override val identity: FoodIdentity) : FoodSearchUiModel

    @Immutable
    sealed interface Branded : FoodSearchUiModel {
        val name: FoodName?
        val brand: FoodBrand?
    }

    @Immutable
    data class Error(
        override val identity: FoodIdentity,
        override val name: FoodName?,
        override val brand: FoodBrand?,
        val error: Throwable,
    ) : Branded

    @Immutable
    data class Loaded(
        override val identity: FoodIdentity,
        override val name: FoodName,
        override val brand: FoodBrand?,
        val barcode: Barcode?,
        val image: FoodImage?,
        val nutritionFacts: NutritionFacts,
        val servingQuantity: AbsoluteQuantity?,
        val packageQuantity: AbsoluteQuantity?,
        val isLiquid: Boolean,
        val suggestedQuantity: Quantity,
    ) : Branded {
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
                        is Error ->
                            if (a.name != null) nameSelector.select(a.name)
                            else return@Comparator Int.MAX_VALUE
                    }

                val nameB =
                    when (b) {
                        is Loaded -> nameSelector.select(b.name)
                        is Loading -> return@Comparator Int.MAX_VALUE
                        is Error ->
                            if (b.name != null) nameSelector.select(b.name)
                            else return@Comparator Int.MAX_VALUE
                    }

                val brandA = a.brand?.value
                val brandB = b.brand?.value

                val result = nameA.compareTo(nameB, ignoreCase = true)
                if (result == 0 && brandA != null && brandB != null)
                    brandA.compareTo(brandB, ignoreCase = true)
                else result
            }
    }
}
