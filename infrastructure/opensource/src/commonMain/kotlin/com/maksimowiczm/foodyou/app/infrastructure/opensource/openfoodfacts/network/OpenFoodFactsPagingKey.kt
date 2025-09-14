package com.maksimowiczm.foodyou.app.infrastructure.opensource.openfoodfacts.network

internal data class OpenFoodFactsPagingKey(
    val queryString: String,
    val country: String?,
    val fetchedCount: Int,
    val totalCount: Int,
)
