package com.maksimowiczm.foodyou.userfood.domain.recipe

import androidx.room.useReaderConnection
import com.maksimowiczm.foodyou.common.Result
import com.maksimowiczm.foodyou.common.domain.ImageUri
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.event.IntegrationEvent
import com.maksimowiczm.foodyou.common.event.ListEventBus
import com.maksimowiczm.foodyou.common.infrastructure.filekit.FileKitBlobStorage
import com.maksimowiczm.foodyou.common.infrastructure.provideRoomDatabaseBuilder
import com.maksimowiczm.foodyou.userfood.infrastructure.UserFoodDatabase
import com.maksimowiczm.foodyou.userfood.infrastructure.UserFoodDatabase.Companion.buildDatabase
import com.maksimowiczm.foodyou.userfood.infrastructure.recipe.UserRecipeRepositoryImpl
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.write
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import okio.ByteString.Companion.toByteString

class UserRecipeRepositoryIntegrationTest {
    private lateinit var database: UserFoodDatabase
    private lateinit var repository: UserRecipeRepository
    private lateinit var eventBus: ListEventBus<IntegrationEvent>
    private lateinit var blobStorage: FileKitBlobStorage

    @BeforeTest
    fun setup() {
        database = provideRoomDatabaseBuilder<UserFoodDatabase>().buildDatabase()
        eventBus = ListEventBus()
        blobStorage = FileKitBlobStorage()
        repository =
            UserRecipeRepositoryImpl(
                database = database,
                integrationEventBus = eventBus,
                blobStorage = blobStorage,
            )
    }

    @AfterTest
    fun tearDown() {
        if (this::database.isInitialized) {
            database.close()
        }
    }

    @Test
    fun delete_publishesRecipeDeletedEvent() = runTest {
        // Arrange - Create recipe
        val createResult =
            repository.create(
                name = UserRecipeName("Recipe to Delete"),
                servings = 1.0,
                image = null,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct(Uuid.random()),
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )
        val identity = assertIs<Result.Success<UserRecipeIdentity, *>>(createResult).data

        // Act - Delete recipe
        repository.delete(identity)

        // Assert - Event was published with correct data
        val publishedEvents = eventBus.publishedEvents
        assertEquals(1, publishedEvents.size)
        val event = assertIs<UserRecipeDeletedEvent>(publishedEvents[0])
        assertEquals(identity, event.identity)
        assertEquals(identity.id, event.identity.id)
    }

