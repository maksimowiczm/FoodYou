package com.maksimowiczm.foodyou.account.application

import com.maksimowiczm.foodyou.account.domain.Account
import com.maksimowiczm.foodyou.account.domain.AccountCommand
import com.maksimowiczm.foodyou.account.domain.AccountEvent
import com.maksimowiczm.foodyou.account.domain.accountDecider
import com.maksimowiczm.foodyou.account.domain.toAccount
import com.maksimowiczm.foodyou.common.event.EventNotifier
import com.maksimowiczm.foodyou.common.event.EventStore
import com.maksimowiczm.foodyou.common.event.asEventSink
import com.maksimowiczm.foodyou.common.event.asHandler
import com.maksimowiczm.foodyou.common.observe
import com.maksimowiczm.foodyou.common.plus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountService(
    private val eventStore: EventStore,
    eventNotifier: EventNotifier,
) {
    private val commandHandler =
        accountDecider.asHandler(
            eventStore,
            eventStore + eventNotifier.asEventSink(),
        )

    suspend fun handle(command: AccountCommand) {
        val _ = commandHandler(STREAM, command)
    }

    fun observe(): Flow<Account?> =
        eventStore.observe<AccountEvent>(STREAM).map { events ->
            if (events.none()) null else events.toAccount()
        }

    private companion object {
        private const val STREAM = "Account"
    }
}
