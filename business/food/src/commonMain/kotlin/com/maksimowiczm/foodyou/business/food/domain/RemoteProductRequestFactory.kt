package com.maksimowiczm.foodyou.business.food.domain

interface RemoteProductRequestFactory {
    suspend fun createFromUrl(url: String): RemoteProductRequest?
}
