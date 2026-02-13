package com.maksimowiczm.foodyou.userfood.domain.recipe

import com.maksimowiczm.foodyou.common.event.IntegrationEvent

/**
 * Integration event emitted when a recipe is deleted.
 *
 * This event notifies other bounded contexts that a recipe has been removed, allowing them to clean
 * up any dependent data.
 *
 * @property identity The identity of the deleted recipe
 */
data class UserRecipeDeletedEvent(val identity: UserRecipeIdentity) : IntegrationEvent
