package com.maksimowiczm.foodyou.core.feature.product.network.openfoodfacts.model.v1

import com.maksimowiczm.foodyou.core.feature.product.network.openfoodfacts.model.OpenFoodPageResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class OpenFoodPageResponseV1(
    @SerialName("count")
    override val count: Int,
    @SerialName("page")
    override val page: Int,
    @SerialName("page_size")
    override val pageSize: Int,
    @SerialName("products")
    override val products: List<OpenFoodProductV1>
) : OpenFoodPageResponse
