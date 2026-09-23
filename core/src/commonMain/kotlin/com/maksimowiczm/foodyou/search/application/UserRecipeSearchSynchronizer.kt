package com.maksimowiczm.foodyou.search.application

import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.search.domain.SearchRepository
import com.maksimowiczm.foodyou.search.domain.SearchResult
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeCreatedEvent
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeDeletedEvent
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeEvent
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeUpdatedEvent

class UserRecipeSearchSynchronizer(private val searchRepository: SearchRepository) :
    EventHandler<UserRecipeEvent> {
    override suspend fun handle(event: UserRecipeEvent) {
        when (event) {
            is UserRecipeCreatedEvent ->
                searchRepository.save(
                    SearchResult.UserRecipe(
                        id = event.recipe.id.value,
                        name = event.recipe.name,
                        note = event.recipe.note,
                        image = event.recipe.image,
                        nutritionFacts = event.recipe.nutritionFacts,
                        servingWeight = event.recipe.servingWeight,
                        totalWeight = event.recipe.totalWeight,
                    )
                )

            is UserRecipeUpdatedEvent ->
                searchRepository.save(
                    SearchResult.UserRecipe(
                        id = event.recipe.id.value,
                        name = event.recipe.name,
                        note = event.recipe.note,
                        image = event.recipe.image,
                        nutritionFacts = event.recipe.nutritionFacts,
                        servingWeight = event.recipe.servingWeight,
                        totalWeight = event.recipe.totalWeight,
                    )
                )

            is UserRecipeDeletedEvent -> searchRepository.deleteRecipe(event.userRecipeId.value)
        }
    }
}
