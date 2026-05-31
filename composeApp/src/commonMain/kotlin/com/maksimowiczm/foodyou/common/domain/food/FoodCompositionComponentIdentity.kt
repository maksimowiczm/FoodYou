package com.maksimowiczm.foodyou.common.domain.food

import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
sealed interface FoodCompositionComponentIdentity {
    @Serializable sealed interface Leaf : FoodCompositionComponentIdentity

    @Serializable sealed interface Composite : FoodCompositionComponentIdentity

    @Serializable data class UserProduct(val id: Uuid) : Leaf

    @Serializable data class OpenFoodFacts(val barcode: String) : Leaf

    @Serializable data class FoodDataCentral(val fdcId: Int) : Leaf

    @Serializable data class Recipe(val id: Uuid) : Composite
}
