package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.LocalAccountId
import kotlinx.coroutines.flow.Flow

interface AccountManager {
    fun observePrimaryAccountId(): Flow<LocalAccountId>
}
