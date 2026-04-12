package com.maksimowiczm.foodyou.openfoodfacts.domain

import com.maksimowiczm.foodyou.common.domain.ImageUri
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts

data class OpenFoodFactsProduct(
    val identity: OpenFoodFactsProductIdentity,
    val name: FoodName,
    val brand: String?,
    val nutritionFacts: NutritionFacts,
    val servingQuantity: AbsoluteQuantity?,
    val packageQuantity: AbsoluteQuantity?,
    val thumbnail: ImageUri?,
    val image: ImageUri?,
    val source: String,
)
