package com.maksimowiczm.foodyou.userfood.domain.recipe

import androidx.room.useReaderConnection
import com.maksimowiczm.foodyou.common.Result
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.Grams
import com.maksimowiczm.foodyou.common.event.IntegrationEvent
import com.maksimowiczm.foodyou.common.event.ListEventBus
import com.maksimowiczm.foodyou.common.infrastructure.provideRoomDatabaseBuilder
import com.maksimowiczm.foodyou.userfood.infrastructure.recipe.UserRecipeRepositoryImpl
import com.maksimowiczm.foodyou.userfood.infrastructure.room.UserFoodDatabase
import com.maksimowiczm.foodyou.userfood.infrastructure.room.UserFoodDatabase.Companion.buildDatabase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest

class UserUserRecipeRepositoryIntegrationTest {
    private lateinit var database: UserFoodDatabase
    private lateinit var repository: UserRecipeRepository
    private lateinit var eventBus: ListEventBus<IntegrationEvent>
    private val testAccountId = LocalAccountId("test-account-id")

    @BeforeTest
    fun setup() {
        database = provideRoomDatabaseBuilder<UserFoodDatabase>().buildDatabase()
        eventBus = ListEventBus()
        repository = UserRecipeRepositoryImpl(database = database, integrationEventBus = eventBus)
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
                accountId = testAccountId,
                name = UserRecipeName("Recipe to Delete"),
                servings = 1.0,
                image = null,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct("user-food-1"),
                            quantity = AbsoluteQuantity.Weight(Grams(100.0)),
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
        assertEquals(testAccountId, event.identity.accountId)
    }

