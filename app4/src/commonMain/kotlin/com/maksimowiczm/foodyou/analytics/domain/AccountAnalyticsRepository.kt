package com.maksimowiczm.foodyou.analytics.domain

import com.maksimowiczm.foodyou.common.domain.LocalAccountId

interface AccountAnalyticsRepository {
    suspend fun load(localAccountId: LocalAccountId): AccountAnalytics?

    suspend fun save(accountAnalytics: AccountAnalytics)
}
