package com.maksimowiczm.foodyou.feature.diary.core.network.model

internal interface OpenFoodPageResponse {
    val count: Int
    val page: Int
    val pageSize: Int
    val products: List<OpenFoodFactsProduct>
}
