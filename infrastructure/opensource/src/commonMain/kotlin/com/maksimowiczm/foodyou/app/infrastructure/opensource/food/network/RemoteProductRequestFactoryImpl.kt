package com.maksimowiczm.foodyou.app.infrastructure.opensource.food.network

import com.maksimowiczm.foodyou.app.infrastructure.opensource.openfoodfacts.network.OpenFoodFactsFacade
import com.maksimowiczm.foodyou.app.infrastructure.opensource.usda.network.USDAFacade
import com.maksimowiczm.foodyou.food.domain.entity.RemoteProductRequest
import com.maksimowiczm.foodyou.food.domain.repository.RemoteProductRequestFactory

internal class RemoteProductRequestFactoryImpl(
    private val openFoodFacts: OpenFoodFactsFacade,
    private val usda: USDAFacade,
) : RemoteProductRequestFactory {
    override suspend fun create(url: String): RemoteProductRequest? =
        when {
            openFoodFacts.matches(url) ->
                openFoodFacts.extractBarcode(url)?.let(openFoodFacts::createRequest)

            usda.matches(url) -> usda.extractId(url)?.let { usda.createRequest(it) }

            else -> null
        }
}
