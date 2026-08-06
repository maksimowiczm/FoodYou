package com.maksimowiczm.foodyou.common.domain.food

import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
sealed interface FoodCompositionComponentIdentity {
    @Serializable data class Anonymous(val id: Uuid) : FoodCompositionComponentIdentity

    @Serializable sealed interface Identified : FoodCompositionComponentIdentity

    @Serializable sealed interface Leaf : Identified

    @Serializable sealed interface Composite : Identified

    @Serializable data class UserProduct(val id: Uuid) : Leaf

    @Serializable data class OpenFoodFacts(val barcode: String) : Leaf

    @Serializable data class FoodDataCentral(val fdcId: Int) : Leaf

    @Serializable data class Recipe(val id: Uuid) : Composite
}
