package com.maksimowiczm.foodyou.core.feature.product.network.openfoodfacts.model

interface OpenFoodPageResponse {
    val count: Int
    val page: Int
    val pageSize: Int
    val products: List<OpenFoodFactsProduct>
}
