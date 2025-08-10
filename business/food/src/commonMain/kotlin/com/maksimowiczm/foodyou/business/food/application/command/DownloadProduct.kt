package com.maksimowiczm.foodyou.business.food.application.command

import com.maksimowiczm.foodyou.business.food.domain.RemoteProduct
import com.maksimowiczm.foodyou.business.food.infrastructure.network.RemoteProductRequestFactory
import com.maksimowiczm.foodyou.business.shared.domain.error.ErrorLoggingUtils
import com.maksimowiczm.foodyou.externaldatabase.usda.USDAException
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.result.Ok
import com.maksimowiczm.foodyou.shared.common.domain.result.Result

data class DownloadProductCommand(val url: String) : Command<RemoteProduct, DownloadProductError>

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

internal class DownloadProductCommandHandler(
    private val remoteRequestFactory: RemoteProductRequestFactory
) : CommandHandler<DownloadProductCommand, RemoteProduct, DownloadProductError> {

    override suspend fun handle(
        command: DownloadProductCommand
    ): Result<RemoteProduct, DownloadProductError> {
        val (url) = command

        val link = linkRegex.find(url)?.value

        if (link == null) {
            return ErrorLoggingUtils.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = DownloadProductError.Generic.UrlNotFound,
                message = { "No valid URL found in the command: $command" },
            )
        }

        val request = remoteRequestFactory.createFromUrl(link)

        if (request == null) {
            return ErrorLoggingUtils.logAndReturnFailure(
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
                        ErrorLoggingUtils.logAndReturnFailure(
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
                                    ErrorLoggingUtils.logAndReturnFailure(
                                        tag = "DownloadProductCommandHandler",
                                        throwable = error,
                                        error = DownloadProductError.Usda.ApiKeyInvalid,
                                        message = { "USDA API key is invalid for URL: $link" },
                                    )

                                is USDAException.ApiKeyUnverifiedException ->
                                    ErrorLoggingUtils.logAndReturnFailure(
                                        tag = "DownloadProductCommandHandler",
                                        throwable = error,
                                        error = DownloadProductError.Usda.ApiKeyUnverified,
                                        message = { "USDA API key is unverified for URL: $link" },
                                    )

                                is USDAException.RateLimitException ->
                                    ErrorLoggingUtils.logAndReturnFailure(
                                        tag = "DownloadProductCommandHandler",
                                        throwable = error,
                                        error = DownloadProductError.Usda.RateLimit,
                                        message = { "USDA rate limit exceeded for URL: $link" },
                                    )
                            }

                        else ->
                            ErrorLoggingUtils.logAndReturnFailure(
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
        private const val TAG = "DownloadProductCommandHandler"
    }
}
