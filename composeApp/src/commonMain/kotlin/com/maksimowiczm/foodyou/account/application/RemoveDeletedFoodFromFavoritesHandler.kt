package com.maksimowiczm.foodyou.account.application

import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.domain.removeFavoriteUserFood
import com.maksimowiczm.foodyou.account.domain.update
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductDeletedEvent

/**
 * Handles the [UserProductDeletedEvent] by removing the deleted food from the owner's account
 * favorites.
 *
 * When a user food product is deleted, this handler ensures data consistency by removing the
 * reference from the owner's favorite list.
 */
class RemoveDeletedFoodFromFavoritesHandler(private val accountRepository: AccountRepository) :
    EventHandler<UserProductDeletedEvent> {

    override suspend fun handle(event: UserProductDeletedEvent) {
        accountRepository.update { removeFavoriteUserFood(event.identity) }
    }
}
