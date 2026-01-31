package com.maksimowiczm.foodyou.common.domain.food

import kotlin.jvm.JvmInline

/**
 * Sealed interface representing the source of food product data.
 *
 * Tracks where food product information originated from, enabling source attribution and different
 * handling based on data provenance.
 */
sealed interface FoodSource {

    /**
     * Food sourced from Open Food Facts database.
     *
     * @property url URL to the product page on Open Food Facts website
     */
    @JvmInline value class OpenFoodFacts(val url: String) : FoodSource

    /**
     * Food sourced from FoodData Central (USDA) database.
     *
     * @property url URL to the product page on FoodData Central website
     */
    @JvmInline value class FoodDataCentral(val url: String) : FoodSource

    /**
     * Food added manually by the user.
     *
     * @property value User-provided source information (free-form text)
     */
    @JvmInline value class UserAdded(val value: String) : FoodSource
}
