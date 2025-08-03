package com.maksimowiczm.foodyou.feature.food.domain

import com.maksimowiczm.foodyou.feature.food.data.network.openfoodfacts.OpenFoodFactsFacade
import com.maksimowiczm.foodyou.feature.food.data.network.usda.USDAFacade

interface RemoteProductRequestFactory {
    suspend fun createFromUrl(url: String): RemoteProductRequest?
}

internal class RemoteProductRequestFactoryImpl(
    private val openFoodFacts: OpenFoodFactsFacade,
    private val usda: USDAFacade
) : RemoteProductRequestFactory {
    override suspend fun createFromUrl(url: String) = when {
        openFoodFacts.matches(url) ->
            openFoodFacts
                .extractBarcode(url)
                ?.let(openFoodFacts::createRequest)

        usda.matches(url) ->
            usda
                .extractId(url)
                ?.let { usda.createRequest(it) }

        else -> null
    }
}
