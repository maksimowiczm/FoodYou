package com.maksimowiczm.foodyou.business.food.infrastructure.network

import com.maksimowiczm.foodyou.business.food.domain.remote.RemoteProductRequestFactory
import com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts.OpenFoodFactsFacade
import com.maksimowiczm.foodyou.business.food.infrastructure.network.usda.USDAFacade

internal class RemoteProductRequestFactoryImpl(
    private val openFoodFacts: OpenFoodFactsFacade,
    private val usda: USDAFacade,
) : RemoteProductRequestFactory {
    override suspend fun createFromUrl(url: String) =
        when {
            openFoodFacts.matches(url) ->
                openFoodFacts.extractBarcode(url)?.let(openFoodFacts::createRequest)

            usda.matches(url) -> usda.extractId(url)?.let { usda.createRequest(it) }

            else -> null
        }
}
