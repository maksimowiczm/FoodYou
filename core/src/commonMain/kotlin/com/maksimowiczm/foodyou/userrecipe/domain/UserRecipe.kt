@file:MustUseReturnValues

package com.maksimowiczm.foodyou.userrecipe.domain

import com.maksimowiczm.foodyou.common.domain.BlobDigest
import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.Weight
import com.maksimowiczm.foodyou.common.domain.food.CompositeFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotImage
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.allComponentIdentities
import com.maksimowiczm.foodyou.common.domain.food.nutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.totalWeight
import kotlin.time.Clock
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable data class UserRecipeIdentity(val id: Uuid)

@Serializable
data class UserRecipe(
    val identity: UserRecipeIdentity,
    val name: FoodName,
    val note: String?,
    val image: BlobDigest?,
    val servings: Double,
    val components: List<MeasuredFoodSnapshot>,
) {
    init {
        require(note == null || note.isNotBlank()) { "Note cannot be blank" }
        require(servings > 0) { "Servings must be positive number" }
        require(FoodSnapshotId.UserRecipe(identity.id) !in components.allComponentIdentities) {
            "Circular dependency: Recipe cannot contain itself in its components"
        }
    }

    val nutritionFacts: NutritionFacts = components.nutritionFacts
    val totalWeight: Weight = components.totalWeight
    val servingWeight: Weight = totalWeight / servings

    companion object {
        fun create(recipe: UserRecipe, clock: Clock = Clock.System): List<UserRecipeEvent> =
            listOf(UserRecipeCreatedEvent(recipe = recipe, timestamp = clock.now()))
    }
}

inline fun UserRecipe.update(
    clock: Clock = Clock.System,
    transform: (UserRecipe) -> UserRecipe,
): List<UserRecipeEvent> = buildList {
    val updated = transform(this@update)
    if (updated != this@update)
        add(UserRecipeUpdatedEvent(recipe = updated, timestamp = clock.now()))
}

fun UserRecipe.remove(
    strategy: DeleteStrategy,
    clock: Clock = Clock.System,
): List<UserRecipeEvent> =
    listOf(
        UserRecipeDeletedEvent(identity = identity, strategy = strategy, timestamp = clock.now())
    )

fun UserRecipe?.apply(event: UserRecipeEvent): UserRecipe? =
    when (event) {
        is UserRecipeCreatedEvent -> event.recipe
        is UserRecipeUpdatedEvent -> event.recipe
        is UserRecipeDeletedEvent -> null
    }

fun Iterable<UserRecipeEvent>.toUserRecipe(): UserRecipe? =
    fold(null) { state, event -> state.apply(event) }

fun UserRecipe.toSnapshot() =
    CompositeFoodSnapshot(
        id = FoodSnapshotId.UserRecipe(identity.id),
        name = name,
        brand = null,
        image = image?.let(FoodSnapshotImage::Blob),
        components = components,
    )
