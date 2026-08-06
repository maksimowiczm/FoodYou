package com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network

internal interface OpenFoodFactsNetworkPage {
    val count: Int
    val page: Int
    val pageSize: Int
    val products: List<OpenFoodFactsProductNetwork>
}
