package com.maksimowiczm.foodyou.feature.product.network.openfoodfacts.model.v2

import com.maksimowiczm.foodyou.feature.product.network.openfoodfacts.model.OpenFoodNutriments
import com.maksimowiczm.foodyou.feature.product.network.openfoodfacts.model.OpenFoodProduct
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class OpenFoodProductV2(
    @SerialName("product_name")
    override val productName: String,
    @SerialName("brands")
    override val brands: String? = null,
    @SerialName("code")
    override val code: String? = null,
    @SerialName("image_url")
    override val imageUrl: String? = null,
    @SerialName("nutriments")
    override val nutriments: OpenFoodNutriments,
    @SerialName("product_quantity")
    val packageQuantityString: String? = null,
    @SerialName("product_quantity_unit")
    override val packageQuantityUnit: String? = null,
    @SerialName("serving_quantity")
    val servingQuantityString: String? = null,
    @SerialName("serving_quantity_unit")
    override val servingQuantityUnit: String? = null
) : OpenFoodProduct {
    override val packageQuantity: Float?
        get() = packageQuantityString?.toFloatOrNull()
    override val servingQuantity: Float?
        get() = servingQuantityString?.toFloatOrNull()
}
