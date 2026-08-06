package com.maksimowiczm.foodyou.openfoodfacts.domain

fun interface OpenFoodFactsLoginService {
    suspend fun login(username: String, password: String)
}