    @Test
    fun delete_recipeWithIngredients_cascadesDelete() = runTest {
        // Arrange - Create recipe with ingredients
        val createResult =
            repository.create(
                name = UserRecipeName("Recipe with Ingredients"),
                servings = 1.0,
                image = null,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct(Uuid.random()),
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        ),
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct(Uuid.random()),
                            quantity = AbsoluteQuantity.Weight(50.grams),
                        ),
                    ),
            )
        val identity = assertIs<Result.Success<UserRecipeIdentity, *>>(createResult).data

        // Verify ingredients were created
        database.useReaderConnection { transactor ->
            transactor.usePrepared("SELECT COUNT(*) FROM RecipeIngredient") {
                it.step()
                val count = it.getInt(0)
                assertEquals(2, count)
            }
        }

        // Act - Delete recipe
        repository.delete(identity)

        // Assert - Recipe and ingredients are deleted due to cascade
        assertNull(repository.observe(identity).firstOrNull())

        database.useReaderConnection { transactor ->
            transactor.usePrepared("SELECT COUNT(*) FROM RecipeIngredient") {
                it.step()
                val count = it.getInt(0)
                assertEquals(0, count, "Ingredients should be cascade deleted")
            }
        }
    }

    @Test
    fun update_replacesIngredients() = runTest {
        // Arrange - Create recipe with initial ingredients
        val createResult =
            repository.create(
                name = UserRecipeName("Original Recipe"),
                servings = 1.0,
                image = null,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct(Uuid.random()),
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )
        val identity = assertIs<Result.Success<UserRecipeIdentity, *>>(createResult).data

        val reference1 = FoodReference.UserProduct(Uuid.random())
        val reference2 = FoodReference.UserProduct(Uuid.random())

        // Act - Update with new ingredients
        val updateResult =
            repository.update(
                identity = identity,
                name = UserRecipeName("Updated Recipe"),
                servings = 2.0,
                image = null,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = reference1,
                            quantity = AbsoluteQuantity.Weight(200.grams),
                        ),
                        UserRecipeIngredient(
                            foodReference = reference2,
                            quantity = AbsoluteQuantity.Weight(150.grams),
                        ),
                    ),
            )

        // Assert - Update succeeded and ingredients were replaced atomically
        val _ = assertIs<Result.Success<Unit, *>>(updateResult)

        val recipe = repository.observe(identity).first()
        assertNotNull(recipe)
        assertEquals(2, recipe.ingredients.size)
        assertEquals(
            reference1.id,
            (recipe.ingredients[0].foodReference as FoodReference.UserProduct).id,
        )
        assertEquals(
            reference2.id,
            (recipe.ingredients[1].foodReference as FoodReference.UserProduct).id,
        )

        // Verify old ingredients are removed
        database.useReaderConnection { transactor ->
            transactor.usePrepared("SELECT COUNT(*) FROM RecipeIngredient") {
                it.step()
                val count = it.getInt(0)
                assertEquals(2, count, "Should only have new ingredients")
            }
        }
    }

    @Test
    fun update_withCircularReference_rollsBackTransaction() = runTest {
        // Arrange - Create Recipe A
        val recipeAResult =
            repository.create(
                name = UserRecipeName("Recipe A"),
                servings = 1.0,
                image = null,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct(Uuid.random()),
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )
        val recipeAId = assertIs<Result.Success<UserRecipeIdentity, *>>(recipeAResult).data

        // Store original recipe state
        val originalRecipe = repository.observe(recipeAId).first()
        assertNotNull(originalRecipe)

        // Act - Try to update Recipe A to include itself (circular reference)
        val updateResult =
            repository.update(
                identity = recipeAId,
                name = UserRecipeName("Recipe A Updated"),
                servings = 2.0,
                image = null,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserRecipe(recipeAId.id),
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )

        // Assert - Update failed with circular reference error
        val error = assertIs<Result.Error<*, CircularUserRecipeReferenceError>>(updateResult).error
        assertEquals(recipeAId.id, error.recipeId)

        // Verify transaction was rolled back - recipe remains unchanged
        val unchangedRecipe = repository.observe(recipeAId).first()
        assertNotNull(unchangedRecipe)
        assertEquals(originalRecipe.name.value, unchangedRecipe.name.value)
        assertEquals(originalRecipe.servings, unchangedRecipe.servings)
        assertEquals(originalRecipe.ingredients.size, unchangedRecipe.ingredients.size)
    }

    @Test
    fun update_withIndirectCircularReference_detectsCycleAndRollsBack() = runTest {
        // Arrange - Create Recipe A with UserFood
        val recipeAFoodReference = FoodReference.UserProduct(Uuid.random())
        val recipeAResult =
            repository.create(
                name = UserRecipeName("Recipe A"),
                servings = 1.0,
                image = null,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = recipeAFoodReference,
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )
        val recipeAId = assertIs<Result.Success<UserRecipeIdentity, *>>(recipeAResult).data

        // Create Recipe B that includes Recipe A
        val recipeBResult =
            repository.create(
                name = UserRecipeName("Recipe B"),
                servings = 1.0,
                image = null,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserRecipe(recipeAId.id),
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )
        val recipeBId = assertIs<Result.Success<UserRecipeIdentity, *>>(recipeBResult).data

        // Act - Try to update Recipe A to include Recipe B (creates cycle: A -> B -> A)
        val updateResult =
            repository.update(
                identity = recipeAId,
                name = UserRecipeName("Recipe A Updated"),
                servings = 1.0,
                image = null,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserRecipe(recipeBId.id),
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )

        // Assert - Update failed and transaction rolled back
        val _ = assertIs<Result.Error<*, CircularUserRecipeReferenceError>>(updateResult)

        // Verify Recipe A is unchanged
        val unchangedRecipeA = repository.observe(recipeAId).first()
        assertNotNull(unchangedRecipeA)
        assertEquals(
            recipeAFoodReference.id,
            (unchangedRecipeA.ingredients[0].foodReference as FoodReference.UserProduct).id,
        )

        // Verify Recipe B is also unchanged
        val recipeB = repository.observe(recipeBId).first()
        assertNotNull(recipeB)
        assertEquals(
            recipeAId.id,
            (recipeB.ingredients[0].foodReference as FoodReference.UserRecipe).id,
        )
    }

    @Test
    fun findRecipesUsingFood_detectsDependencies() = runTest {
        // Arrange - Create a base recipe
        val baseRecipeResult =
            repository.create(
                name = UserRecipeName("Base Recipe"),
                servings = 1.0,
                image = null,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct(Uuid.random()),
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )
        val baseRecipeId = assertIs<Result.Success<UserRecipeIdentity, *>>(baseRecipeResult).data

        // Create recipes that depend on base recipe
        repository.create(
            name = UserRecipeName("Dependent Recipe 1"),
            servings = 1.0,
            image = null,
            note = null,
            finalWeight = null,
            ingredients =
                listOf(
                    UserRecipeIngredient(
                        foodReference = FoodReference.UserRecipe(baseRecipeId.id),
                        quantity = AbsoluteQuantity.Weight(50.grams),
                    )
                ),
        )

        repository.create(
            name = UserRecipeName("Dependent Recipe 2"),
            servings = 1.0,
            image = null,
            note = null,
            finalWeight = null,
            ingredients =
                listOf(
                    UserRecipeIngredient(
                        foodReference = FoodReference.UserRecipe(baseRecipeId.id),
                        quantity = AbsoluteQuantity.Weight(75.grams),
                    )
                ),
        )

        // Create a recipe that doesn't depend on base recipe
        repository.create(
            name = UserRecipeName("Independent Recipe"),
            servings = 1.0,
            image = null,
            note = null,
            finalWeight = null,
            ingredients =
                listOf(
                    UserRecipeIngredient(
                        foodReference = FoodReference.UserProduct(Uuid.random()),
                        quantity = AbsoluteQuantity.Weight(100.grams),
                    )
                ),
        )

        // Act - Find recipes that use the base recipe
        val dependentRecipes =
            repository.findRecipesUsingFood(
                foodReference = FoodReference.UserRecipe(baseRecipeId.id)
            )

        // Assert - Only dependent recipes are found
        assertEquals(2, dependentRecipes.size)
        val dependentNames = dependentRecipes.map { it.name.value }.toSet()
        assertEquals(setOf("Dependent Recipe 1", "Dependent Recipe 2"), dependentNames)
    }

    @Test
    fun findRecipesUsingFood_withUserFoodReference_findsAllDependencies() = runTest {
        // Arrange - Create recipes using specific user food
        val targetFoodId = Uuid.random()

        repository.create(
            name = UserRecipeName("Direct User 1"),
            servings = 1.0,
            image = null,
            note = null,
            finalWeight = null,
            ingredients =
                listOf(
                    UserRecipeIngredient(
                        foodReference = FoodReference.UserProduct(targetFoodId),
                        quantity = AbsoluteQuantity.Weight(100.grams),
                    )
                ),
        )

        repository.create(
            name = UserRecipeName("Direct User 2"),
            servings = 1.0,
            image = null,
            note = null,
            finalWeight = null,
            ingredients =
                listOf(
                    UserRecipeIngredient(
                        foodReference = FoodReference.UserProduct(targetFoodId),
                        quantity = AbsoluteQuantity.Weight(50.grams),
                    ),
                    UserRecipeIngredient(
                        foodReference = FoodReference.UserProduct(Uuid.random()),
                        quantity = AbsoluteQuantity.Weight(50.grams),
                    ),
                ),
        )

        repository.create(
            name = UserRecipeName("Non-User"),
            servings = 1.0,
            image = null,
            note = null,
            finalWeight = null,
            ingredients =
                listOf(
                    UserRecipeIngredient(
                        foodReference = FoodReference.UserProduct(Uuid.random()),
                        quantity = AbsoluteQuantity.Weight(100.grams),
                    )
                ),
        )

        // Act
        val recipes =
            repository.findRecipesUsingFood(foodReference = FoodReference.UserProduct(targetFoodId))

        // Assert - Cross-bounded context query finds all recipes using this user food
        assertEquals(2, recipes.size)
        val names = recipes.map { it.name.value }.toSet()
        assertEquals(setOf("Direct User 1", "Direct User 2"), names)
    }

    @Test
    fun create_withImage_storesBlobAndPersistsDigestUri() = runTest {
        val sourceBytes = "recipe-image-create-${Uuid.random()}".encodeToByteArray()
        val sourceImageUri = createSourceImage(sourceBytes)

        val createResult =
            repository.create(
                name = UserRecipeName("Recipe with image"),
                servings = 1.0,
                image = sourceImageUri,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct(Uuid.random()),
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )

        val identity = assertIs<Result.Success<UserRecipeIdentity, *>>(createResult).data
        val storedRecipe = repository.observe(identity).first()
        assertNotNull(storedRecipe)

        val expectedDigest = sourceBytes.toByteString().sha256().hex()
        val expectedUri = blobStorage.uri(expectedDigest)
        assertEquals(expectedUri.value, storedRecipe.image?.value)

        val storedBytes = PlatformFile(expectedUri.value).readBytes()
        assertContentEquals(sourceBytes, storedBytes)
    }

    @Test
    fun update_withNewImage_storesNewBlobAndUpdatesRecipeImage() = runTest {
        val initialBytes = "recipe-image-initial-${Uuid.random()}".encodeToByteArray()
        val initialImageUri = createSourceImage(initialBytes)

        val createResult =
            repository.create(
                name = UserRecipeName("Recipe image update"),
                servings = 1.0,
                image = initialImageUri,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct(Uuid.random()),
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )
        val identity = assertIs<Result.Success<UserRecipeIdentity, *>>(createResult).data

        val updatedBytes = "recipe-image-updated-${Uuid.random()}".encodeToByteArray()
        val updatedImageUri = createSourceImage(updatedBytes)
        val updateResult =
            repository.update(
                identity = identity,
                name = UserRecipeName("Recipe image update"),
                servings = 1.0,
                image = updatedImageUri,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct(Uuid.random()),
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )
        val _ = assertIs<Result.Success<Unit, *>>(updateResult)

        val storedRecipe = repository.observe(identity).first()
        assertNotNull(storedRecipe)

        val oldUri = blobStorage.uri(initialBytes.toByteString().sha256().hex())
        val updatedUri = blobStorage.uri(updatedBytes.toByteString().sha256().hex())
        assertEquals(updatedUri.value, storedRecipe.image?.value)
        assertEquals(false, oldUri.value == updatedUri.value)
        assertContentEquals(updatedBytes, PlatformFile(updatedUri.value).readBytes())
    }

    @Test
    fun delete_withImage_keepsStoredBlobFileAvailable() = runTest {
        val sourceBytes = "recipe-image-delete-${Uuid.random()}".encodeToByteArray()
        val sourceImageUri = createSourceImage(sourceBytes)

        val createResult =
            repository.create(
                name = UserRecipeName("Recipe image delete"),
                servings = 1.0,
                image = sourceImageUri,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct(Uuid.random()),
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )
        val identity = assertIs<Result.Success<UserRecipeIdentity, *>>(createResult).data

        repository.delete(identity)

        assertNull(repository.observe(identity).firstOrNull())

        val storedUri = blobStorage.uri(sourceBytes.toByteString().sha256().hex())
        val storedBytes = PlatformFile(storedUri.value).readBytes()
        assertContentEquals(sourceBytes, storedBytes)
    }

    @Test
    fun delete_withNestedRecipeReferences_maintainsDataIntegrity() = runTest {
        // Arrange - Create a dependency chain: Recipe C uses Recipe B uses Recipe A
        val recipeAResult =
            repository.create(
                name = UserRecipeName("Recipe A"),
                servings = 1.0,
                image = null,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct(Uuid.random()),
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )
        val recipeAId = assertIs<Result.Success<UserRecipeIdentity, *>>(recipeAResult).data

        val recipeBResult =
            repository.create(
                name = UserRecipeName("Recipe B"),
                servings = 1.0,
                image = null,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserRecipe(recipeAId.id),
                            quantity = AbsoluteQuantity.Weight(50.grams),
                        )
                    ),
            )
        val recipeBId = assertIs<Result.Success<UserRecipeIdentity, *>>(recipeBResult).data

        val recipeCResult =
            repository.create(
                name = UserRecipeName("Recipe C"),
                servings = 1.0,
                image = null,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserRecipe(recipeBId.id),
                            quantity = AbsoluteQuantity.Weight(25.grams),
                        )
                    ),
            )
        val recipeCId = assertIs<Result.Success<UserRecipeIdentity, *>>(recipeCResult).data

        // Verify dependency chain
        val recipesUsingA = repository.findRecipesUsingFood(FoodReference.UserRecipe(recipeAId.id))
        assertEquals(1, recipesUsingA.size)
        assertEquals("Recipe B", recipesUsingA[0].name.value)

        val recipesUsingB = repository.findRecipesUsingFood(FoodReference.UserRecipe(recipeBId.id))
        assertEquals(1, recipesUsingB.size)
        assertEquals("Recipe C", recipesUsingB[0].name.value)

        // Act - Delete Recipe B (middle of chain)
        repository.delete(recipeBId)

        // Assert - Recipe B is deleted, but A and C remain intact
        assertNull(repository.observe(recipeBId).firstOrNull())
        assertNotNull(repository.observe(recipeAId).firstOrNull())
        assertNotNull(repository.observe(recipeCId).firstOrNull())

        // Recipe C still exists but now has an invalid reference (should be handled by app logic)
        val recipeC = repository.observe(recipeCId).first()
        assertNotNull(recipeC)
        assertEquals(
            recipeBId.id,
            (recipeC.ingredients[0].foodReference as FoodReference.UserRecipe).id,
        )

        // Event was published
        val events = eventBus.publishedEvents.filterIsInstance<UserRecipeDeletedEvent>()
        assertEquals(1, events.size)
        assertEquals(recipeBId.id, events[0].identity.id)
    }

    private suspend fun createSourceImage(bytes: ByteArray): ImageUri {
        val sourceFilePath = (FileKit.filesDir / "test-source-${Uuid.random()}.img").absolutePath()
        val sourceFile = PlatformFile(sourceFilePath)
        sourceFile.write(bytes)
        return ImageUri(sourceFilePath)
    }
}
