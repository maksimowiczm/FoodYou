package com.maksimowiczm.foodyou.business.food.domain

interface RemoteProductRequest {
    /**
     * Executes the request to fetch a remote product.
     *
     * @return A [Result] containing the [RemoteProduct] if successful, or an error if the request
     *   fails. If the product is not found, the result will contain `null`.
     */
    suspend fun execute(): Result<RemoteProduct?>
}
