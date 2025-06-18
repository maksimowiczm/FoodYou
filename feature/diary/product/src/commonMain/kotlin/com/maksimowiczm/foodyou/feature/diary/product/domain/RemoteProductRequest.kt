package com.maksimowiczm.foodyou.feature.diary.product.domain

internal interface RemoteProductRequest {
    suspend fun execute(): Result<RemoteProduct>
}
