package com.maksimowiczm.foodyou.app.infrastructure.opensource.usda.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface UsdaFoodPageResponse {
    val totalHits: Int
    val currentPage: Int
    val totalPages: Int
    val foods: List<Food>
}

@Serializable
internal data class UsdaFoodPageResponseImpl(
    @SerialName("totalHits") override val totalHits: Int,
    @SerialName("currentPage") override val currentPage: Int,
    @SerialName("totalPages") override val totalPages: Int,
    @SerialName("foods") override val foods: List<SearchFood>,
) : UsdaFoodPageResponse
