package com.maksimowiczm.foodyou.app.infrastructure.network.openfoodfacts

internal data class OpenFoodFactsPagingKey(
    val queryString: String,
    val country: String?,
    val fetchedCount: Int,
    val totalCount: Int,
)
