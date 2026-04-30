package com.maksimowiczm.foodyou.userfood.application

import com.maksimowiczm.foodyou.common.Result
import com.maksimowiczm.foodyou.common.domain.BlobStorage
import com.maksimowiczm.foodyou.common.event.EventBus
import com.maksimowiczm.foodyou.common.event.IntegrationEvent
import com.maksimowiczm.foodyou.common.map
import com.maksimowiczm.foodyou.userfood.domain.UserFoodNote
import com.maksimowiczm.foodyou.userfood.domain.recipe.CircularUserRecipeReferenceError
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipe
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipeDeletedEvent
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipeIdentity
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipeIngredient
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipeName
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipeRepository
import kotlin.uuid.Uuid

class UserRecipeService(
    private val repository: UserRecipeRepository,
    private val blobStorage: BlobStorage,
    private val integrationEventBus: EventBus<IntegrationEvent>,
) {
    suspend fun create(
        name: UserRecipeName,
        servings: Double,
        imageBytes: ByteArray?,
        note: UserFoodNote?,
        ingredients: List<UserRecipeIngredient>,
    ): Result<UserRecipeIdentity, CircularUserRecipeReferenceError> {
        val identity = UserRecipeIdentity(Uuid.random())

        // defer saving image until business rules validated
        val recipe =
            UserRecipe(
                    identity = identity,
                    name = name,
                    servings = servings,
                    image = null,
                    note = note,
                    ingredients = ingredients,
                )
                .copy(image = imageBytes?.let { blobStorage.store(it) })

        return repository.save(recipe).map { identity }
    }

    suspend fun update(
        identity: UserRecipeIdentity,
        name: UserRecipeName,
        servings: Double,
        imageBytes: ByteArray?,
        note: UserFoodNote?,
        ingredients: List<UserRecipeIngredient>,
    ): Result<Unit, CircularUserRecipeReferenceError> {

        // defer saving image until business rules validated
        val recipe =
            UserRecipe(
                    identity = identity,
                    name = name,
                    servings = servings,
                    image = null,
                    note = note,
                    ingredients = ingredients,
                )
                .copy(image = imageBytes?.let { blobStorage.store(it) })

        return repository.save(recipe)
    }

    suspend fun delete(identity: UserRecipeIdentity) {
        repository.delete(identity)
        integrationEventBus.publish(UserRecipeDeletedEvent(identity))
    }
}
