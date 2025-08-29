package com.maksimowiczm.foodyou.business.food.domain.remote

interface RemoteProductRequestFactory {
    suspend fun createFromUrl(url: String): RemoteProductRequest?
}
