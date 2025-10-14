package com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.network.model.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenFoodFactsProductResponseV2(
    @SerialName("product") val product: OpenFoodFactsProductV2
)
