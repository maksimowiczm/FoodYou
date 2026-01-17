package com.maksimowiczm.foodyou.analytics.domain

import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.domain.testLocalAccountId

fun testAccountAnalytics(id: LocalAccountId = testLocalAccountId()): AccountAnalytics =
    AccountAnalytics.of(id)
