package com.maksimowiczm.foodyou.userfood.domain

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.common.domain.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.Barcode
import com.maksimowiczm.foodyou.common.domain.FoodBrand
import com.maksimowiczm.foodyou.common.domain.FoodName
import com.maksimowiczm.foodyou.common.domain.FoodNote
import com.maksimowiczm.foodyou.common.domain.FoodSource
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.domain.NutritionFacts
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
        source: FoodSource.UserAdded?,
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
        source: FoodSource.UserAdded?,
        nutritionFacts: NutritionFacts,
        servingQuantity: AbsoluteQuantity?,
        packageQuantity: AbsoluteQuantity?,
        accountId: LocalAccountId,
        isLiquid: Boolean,
    )

    fun observe(identity: UserFoodProductIdentity): Flow<UserFoodProduct?>

    suspend fun delete(identity: UserFoodProductIdentity)
}
