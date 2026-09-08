@file:MustUseReturnValues

package com.maksimowiczm.foodyou.userrecipe.domain

import com.maksimowiczm.foodyou.common.Decider
import com.maksimowiczm.foodyou.common.domain.BlobDigest
import com.maksimowiczm.foodyou.common.domain.Weight
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotImage
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.TrackedCompositeFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.allComponentIdentities
import com.maksimowiczm.foodyou.common.domain.food.nutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.totalWeight
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable data class UserRecipeId(val value: Uuid = Uuid.random())

@Serializable
data class UserRecipe(
    val id: UserRecipeId,
    val name: FoodName,
    val note: String?,
    val image: BlobDigest?,
    val servings: Double,
    val components: List<MeasuredFoodSnapshot>,
) {
    init {
        require(note == null || note.isNotBlank()) { "Note cannot be blank" }
        require(servings > 0) { "Servings must be positive number" }
        require(FoodSnapshotId.UserRecipe(id.value) !in components.allComponentIdentities) {
            "Circular dependency: Recipe cannot contain itself in its components"
        }
    }

    val nutritionFacts: NutritionFacts = components.nutritionFacts
    val totalWeight: Weight = components.totalWeight
    val servingWeight: Weight = totalWeight / servings
}

fun UserRecipe?.decide(command: UserRecipeCommand): List<UserRecipeEvent> =
    when (command) {
        is UserRecipeCommand.Create ->
            if (this == null) {
                listOf(
                    UserRecipeCreatedEvent(recipe = command.recipe, timestamp = command.timestamp)
                )
            } else {
                emptyList()
            }

        is UserRecipeCommand.Update ->
            if (this != null) {
                val updated = command.transform(this)
                if (updated != this) {
                    listOf(UserRecipeUpdatedEvent(recipe = updated, timestamp = command.timestamp))
                } else {
                    emptyList()
                }
            } else {
                emptyList()
            }

        is UserRecipeCommand.Remove ->
            if (this != null) {
                listOf(
                    UserRecipeDeletedEvent(
                        userRecipeId = id,
                        strategy = command.strategy,
                        timestamp = command.timestamp,
                    )
                )
            } else {
                emptyList()
            }
    }

fun UserRecipe?.apply(event: UserRecipeEvent): UserRecipe? =
    when (event) {
        is UserRecipeCreatedEvent -> event.recipe
        is UserRecipeUpdatedEvent -> event.recipe
        is UserRecipeDeletedEvent -> null
    }

fun Iterable<UserRecipeEvent>.toUserRecipe(): UserRecipe? =
    fold(null) { state, event -> state.apply(event) }

fun UserRecipe.toSnapshot() =
    TrackedCompositeFoodSnapshot(
        id = FoodSnapshotId.UserRecipe(id.value),
        name = name,
        brand = null,
        image = image?.let(FoodSnapshotImage::Blob),
        components = components,
    )

fun FoodSnapshotId.UserRecipe.toUserRecipeId() = UserRecipeId(id)

val userRecipeDecider =
    Decider<UserRecipeCommand, UserRecipeEvent, UserRecipe?>(
        decide = { command, state -> state.decide(command) },
        evolve = { state, event -> state.apply(event) },
        initialState = null,
    )
