package com.maksimowiczm.foodyou.feature.openfoodfacts.network.model

interface OpenFoodPageResponse {
    val count: Int
    val page: Int
    val pageSize: Int
    val products: List<OpenFoodFactsProduct>
}
