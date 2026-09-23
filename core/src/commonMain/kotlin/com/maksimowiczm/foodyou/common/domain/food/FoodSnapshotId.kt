package com.maksimowiczm.foodyou.common.domain.food

import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
sealed interface FoodSnapshotId {
    @Serializable sealed interface Tracked : FoodSnapshotId

    /**
     * An identity for food snapshots that are not tracked.
     *
     * @property id Unique identifier for this anonymous snapshot.
     * @property trackedId The original tracked identity this snapshot was created from, if any.
     */
    @Serializable
    data class Anonymous(val id: Uuid = Uuid.random(), val trackedId: Tracked? = null) :
        FoodSnapshotId

    @Serializable data class UserProduct(val id: Uuid) : Tracked

    @Serializable data class OpenFoodFacts(val barcode: String) : Tracked

    @Serializable data class FoodDataCentral(val fdcId: Int) : Tracked

    @Serializable data class UserRecipe(val id: Uuid) : Tracked
}
