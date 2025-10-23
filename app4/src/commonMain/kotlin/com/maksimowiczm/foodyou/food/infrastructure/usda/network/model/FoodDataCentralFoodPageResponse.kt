package com.maksimowiczm.foodyou.food.infrastructure.usda.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface FoodDataCentralFoodPageResponse {
    val totalHits: Int
    val currentPage: Int
    val totalPages: Int
    val foods: List<Food>
}

@Serializable
internal data class FoodDataCentralFoodPageResponseImpl(
    @SerialName("totalHits") override val totalHits: Int,
    @SerialName("currentPage") override val currentPage: Int,
    @SerialName("totalPages") override val totalPages: Int,
    @SerialName("foods") override val foods: List<SearchFood>,
) : FoodDataCentralFoodPageResponse
