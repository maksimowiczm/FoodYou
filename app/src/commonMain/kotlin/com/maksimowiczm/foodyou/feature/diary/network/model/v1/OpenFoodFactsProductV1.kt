package com.maksimowiczm.foodyou.feature.diary.network.model.v1

import com.maksimowiczm.foodyou.feature.diary.network.model.OpenFoodFactsNutrients
import com.maksimowiczm.foodyou.feature.diary.network.model.OpenFoodFactsProduct
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class OpenFoodFactsProductV1(
    @SerialName("product_name")
    override val productName: String? = null,
    @SerialName("brands")
    override val brands: String? = null,
    @SerialName("code")
    override val code: String? = null,
    @SerialName("image_url")
    override val imageUrl: String? = null,
    @SerialName("nutriments")
    override val nutrients: OpenFoodFactsNutrients? = null,
    @SerialName("product_quantity")
    override val packageQuantity: Float? = null,
    @SerialName("product_quantity_unit")
    override val packageQuantityUnit: String? = null,
    @SerialName("serving_quantity")
    override val servingQuantity: Float? = null,
    @SerialName("serving_quantity_unit")
    override val servingQuantityUnit: String? = null
) : OpenFoodFactsProduct
