package com.maksimowiczm.foodyou.app.infrastructure.opensource.usda.network

internal data class USDAPagingKey(
    val queryString: String,
    val fetchedCount: Int,
    val totalCount: Int,
)
