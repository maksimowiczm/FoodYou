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

    fun anonymize(): FoodSnapshot
}

@Serializable sealed interface LeafFoodSnapshot : FoodSnapshot

@Serializable
sealed interface CompositeFoodSnapshot : FoodSnapshot {
    val components: List<MeasuredFoodSnapshot>

    fun copy(components: List<MeasuredFoodSnapshot>): CompositeFoodSnapshot
}

@Serializable
sealed interface TrackedFoodSnapshot : FoodSnapshot {
    override val id: FoodSnapshotId.Tracked
}

@Serializable
data class AnonymousLeafFoodSnapshot(
    override val id: FoodSnapshotId.Anonymous,
    override val name: FoodName,
    override val brand: String?,
    override val image: FoodSnapshotImage?,
    override val nutritionFacts: NutritionFacts,
) : LeafFoodSnapshot {
    override val allIdentities = emptySet<FoodSnapshotId>()

    override fun anonymize() = this
}

@Serializable
data class AnonymousCompositeFoodSnapshot(
    override val id: FoodSnapshotId.Anonymous,
    override val name: FoodName,
    override val brand: String?,
    override val image: FoodSnapshotImage?,
    override val components: List<MeasuredFoodSnapshot>,
) : CompositeFoodSnapshot {
    override val nutritionFacts = components.nutritionFacts
    override val allIdentities = components.allComponentIdentities

    override fun anonymize() = this

    override fun copy(components: List<MeasuredFoodSnapshot>) =
        copy(
            name = name,
            components = components,
        )
}

@Serializable
data class TrackedCompositeFoodSnapshot(
    override val id: FoodSnapshotId.Tracked,
    override val name: FoodName,
    override val brand: String?,
    override val image: FoodSnapshotImage?,
    override val components: List<MeasuredFoodSnapshot>,
) : CompositeFoodSnapshot, TrackedFoodSnapshot {
    override val nutritionFacts = components.nutritionFacts
    override val allIdentities = setOf(id) + components.allComponentIdentities

    override fun anonymize() =
        AnonymousCompositeFoodSnapshot(
            id = FoodSnapshotId.Anonymous(trackedId = id),
            name = name,
            brand = brand,
            image = image,
            components = components.map { it.anonymize() },
        )

    override fun copy(components: List<MeasuredFoodSnapshot>) =
        copy(
            name = name,
            components = components,
        )
}

@Serializable
data class TrackedLeafFoodSnapshot(
    override val id: FoodSnapshotId.Tracked,
    override val name: FoodName,
    override val brand: String?,
    override val image: FoodSnapshotImage?,
    override val nutritionFacts: NutritionFacts,
) : LeafFoodSnapshot, TrackedFoodSnapshot {
    override val allIdentities = setOf(id)

    override fun anonymize() =
        AnonymousLeafFoodSnapshot(
            id = FoodSnapshotId.Anonymous(trackedId = id),
            name = name,
            brand = brand,
            image = image,
            nutritionFacts = nutritionFacts,
        )
}
