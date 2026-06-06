package com.maksimowiczm.foodyou.search.domain

import com.maksimowiczm.foodyou.common.domain.BlobDigest
import com.maksimowiczm.foodyou.common.domain.Weight
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.userproduct.domain.UserProductBarcode
import com.maksimowiczm.foodyou.userproduct.domain.UserProductIdentity
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeIdentity

sealed interface SearchResult {
    data class UserProduct(
        val identity: UserProductIdentity,
        val name: FoodName,
        val brand: String?,
        val barcode: UserProductBarcode?,
        val note: String?,
        val image: BlobDigest?,
        val nutritionFacts: NutritionFacts,
        val servingQuantity: AbsoluteQuantity?,
        val packageQuantity: AbsoluteQuantity?,
        val isLiquid: Boolean,
    ) : SearchResult

    data class UserRecipe(
        val identity: UserRecipeIdentity,
        val name: FoodName,
        val note: String?,
        val image: BlobDigest?,
        val nutritionFacts: NutritionFacts,
        val servingWeight: Weight,
        val totalWeight: Weight,
    ) : SearchResult
}
