package com.maksimowiczm.foodyou.food.domain.repository

import kotlinx.coroutines.flow.Flow

interface OpenFoodFactsCredentialsRepository {
    suspend fun store(login: String, password: String)

    suspend fun clear()

    fun hasCredentials(): Flow<Boolean>
}
