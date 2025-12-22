package com.maksimowiczm.foodyou.food.infrastructure.usda.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FoodSearchCriteria(
    @SerialName("query") val query: String,
    @SerialName("dataType") val dataType: List<String>? = null,
    @SerialName("pageSize") val pageSize: Int? = null,
    @SerialName("pageNumber") val pageNumber: Int? = null,
    @SerialName("sortBy") val sortBy: String? = null,
    @SerialName("sortOrder") val sortOrder: String? = null,
    @SerialName("brandOwner") val brandOwner: String? = null,
    @SerialName("tradeChannel") val tradeChannel: List<String>? = null,
    @SerialName("startDate") val startDate: String? = null,
    @SerialName("endDate") val endDate: String? = null,
)
