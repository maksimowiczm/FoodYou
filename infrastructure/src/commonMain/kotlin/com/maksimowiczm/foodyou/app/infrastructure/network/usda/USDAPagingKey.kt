package com.maksimowiczm.foodyou.app.infrastructure.network.usda

internal data class USDAPagingKey(
    val queryString: String,
    val fetchedCount: Int,
    val totalCount: Int,
)
