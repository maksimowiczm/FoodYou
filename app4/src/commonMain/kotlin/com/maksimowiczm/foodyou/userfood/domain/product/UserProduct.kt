package com.maksimowiczm.foodyou.userfood.domain.product

import com.maksimowiczm.foodyou.common.domain.Image
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.Barcode
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.userfood.domain.UserFoodNote

data class UserProduct(
    val identity: UserProductIdentity,
    val name: FoodName,
    val brand: UserProductBrand?,
    val barcode: Barcode?,
    val note: UserFoodNote?,
    val image: Image.Local?,
    val source: UserProductSource?,
    val nutritionFacts: NutritionFacts,
    val servingQuantity: AbsoluteQuantity?,
    val packageQuantity: AbsoluteQuantity?,
    val isLiquid: Boolean,
)
