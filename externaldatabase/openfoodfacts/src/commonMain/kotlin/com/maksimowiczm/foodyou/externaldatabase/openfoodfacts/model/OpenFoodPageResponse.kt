package com.maksimowiczm.foodyou.externaldatabase.openfoodfacts.model

interface OpenFoodPageResponse {
    val count: Int
    val page: Int
    val pageSize: Int
    val products: List<OpenFoodFactsProduct>
}
