package com.maksimowiczm.foodyou.account.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface AccountRepository {
    fun observe(): Flow<Account?>

    suspend fun load(): Account? = observe().first()

    suspend fun save(account: Account)
}
