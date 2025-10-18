package com.maksimowiczm.foodyou.food.domain

import kotlin.jvm.JvmInline

sealed interface FoodSource {

    /**
     * Food sourced from Open Food Facts database
     *
     * @param url URL to the product page on Open Food Facts website
     */
    @JvmInline value class OpenFoodFacts(val url: String) : FoodSource

    /**
     * Food added by the user manually
     *
     * @param value User provided data, whatever string user wants to put here
     */
    @JvmInline value class UserAdded(val value: String) : FoodSource
}
