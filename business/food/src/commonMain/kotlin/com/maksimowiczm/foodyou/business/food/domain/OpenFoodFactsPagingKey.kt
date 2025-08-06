package com.maksimowiczm.foodyou.business.food.domain

internal data class OpenFoodFactsPagingKey(
    val queryString: String,
    val country: String?,
    val fetchedCount: Int,
    val totalCount: Int,
)