    @Test
    fun delete_recipeWithIngredients_cascadesDelete() = runTest {
        // Arrange - Create recipe with ingredients
        val createResult =
            repository.create(
                accountId = testAccountId,
                name = UserRecipeName("Recipe with Ingredients"),
                servings = 1.0,
                image = null,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct("food-1"),
                            quantity = AbsoluteQuantity.Weight(Grams(100.0)),
                        ),
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct("food-2"),
                            quantity = AbsoluteQuantity.Weight(Grams(50.0)),
                        ),
                    ),
            )
        val identity = assertIs<Result.Success<UserRecipeIdentity, *>>(createResult).data

        // Verify ingredients were created
        database.useReaderConnection { transactor ->
            transactor.usePrepared("SELECT COUNT(*) FROM UserRecipeIngredient") {
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
            transactor.usePrepared("SELECT COUNT(*) FROM UserRecipeIngredient") {
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
                accountId = testAccountId,
                name = UserRecipeName("Original Recipe"),
                servings = 1.0,
                image = null,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct("food-1"),
                            quantity = AbsoluteQuantity.Weight(Grams(100.0)),
                        )
                    ),
            )
        val identity = assertIs<Result.Success<UserRecipeIdentity, *>>(createResult).data

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
                            foodReference = FoodReference.UserProduct("food-2"),
                            quantity = AbsoluteQuantity.Weight(Grams(200.0)),
                        ),
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct("food-3"),
                            quantity = AbsoluteQuantity.Weight(Grams(150.0)),
                        ),
                    ),
            )

        // Assert - Update succeeded and ingredients were replaced atomically
        val _ = assertIs<Result.Success<Unit, *>>(updateResult)

        val recipe = repository.observe(identity).first()
        assertNotNull(recipe)
        assertEquals(2, recipe.ingredients.size)
        assertEquals("food-2", recipe.ingredients[0].foodReference.foodId)
        assertEquals("food-3", recipe.ingredients[1].foodReference.foodId)

        // Verify old ingredients are removed
        database.useReaderConnection { transactor ->
            transactor.usePrepared("SELECT COUNT(*) FROM UserRecipeIngredient") {
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
                accountId = testAccountId,
                name = UserRecipeName("Recipe A"),
                servings = 1.0,
                image = null,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct("food-1"),
                            quantity = AbsoluteQuantity.Weight(Grams(100.0)),
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
                            quantity = AbsoluteQuantity.Weight(Grams(100.0)),
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
        val recipeAResult =
            repository.create(
                accountId = testAccountId,
                name = UserRecipeName("Recipe A"),
                servings = 1.0,
                image = null,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct("food-1"),
                            quantity = AbsoluteQuantity.Weight(Grams(100.0)),
                        )
                    ),
            )
        val recipeAId = assertIs<Result.Success<UserRecipeIdentity, *>>(recipeAResult).data

        // Create Recipe B that includes Recipe A
        val recipeBResult =
            repository.create(
                accountId = testAccountId,
                name = UserRecipeName("Recipe B"),
                servings = 1.0,
                image = null,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserRecipe(recipeAId.id),
                            quantity = AbsoluteQuantity.Weight(Grams(100.0)),
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
                            quantity = AbsoluteQuantity.Weight(Grams(100.0)),
                        )
                    ),
            )

        // Assert - Update failed and transaction rolled back
        val _ = assertIs<Result.Error<*, CircularUserRecipeReferenceError>>(updateResult)

        // Verify Recipe A is unchanged
        val unchangedRecipeA = repository.observe(recipeAId).first()
        assertNotNull(unchangedRecipeA)
        assertEquals("food-1", unchangedRecipeA.ingredients[0].foodReference.foodId)

        // Verify Recipe B is also unchanged
        val recipeB = repository.observe(recipeBId).first()
        assertNotNull(recipeB)
        assertEquals(
            recipeAId.id,
            (recipeB.ingredients[0].foodReference as FoodReference.UserRecipe).foodId,
        )
    }

    @Test
    fun findRecipesUsingFood_detectsDependencies() = runTest {
        // Arrange - Create a base recipe
        val baseRecipeResult =
            repository.create(
                accountId = testAccountId,
                name = UserRecipeName("Base Recipe"),
                servings = 1.0,
                image = null,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct("food-1"),
                            quantity = AbsoluteQuantity.Weight(Grams(100.0)),
                        )
                    ),
            )
        val baseRecipeId = assertIs<Result.Success<UserRecipeIdentity, *>>(baseRecipeResult).data

        // Create recipes that depend on base recipe
        repository.create(
            accountId = testAccountId,
            name = UserRecipeName("Dependent Recipe 1"),
            servings = 1.0,
            image = null,
            note = null,
            finalWeight = null,
            ingredients =
                listOf(
                    UserRecipeIngredient(
                        foodReference = FoodReference.UserRecipe(baseRecipeId.id),
                        quantity = AbsoluteQuantity.Weight(Grams(50.0)),
                    )
                ),
        )

        repository.create(
            accountId = testAccountId,
            name = UserRecipeName("Dependent Recipe 2"),
            servings = 1.0,
            image = null,
            note = null,
            finalWeight = null,
            ingredients =
                listOf(
                    UserRecipeIngredient(
                        foodReference = FoodReference.UserRecipe(baseRecipeId.id),
                        quantity = AbsoluteQuantity.Weight(Grams(75.0)),
                    )
                ),
        )

        // Create a recipe that doesn't depend on base recipe
        repository.create(
            accountId = testAccountId,
            name = UserRecipeName("Independent Recipe"),
            servings = 1.0,
            image = null,
            note = null,
            finalWeight = null,
            ingredients =
                listOf(
                    UserRecipeIngredient(
                        foodReference = FoodReference.UserProduct("food-2"),
                        quantity = AbsoluteQuantity.Weight(Grams(100.0)),
                    )
                ),
        )

        // Act - Find recipes that use the base recipe
        val dependentRecipes =
            repository.findRecipesUsingFood(
                foodReference = FoodReference.UserRecipe(baseRecipeId.id),
                accountId = testAccountId,
            )

        // Assert - Only dependent recipes are found
        assertEquals(2, dependentRecipes.size)
        val dependentNames = dependentRecipes.map { it.name.value }.toSet()
        assertEquals(setOf("Dependent Recipe 1", "Dependent Recipe 2"), dependentNames)
    }

    @Test
    fun findRecipesUsingFood_withUserFoodReference_findsAllDependencies() = runTest {
        // Arrange - Create recipes using specific user food
        val targetFoodId = "target-user-food"

        repository.create(
            accountId = testAccountId,
            name = UserRecipeName("Direct User 1"),
            servings = 1.0,
            image = null,
            note = null,
            finalWeight = null,
            ingredients =
                listOf(
                    UserRecipeIngredient(
                        foodReference = FoodReference.UserProduct(targetFoodId),
                        quantity = AbsoluteQuantity.Weight(Grams(100.0)),
                    )
                ),
        )

        repository.create(
            accountId = testAccountId,
            name = UserRecipeName("Direct User 2"),
            servings = 1.0,
            image = null,
            note = null,
            finalWeight = null,
            ingredients =
                listOf(
                    UserRecipeIngredient(
                        foodReference = FoodReference.UserProduct(targetFoodId),
                        quantity = AbsoluteQuantity.Weight(Grams(50.0)),
                    ),
                    UserRecipeIngredient(
                        foodReference = FoodReference.UserProduct("other-food"),
                        quantity = AbsoluteQuantity.Weight(Grams(50.0)),
                    ),
                ),
        )

        repository.create(
            accountId = testAccountId,
            name = UserRecipeName("Non-User"),
            servings = 1.0,
            image = null,
            note = null,
            finalWeight = null,
            ingredients =
                listOf(
                    UserRecipeIngredient(
                        foodReference = FoodReference.UserProduct("different-food"),
                        quantity = AbsoluteQuantity.Weight(Grams(100.0)),
                    )
                ),
        )

        // Act
        val recipes =
            repository.findRecipesUsingFood(
                foodReference = FoodReference.UserProduct(targetFoodId),
                accountId = testAccountId,
            )

        // Assert - Cross-bounded context query finds all recipes using this user food
        assertEquals(2, recipes.size)
        val names = recipes.map { it.name.value }.toSet()
        assertEquals(setOf("Direct User 1", "Direct User 2"), names)
    }

    @Test
    fun delete_withNestedRecipeReferences_maintainsDataIntegrity() = runTest {
        // Arrange - Create a dependency chain: Recipe C uses Recipe B uses Recipe A
        val recipeAResult =
            repository.create(
                accountId = testAccountId,
                name = UserRecipeName("Recipe A"),
                servings = 1.0,
                image = null,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct("food-1"),
                            quantity = AbsoluteQuantity.Weight(Grams(100.0)),
                        )
                    ),
            )
        val recipeAId = assertIs<Result.Success<UserRecipeIdentity, *>>(recipeAResult).data

        val recipeBResult =
            repository.create(
                accountId = testAccountId,
                name = UserRecipeName("Recipe B"),
                servings = 1.0,
                image = null,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserRecipe(recipeAId.id),
                            quantity = AbsoluteQuantity.Weight(Grams(50.0)),
                        )
                    ),
            )
        val recipeBId = assertIs<Result.Success<UserRecipeIdentity, *>>(recipeBResult).data

        val recipeCResult =
            repository.create(
                accountId = testAccountId,
                name = UserRecipeName("Recipe C"),
                servings = 1.0,
                image = null,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserRecipe(recipeBId.id),
                            quantity = AbsoluteQuantity.Weight(Grams(25.0)),
                        )
                    ),
            )
        val recipeCId = assertIs<Result.Success<UserRecipeIdentity, *>>(recipeCResult).data

        // Verify dependency chain
        val recipesUsingA =
            repository.findRecipesUsingFood(FoodReference.UserRecipe(recipeAId.id), testAccountId)
        assertEquals(1, recipesUsingA.size)
        assertEquals("Recipe B", recipesUsingA[0].name.value)

        val recipesUsingB =
            repository.findRecipesUsingFood(FoodReference.UserRecipe(recipeBId.id), testAccountId)
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
            (recipeC.ingredients[0].foodReference as FoodReference.UserRecipe).foodId,
        )

        // Event was published
        val events = eventBus.publishedEvents.filterIsInstance<UserRecipeDeletedEvent>()
        assertEquals(1, events.size)
        assertEquals(recipeBId.id, events[0].identity.id)
    }

    @Test
    fun accountIsolation_recipesAreAccountSpecific() = runTest {
        // Arrange - Create recipes for different accounts
        val account1 = LocalAccountId("account-1")
        val account2 = LocalAccountId("account-2")

        val recipe1Result =
            repository.create(
                accountId = account1,
                name = UserRecipeName("Account 1 Recipe"),
                servings = 1.0,
                image = null,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct("food-1"),
                            quantity = AbsoluteQuantity.Weight(Grams(100.0)),
                        )
                    ),
            )
        val recipe1Id = assertIs<Result.Success<UserRecipeIdentity, *>>(recipe1Result).data

        val recipe2Result =
            repository.create(
                accountId = account2,
                name = UserRecipeName("Account 2 Recipe"),
                servings = 1.0,
                image = null,
                note = null,
                finalWeight = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct("food-2"),
                            quantity = AbsoluteQuantity.Weight(Grams(100.0)),
                        )
                    ),
            )
        val recipe2Id = assertIs<Result.Success<UserRecipeIdentity, *>>(recipe2Result).data

        // Act & Assert - Can observe own recipe
        assertNotNull(repository.observe(recipe1Id).firstOrNull())
        assertNotNull(repository.observe(recipe2Id).firstOrNull())

        // Can't observe other account's recipes
        assertNull(repository.observe(recipe1Id.copy(accountId = account2)).firstOrNull())
        assertNull(repository.observe(recipe2Id.copy(accountId = account1)).firstOrNull())

        // Account 1 can't find Account 2's recipes using food reference queries
        val account1Recipes =
            repository.findRecipesUsingFood(FoodReference.UserProduct("food-2"), account1)
        assertEquals(0, account1Recipes.size)

        val account2Recipes =
            repository.findRecipesUsingFood(FoodReference.UserProduct("food-1"), account2)
        assertEquals(0, account2Recipes.size)
    }
}
