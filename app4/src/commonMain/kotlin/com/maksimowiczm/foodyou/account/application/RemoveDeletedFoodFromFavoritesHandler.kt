package com.maksimowiczm.foodyou.account.application

import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.userfood.domain.UserFoodDeletedEvent

/**
 * Handles the [UserFoodDeletedEvent] by removing the deleted food from all accounts' favorites.
 *
 * When a local food item is deleted from the system, this handler ensures data consistency by
 * removing references to that food from the favorite lists of all accounts that may have favorited
 * it.
 *
 * @property accountRepository Repository for loading and persisting account aggregates
 */
class RemoveDeletedFoodFromFavoritesHandler(private val accountRepository: AccountRepository) :
    EventHandler<UserFoodDeletedEvent> {
    override suspend fun handle(event: UserFoodDeletedEvent) {
        val accounts = accountRepository.loadAll()
        accounts.forEach {
            // TODO
            // it.removeLocalFavoriteFood(event.identity)
            accountRepository.save(it)
        }
    }
}
