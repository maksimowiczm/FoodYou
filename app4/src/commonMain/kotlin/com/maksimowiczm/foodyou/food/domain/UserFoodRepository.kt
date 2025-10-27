package com.maksimowiczm.foodyou.food.domain

import com.maksimowiczm.foodyou.common.domain.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.LocalAccountId

interface UserFoodRepository {
    suspend fun create(
        name: FoodName,
        brand: FoodBrand?,
        barcode: Barcode?,
        note: FoodNote?,
        imageUri: String?,
        source: FoodSource.UserAdded?,
        nutritionFacts: NutritionFacts,
        servingQuantity: AbsoluteQuantity?,
        packageQuantity: AbsoluteQuantity?,
        accountId: LocalAccountId,
    ): FoodProductIdentity.Local
}
