package com.maksimowiczm.foodyou.feature.usda

import com.maksimowiczm.foodyou.feature.usda.model.AbridgedFoodItem

interface USDARemoteDataSource {
    suspend fun getProduct(id: String, apiKey: String): Result<AbridgedFoodItem>
}
