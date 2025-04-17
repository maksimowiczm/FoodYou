package com.maksimowiczm.foodyou.core.domain.source

import com.maksimowiczm.foodyou.core.domain.model.openfoodfacts.OpenFoodFactsProduct

interface OpenFoodFactsRemoteDataSource {
    suspend fun getProduct(code: String, country: String?): OpenFoodFactsProduct?
}
