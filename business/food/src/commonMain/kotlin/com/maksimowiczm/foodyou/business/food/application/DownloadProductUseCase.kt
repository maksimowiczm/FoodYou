package com.maksimowiczm.foodyou.business.food.application

import com.maksimowiczm.foodyou.business.food.domain.remote.RemoteProduct
import com.maksimowiczm.foodyou.business.food.domain.remote.RemoteProductRequestFactory
import com.maksimowiczm.foodyou.business.shared.application.error.logAndReturnFailure
import com.maksimowiczm.foodyou.externaldatabase.usda.USDAException
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
                onSuccess = { product ->
                    if (product == null) {
                        logger.logAndReturnFailure(
                            tag = TAG,
                            throwable = null,
                            error = DownloadProductError.Generic.ProductNotFound,
                            message = { "No product found for URL: $link" },
                        )
                    } else {
                        Ok(product)
                    }
                },
                onFailure = { error ->
                    when (error) {
                        is USDAException ->
                            when (error) {
                                is USDAException.ApiKeyDisabledException,
                                is USDAException.ApiKeyInvalidException,
                                is USDAException.ApiKeyIsMissingException,
                                is USDAException.ApiKeyUnauthorizedException,
                                is USDAException.ProductNotFoundException ->
                                    logger.logAndReturnFailure(
                                        tag = "DownloadProductCommandHandler",
                                        throwable = error,
                                        error = DownloadProductError.Usda.ApiKeyInvalid,
                                        message = { "USDA API key is invalid for URL: $link" },
                                    )

                                is USDAException.ApiKeyUnverifiedException ->
                                    logger.logAndReturnFailure(
                                        tag = "DownloadProductCommandHandler",
                                        throwable = error,
                                        error = DownloadProductError.Usda.ApiKeyUnverified,
                                        message = { "USDA API key is unverified for URL: $link" },
                                    )

                                is USDAException.RateLimitException ->
                                    logger.logAndReturnFailure(
                                        tag = "DownloadProductCommandHandler",
                                        throwable = error,
                                        error = DownloadProductError.Usda.RateLimit,
                                        message = { "USDA rate limit exceeded for URL: $link" },
                                    )
                            }

                        else ->
                            logger.logAndReturnFailure(
                                tag = TAG,
                                throwable = error,
                                error = DownloadProductError.Generic.Custom(error.message),
                                message = { "Failed to download product from URL: $link" },
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
