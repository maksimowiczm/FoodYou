package com.maksimowiczm.foodyou.account.application

import com.maksimowiczm.foodyou.account.domain.AccountCommand
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.userproduct.domain.UserProductDeletedEvent

/**
 * Handles the [UserProductDeletedEvent] by removing the deleted food from the owner's account
 * favorites.
 *
 * When a user food product is deleted, this handler ensures data consistency by removing the
 * reference from the owner's favorite list.
 */
class RemoveDeletedFoodFromFavoritesHandler(private val accountService: AccountService) :
    EventHandler<UserProductDeletedEvent> {
    override suspend fun handle(event: UserProductDeletedEvent) {
        accountService.handle(AccountCommand.RemoveFavoriteUserFood(event.userProductId))
    }
}
