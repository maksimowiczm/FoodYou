package com.maksimowiczm.foodyou.fooddatacentral.infrastructure.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class FoodDataCentralFoodPageResponse(
    @SerialName("totalHits") val totalHits: Int,
    @SerialName("currentPage") val currentPage: Int,
    @SerialName("totalPages") val totalPages: Int,
    @SerialName("foods") val foods: List<SearchFood>,
)
