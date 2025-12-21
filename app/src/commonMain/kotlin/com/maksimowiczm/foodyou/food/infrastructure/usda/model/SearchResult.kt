package com.maksimowiczm.foodyou.food.infrastructure.usda.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResult(
    @SerialName("foodSearchCriteria") val foodSearchCriteria: FoodSearchCriteria,
    @SerialName("totalHits") val totalHits: Int,
    @SerialName("currentPage") val currentPage: Int,
    @SerialName("totalPages") val totalPages: Int,
    @SerialName("foods") val foods: List<SearchResultFood> = emptyList(),
)

@Serializable
data class SearchResultFood(
    @SerialName("fdcId") val fdcId: Int,
    @SerialName("description") val description: String,
    @SerialName("dataType") val dataType: String? = null,
    @SerialName("foodNutrients") val foodNutrients: List<SearchResultFoodNutrient> = emptyList(),
    @SerialName("brandOwner") val brandOwner: String? = null,
    @SerialName("gtinUpc") val gtinUpc: String? = null,
)

@Serializable
data class SearchResultFoodNutrient(
    @SerialName("nutrientNumber") val nutrientNumber: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("value") val amount: Double? = null,
    @SerialName("unitName") val unitName: String? = null,
) {
    val number: Int? by lazy { nutrientNumber?.toIntOrNull() }
}
