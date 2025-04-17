package com.maksimowiczm.foodyou.core.data.openfoodfacts.model

import com.maksimowiczm.foodyou.core.domain.model.openfoodfacts.OpenFoodFactsProduct

internal interface OpenFoodPageResponse {
    val count: Int
    val page: Int
    val pageSize: Int
    val products: List<OpenFoodFactsProduct>
}
