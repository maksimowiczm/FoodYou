package com.maksimowiczm.foodyou.core.feature.openfoodfacts.network.model.v2

import com.maksimowiczm.foodyou.core.feature.openfoodfacts.network.model.OpenFoodFactsProduct
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class OpenFoodFactsProductResponseV2(
    @SerialName("code")
    private val code: String,
    @SerialName("product")
    private val innerProduct: OpenFoodFactsProductV2
) {
    val product: OpenFoodFactsProduct
        get() = innerProduct.copy(code = code)
}
