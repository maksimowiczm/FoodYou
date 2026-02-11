package com.maksimowiczm.foodyou.userfood.domain.product

import com.maksimowiczm.foodyou.common.event.IntegrationEvent

/**
 * Integration event emitted when a user food product is deleted.
 *
 * This event notifies other bounded contexts that a user-created food product has been removed from
 * the local database, allowing them to clean up any dependent data (e.g., meal entries, favorites).
 *
 * @property identity The identity of the deleted user food product
 */
data class UserFoodProductDeletedEvent(val identity: UserFoodProductIdentity) : IntegrationEvent
