package com.maksimowiczm.foodyou.common.domain.food

import com.maksimowiczm.foodyou.common.domain.Weight
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.sum
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
data class MeasuredFoodSnapshot(
    val snapshot: FoodSnapshot,
    val quantity: FoodSnapshotQuantity,
) {
    val id: FoodSnapshotId
        get() = snapshot.id

    val name: FoodName
        get() = snapshot.name

    val image: FoodSnapshotImage?
        get() = snapshot.image

    val nutritionFacts: NutritionFacts
        get() = snapshot.nutritionFacts

    val measuredNutritionFacts: NutritionFacts =
        nutritionFacts * quantity.absoluteWeight.grams / 100.0

    val allIdentities: Set<FoodSnapshotId>
        get() = snapshot.allIdentities

    /**
     * Creates a copy of this snapshot with an anonymous id.
     *
     * If the current snapshot has a tracked identity, it is preserved in the new anonymous id to
     * allow for potential re-tracking. If the snapshot is already anonymous, it is returned as is.
     */
    fun anonymize(): MeasuredFoodSnapshot =
        if (snapshot.id is FoodSnapshotId.Anonymous) this
        else
            copy(
                snapshot =
                    AnonymousFoodSnapshot(
                        id =
                            FoodSnapshotId.Anonymous(
                                id = Uuid.random(),
                                trackedId = snapshot.id as? FoodSnapshotId.Tracked,
                            ),
                        name = name,
                        brand = snapshot.brand,
                        image = image,
                        nutritionFacts = nutritionFacts,
                    )
            )

    /** Creates a copy of this component with a new quantity. */
    fun withNewQuantity(quantity: FoodSnapshotQuantity): MeasuredFoodSnapshot =
        copy(quantity = quantity)
}

/** Sum of the absolute weights of all components. */
val Iterable<MeasuredFoodSnapshot>.totalWeight: Weight
    get() = map { it.quantity.absoluteWeight }.sum()

/** Set of all identities present in these components and their subcomponents. */
val Iterable<MeasuredFoodSnapshot>.allComponentIdentities: Set<FoodSnapshotId>
    get() = flatMap { it.allIdentities }.toSet()

/**
 * Nutrition facts normalized to 100 g of this food composition.
 *
 * ```
 * nutritionFacts = Σ(measuredNutritionFacts) / totalWeight.grams * 100
 * ```
 */
val Iterable<MeasuredFoodSnapshot>.nutritionFacts: NutritionFacts
    get() {
        val totalWeight = totalWeight
        return if (totalWeight == 0.grams) NutritionFacts.zeroCompleted
        else map { it.measuredNutritionFacts }.sum() / totalWeight.grams * 100.0
    }
