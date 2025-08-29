package com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts.model.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class OpenFoodFactsProductResponseV2(
    @SerialName("product") val product: OpenFoodFactsProductV2
)
