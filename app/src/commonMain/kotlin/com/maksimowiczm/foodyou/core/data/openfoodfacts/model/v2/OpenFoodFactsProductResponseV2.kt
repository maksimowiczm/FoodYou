package com.maksimowiczm.foodyou.core.data.openfoodfacts.model.v2

import com.maksimowiczm.foodyou.core.domain.model.openfoodfacts.OpenFoodFactsProduct
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
