package com.maksimowiczm.foodyou.account.infrastructure

import com.maksimowiczm.foodyou.account.domain.AccountManager
import com.maksimowiczm.foodyou.common.LocalAccountId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class AccountManagerImpl : AccountManager {
    override fun observePrimaryAccountId(): Flow<LocalAccountId> = flowOf(LocalAccountId.DEFAULT)
}
