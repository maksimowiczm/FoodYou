package com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.model.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class OpenFoodFactsProductResponseV2(
    @SerialName("product")
    val product: OpenFoodFactsProductV2
)
