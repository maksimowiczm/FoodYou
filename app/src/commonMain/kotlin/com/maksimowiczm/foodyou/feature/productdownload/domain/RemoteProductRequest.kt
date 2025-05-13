package com.maksimowiczm.foodyou.feature.productdownload.domain

interface RemoteProductRequest {
    suspend fun getProduct(): Result<RemoteProduct>
}
