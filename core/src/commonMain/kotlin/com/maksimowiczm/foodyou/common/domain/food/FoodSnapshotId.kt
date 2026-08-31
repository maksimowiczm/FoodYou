package com.maksimowiczm.foodyou.common.domain.food

import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
sealed interface FoodSnapshotId {
    @Serializable sealed interface Leaf : FoodSnapshotId

    @Serializable sealed interface Composite : FoodSnapshotId

    @Serializable sealed interface Tracked : FoodSnapshotId

    @Serializable data class Anonymous(val id: Uuid) : Leaf

    @Serializable data class UserProduct(val id: Uuid) : Leaf, Tracked

    @Serializable data class OpenFoodFacts(val barcode: String) : Leaf, Tracked

    @Serializable data class FoodDataCentral(val fdcId: Int) : Leaf, Tracked

    @Serializable data class UserRecipe(val id: Uuid) : Composite, Tracked
}
