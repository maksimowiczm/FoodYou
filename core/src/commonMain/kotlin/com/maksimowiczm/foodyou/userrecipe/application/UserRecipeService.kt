package com.maksimowiczm.foodyou.userrecipe.application

import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotQuantityUpdateService
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotUpdateService
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
    private val commandHandler =
        userRecipeDecider.asHandler(
            eventStore,
            eventStore + eventNotifier.asEventSink(),
        )

    private fun stream(id: UserRecipeId) = "UserRecipe-${id.value}"

    fun observe(id: UserRecipeId): Flow<UserRecipe?> =
        eventStore.observe<UserRecipeEvent>(stream(id)).map { it.toUserRecipe() }

    fun observeAncestors(id: UserRecipeId): Flow<Set<UserRecipeId>> =
        compositionRepository.observeAncestors(id)

    suspend fun handle(id: UserRecipeId, command: UserRecipeCommand) {
        val _ = commandHandler(stream(id), command)
    }

    /**
     * Updates all user recipes containing the food identified by [TrackedFoodSnapshot.id] with the
     * new [snapshot]. The quantity is also updated to match new [servingQuantity] and
     * [packageQuantity].
     */
    suspend fun updateRecipesUsing(
        snapshot: TrackedFoodSnapshot,
        servingQuantity: AbsoluteQuantity? = null,
        packageQuantity: AbsoluteQuantity? = null,
    ) = coroutineScope {
        compositionRepository
            .findRecipesUsing(snapshot.id)
            .map { id ->
                async {
                    val command = UserRecipeCommand.Update { recipe ->
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
                                                    servingQuantity = servingQuantity,
                                                    packageQuantity = packageQuantity,
                                                ),
                                        )
                                    },
                                )
                        )
                    }
                    handle(id, command)
                }
            }
            .awaitAll()
    }

    suspend fun removeComponentFromRecipes(id: FoodSnapshotId.Tracked) = coroutineScope {
        compositionRepository
            .findRecipesUsing(id)
            .map { recipeId ->
                async {
                    val command = UserRecipeCommand.Update { recipe ->
                        val updatedComposition =
                            FoodSnapshotUpdateService.remove(recipe.components, id)
                        recipe.copy(components = updatedComposition)
                    }
                    handle(recipeId, command)
                }
            }
            .awaitAll()
    }

    suspend fun unlinkComponentFromRecipes(snapshotId: FoodSnapshotId.Tracked) = coroutineScope {
        compositionRepository
            .findRecipesUsing(snapshotId)
            .map { id ->
                async {
                    val command = UserRecipeCommand.Update { recipe ->
                        val updatedComposition =
                            FoodSnapshotUpdateService.unlink(recipe.components, snapshotId)
                        recipe.copy(components = updatedComposition)
                    }
                    handle(id, command)
                }
            }
            .awaitAll()
    }
}
