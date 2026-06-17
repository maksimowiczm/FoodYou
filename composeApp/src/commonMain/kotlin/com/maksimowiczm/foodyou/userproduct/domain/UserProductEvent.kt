package com.maksimowiczm.foodyou.userproduct.domain

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.InstantComponentSerializer

@Serializable sealed class UserProductEvent(override val id: Uuid = Uuid.random()) : DomainEvent

@Serializable
data class UserProductCreatedEvent(
    val product: UserProduct,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : UserProductEvent()

@Serializable
data class UserProductUpdatedEvent(
    val product: UserProduct,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : UserProductEvent()

@Serializable
data class UserProductDeletedEvent(
    val identity: UserProductIdentity,
    val strategy: DeleteStrategy,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : UserProductEvent()
