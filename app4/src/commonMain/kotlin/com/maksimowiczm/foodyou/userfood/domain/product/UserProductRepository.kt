package com.maksimowiczm.foodyou.userfood.domain.product

import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.userfood.domain.UserFoodNote
import kotlinx.coroutines.flow.Flow

interface UserProductRepository {
    suspend fun create(
        name: FoodName,
        brand: UserProductBrand?,
        barcode: UserProductBarcode?,
        note: UserFoodNote?,
        imageUri: String?,
        source: UserProductSource?,
        nutritionFacts: NutritionFacts,
        servingQuantity: AbsoluteQuantity?,
        packageQuantity: AbsoluteQuantity?,
        accountId: LocalAccountId,
        isLiquid: Boolean,
    ): UserProductIdentity

    suspend fun edit(
        identity: UserProductIdentity,
        name: FoodName,
        brand: UserProductBrand?,
        barcode: UserProductBarcode?,
        note: UserFoodNote?,
        imageUri: String?,
        source: UserProductSource?,
        nutritionFacts: NutritionFacts,
        servingQuantity: AbsoluteQuantity?,
        packageQuantity: AbsoluteQuantity?,
        accountId: LocalAccountId,
        isLiquid: Boolean,
    )

    fun observe(identity: UserProductIdentity): Flow<UserProduct?>

    suspend fun delete(identity: UserProductIdentity)
}
