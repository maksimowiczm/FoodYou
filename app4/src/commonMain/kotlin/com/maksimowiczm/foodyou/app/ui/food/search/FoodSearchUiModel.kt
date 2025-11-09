package com.maksimowiczm.foodyou.app.ui.food.search

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.common.domain.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.Grams
import com.maksimowiczm.foodyou.common.domain.Quantity
import com.maksimowiczm.foodyou.food.domain.FoodIdentity
import com.maksimowiczm.foodyou.food.domain.FoodImage
import com.maksimowiczm.foodyou.food.domain.FoodName
import com.maksimowiczm.foodyou.food.domain.FoodNameSelector
import com.maksimowiczm.foodyou.food.domain.FoodProductDto
import com.maksimowiczm.foodyou.food.domain.NutritionFacts
import com.maksimowiczm.foodyou.food.search.domain.SearchableFoodDto

@Immutable
sealed interface FoodSearchUiModel {
    val identity: FoodIdentity

    @Immutable data class Loading(override val identity: FoodIdentity) : FoodSearchUiModel

    @Immutable
    data class Loaded(
        override val identity: FoodIdentity,
        val name: FoodName,
        val brand: String?,
        val barcode: String?,
        val image: FoodImage?,
        val nutritionFacts: NutritionFacts,
        val servingQuantity: AbsoluteQuantity?,
        val packageQuantity: AbsoluteQuantity?,
        val isLiquid: Boolean,
        val suggestedQuantity: Quantity,
    ) : FoodSearchUiModel {
        fun localizedName(foodNameSelector: FoodNameSelector): String {
            val brandSuffix = brand?.let { " ($it)" } ?: ""
            return foodNameSelector.select(name) + brandSuffix
        }

        constructor(
            searchableFoodDto: SearchableFoodDto
        ) : this(
            identity = searchableFoodDto.identity,
            name = searchableFoodDto.name,
            brand = searchableFoodDto.brand?.value,
            barcode = null,
            image = searchableFoodDto.image,
            nutritionFacts = searchableFoodDto.nutritionFacts,
            servingQuantity = searchableFoodDto.servingQuantity,
            packageQuantity = searchableFoodDto.packageQuantity,
            isLiquid = searchableFoodDto.isLiquid,
            suggestedQuantity = searchableFoodDto.suggestedQuantity,
        )

        constructor(
            foodProductDto: FoodProductDto
        ) : this(
            identity = foodProductDto.identity,
            name = foodProductDto.name,
            brand = foodProductDto.brand?.value,
            barcode = foodProductDto.barcode?.value,
            image = foodProductDto.image,
            nutritionFacts = foodProductDto.nutritionFacts,
            servingQuantity = foodProductDto.servingQuantity,
            packageQuantity = foodProductDto.packageQuantity,
            isLiquid = foodProductDto.isLiquid,
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
                    a.brand.compareTo(b.brand, ignoreCase = true)
                else result
            }
    }
}
