package com.maksimowiczm.foodyou.userrecipe.application

import com.maksimowiczm.foodyou.common.domain.BlobStorage
import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.EventStore
import com.maksimowiczm.foodyou.common.domain.Weight
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotQuantityUpdateService
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotUpdateService
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.TrackedFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.load
import com.maksimowiczm.foodyou.common.domain.observe
import com.maksimowiczm.foodyou.common.event.EventBus
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipe
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeCompositionRepository
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeEvent
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeIdentity
import com.maksimowiczm.foodyou.userrecipe.domain.remove
import com.maksimowiczm.foodyou.userrecipe.domain.toUserRecipe
import com.maksimowiczm.foodyou.userrecipe.domain.update
import kotlin.uuid.Uuid
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRecipeService(
    private val eventStore: EventStore,
    private val blobStorage: BlobStorage,
    private val eventBus: EventBus,
    private val compositionRepository: UserRecipeCompositionRepository,
) {
    private fun streamId(identity: UserRecipeIdentity) = "UserRecipe-${identity.id}"

    private suspend inline fun transact(
        identity: UserRecipeIdentity,
        block: (UserRecipe?) -> List<UserRecipeEvent>,
    ) {
        val recipe = eventStore.load<UserRecipeEvent>(streamId(identity)).toUserRecipe()
        val newEvents = block(recipe)
        if (newEvents.isNotEmpty()) {
            eventStore.append(streamId(identity), newEvents)
            eventBus.publish(newEvents)
        }
    }

    fun observe(identity: UserRecipeIdentity): Flow<UserRecipe?> =
        eventStore.observe<UserRecipeEvent>(streamId(identity)).map { it.toUserRecipe() }

    fun observeAncestors(identity: UserRecipeIdentity): Flow<Set<UserRecipeIdentity>> =
        compositionRepository.observeAncestors(identity)

    suspend fun create(
        name: FoodName,
        note: String?,
        imageBytes: ByteArray?,
        servings: Double,
        components: List<MeasuredFoodSnapshot>,
    ): UserRecipeIdentity {
        val identity = UserRecipeIdentity(Uuid.random())
        transact(identity) {
            UserRecipe.create(
                UserRecipe(
                    identity = identity,
                    name = name,
                    note = note,
                    image = imageBytes?.let { blobStorage.store(it) },
                    servings = servings,
                    components = components,
                )
            )
        }
        return identity
    }

    suspend fun edit(
        identity: UserRecipeIdentity,
        name: FoodName,
        note: String?,
        imageBytes: ByteArray?,
        servings: Double,
        components: List<MeasuredFoodSnapshot>,
    ) {
        transact(identity) { recipe ->
            checkNotNull(recipe) { "Recipe with ID $identity not found" }
            recipe.update {
                it.copy(
                    name = name,
                    note = note,
                    image = imageBytes?.let { bytes -> blobStorage.store(bytes) },
                    servings = servings,
                    components = components,
                )
            }
        }
    }

    suspend fun delete(identity: UserRecipeIdentity, strategy: DeleteStrategy) {
        transact(identity) { recipe ->
            checkNotNull(recipe) { "Recipe with ID $identity not found" }
            recipe.remove(strategy)
        }
    }

    /**
     * Updates all user recipes containing the food identified by [TrackedFoodSnapshot.id] with the
     * new [snapshot]. The quantity is also updated to match new [servingWeight] and
     * [packageWeight].
     */
    suspend fun updateRecipesUsing(
        snapshot: TrackedFoodSnapshot,
        servingWeight: Weight? = null,
        packageWeight: Weight? = null,
    ) = coroutineScope {
        compositionRepository
            .findRecipesUsing(snapshot.id)
            .map { recipeIdentity ->
                async {
                    transact(recipeIdentity) { recipe ->
                        recipe?.update {
                            it.copy(
                                components =
                                    FoodSnapshotUpdateService.update(
                                        components = it.components,
                                        id = snapshot.id,
                                        transform = { current ->
                                            current.copy(
                                                snapshot = snapshot,
                                                quantity =
                                                    FoodSnapshotQuantityUpdateService.update(
                                                        current = current.quantity,
                                                        servingWeight = servingWeight,
                                                        packageWeight = packageWeight,
                                                    ),
                                            )
                                        },
                                    )
                            )
                        } ?: emptyList()
                    }
                }
            }
            .awaitAll()
    }

    suspend fun removeComponentFromRecipes(identity: FoodSnapshotId.Tracked) = coroutineScope {
        compositionRepository
            .findRecipesUsing(identity)
            .map { recipeIdentity ->
                async {
                    transact(recipeIdentity) { recipe ->
                        recipe ?: return@transact emptyList()
                        val updatedComposition =
                            FoodSnapshotUpdateService.remove(recipe.components, identity)
                        recipe.update { it.copy(components = updatedComposition) }
                    }
                }
            }
            .awaitAll()
    }

    suspend fun unlinkComponentFromRecipes(identity: FoodSnapshotId.Tracked) = coroutineScope {
        compositionRepository
            .findRecipesUsing(identity)
            .map { recipeIdentity ->
                async {
                    transact(recipeIdentity) { recipe ->
                        recipe ?: return@transact emptyList()
                        val updatedComposition =
                            FoodSnapshotUpdateService.unlink(recipe.components, identity)
                        recipe.update { it.copy(components = updatedComposition) }
                    }
                }
            }
            .awaitAll()
    }
}
