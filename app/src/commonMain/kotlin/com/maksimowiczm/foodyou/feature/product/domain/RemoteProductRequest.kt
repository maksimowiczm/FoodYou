package com.maksimowiczm.foodyou.feature.product.domain

interface RemoteProductRequest {
    suspend fun execute(): Result<RemoteProduct>
}
