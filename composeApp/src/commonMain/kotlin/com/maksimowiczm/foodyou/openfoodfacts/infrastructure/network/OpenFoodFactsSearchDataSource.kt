package com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network

internal interface OpenFoodFactsSearchDataSource {
    suspend fun search(
        query: String,
        pageSize: Int = 10,
        page: Int = 1,
        sortBy: String? = null,
    ): OpenFoodFactsNetworkPage
}
