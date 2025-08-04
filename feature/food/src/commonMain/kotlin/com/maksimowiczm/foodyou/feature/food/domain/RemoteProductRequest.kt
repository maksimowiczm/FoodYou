package com.maksimowiczm.foodyou.feature.food.domain

internal interface RemoteProductRequest {

    // Returning null instead of an not found exception is better for now because we don't have to
    // create shared module for exceptions.

    /**
     * Executes the request to fetch a remote product.
     *
     * @return A [Result] containing the [RemoteProduct] if successful, or an error if the request fails. If the product is not found, the result will contain `null`.
     */
    suspend fun execute(): Result<RemoteProduct?>
}
