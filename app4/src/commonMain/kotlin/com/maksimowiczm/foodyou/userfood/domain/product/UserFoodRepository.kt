package com.maksimowiczm.foodyou.userfood.domain.product

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.Barcode
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.userfood.domain.FoodNote
import kotlinx.coroutines.flow.Flow

interface UserFoodRepository {
    fun search(
        parameters: UserFoodSearchParameters,
        pageSize: Int,
    ): Flow<PagingData<UserFoodProduct>>

    fun count(parameters: UserFoodSearchParameters): Flow<Int>

    suspend fun create(
        name: FoodName,
        brand: FoodBrand?,
        barcode: Barcode?,
        note: FoodNote?,
        imageUri: String?,
        source: FoodSource?,
        nutritionFacts: NutritionFacts,
        servingQuantity: AbsoluteQuantity?,
        packageQuantity: AbsoluteQuantity?,
        accountId: LocalAccountId,
        isLiquid: Boolean,
    ): UserFoodProductIdentity

    suspend fun edit(
        identity: UserFoodProductIdentity,
        name: FoodName,
        brand: FoodBrand?,
        barcode: Barcode?,
        note: FoodNote?,
        imageUri: String?,
        source: FoodSource?,
        nutritionFacts: NutritionFacts,
        servingQuantity: AbsoluteQuantity?,
        packageQuantity: AbsoluteQuantity?,
        accountId: LocalAccountId,
        isLiquid: Boolean,
    )

    fun observe(identity: UserFoodProductIdentity): Flow<UserFoodProduct?>

    suspend fun delete(identity: UserFoodProductIdentity)
}
