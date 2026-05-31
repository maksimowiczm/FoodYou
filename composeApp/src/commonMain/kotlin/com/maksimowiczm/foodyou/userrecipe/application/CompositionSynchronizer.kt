package com.maksimowiczm.foodyou.userrecipe.application

import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeCompositionRepository
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeCreatedEvent
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeDeletedEvent
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeEvent
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeUpdatedEvent

class CompositionSynchronizer(private val repository: UserRecipeCompositionRepository) :
    EventHandler<UserRecipeEvent> {
    override suspend fun handle(event: UserRecipeEvent) {
        when (event) {
            is UserRecipeCreatedEvent ->
                repository.saveReferences(
                    event.recipe.identity,
                    event.recipe.composition.allComponentIdentities,
                )

            is UserRecipeUpdatedEvent ->
                repository.saveReferences(
                    event.recipe.identity,
                    event.recipe.composition.allComponentIdentities,
                )

            is UserRecipeDeletedEvent -> repository.removeReferences(event.identity)
        }
    }
}
