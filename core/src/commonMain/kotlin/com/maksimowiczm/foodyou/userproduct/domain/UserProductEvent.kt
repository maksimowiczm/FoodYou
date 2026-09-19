package com.maksimowiczm.foodyou.userproduct.domain

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlinx.serialization.Serializable

@Serializable sealed interface UserProductEvent : DomainEvent

@Serializable data class UserProductCreatedEvent(val product: UserProduct) : UserProductEvent

@Serializable data class UserProductUpdatedEvent(val product: UserProduct) : UserProductEvent

@Serializable
data class UserProductDeletedEvent(
    val userProductId: UserProductId,
    val strategy: DeleteStrategy,
) : UserProductEvent
