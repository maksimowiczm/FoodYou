package com.maksimowiczm.foodyou.userfood.infrastructure.recipe

import com.maksimowiczm.foodyou.common.Err
import com.maksimowiczm.foodyou.common.Ok
import com.maksimowiczm.foodyou.common.Result
import com.maksimowiczm.foodyou.common.infrastructure.room.immediateTransaction
import com.maksimowiczm.foodyou.userfood.domain.recipe.CircularUserRecipeReferenceError
import com.maksimowiczm.foodyou.userfood.domain.recipe.FoodReference
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipe
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipeIdentity
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipeIngredient
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipeRepository
import com.maksimowiczm.foodyou.userfood.infrastructure.UserFoodDatabase
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

internal class UserRecipeRepositoryImpl(private val database: UserFoodDatabase) :
    UserRecipeRepository {
    private val mapper = RecipeMapper()
    private val dao = database.recipeDao

    override suspend fun save(recipe: UserRecipe): Result<Unit, CircularUserRecipeReferenceError> {
        val recipeId = recipe.identity.id
        val existingEntity = dao.observe(recipeId).first()

        val recipeEntity = mapper.toEntity(recipe, sqliteId = existingEntity?.recipe?.sqliteId ?: 0)
        val ingredientEntities = mapper.toIngredientEntities(recipe.ingredients)

        return database.immediateTransaction<Result<Unit, CircularUserRecipeReferenceError>> {
            try {
                checkCircularReference(recipeId, recipe.ingredients)
                if (existingEntity == null) {
                    dao.insertRecipeWithIngredients(recipeEntity, ingredientEntities)
                } else {
                    dao.updateRecipeWithIngredients(recipeEntity, ingredientEntities)
                }
                Ok()
            } catch (e: CircularRecipeReferenceException) {
                Err(CircularUserRecipeReferenceError(e.recipeId, e.cyclePath))
            }
        }
    }

    override fun observe(identity: UserRecipeIdentity): Flow<UserRecipe?> =
        dao.observe(identity.id).map { entity -> entity?.let(mapper::toDomain) }

    override suspend fun delete(identity: UserRecipeIdentity) {
        val existingEntity = dao.observe(identity.id).first()

        requireNotNull(existingEntity) {
            "Cannot delete non-existing recipe with id: ${identity.id}"
        }

        dao.deleteRecipe(existingEntity.recipe)
    }

    override suspend fun findRecipesUsingFood(foodReference: FoodReference): List<UserRecipe> {
        val foodReferenceJson = Json.encodeToString(foodReference)

        return dao.findRecipesUsingFood(foodReferenceJson = foodReferenceJson).map {
            mapper.toDomain(it)
        }
    }

    /**
     * Checks if adding the given ingredients to a recipe would create a circular reference.
     *
     * Uses depth-first search to detect cycles in the recipe dependency graph.
     *
     * @param recipeId The recipe being created/updated
     * @param ingredients The ingredients to check
     * @throws CircularUserRecipeReferenceError if a circular reference is detected
     */
    private suspend fun checkCircularReference(
        recipeId: Uuid,
        ingredients: List<UserRecipeIngredient>,
    ) {
        // Get all recipe references in ingredients
        val referencedRecipes =
            ingredients.mapNotNull { ingredient ->
                when (val ref = ingredient.foodReference) {
                    is FoodReference.UserRecipe -> ref.id
                    else -> null
                }
            }

        // Check each referenced recipe for cycles
        referencedRecipes.forEach { referencedRecipeId ->
            checkCircularReferenceRecursive(
                currentRecipeId = referencedRecipeId,
                targetRecipeId = recipeId,
                visitedPath = mutableListOf(recipeId),
            )
        }
    }

    /**
     * Recursively checks for circular references using depth-first search.
     *
     * @param currentRecipeId The recipe currently being examined
     * @param targetRecipeId The original recipe we're checking (looking for cycle back to this)
     * @param visitedPath The path of recipes visited so far (for error reporting)
     * @throws CircularUserRecipeReferenceError if a cycle is detected
     */
    private suspend fun checkCircularReferenceRecursive(
        currentRecipeId: Uuid,
        targetRecipeId: Uuid,
        visitedPath: MutableList<Uuid>,
    ) {
        // Check if we've found a cycle back to the target
        if (currentRecipeId == targetRecipeId) {
            throw CircularRecipeReferenceException(
                recipeId = targetRecipeId,
                cyclePath = visitedPath.toList(),
            )
        }

        // Prevent infinite loops in case of existing circular references
        if (currentRecipeId in visitedPath) {
            return
        }

        // Get the current recipe
        val currentRecipe = dao.observe(currentRecipeId).first() ?: return // Recipe doesn't exist

        visitedPath.add(currentRecipeId)

        // Get all recipe references in the current recipe's ingredients
        val nestedRecipes =
            currentRecipe.ingredients.mapNotNull { ingredient ->
                Json.decodeFromString<FoodReference>(ingredient.foodReferenceJson)
                    as? FoodReference.UserRecipe
            }

        // Recursively check each nested recipe
        nestedRecipes.forEach { nestedRecipeId ->
            checkCircularReferenceRecursive(
                currentRecipeId = nestedRecipeId.id,
                targetRecipeId = targetRecipeId,
                visitedPath = visitedPath,
            )
        }

        visitedPath.removeAt(visitedPath.size - 1)
    }

    private class CircularRecipeReferenceException(val recipeId: Uuid, val cyclePath: List<Uuid>) :
        Exception()
}
