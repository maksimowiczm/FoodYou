package com.maksimowiczm.foodyou.analytics.domain

import com.maksimowiczm.foodyou.common.domain.LocalAccountId

class FakeAccountAnalyticsRepository(
    private val onLoad: suspend (LocalAccountId) -> AccountAnalytics = { testAccountAnalytics() },
    private val onSave: suspend (AccountAnalytics) -> Unit = {},
) : AccountAnalyticsRepository {
    override suspend fun load(localAccountId: LocalAccountId) = onLoad(localAccountId)

    override suspend fun save(accountAnalytics: AccountAnalytics) = onSave(accountAnalytics)
}
