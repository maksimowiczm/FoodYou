package com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.model

interface OpenFoodPageResponse {
    val count: Int
    val page: Int
    val pageSize: Int
    val products: List<OpenFoodFactsProduct>
}
