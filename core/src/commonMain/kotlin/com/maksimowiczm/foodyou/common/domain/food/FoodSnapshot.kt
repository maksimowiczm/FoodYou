package com.maksimowiczm.foodyou.common.domain.food

import kotlinx.serialization.Serializable

@Serializable
sealed interface FoodSnapshot {
    val id: FoodSnapshotId
    val name: FoodName
    val brand: String?
    val image: FoodSnapshotImage?
    val nutritionFacts: NutritionFacts
    val allIdentities: Set<FoodSnapshotId>
}

@Serializable
sealed interface TrackedFoodSnapshot : FoodSnapshot {
    override val id: FoodSnapshotId.Tracked
}

@Serializable
data class AnonymousFoodSnapshot(
    override val id: FoodSnapshotId.Anonymous,
    override val name: FoodName,
    override val brand: String?,
    override val image: FoodSnapshotImage?,
    override val nutritionFacts: NutritionFacts,
) : FoodSnapshot {
    override val allIdentities = emptySet<FoodSnapshotId>()
}

@Serializable
data class CompositeFoodSnapshot(
    override val id: FoodSnapshotId.Tracked,
    override val name: FoodName,
    override val brand: String?,
    override val image: FoodSnapshotImage?,
    val components: List<MeasuredFoodSnapshot>,
) : TrackedFoodSnapshot {
    override val nutritionFacts = components.nutritionFacts
    override val allIdentities = setOf(id) + components.allComponentIdentities
}

@Serializable
data class LeafFoodSnapshot(
    override val id: FoodSnapshotId.Tracked,
    override val name: FoodName,
    override val brand: String?,
    override val image: FoodSnapshotImage?,
    override val nutritionFacts: NutritionFacts,
) : TrackedFoodSnapshot {
    override val allIdentities = setOf(id)
}
