package com.maksimowiczm.foodyou.feature.productdownload.domain

import com.maksimowiczm.foodyou.feature.productdownload.data.network.openfoodfacts.OpenFoodFactsFacade
import com.maksimowiczm.foodyou.feature.productdownload.data.network.openfoodfacts.OpenFoodFactsProductRequest

interface RemoteProductRequestFactory {
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
