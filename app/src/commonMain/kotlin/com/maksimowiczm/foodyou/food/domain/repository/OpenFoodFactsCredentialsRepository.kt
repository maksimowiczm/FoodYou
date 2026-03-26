package com.maksimowiczm.foodyou.food.domain.repository

interface OpenFoodFactsCredentialsRepository {
    suspend fun store(login: String, password: String)
}
