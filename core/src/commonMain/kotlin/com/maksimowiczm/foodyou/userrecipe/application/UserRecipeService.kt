package com.maksimowiczm.foodyou.userrecipe.application

import com.maksimowiczm.foodyou.common.domain.BlobDigest
import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.Weight
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotQuantityUpdateService
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotUpdateService
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.TrackedFoodSnapshot
import com.maksimowiczm.foodyou.common.event.EventNotifier
import com.maksimowiczm.foodyou.common.event.EventStore
import com.maksimowiczm.foodyou.common.event.asEventSink
import com.maksimowiczm.foodyou.common.event.asHandler
import com.maksimowiczm.foodyou.common.observe
import com.maksimowiczm.foodyou.common.plus
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipe
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeCommand
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeCompositionRepository
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeEvent
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeId
import com.maksimowiczm.foodyou.userrecipe.domain.toUserRecipe
import com.maksimowiczm.foodyou.userrecipe.domain.userRecipeDecider
import kotlin.time.Clock
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRecipeService(
    private val eventStore: EventStore,
    eventNotifier: EventNotifier,
    private val compositionRepository: UserRecipeCompositionRepository,
) {
    private val clock: Clock = Clock.System
    private val commandHandler =
        userRecipeDecider.asHandler(
            eventStore,
            eventStore + eventNotifier.asEventSink(),
        )

    private fun streamId(id: UserRecipeId) = "UserRecipe-${id.value}"

    fun observe(id: UserRecipeId): Flow<UserRecipe?> =
        eventStore.observe<UserRecipeEvent>(streamId(id)).map { it.toUserRecipe() }

    fun observeAncestors(id: UserRecipeId): Flow<Set<UserRecipeId>> =
        compositionRepository.observeAncestors(id)

    suspend fun create(
        name: FoodName,
        note: String?,
        image: BlobDigest?,
        servings: Double,
        components: List<MeasuredFoodSnapshot>,
    ): UserRecipeId {
        val id = UserRecipeId()
        val recipe =
            UserRecipe(
                id = id,
                name = name,
                note = note,
                image = image,
                servings = servings,
                components = components,
            )
        val command = UserRecipeCommand.Create(recipe = recipe, timestamp = clock.now())
        val _ = commandHandler(streamId(id), command)

        return id
    }

    suspend fun edit(
        id: UserRecipeId,
        name: FoodName,
        note: String?,
        image: BlobDigest?,
        servings: Double,
        components: List<MeasuredFoodSnapshot>,
    ) {
        val command =
            UserRecipeCommand.Update(timestamp = clock.now()) { recipe ->
                recipe.copy(
                    name = name,
                    note = note,
                    image = image,
                    servings = servings,
                    components = components,
                )
            }
        val _ = commandHandler(streamId(id), command)
    }

    suspend fun delete(id: UserRecipeId, strategy: DeleteStrategy) {
        val command = UserRecipeCommand.Remove(strategy = strategy, timestamp = clock.now())
        val _ = commandHandler(streamId(id), command)
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
            .map { id ->
                async {
                    val command =
                        UserRecipeCommand.Update(timestamp = clock.now()) { recipe ->
                            recipe.copy(
                                components =
                                    FoodSnapshotUpdateService.update(
                                        components = recipe.components,
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
                        }
                    val _ = commandHandler(streamId(id), command)
                }
            }
            .awaitAll()
    }

    suspend fun removeComponentFromRecipes(id: FoodSnapshotId.Tracked) = coroutineScope {
        compositionRepository
            .findRecipesUsing(id)
            .map { recipeId ->
                async {
                    val command =
                        UserRecipeCommand.Update(timestamp = clock.now()) { recipe ->
                            val updatedComposition =
                                FoodSnapshotUpdateService.remove(recipe.components, id)
                            recipe.copy(components = updatedComposition)
                        }
                    val _ = commandHandler(streamId(recipeId), command)
                }
            }
            .awaitAll()
    }

    suspend fun unlinkComponentFromRecipes(snapshotId: FoodSnapshotId.Tracked) = coroutineScope {
        compositionRepository
            .findRecipesUsing(snapshotId)
            .map { id ->
                async {
                    val command =
                        UserRecipeCommand.Update(timestamp = clock.now()) { recipe ->
                            val updatedComposition =
                                FoodSnapshotUpdateService.unlink(recipe.components, snapshotId)
                            recipe.copy(components = updatedComposition)
                        }
                    val _ = commandHandler(streamId(id), command)
                }
            }
            .awaitAll()
    }
}
