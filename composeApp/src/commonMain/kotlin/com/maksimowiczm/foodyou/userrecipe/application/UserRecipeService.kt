package com.maksimowiczm.foodyou.userrecipe.application

import com.maksimowiczm.foodyou.common.domain.BlobStorage
import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.EventStore
import com.maksimowiczm.foodyou.common.domain.Weight
import com.maksimowiczm.foodyou.common.domain.food.FoodComposition
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentImage
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionUpdateService
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
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
        composition: FoodComposition,
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
                    composition = composition,
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
        composition: FoodComposition,
    ) {
        transact(identity) { recipe ->
            checkNotNull(recipe) { "Recipe with ID $identity not found" }
            recipe.update {
                it.copy(
                    name = name,
                    note = note,
                    image = imageBytes?.let { bytes -> blobStorage.store(bytes) },
                    servings = servings,
                    composition = composition,
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

    suspend fun updateRecipesWithComponent(
        identity: FoodCompositionComponentIdentity.Leaf,
        name: FoodName,
        nutritionFacts: NutritionFacts,
        servingWeight: Weight? = null,
        packageWeight: Weight? = null,
        image: FoodCompositionComponentImage? = null,
    ) = coroutineScope {
        compositionRepository
            .findRecipesUsing(identity)
            .map { recipeIdentity ->
                async {
                    transact(recipeIdentity) { recipe ->
                        recipe?.update {
                            it.copy(
                                composition =
                                    FoodCompositionUpdateService.update(
                                        composition = it.composition,
                                        identity = identity,
                                        name = name,
                                        nutritionFacts = nutritionFacts,
                                        servingWeight = servingWeight,
                                        packageWeight = packageWeight,
                                        image = image,
                                    )
                            )
                        } ?: emptyList()
                    }
                }
            }
            .awaitAll()
    }

    suspend fun updateRecipesWithRecipe(
        identity: FoodCompositionComponentIdentity.Composite,
        name: FoodName,
        composition: FoodComposition,
        servingWeight: Weight? = null,
        packageWeight: Weight? = null,
        image: FoodCompositionComponentImage? = null,
    ) = coroutineScope {
        compositionRepository
            .findRecipesUsing(identity)
            .map { recipeIdentity ->
                async {
                    transact(recipeIdentity) { recipe ->
                        recipe?.update {
                            it.copy(
                                composition =
                                    FoodCompositionUpdateService.update(
                                        composition = it.composition,
                                        identity = identity,
                                        name = name,
                                        newComposition = composition,
                                        servingWeight = servingWeight,
                                        packageWeight = packageWeight,
                                        image = image,
                                    )
                            )
                        } ?: emptyList()
                    }
                }
            }
            .awaitAll()
    }

    suspend fun removeComponentFromRecipes(identity: FoodCompositionComponentIdentity.Identified) =
        coroutineScope {
            compositionRepository
                .findRecipesUsing(identity)
                .map { recipeIdentity ->
                    async {
                        transact(recipeIdentity) { recipe ->
                            recipe ?: return@transact emptyList()
                            val updatedComposition =
                                FoodCompositionUpdateService.remove(recipe.composition, identity)

                            // Delete recipe without ingredients
                            if (updatedComposition == null) recipe.remove(DeleteStrategy.Delete)
                            else recipe.update { it.copy(composition = updatedComposition) }
                        }
                    }
                }
                .awaitAll()
        }

    suspend fun unlinkComponentFromRecipes(identity: FoodCompositionComponentIdentity.Identified) =
        coroutineScope {
            compositionRepository
                .findRecipesUsing(identity)
                .map { recipeIdentity ->
                    async {
                        transact(recipeIdentity) { recipe ->
                            recipe ?: return@transact emptyList()
                            val updatedComposition =
                                FoodCompositionUpdateService.unlink(recipe.composition, identity)
                            recipe.update { it.copy(composition = updatedComposition) }
                        }
                    }
                }
                .awaitAll()
        }
}
