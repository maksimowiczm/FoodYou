package com.maksimowiczm.foodyou.fooddatacentral.domain

fun interface FoodDataCentralApiKeyVerificationService {
    suspend fun verify(apiKey: String)
}
