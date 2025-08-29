package com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts.model

interface OpenFoodPageResponse {
    val count: Int
    val page: Int
    val pageSize: Int
    val products: List<OpenFoodFactsProduct>
}
