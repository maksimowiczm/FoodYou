package com.maksimowiczm.foodyou.food.domain

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
     * FoodData Central identifier from the USDA database.
     *
     * @property fdcId The FoodData Central unique identifier
     */
    @JvmInline value class FoodDataCentral(val fdcId: Int) : FoodProductIdentity
}
