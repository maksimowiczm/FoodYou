package com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class OpenFoodFactsProductResponseV2(
    @SerialName("product") val product: OpenFoodFactsProductNetwork
)
