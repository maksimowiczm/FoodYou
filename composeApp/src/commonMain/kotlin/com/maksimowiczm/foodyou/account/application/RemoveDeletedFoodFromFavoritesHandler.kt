package com.maksimowiczm.foodyou.account.application

import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductDeletedEvent

/**
 * Handles the [UserProductDeletedEvent] by removing the deleted food from the owner's account
 * favorites.
 *
 * When a user food product is deleted, this handler ensures data consistency by removing the
 * reference from the owner's favorite list.
 */
class RemoveDeletedFoodFromFavoritesHandler(
    private val accountRepository: AccountRepository,
    logger: Logger,
) : EventHandler<UserProductDeletedEvent> {
    private val logger = logger.withTag("RemoveDeletedFoodFromFavoritesHandler")

    override suspend fun handle(event: UserProductDeletedEvent) {
        accountRepository
            .load(event.identity.accountId)
            ?.apply { removeFavoriteUserFood(event.identity) }
            ?.also { accountRepository.save(it) }
            ?: logger.w { "Account not found for user food product with id: ${event.identity.id}" }
    }
}
