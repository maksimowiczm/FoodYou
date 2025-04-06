package com.maksimowiczm.foodyou.feature.diary.network.model

internal interface OpenFoodPageResponse {
    val count: Int
    val page: Int
    val pageSize: Int
    val products: List<OpenFoodFactsProduct>
}
