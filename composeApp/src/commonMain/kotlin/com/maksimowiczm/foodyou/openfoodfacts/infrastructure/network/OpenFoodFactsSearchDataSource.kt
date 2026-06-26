package com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network

import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsCredentials

internal interface OpenFoodFactsSearchDataSource {
    suspend fun search(
        query: String,
        credentials: OpenFoodFactsCredentials.Decrypted? = null,
        pageSize: Int = 10,
        page: Int = 1,
        sortBy: String? = null,
    ): OpenFoodFactsNetworkPage
}
