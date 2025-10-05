package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface AccountRepository {
    fun observe(localAccountId: LocalAccountId): Flow<Account?>

    suspend fun load(localAccountId: LocalAccountId): Account? = observe(localAccountId).first()

    suspend fun save(account: Account)
}
