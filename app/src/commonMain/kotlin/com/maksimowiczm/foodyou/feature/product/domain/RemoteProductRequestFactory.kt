package com.maksimowiczm.foodyou.feature.product.domain

import com.maksimowiczm.foodyou.feature.product.data.network.openfoodfacts.OpenFoodFactsFacade
import com.maksimowiczm.foodyou.feature.product.data.network.openfoodfacts.OpenFoodFactsProductRequest

internal interface RemoteProductRequestFactory {
    fun createFromUrl(url: String): RemoteProductRequest?
}

internal class RemoteProductRequestFactoryImpl(private val openFoodFacts: OpenFoodFactsFacade) :
    RemoteProductRequestFactory {
    override fun createFromUrl(url: String) = when {
        openFoodFacts.matches(url) -> {
            val barcode = openFoodFacts.extractBarcode(url) ?: return null

            OpenFoodFactsProductRequest(
                dataSource = openFoodFacts.remoteDataSource,
                barcode = barcode
            )
        }

        else -> null
    }
}
