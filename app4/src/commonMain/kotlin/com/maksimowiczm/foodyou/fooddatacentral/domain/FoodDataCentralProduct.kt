package com.maksimowiczm.foodyou.fooddatacentral.domain

import com.maksimowiczm.foodyou.common.domain.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.Barcode
import com.maksimowiczm.foodyou.common.domain.FoodBrand
import com.maksimowiczm.foodyou.common.domain.FoodImage
import com.maksimowiczm.foodyou.common.domain.FoodName
import com.maksimowiczm.foodyou.common.domain.FoodNote
import com.maksimowiczm.foodyou.common.domain.FoodSource
import com.maksimowiczm.foodyou.common.domain.NutritionFacts

class FoodDataCentralProduct(
    val identity: FoodDataCentralProductIdentity,
    val name: FoodName,
    val brand: FoodBrand?,
    val barcode: Barcode?,
    val note: FoodNote?,
    val image: FoodImage?,
    val source: FoodSource?,
    val nutritionFacts: NutritionFacts,
    val servingQuantity: AbsoluteQuantity?,
    val packageQuantity: AbsoluteQuantity?,
    val isLiquid: Boolean,
)
