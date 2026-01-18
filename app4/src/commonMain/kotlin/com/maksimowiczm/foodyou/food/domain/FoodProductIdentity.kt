package com.maksimowiczm.foodyou.food.domain

import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import kotlin.jvm.JvmInline

/**
 * Sealed interface representing the identity of a food product from various sources.
 *
 * Food products can originate from different sources (local database, external APIs), each with
 * their own identification scheme. This type ensures type-safe handling of identifiers across
 * different food data sources.
 */
sealed interface FoodProductIdentity {
    /**
     * Local database identifier for user-created or imported food products.
     *
     * @property id The unique identifier within the local database
     * @property accountId The account that owns this food product
     */
    data class Local(val id: String, val accountId: LocalAccountId) : FoodProductIdentity

    /**
     * Open Food Facts identifier using product barcode.
     *
     * @property barcode The product barcode
     */
    @JvmInline value class OpenFoodFacts(val barcode: String) : FoodProductIdentity

    /**
     * FoodData Central identifier from the USDA database.
     *
     * @property fdcId The FoodData Central unique identifier
     */
    @JvmInline value class FoodDataCentral(val fdcId: Int) : FoodProductIdentity
}
