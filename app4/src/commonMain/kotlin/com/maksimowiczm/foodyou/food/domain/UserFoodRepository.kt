package com.maksimowiczm.foodyou.food.domain

import com.maksimowiczm.foodyou.common.domain.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import kotlinx.coroutines.flow.Flow

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
        isLiquid: Boolean,
    ): FoodProductIdentity.Local

    suspend fun edit(
        identity: FoodProductIdentity.Local,
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
        isLiquid: Boolean,
    )

    fun observe(identity: FoodProductIdentity.Local): Flow<FoodProductDto?>

    suspend fun delete(identity: FoodProductIdentity.Local)
}
