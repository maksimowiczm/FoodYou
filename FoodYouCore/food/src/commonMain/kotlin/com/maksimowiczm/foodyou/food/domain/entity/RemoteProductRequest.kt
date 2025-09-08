package com.maksimowiczm.foodyou.food.domain.entity

import com.maksimowiczm.foodyou.shared.domain.Result

interface RemoteProductRequest {
    /** Executes the request to fetch a remote product. */
    suspend fun execute(): Result<RemoteProduct, RemoteFoodException>
}
