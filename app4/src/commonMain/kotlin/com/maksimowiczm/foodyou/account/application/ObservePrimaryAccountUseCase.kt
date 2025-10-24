package com.maksimowiczm.foodyou.account.application

import com.maksimowiczm.foodyou.account.domain.Account
import com.maksimowiczm.foodyou.account.domain.AccountManager
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest

/**
 * Use case for observing the primary account. Emits the [Account] whenever the primary account
 * changes. If there is no primary account, the flow will suspend until one is set.
 */
class ObservePrimaryAccountUseCase(
    private val accountManager: AccountManager,
    private val accountRepository: AccountRepository,
) {
    fun observe(): Flow<Account> =
        accountManager.observePrimaryAccountId().filterNotNull().flatMapLatest { accountId ->
            accountRepository.observe(accountId).filterNotNull()
        }
}
