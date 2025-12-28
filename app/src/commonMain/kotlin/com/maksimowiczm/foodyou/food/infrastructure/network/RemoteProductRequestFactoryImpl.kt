package com.maksimowiczm.foodyou.food.infrastructure.network

import com.maksimowiczm.foodyou.food.domain.entity.RemoteProductRequest
import com.maksimowiczm.foodyou.food.domain.repository.RemoteProductRequestFactory
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.OpenFoodFactsFacade

internal class RemoteProductRequestFactoryImpl(
    private val openFoodFacts: OpenFoodFactsFacade,
) : RemoteProductRequestFactory {
    override suspend fun create(url: String): RemoteProductRequest? =
        when {
            openFoodFacts.matches(url) ->
                openFoodFacts.extractBarcode(url)?.let(openFoodFacts::createRequest)

            else -> null
        }
}
