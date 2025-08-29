package com.maksimowiczm.foodyou.business.food.domain.remote

import com.maksimowiczm.foodyou.shared.common.result.Result

interface RemoteProductRequest {
    /** Executes the request to fetch a remote product. */
    suspend fun execute(): Result<RemoteProduct, RemoteFoodException>
}
