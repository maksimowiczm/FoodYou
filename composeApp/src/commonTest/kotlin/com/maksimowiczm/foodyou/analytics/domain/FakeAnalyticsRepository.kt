package com.maksimowiczm.foodyou.analytics.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeAnalyticsRepository(
    private val onLoad: suspend () -> Analytics = { Analytics.of() },
    private val onObserve: () -> Flow<Analytics> = { flowOf(Analytics.of()) },
    private val onSave: suspend (Analytics) -> Unit = {},
) : AnalyticsRepository {
    override suspend fun load() = onLoad()

    override fun observe() = onObserve()

    override suspend fun save(aggregate: Analytics) = onSave(aggregate)
}
