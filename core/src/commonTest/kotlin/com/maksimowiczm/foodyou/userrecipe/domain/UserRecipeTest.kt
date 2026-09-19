package com.maksimowiczm.foodyou.userrecipe.domain

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotQuantity
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.TrackedCompositeFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.TrackedLeafFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.grams
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.uuid.Uuid

class UserRecipeTest {
    private val id = UserRecipeId(Uuid.random())
    private val name = FoodName(fallback = "Apple Pie")
    private val components =
        listOf(
            MeasuredFoodSnapshot(
                snapshot =
                    TrackedLeafFoodSnapshot(
                        id = FoodSnapshotId.OpenFoodFacts("123"),
                        name = FoodName(fallback = "Apple"),
                        brand = null,
                        image = null,
                        nutritionFacts = NutritionFacts(),
                    ),
                quantity =
                    FoodSnapshotQuantity.Weight(
                        absoluteWeight = 100.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )
        )
    private val userRecipe =
        UserRecipe(
            id = id,
            name = name,
            note = "Best pie",
            image = null,
            servings = 8.0,
            components = components,
        )

    @Test
    fun decide_create_returns_created_event() {
        val command = UserRecipeCommand.Create(userRecipe)

        val events = null.decide(command)

        assertEquals(1, events.size)
        val event = assertIs<UserRecipeCreatedEvent>(events[0])
        assertEquals(userRecipe, event.recipe)
    }

    @Test
    fun decide_update_returns_updated_event_when_changed() {
        val updatedName = FoodName(fallback = "Banana Bread")
        val command = UserRecipeCommand.Update { it.copy(name = updatedName) }

        val events = userRecipe.decide(command)

        assertEquals(1, events.size)
        val event = assertIs<UserRecipeUpdatedEvent>(events[0])
        assertEquals(updatedName, event.recipe.name)
    }

    @Test
    fun decide_update_returns_empty_list_when_not_changed() {
        val command = UserRecipeCommand.Update { it }
        val events = userRecipe.decide(command)
        assertEquals(0, events.size)
    }

    @Test
    fun decide_remove_returns_deleted_event() {
        val command = UserRecipeCommand.Remove(DeleteStrategy.Delete)

        val events = userRecipe.decide(command)

        assertEquals(1, events.size)
        val event = assertIs<UserRecipeDeletedEvent>(events[0])
        assertEquals(id, event.userRecipeId)
        assertEquals(DeleteStrategy.Delete, event.strategy)
    }

    @Test
    fun apply_created_event() {
        val event = UserRecipeCreatedEvent(userRecipe)
        val result = null.apply(event)
        assertEquals(userRecipe, result)
    }

    @Test
    fun apply_updated_event() {
        val updatedRecipe = userRecipe.copy(note = "New Note")
        val event = UserRecipeUpdatedEvent(updatedRecipe)
        val result = userRecipe.apply(event)
        assertEquals(updatedRecipe, result)
    }

    @Test
    fun apply_deleted_event() {
        val event = UserRecipeDeletedEvent(id, DeleteStrategy.Delete)
        val result = userRecipe.apply(event)
        assertNull(result)
    }

    @Test
    fun toUserRecipe_reconstructs_state() {
        val updatedRecipe = userRecipe.copy(note = "Updated Note")
        val events =
            listOf(
                UserRecipeCreatedEvent(userRecipe),
                UserRecipeUpdatedEvent(updatedRecipe),
            )

        val result = events.toUserRecipe()
        assertEquals(updatedRecipe, result)
    }

    @Test
    fun toUserRecipe_returns_null_if_deleted() {
        val events =
            listOf(
                UserRecipeCreatedEvent(userRecipe),
                UserRecipeDeletedEvent(
                    id,
                    DeleteStrategy.Delete,
                ),
            )

        val result = events.toUserRecipe()
        assertNull(result)
    }

    @Test
    fun should_throw_exception_if_composition_contains_itself() {
        val recipeId = Uuid.random()
        val userRecipeId = UserRecipeId(recipeId)

        val selfReferencingComponents =
            listOf(
                MeasuredFoodSnapshot(
                    snapshot =
                        TrackedCompositeFoodSnapshot(
                            id = FoodSnapshotId.UserRecipe(recipeId),
                            name = FoodName(fallback = "Self"),
                            brand = null,
                            image = null,
                            components = components, // These components are fine by themselves
                        ),
                    quantity =
                        FoodSnapshotQuantity.Weight(
                            absoluteWeight = 100.grams,
                            servingWeight = null,
                            packageWeight = null,
                        ),
                )
            )

        assertFailsWith<IllegalArgumentException> {
            UserRecipe(
                id = userRecipeId,
                name = name,
                note = null,
                image = null,
                servings = 1.0,
                components = selfReferencingComponents,
            )
        }
    }
}
