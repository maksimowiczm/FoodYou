package com.maksimowiczm.foodyou.common.domain.food

import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
sealed interface FoodSnapshotId {
    @Serializable sealed interface Leaf : FoodSnapshotId

    @Serializable sealed interface Composite : FoodSnapshotId

    @Serializable sealed interface Tracked : FoodSnapshotId

    /**
     * An identity for food snapshots that are not tracked.
     *
     * @property id Unique identifier for this anonymous snapshot.
     * @property trackedId The original tracked identity this snapshot was created from, if any.
     *   This allows for potential re-tracking in the future.
     */
    @Serializable
    data class Anonymous(
        val id: Uuid = Uuid.random(),
        val trackedId: Tracked? = null,
    ) : Leaf

    @Serializable data class UserProduct(val id: Uuid) : Leaf, Tracked

    @Serializable data class OpenFoodFacts(val barcode: String) : Leaf, Tracked

    @Serializable data class FoodDataCentral(val fdcId: Int) : Leaf, Tracked

    @Serializable data class UserRecipe(val id: Uuid) : Composite, Tracked
}
