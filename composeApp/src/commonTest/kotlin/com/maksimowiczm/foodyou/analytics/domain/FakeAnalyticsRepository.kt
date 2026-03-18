package com.maksimowiczm.foodyou.analytics.domain

class FakeAnalyticsRepository(
    private val onLoad: suspend () -> Analytics = { testAccountAnalytics() },
    private val onSave: suspend (Analytics) -> Unit = {},
) : AnalyticsRepository {
    override suspend fun load() = onLoad()

    override suspend fun save(analytics: Analytics) = onSave(analytics)
}
