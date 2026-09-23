package com.maksimowiczm.foodyou.userrecipe.domain

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlinx.serialization.Serializable

@Serializable sealed interface UserRecipeEvent : DomainEvent

@Serializable data class UserRecipeCreatedEvent(val recipe: UserRecipe) : UserRecipeEvent

@Serializable data class UserRecipeUpdatedEvent(val recipe: UserRecipe) : UserRecipeEvent

@Serializable
data class UserRecipeDeletedEvent(
    val userRecipeId: UserRecipeId,
    val strategy: DeleteStrategy,
) : UserRecipeEvent
