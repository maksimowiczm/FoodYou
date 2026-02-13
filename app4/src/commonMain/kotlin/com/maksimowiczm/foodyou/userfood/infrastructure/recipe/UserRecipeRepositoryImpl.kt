package com.maksimowiczm.foodyou.userfood.infrastructure.recipe

import com.maksimowiczm.foodyou.common.Err
import com.maksimowiczm.foodyou.common.Ok
import com.maksimowiczm.foodyou.common.Result
import com.maksimowiczm.foodyou.common.domain.Image
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.event.EventBus
import com.maksimowiczm.foodyou.common.event.IntegrationEvent
import com.maksimowiczm.foodyou.common.infrastructure.filekit.directory
import com.maksimowiczm.foodyou.common.infrastructure.room.immediateTransaction
import com.maksimowiczm.foodyou.common.onError
import com.maksimowiczm.foodyou.common.onSuccess
import com.maksimowiczm.foodyou.userfood.domain.UserFoodNote
import com.maksimowiczm.foodyou.userfood.domain.recipe.CircularUserRecipeReferenceError
import com.maksimowiczm.foodyou.userfood.domain.recipe.FoodReference
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipe
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipeDeletedEvent
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipeIdentity
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipeIngredient
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipeName
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipeRepository
import com.maksimowiczm.foodyou.userfood.infrastructure.room.UserFoodDatabase
import com.maksimowiczm.foodyou.userfood.infrastructure.room.recipe.FoodReferenceType
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.compressImage
import io.github.vinceglb.filekit.createDirectories
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.write
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class UserRecipeRepositoryImpl(
    private val database: UserFoodDatabase,
    private val integrationEventBus: EventBus<IntegrationEvent>,
) : UserRecipeRepository {
    private val mapper = RecipeMapper()
    private val dao = database.recipeDao

    override suspend fun create(
        accountId: LocalAccountId,
        name: UserRecipeName,
        servings: Double,
        image: Image.Local?,
        note: UserFoodNote?,
        finalWeight: Double?,
        ingredients: List<UserRecipeIngredient>,
    ): Result<UserRecipeIdentity, CircularUserRecipeReferenceError> {
        require(ingredients.isNotEmpty()) { "Recipe must have at least one ingredient" }
        require(servings > 0) { "Recipe must have a positive number of servings" }
        require(finalWeight == null || finalWeight > 0) { "Final weight must be a positive number" }

        val recipeId = Uuid.random().toString()

        val recipeDirectory = accountId.directory() / "recipes"
        recipeDirectory.createDirectories()

        val photoPath =
            if (image != null) {
                val sourceFile = PlatformFile(image.uri)
                require(sourceFile.exists()) { "Image file does not exist at path: ${image.uri}" }
                val bytes = sourceFile.readBytes()

                val compressed =
                    FileKit.compressImage(
                        bytes = bytes,
                        quality = 85,
                        imageFormat = ImageFormat.JPEG,
                    )

                val dest = (recipeDirectory / "$recipeId.jpg").apply { write(compressed) }
                dest.path
            } else {
                null
            }

        val recipe =
            UserRecipe(
                identity = UserRecipeIdentity(recipeId, accountId),
                name = name,
                servings = servings,
                image = photoPath?.let { Image.Local(it) },
                note = note,
                finalWeight = finalWeight,
                ingredients = ingredients,
            )

        val recipeEntity = mapper.toEntity(recipe)
        val ingredientEntities = mapper.toIngredientEntities(ingredients)

        return database
            .immediateTransaction<Result<UserRecipeIdentity, CircularUserRecipeReferenceError>> {
                try {
                    checkCircularReference(recipeId, ingredients, accountId)
                    dao.insertRecipeWithIngredients(recipeEntity, ingredientEntities)
                    Ok(UserRecipeIdentity(recipeId, accountId))
                } catch (e: CircularRecipeReferenceException) {
                    Err(CircularUserRecipeReferenceError(e.recipeId, e.cyclePath))
                }
            }
            .onError { PlatformFile("${recipeId}.jpg").delete(mustExist = false) }
    }

    override suspend fun update(
        identity: UserRecipeIdentity,
        name: UserRecipeName,
        servings: Double,
        image: Image.Local?,
        note: UserFoodNote?,
        finalWeight: Double?,
        ingredients: List<UserRecipeIngredient>,
    ): Result<Unit, CircularUserRecipeReferenceError> {
        require(ingredients.isNotEmpty()) { "Recipe must have at least one ingredient" }
        require(servings > 0) { "Recipe must have a positive number of servings" }
        require(finalWeight == null || finalWeight > 0) { "Final weight must be a positive number" }

        val existingEntity = dao.observe(identity.id, identity.accountId.value).first()

        requireNotNull(existingEntity) { "Cannot edit non-existing recipe with id: ${identity.id}" }

        val uuid = identity.id
        val recipeDirectory = identity.accountId.directory() / "recipes"
        recipeDirectory.createDirectories()

        val oldImagePath = existingEntity.recipe.imagePath
        val imagePath: String? =
            if (image != null && existingEntity.recipe.imagePath != image.uri) {
                val sourceFile = PlatformFile(image.uri)
                require(sourceFile.exists()) { "Image file does not exist at path: ${image.uri}" }
                val bytes = sourceFile.readBytes()

                val compressed =
                    FileKit.compressImage(
                        bytes = bytes,
                        quality = 85,
                        imageFormat = ImageFormat.JPEG,
                    )

                val dest = (recipeDirectory / "$uuid.jpg").apply { write(compressed) }

                dest.path
            } else if (image == null && existingEntity.recipe.imagePath != null) {
                null
            } else {
                existingEntity.recipe.imagePath
            }

        val recipe =
            UserRecipe(
                identity = identity,
                name = name,
                servings = servings,
                image = imagePath?.let { Image.Local(it) },
                note = note,
                finalWeight = finalWeight,
                ingredients = ingredients,
            )

        val updatedEntity = mapper.toEntity(recipe, sqliteId = existingEntity.recipe.sqliteId)
        val ingredientEntities = mapper.toIngredientEntities(ingredients)

        return database
            .immediateTransaction<Result<Unit, CircularUserRecipeReferenceError>> {
                try {
                    checkCircularReference(identity.id, ingredients, identity.accountId)
                    dao.updateRecipeWithIngredients(updatedEntity, ingredientEntities)
                    Ok()
                } catch (e: CircularRecipeReferenceException) {
                    Err(CircularUserRecipeReferenceError(e.recipeId, e.cyclePath))
                }
            }
            .onError {
                // Cleanup new image if transaction failed
                if (imagePath != null && imagePath != oldImagePath) {
                    PlatformFile(imagePath).delete(mustExist = false)
                }
            }
            .onSuccess {
                // Delete old image if it was replaced
                if (imagePath != oldImagePath && oldImagePath != null) {
                    val existingFile = PlatformFile(oldImagePath)
                    if (existingFile.exists()) {
                        existingFile.delete()
                    }
                }
            }
    }

    override fun observe(identity: UserRecipeIdentity): Flow<UserRecipe?> =
        dao.observe(identity.id, identity.accountId.value).map { entity ->
            entity?.let(mapper::toDomain)
        }

    override suspend fun delete(identity: UserRecipeIdentity) {
        val existingEntity = dao.observe(identity.id, identity.accountId.value).first()

        requireNotNull(existingEntity) {
            "Cannot delete non-existing recipe with id: ${identity.id}"
        }

        dao.deleteRecipe(existingEntity.recipe)
        PlatformFile("${existingEntity.recipe.uuid}.jpg").delete(mustExist = false)

        integrationEventBus.publish(UserRecipeDeletedEvent(identity))
    }

    override suspend fun findRecipesUsingFood(
        foodReference: FoodReference,
        accountId: LocalAccountId,
    ): List<UserRecipe> {
        val foodReferenceType =
            when (foodReference) {
                is FoodReference.UserProduct -> FoodReferenceType.UserFood
                is FoodReference.FoodDataCentral -> FoodReferenceType.FoodDataCentral
                is FoodReference.OpenFoodFacts -> FoodReferenceType.OpenFoodFacts
                is FoodReference.UserRecipe -> FoodReferenceType.UserRecipe
            }

        return dao.findRecipesUsingFood(
                accountId = accountId.value,
                foodReferenceType = foodReferenceType,
                foodId = foodReference.foodId,
            )
            .map { mapper.toDomain(it) }
    }

    /**
     * Checks if adding the given ingredients to a recipe would create a circular reference.
     *
     * Uses depth-first search to detect cycles in the recipe dependency graph.
     *
     * @param recipeId The recipe being created/updated
     * @param ingredients The ingredients to check
     * @param accountId The account ID for querying recipes
     * @throws CircularUserRecipeReferenceError if a circular reference is detected
     */
    private suspend fun checkCircularReference(
        recipeId: String,
        ingredients: List<UserRecipeIngredient>,
        accountId: LocalAccountId,
    ) {
        // Get all recipe references in ingredients
        val referencedRecipes =
            ingredients.mapNotNull { ingredient ->
                when (val ref = ingredient.foodReference) {
                    is FoodReference.UserRecipe -> ref.foodId
                    else -> null
                }
            }

        // Check each referenced recipe for cycles
        referencedRecipes.forEach { referencedRecipeId ->
            checkCircularReferenceRecursive(
                currentRecipeId = referencedRecipeId,
                targetRecipeId = recipeId,
                accountId = accountId,
                visitedPath = mutableListOf(recipeId),
            )
        }
    }

    /**
     * Recursively checks for circular references using depth-first search.
     *
     * @param currentRecipeId The recipe currently being examined
     * @param targetRecipeId The original recipe we're checking (looking for cycle back to this)
     * @param accountId The account ID
     * @param visitedPath The path of recipes visited so far (for error reporting)
     * @throws CircularUserRecipeReferenceError if a cycle is detected
     */
    private suspend fun checkCircularReferenceRecursive(
        currentRecipeId: String,
        targetRecipeId: String,
        accountId: LocalAccountId,
        visitedPath: MutableList<String>,
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
        val currentRecipe =
            dao.observe(currentRecipeId, accountId.value).first() ?: return // Recipe doesn't exist

        visitedPath.add(currentRecipeId)

        // Get all recipe references in the current recipe's ingredients
        val nestedRecipes =
            currentRecipe.ingredients.mapNotNull { ingredient ->
                when (ingredient.foodReferenceType) {
                    FoodReferenceType.UserRecipe -> ingredient.foodId
                    else -> null
                }
            }

        // Recursively check each nested recipe
        nestedRecipes.forEach { nestedRecipeId ->
            checkCircularReferenceRecursive(
                currentRecipeId = nestedRecipeId,
                targetRecipeId = targetRecipeId,
                accountId = accountId,
                visitedPath = visitedPath,
            )
        }

        visitedPath.removeAt(visitedPath.size - 1)
    }

    private class CircularRecipeReferenceException(
        val recipeId: String,
        val cyclePath: List<String>,
    ) : Exception()
}
