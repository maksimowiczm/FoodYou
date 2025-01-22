package com.maksimowiczm.foodyou.feature.product.network.openfoodfacts.model.v2

import com.maksimowiczm.foodyou.feature.product.network.openfoodfacts.model.OpenFoodProduct
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class OpenFoodProductResponseV2(
    @SerialName("code")
    private val code: String,
    @SerialName("product")
    private val innerProduct: OpenFoodProductV2
) {
    val product: OpenFoodProduct
        get() = innerProduct.copy(code = code)
}
