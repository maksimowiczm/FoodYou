package com.maksimowiczm.foodyou.feature.product.network.openfoodfacts.model

interface OpenFoodPageResponse {
    val count: Int
    val page: Int
    val pageSize: Int
    val products: List<OpenFoodProduct>
}
