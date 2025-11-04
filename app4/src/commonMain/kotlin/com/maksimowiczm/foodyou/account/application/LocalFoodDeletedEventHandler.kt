package com.maksimowiczm.foodyou.account.application

import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.food.domain.LocalFoodDeletedEvent

class LocalFoodDeletedEventHandler(private val accountRepository: AccountRepository) :
    EventHandler<LocalFoodDeletedEvent> {
    override suspend fun handle(event: LocalFoodDeletedEvent) {
        val accounts = accountRepository.loadAll()
        accounts.forEach {
            it.removeLocalFavoriteFood(event.identity)
            accountRepository.save(it)
        }
    }
}
