package com.maksimowiczm.foodyou.core.feature.product.network.openfoodfacts.model.v1

import com.maksimowiczm.foodyou.core.feature.product.network.openfoodfacts.model.OpenFoodNutriments
import com.maksimowiczm.foodyou.core.feature.product.network.openfoodfacts.model.OpenFoodProduct
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class OpenFoodProductV1(
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
    override val packageQuantity: Float? = null,
    @SerialName("product_quantity_unit")
    override val packageQuantityUnit: String? = null,
    @SerialName("serving_quantity")
    override val servingQuantity: Float? = null,
    @SerialName("serving_quantity_unit")
    override val servingQuantityUnit: String? = null
) : OpenFoodProduct
