package com.maksimowiczm.foodyou.food.infrastructure.usda2.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResult(
    @SerialName("foodSearchCriteria") val foodSearchCriteria: FoodSearchCriteria? = null,
    @SerialName("totalHits") val totalHits: Int,
    @SerialName("currentPage") val currentPage: Int? = null,
    @SerialName("totalPages") val totalPages: Int? = null,
    @SerialName("foods") val foods: List<SearchResultFood> = emptyList(),
)

@Serializable
data class SearchResultFood(
    @SerialName("fdcId") val fdcId: Int,
    @SerialName("description") val description: String,
    @SerialName("dataType") val dataType: String? = null,
    @SerialName("foodCode") val foodCode: String? = null,
    @SerialName("foodNutrients") val foodNutrients: List<AbridgedFoodNutrient> = emptyList(),
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
