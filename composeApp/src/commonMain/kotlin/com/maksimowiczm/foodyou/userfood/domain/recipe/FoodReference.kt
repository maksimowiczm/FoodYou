package com.maksimowiczm.foodyou.userfood.domain.recipe

import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

/**
 * Reference to food from any bounded context.
 *
 * This allows recipes to include:
 * - User-created food products (UserFood context)
 * - External food products (FoodDataCentral, OpenFoodFacts contexts)
 * - Other recipes (Recipe context)
 */
@Serializable
sealed interface FoodReference {
    /**
     * Reference to a user-created food product.
     *
     * @property id The unique identifier within the local database
     */
    @Serializable data class UserProduct(val id: Uuid) : FoodReference

    /**
     * Reference to food from FoodDataCentral.
     *
     * @property fdcId The FoodData Central unique identifier
     */
    @Serializable data class FoodDataCentral(val fdcId: Int) : FoodReference

    /** Reference to food from OpenFoodFacts. */
    @Serializable data class OpenFoodFacts(val barcode: String) : FoodReference

    /**
     * Reference to another recipe (allows nested recipes).
     *
     * @property id The unique identifier within the local database
     */
    @Serializable data class UserRecipe(val id: Uuid) : FoodReference
}
