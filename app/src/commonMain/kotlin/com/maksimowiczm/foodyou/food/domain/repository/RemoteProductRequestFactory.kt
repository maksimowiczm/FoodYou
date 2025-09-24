package com.maksimowiczm.foodyou.food.domain.repository

import com.maksimowiczm.foodyou.food.domain.entity.RemoteProductRequest

fun interface RemoteProductRequestFactory {
    suspend fun create(url: String): RemoteProductRequest?
}
