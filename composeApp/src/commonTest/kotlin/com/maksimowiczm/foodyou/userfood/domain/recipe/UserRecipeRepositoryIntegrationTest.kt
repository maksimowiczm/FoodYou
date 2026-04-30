package com.maksimowiczm.foodyou.userfood.domain.recipe

import androidx.room.useReaderConnection
import com.maksimowiczm.foodyou.common.Result
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.infrastructure.provideRoomDatabaseBuilder
import com.maksimowiczm.foodyou.userfood.infrastructure.UserFoodDatabase
import com.maksimowiczm.foodyou.userfood.infrastructure.UserFoodDatabase.Companion.buildDatabase
import com.maksimowiczm.foodyou.userfood.infrastructure.recipe.UserRecipeRepositoryImpl
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest

class UserRecipeRepositoryIntegrationTest {
    private lateinit var database: UserFoodDatabase
    private lateinit var repository: UserRecipeRepository

    @BeforeTest
    fun setup() {
        database = provideRoomDatabaseBuilder<UserFoodDatabase>().buildDatabase()
        repository = UserRecipeRepositoryImpl(database = database)
    }

    @AfterTest
    fun tearDown() {
        if (this::database.isInitialized) {
            database.close()
        }
    }

    @Test
    fun delete_recipeWithIngredients_cascadesDelete() = runTest {
        // Arrange - Create recipe with ingredients
        val identity = UserRecipeIdentity(Uuid.random())
        val recipe =
            UserRecipe(
                identity = identity,
                name = UserRecipeName("Recipe with Ingredients"),
                servings = 1.0,
                image = null,
                note = null,
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
        repository.save(recipe)

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
    fun save_replacesIngredients() = runTest {
        // Arrange - Create recipe with initial ingredients
        val identity = UserRecipeIdentity(Uuid.random())
        val initialRecipe =
            UserRecipe(
                identity = identity,
                name = UserRecipeName("Original Recipe"),
                servings = 1.0,
                image = null,
                note = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct(Uuid.random()),
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )
        repository.save(initialRecipe)

        val reference1 = FoodReference.UserProduct(Uuid.random())
        val reference2 = FoodReference.UserProduct(Uuid.random())

        // Act - Update with new ingredients
        val updatedRecipe =
            UserRecipe(
                identity = identity,
                name = UserRecipeName("Updated Recipe"),
                servings = 2.0,
                image = null,
                note = null,
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
        val updateResult = repository.save(updatedRecipe)

        // Assert - Update succeeded and ingredients were replaced atomically
        assertIs<Result.Success<UserRecipeIdentity, *>>(updateResult)

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
    fun save_withCircularReference_rollsBackTransaction() = runTest {
        // Arrange - Create Recipe A
        val recipeAId = UserRecipeIdentity(Uuid.random())
        val recipeA =
            UserRecipe(
                identity = recipeAId,
                name = UserRecipeName("Recipe A"),
                servings = 1.0,
                image = null,
                note = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct(Uuid.random()),
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )
        repository.save(recipeA)

        // Store original recipe state
        val originalRecipe = repository.observe(recipeAId).first()
        assertNotNull(originalRecipe)

        // Act - Try to update Recipe A to include itself (circular reference)
        val circularRecipe =
            UserRecipe(
                identity = recipeAId,
                name = UserRecipeName("Recipe A Updated"),
                servings = 2.0,
                image = null,
                note = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserRecipe(recipeAId.id),
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )
        val updateResult = repository.save(circularRecipe)

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
    fun save_withIndirectCircularReference_detectsCycleAndRollsBack() = runTest {
        // Arrange - Create Recipe A with UserFood
        val recipeAFoodReference = FoodReference.UserProduct(Uuid.random())
        val recipeAId = UserRecipeIdentity(Uuid.random())
        val recipeA =
            UserRecipe(
                identity = recipeAId,
                name = UserRecipeName("Recipe A"),
                servings = 1.0,
                image = null,
                note = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = recipeAFoodReference,
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )
        repository.save(recipeA)

        // Create Recipe B that includes Recipe A
        val recipeBId = UserRecipeIdentity(Uuid.random())
        val recipeB =
            UserRecipe(
                identity = recipeBId,
                name = UserRecipeName("Recipe B"),
                servings = 1.0,
                image = null,
                note = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserRecipe(recipeAId.id),
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )
        repository.save(recipeB)

        // Act - Try to update Recipe A to include Recipe B (creates cycle: A -> B -> A)
        val circularRecipeA =
            UserRecipe(
                identity = recipeAId,
                name = UserRecipeName("Recipe A Updated"),
                servings = 1.0,
                image = null,
                note = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserRecipe(recipeBId.id),
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )
        val updateResult = repository.save(circularRecipeA)

        // Assert - Update failed and transaction rolled back
        assertIs<Result.Error<*, CircularUserRecipeReferenceError>>(updateResult)

        // Verify Recipe A is unchanged
        val unchangedRecipeA = repository.observe(recipeAId).first()
        assertNotNull(unchangedRecipeA)
        assertEquals(
            recipeAFoodReference.id,
            (unchangedRecipeA.ingredients[0].foodReference as FoodReference.UserProduct).id,
        )

        // Verify Recipe B is also unchanged
        val recipeBUnchanged = repository.observe(recipeBId).first()
        assertNotNull(recipeBUnchanged)
        assertEquals(
            recipeAId.id,
            (recipeBUnchanged.ingredients[0].foodReference as FoodReference.UserRecipe).id,
        )
    }

    @Test
    fun findRecipesUsingFood_detectsDependencies() = runTest {
        // Arrange - Create a base recipe
        val baseRecipeId = UserRecipeIdentity(Uuid.random())
        val baseRecipe =
            UserRecipe(
                identity = baseRecipeId,
                name = UserRecipeName("Base Recipe"),
                servings = 1.0,
                image = null,
                note = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct(Uuid.random()),
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )
        repository.save(baseRecipe)

        // Create recipes that depend on base recipe
        repository.save(
            UserRecipe(
                identity = UserRecipeIdentity(Uuid.random()),
                name = UserRecipeName("Dependent Recipe 1"),
                servings = 1.0,
                image = null,
                note = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserRecipe(baseRecipeId.id),
                            quantity = AbsoluteQuantity.Weight(50.grams),
                        )
                    ),
            )
        )

        repository.save(
            UserRecipe(
                identity = UserRecipeIdentity(Uuid.random()),
                name = UserRecipeName("Dependent Recipe 2"),
                servings = 1.0,
                image = null,
                note = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserRecipe(baseRecipeId.id),
                            quantity = AbsoluteQuantity.Weight(75.grams),
                        )
                    ),
            )
        )

        // Create a recipe that doesn't depend on base recipe
        repository.save(
            UserRecipe(
                identity = UserRecipeIdentity(Uuid.random()),
                name = UserRecipeName("Independent Recipe"),
                servings = 1.0,
                image = null,
                note = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct(Uuid.random()),
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )
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

        repository.save(
            UserRecipe(
                identity = UserRecipeIdentity(Uuid.random()),
                name = UserRecipeName("Direct User 1"),
                servings = 1.0,
                image = null,
                note = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct(targetFoodId),
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )
        )

        repository.save(
            UserRecipe(
                identity = UserRecipeIdentity(Uuid.random()),
                name = UserRecipeName("Direct User 2"),
                servings = 1.0,
                image = null,
                note = null,
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
        )

        repository.save(
            UserRecipe(
                identity = UserRecipeIdentity(Uuid.random()),
                name = UserRecipeName("Non-User"),
                servings = 1.0,
                image = null,
                note = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct(Uuid.random()),
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )
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
    fun delete_withNestedRecipeReferences_maintainsDataIntegrity() = runTest {
        // Arrange - Create a dependency chain: Recipe C uses Recipe B uses Recipe A
        val recipeAId = UserRecipeIdentity(Uuid.random())
        repository.save(
            UserRecipe(
                identity = recipeAId,
                name = UserRecipeName("Recipe A"),
                servings = 1.0,
                image = null,
                note = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct(Uuid.random()),
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )
        )

        val recipeBId = UserRecipeIdentity(Uuid.random())
        repository.save(
            UserRecipe(
                identity = recipeBId,
                name = UserRecipeName("Recipe B"),
                servings = 1.0,
                image = null,
                note = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserRecipe(recipeAId.id),
                            quantity = AbsoluteQuantity.Weight(50.grams),
                        )
                    ),
            )
        )

        val recipeCId = UserRecipeIdentity(Uuid.random())
        repository.save(
            UserRecipe(
                identity = recipeCId,
                name = UserRecipeName("Recipe C"),
                servings = 1.0,
                image = null,
                note = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserRecipe(recipeBId.id),
                            quantity = AbsoluteQuantity.Weight(25.grams),
                        )
                    ),
            )
        )

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
    }
}
