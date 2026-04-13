package com.maksimowiczm.foodyou.analytics.domain

import kotlinx.coroutines.flow.Flow

interface AnalyticsRepository {
    suspend fun load(): Analytics

    fun observe(): Flow<Analytics>

    suspend fun save(aggregate: Analytics)
}
