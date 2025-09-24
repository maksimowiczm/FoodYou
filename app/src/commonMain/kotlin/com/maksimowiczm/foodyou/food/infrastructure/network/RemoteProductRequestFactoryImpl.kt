package com.maksimowiczm.foodyou.food.infrastructure.network

import com.maksimowiczm.foodyou.food.domain.entity.RemoteProductRequest
import com.maksimowiczm.foodyou.food.domain.repository.RemoteProductRequestFactory
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.OpenFoodFactsFacade
import com.maksimowiczm.foodyou.food.infrastructure.usda.USDAFacade

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
