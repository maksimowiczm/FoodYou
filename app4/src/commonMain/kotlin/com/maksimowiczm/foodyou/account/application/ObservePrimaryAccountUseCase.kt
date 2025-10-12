package com.maksimowiczm.foodyou.account.application

import com.maksimowiczm.foodyou.account.domain.Account
import com.maksimowiczm.foodyou.account.domain.AccountManager
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObservePrimaryAccountUseCase(
    private val accountManager: AccountManager,
    private val accountRepository: AccountRepository,
) {
    fun observe(): Flow<Account?> =
        accountManager.observePrimaryAccountId().flatMapLatest { accountId ->
            if (accountId == null) flowOf(null) else accountRepository.observe(accountId)
        }
}
