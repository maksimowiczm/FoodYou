package com.maksimowiczm.foodyou.business.food.application

import com.maksimowiczm.foodyou.business.food.domain.remote.RemoteFoodException
import com.maksimowiczm.foodyou.business.food.domain.remote.RemoteProduct
import com.maksimowiczm.foodyou.business.food.domain.remote.RemoteProductRequestFactory
import com.maksimowiczm.foodyou.business.shared.application.error.logAndReturnFailure
import com.maksimowiczm.foodyou.shared.common.application.log.Logger
import com.maksimowiczm.foodyou.shared.common.result.Ok
import com.maksimowiczm.foodyou.shared.common.result.Result

sealed interface DownloadProductError {

    sealed interface Generic : DownloadProductError {
        data object UrlNotFound : Generic

        data object UrlNotSupported : Generic

        data object ProductNotFound : Generic

        data class Custom(val message: String?) : Generic
    }

    sealed interface Usda : DownloadProductError {
        data object RateLimit : Usda

        data object ApiKeyInvalid : Usda

        data object ApiKeyUnverified : Usda
    }

    sealed interface OpenFoodFacts : DownloadProductError {
        data object Timeout : OpenFoodFacts
    }
}

fun interface DownloadProductUseCase {
    suspend fun download(url: String): Result<RemoteProduct, DownloadProductError>
}

internal class DownloadProductUseCaseImpl(
    private val remoteRequestFactory: RemoteProductRequestFactory,
    private val logger: Logger,
) : DownloadProductUseCase {
    override suspend fun download(url: String): Result<RemoteProduct, DownloadProductError> {
        val link = linkRegex.find(url)?.value

        if (link == null) {
            return logger.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = DownloadProductError.Generic.UrlNotFound,
                message = { "No valid URL found in: $url" },
            )
        }

        val request = remoteRequestFactory.createFromUrl(link)

        if (request == null) {
            return logger.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = DownloadProductError.Generic.UrlNotSupported,
                message = { "No supported request found for URL: $link" },
            )
        }

        return request
            .execute()
            .fold(
                onSuccess = ::Ok,
                onFailure = { error ->
                    when (error) {
                        is RemoteFoodException.OpenFoodFacts.Timeout ->
                            logger.logAndReturnFailure(
                                tag = TAG,
                                throwable = error,
                                error = DownloadProductError.OpenFoodFacts.Timeout,
                                message = { "Timeout when fetching product for URL: $link" },
                            )

                        is RemoteFoodException.ProductNotFoundException ->
                            logger.logAndReturnFailure(
                                tag = TAG,
                                throwable = error,
                                error = DownloadProductError.Generic.ProductNotFound,
                                message = { "No product found for URL: $link" },
                            )

                        is RemoteFoodException.USDA.ApiKeyDisabledException,
                        is RemoteFoodException.USDA.ApiKeyInvalidException,
                        is RemoteFoodException.USDA.ApiKeyIsMissingException,
                        is RemoteFoodException.USDA.ApiKeyUnauthorizedException ->
                            logger.logAndReturnFailure(
                                tag = TAG,
                                throwable = error,
                                error = DownloadProductError.Usda.ApiKeyInvalid,
                                message = {
                                    "Invalid USDA API key when fetching product for URL: $link"
                                },
                            )

                        is RemoteFoodException.USDA.ApiKeyUnverifiedException ->
                            logger.logAndReturnFailure(
                                tag = TAG,
                                throwable = error,
                                error = DownloadProductError.Usda.ApiKeyUnverified,
                                message = {
                                    "USDA API key unverified when fetching product for URL: $link"
                                },
                            )

                        is RemoteFoodException.USDA.RateLimitException ->
                            logger.logAndReturnFailure(
                                tag = TAG,
                                throwable = error,
                                error = DownloadProductError.Usda.RateLimit,
                                message = {
                                    "USDA rate limit exceeded when fetching product for URL: $link"
                                },
                            )

                        is RemoteFoodException.Unknown ->
                            logger.logAndReturnFailure(
                                tag = TAG,
                                throwable = error,
                                error = DownloadProductError.Generic.Custom(error.message),
                                message = { "Unknown error when fetching product for URL: $link" },
                            )
                    }
                },
            )
    }

    private val linkRegex by lazy {
        Regex(
            """(?:https://)?[-a-zA-Z0-9@:%._+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)""",
            RegexOption.IGNORE_CASE,
        )
    }

    private companion object {
        private const val TAG = "DownloadProductUseCaseImpl"
    }
}
