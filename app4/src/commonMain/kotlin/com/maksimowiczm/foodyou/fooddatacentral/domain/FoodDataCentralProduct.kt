package com.maksimowiczm.foodyou.fooddatacentral.domain

import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.Barcode
import com.maksimowiczm.foodyou.common.domain.food.FoodBrand
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts

class FoodDataCentralProduct(
    val identity: FoodDataCentralProductIdentity,
    val name: FoodName,
    val brand: FoodBrand?,
    val barcode: Barcode?,
    val source: String,
    val nutritionFacts: NutritionFacts,
    val servingQuantity: AbsoluteQuantity?,
    val packageQuantity: AbsoluteQuantity?,
    val isLiquid: Boolean,
)
