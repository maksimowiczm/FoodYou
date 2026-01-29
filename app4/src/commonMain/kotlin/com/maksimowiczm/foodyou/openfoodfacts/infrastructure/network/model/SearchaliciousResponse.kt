package com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Root response from the Searchalicious API. */
@Serializable
internal data class SearchaliciousResponse(
    @SerialName("hits") val hits: List<OpenFoodFactsProductNetwork>,
    @SerialName("page") val page: Int,
    @SerialName("page_size") val pageSize: Int,
    @SerialName("page_count") val pageCount: Int,
    @SerialName("count") val count: Int,
    @SerialName("is_count_exact") val isCountExact: Boolean,
    @SerialName("took") val took: Int? = null,
    @SerialName("timed_out") val timedOut: Boolean? = null,
)
