package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.LocalAccountId
import kotlinx.coroutines.flow.Flow

interface AccountManager {
    suspend fun setPrimaryAccountId(accountId: LocalAccountId)

    fun observePrimaryAccountId(): Flow<LocalAccountId?>
}
