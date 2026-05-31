package com.maksimowiczm.foodyou.userrecipe.domain

import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlin.time.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.InstantComponentSerializer

@Serializable sealed interface UserRecipeEvent : DomainEvent

@Serializable
data class UserRecipeCreatedEvent(
    val recipe: UserRecipe,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : UserRecipeEvent

@Serializable
data class UserRecipeUpdatedEvent(
    val recipe: UserRecipe,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : UserRecipeEvent

@Serializable
data class UserRecipeDeletedEvent(
    val identity: UserRecipeIdentity,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : UserRecipeEvent
