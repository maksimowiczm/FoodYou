package com.maksimowiczm.foodyou.feature.diary.network.model.v2

import com.maksimowiczm.foodyou.feature.diary.network.model.OpenFoodFactsNutrients
import com.maksimowiczm.foodyou.feature.diary.network.model.OpenFoodFactsProduct
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class OpenFoodFactsProductV2(
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
    val packageQuantityString: String? = null,
    @SerialName("product_quantity_unit")
    override val packageQuantityUnit: String? = null,
    @SerialName("serving_quantity")
    val servingQuantityString: String? = null,
    @SerialName("serving_quantity_unit")
    override val servingQuantityUnit: String? = null
) : OpenFoodFactsProduct {
    override val packageQuantity: Float?
        get() = packageQuantityString?.toFloatOrNull()
    override val servingQuantity: Float?
        get() = servingQuantityString?.toFloatOrNull()
}
