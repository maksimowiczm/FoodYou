package com.maksimowiczm.foodyou.business.food.domain

data class FoodPreferences(val openFoodFacts: OpenFoodFactsPreferences, val usda: UsdaPreferences)

data class OpenFoodFactsPreferences(val enabled: Boolean)

data class UsdaPreferences(val enabled: Boolean, val apiKey: String?)
