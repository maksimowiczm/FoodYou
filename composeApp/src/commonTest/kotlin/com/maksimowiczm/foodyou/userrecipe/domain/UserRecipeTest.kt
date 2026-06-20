package com.maksimowiczm.foodyou.userrecipe.domain

import com.maksimowiczm.foodyou.common.clock.staticClock
import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.food.FoodComponentComponentQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponent
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.grams
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.time.Instant
import kotlin.uuid.Uuid

class UserRecipeTest {
    private val identity = UserRecipeIdentity(Uuid.random())
    private val name = FoodName(fallback = "Apple Pie")
    private val components =
        listOf(
            FoodCompositionComponent.Simple(
                identity = FoodCompositionComponentIdentity.OpenFoodFacts("123"),
                name = FoodName(fallback = "Apple"),
                image = null,
                nutritionFacts = NutritionFacts(),
                quantity = FoodComponentComponentQuantity.Weight(100.grams),
                servingWeight = null,
                packageWeight = null,
            )
        )
    private val userRecipe =
        UserRecipe(
            identity = identity,
            name = name,
            note = "Best pie",
            image = null,
            servings = 8.0,
            components = components,
        )

    @Test
    fun create_returns_created_event() {
        val now = Instant.fromEpochSeconds(1000)
        val clock = staticClock(now)

        val events = UserRecipe.create(userRecipe, clock)

        assertEquals(1, events.size)
        val event = assertIs<UserRecipeCreatedEvent>(events[0])
        assertEquals(userRecipe, event.recipe)
        assertEquals(now, event.timestamp)
    }

    @Test
    fun update_returns_updated_event_when_changed() {
        val now = Instant.fromEpochSeconds(2000)
        val clock = staticClock(now)
        val updatedName = FoodName(fallback = "Banana Bread")

        val events = userRecipe.update(clock) { it.copy(name = updatedName) }

        assertEquals(1, events.size)
        val event = assertIs<UserRecipeUpdatedEvent>(events[0])
        assertEquals(updatedName, event.recipe.name)
        assertEquals(now, event.timestamp)
    }

    @Test
    fun update_returns_empty_list_when_not_changed() {
        val events = userRecipe.update { it }
        assertEquals(0, events.size)
    }

    @Test
    fun remove_returns_deleted_event() {
        val now = Instant.fromEpochSeconds(3000)
        val clock = staticClock(now)

        val events = userRecipe.remove(DeleteStrategy.Delete, clock)

        assertEquals(1, events.size)
        val event = assertIs<UserRecipeDeletedEvent>(events[0])
        assertEquals(identity, event.identity)
        assertEquals(DeleteStrategy.Delete, event.strategy)
        assertEquals(now, event.timestamp)
    }

    @Test
    fun apply_created_event() {
        val event = UserRecipeCreatedEvent(userRecipe, Instant.DISTANT_PAST)
        val result = null.apply(event)
        assertEquals(userRecipe, result)
    }

    @Test
    fun apply_updated_event() {
        val updatedRecipe = userRecipe.copy(note = "New Note")
        val event = UserRecipeUpdatedEvent(updatedRecipe, Instant.DISTANT_PAST)
        val result = userRecipe.apply(event)
        assertEquals(updatedRecipe, result)
    }

    @Test
    fun apply_deleted_event() {
        val event = UserRecipeDeletedEvent(identity, DeleteStrategy.Delete, Instant.DISTANT_PAST)
        val result = userRecipe.apply(event)
        assertNull(result)
    }

    @Test
    fun toUserRecipe_reconstructs_state() {
        val updatedRecipe = userRecipe.copy(note = "Updated Note")
        val events =
            listOf(
                UserRecipeCreatedEvent(userRecipe, Instant.fromEpochSeconds(1)),
                UserRecipeUpdatedEvent(updatedRecipe, Instant.fromEpochSeconds(2)),
            )

        val result = events.toUserRecipe()
        assertEquals(updatedRecipe, result)
    }

    @Test
    fun toUserRecipe_returns_null_if_deleted() {
        val events =
            listOf(
                UserRecipeCreatedEvent(userRecipe, Instant.fromEpochSeconds(1)),
                UserRecipeDeletedEvent(identity, DeleteStrategy.Delete, Instant.fromEpochSeconds(2)),
            )

        val result = events.toUserRecipe()
        assertNull(result)
    }

    @Test
    fun should_throw_exception_if_composition_contains_itself() {
        val recipeId = Uuid.random()
        val identity = UserRecipeIdentity(recipeId)

        val selfReferencingComponents =
            listOf(
                FoodCompositionComponent.Composite(
                    identity = FoodCompositionComponentIdentity.Recipe(recipeId),
                    name = FoodName(fallback = "Self"),
                    image = null,
                    quantity = FoodComponentComponentQuantity.Weight(100.grams),
                    components = components, // These components are fine by themselves
                    servingWeight = null,
                    packageWeight = null,
                )
            )

        assertFailsWith<IllegalArgumentException> {
            UserRecipe(
                identity = identity,
                name = name,
                note = null,
                image = null,
                servings = 1.0,
                components = selfReferencingComponents,
            )
        }
    }
}
