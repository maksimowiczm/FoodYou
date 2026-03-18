package com.maksimowiczm.foodyou.analytics.domain

interface AnalyticsRepository {
    suspend fun load(): Analytics

    suspend fun save(analytics: Analytics)
}
