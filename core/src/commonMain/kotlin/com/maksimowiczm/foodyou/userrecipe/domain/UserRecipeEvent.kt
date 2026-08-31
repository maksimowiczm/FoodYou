package com.maksimowiczm.foodyou.userrecipe.domain

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.InstantComponentSerializer

@Serializable sealed class UserRecipeEvent(override val id: Uuid = Uuid.random()) : DomainEvent

@Serializable
data class UserRecipeCreatedEvent(
    val recipe: UserRecipe,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : UserRecipeEvent()

@Serializable
data class UserRecipeUpdatedEvent(
    val recipe: UserRecipe,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : UserRecipeEvent()

@Serializable
data class UserRecipeDeletedEvent(
    val userRecipeId: UserRecipeId,
    val strategy: DeleteStrategy,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : UserRecipeEvent()
