package com.maksimowiczm.foodyou.userproduct.domain

import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlin.time.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.InstantComponentSerializer

@Serializable sealed interface UserProductEvent : DomainEvent

@Serializable
data class UserProductCreatedEvent(
    val product: UserProduct,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : UserProductEvent

@Serializable
data class UserProductUpdatedEvent(
    val product: UserProduct,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : UserProductEvent

@Serializable
data class UserProductDeletedEvent(
    val identity: UserProductIdentity,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : UserProductEvent
