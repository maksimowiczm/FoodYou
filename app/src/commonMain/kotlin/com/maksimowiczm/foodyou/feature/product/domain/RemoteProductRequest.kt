package com.maksimowiczm.foodyou.feature.product.domain

internal interface RemoteProductRequest {
    suspend fun execute(): Result<RemoteProduct>
}
