package com.maksimowiczm.foodyou.business.food.application

import com.maksimowiczm.foodyou.business.food.domain.remote.RemoteFoodException
import com.maksimowiczm.foodyou.business.food.domain.remote.RemoteProduct
import com.maksimowiczm.foodyou.business.food.domain.remote.RemoteProductRequestFactory
import com.maksimowiczm.foodyou.business.shared.application.error.logAndReturnFailure
import com.maksimowiczm.foodyou.shared.common.application.log.Logger
import com.maksimowiczm.foodyou.shared.common.result.Ok
import com.maksimowiczm.foodyou.shared.common.result.Result

sealed interface DownloadProductError {

    data object UrlNotFound : DownloadProductError

    data object UrlNotSupported : DownloadProductError

    data class RemoteFoodError(val exception: RemoteFoodException) : DownloadProductError
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
                error = DownloadProductError.UrlNotFound,
                message = { "No valid URL found in: $url" },
            )
        }

        val request = remoteRequestFactory.createFromUrl(link)

        if (request == null) {
            return logger.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = DownloadProductError.UrlNotSupported,
                message = { "No supported request found for URL: $link" },
            )
        }

        return request
            .execute()
            .fold(
                onSuccess = ::Ok,
                onFailure = { error ->
                    logger.logAndReturnFailure(
                        tag = TAG,
                        throwable = error,
                        error = DownloadProductError.RemoteFoodError(error),
                        message = { "Error when fetching product for URL: $link" },
                    )
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
