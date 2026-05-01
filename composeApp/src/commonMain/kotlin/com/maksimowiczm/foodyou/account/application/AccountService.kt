package com.maksimowiczm.foodyou.account.application

import com.maksimowiczm.foodyou.account.domain.Account
import com.maksimowiczm.foodyou.account.domain.AccountEvent
import com.maksimowiczm.foodyou.account.domain.toAccount
import com.maksimowiczm.foodyou.common.domain.EventStore
import com.maksimowiczm.foodyou.common.domain.load
import com.maksimowiczm.foodyou.common.domain.observe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountService(private val eventStore: EventStore) {
    fun observe(): Flow<Account?> =
        eventStore.observe<AccountEvent>(EVENT_STREAM).map { events ->
            if (events.none()) null else events.toAccount()
        }

    suspend fun update(block: Account.() -> List<AccountEvent>) {
        val account = eventStore.load<AccountEvent>(EVENT_STREAM).toAccount()
        val newEvents = account.block()
        if (newEvents.isNotEmpty()) {
            eventStore.append(EVENT_STREAM, newEvents)
        }
    }

    private companion object {
        private const val EVENT_STREAM = "Account"
    }
}
