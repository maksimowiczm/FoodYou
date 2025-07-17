package com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.model.v1

import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.model.OpenFoodFactsNutrients
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.model.OpenFoodFactsProduct
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class OpenFoodFactsProductV1(
    @SerialName("product_name")
    override val name: String? = null,
    @SerialName("brands")
    override val brand: String? = null,
    @SerialName("code")
    override val barcode: String? = null,
    @SerialName("nutriments")
    override val nutritionFacts: OpenFoodFactsNutrients,
    @SerialName("product_quantity")
    override val packageWeight: Float? = null,
    @SerialName("product_quantity_unit")
    override val packageQuantityUnit: String? = null,
    @SerialName("serving_quantity")
    override val servingWeight: Float? = null,
    @SerialName("serving_quantity_unit")
    override val servingQuantityUnit: String? = null,
    @SerialName("url")
    override val url: String? = null
) : OpenFoodFactsProduct
