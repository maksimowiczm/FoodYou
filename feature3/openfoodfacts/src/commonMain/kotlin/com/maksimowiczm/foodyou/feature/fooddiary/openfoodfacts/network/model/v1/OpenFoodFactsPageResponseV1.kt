package com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.model.v1

import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.model.OpenFoodPageResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class OpenFoodFactsPageResponseV1(
    @SerialName("count")
    override val count: Int,
    @SerialName("page")
    override val page: Int,
    @SerialName("page_size")
    override val pageSize: Int,
    @SerialName("products")
    override val products: List<OpenFoodFactsProductV1>
) : OpenFoodPageResponse
