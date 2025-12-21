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
    // Food code in search response is a number but in find by id response it's a string 🫠
    @SerialName("foodCode") val foodCode: Int? = null,
    @SerialName("foodNutrients") val foodNutrients: List<SearchResultFoodNutrient> = emptyList(),
    @SerialName("publicationDate") val publicationDate: String? = null,
    @SerialName("scientificName") val scientificName: String? = null,
    @SerialName("brandOwner") val brandOwner: String? = null,
    @SerialName("gtinUpc") val gtinUpc: String? = null,
    @SerialName("ingredients") val ingredients: String? = null,
    @SerialName("ndbNumber") val ndbNumber: Int? = null,
    @SerialName("additionalDescriptions") val additionalDescriptions: String? = null,
    @SerialName("allHighlightFields") val allHighlightFields: String? = null,
    @SerialName("score") val score: Double? = null,
)

@Serializable
data class SearchResultFoodNutrient(
    @SerialName("nutrientNumber") val nutrientNumber: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("value") val amount: Double? = null,
    @SerialName("unitName") val unitName: String? = null,
    @SerialName("derivationCode") val derivationCode: String? = null,
    @SerialName("derivationDescription") val derivationDescription: String? = null,
) {
    val number: Int? by lazy { nutrientNumber?.toIntOrNull() }
}
