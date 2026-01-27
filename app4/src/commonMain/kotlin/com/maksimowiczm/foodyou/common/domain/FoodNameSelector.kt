package com.maksimowiczm.foodyou.common.domain

/**
 * Service for selecting appropriate food names based on user locale preferences.
 *
 * This interface handles the selection of localized food names from multilingual food data,
 * ensuring users see food names in their preferred language or a suitable fallback.
 */
interface FoodNameSelector {

    /**
     * Selects the most appropriate food name based on the user's locale preferences.
     *
     * @param foodName The multilingual food name data
     * @return The food name string in the user's preferred language or fallback
     */
    fun select(foodName: FoodName): String

    /**
     * Selects the most appropriate food name language based on the user's locale preferences.
     *
     * @return The preferred language for food names
     */
    fun select(): Language
}
