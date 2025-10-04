package com.maksimowiczm.foodyou.food.domain.usecase

import com.maksimowiczm.foodyou.common.log.Logger
import com.maksimowiczm.foodyou.common.log.logAndReturnFailure
import com.maksimowiczm.foodyou.common.result.Ok
import com.maksimowiczm.foodyou.common.result.Result
import com.maksimowiczm.foodyou.common.result.fold
import com.maksimowiczm.foodyou.food.domain.entity.RemoteFoodException
import com.maksimowiczm.foodyou.food.domain.entity.RemoteProduct
import com.maksimowiczm.foodyou.food.domain.repository.RemoteProductRequestFactory

sealed interface DownloadProductError {

    data object UrlNotFound : DownloadProductError

    data object UrlNotSupported : DownloadProductError

    data class RemoteFoodError(val exception: RemoteFoodException) : DownloadProductError
}

class DownloadProductUseCase(
    private val remoteRequestFactory: RemoteProductRequestFactory,
    private val logger: Logger,
) {
    suspend fun download(url: String): Result<RemoteProduct, DownloadProductError> {
        val link = linkRegex.find(url)?.value

        if (link == null) {
            return logger.logAndReturnFailure(
                tag = TAG,
                error = DownloadProductError.UrlNotFound,
                message = { "No valid URL found in: $url" },
            )
        }

        val request = remoteRequestFactory.create(link)

        if (request == null) {
            return logger.logAndReturnFailure(
                tag = TAG,
                error = DownloadProductError.UrlNotSupported,
                message = { "No supported request found for URL: $link" },
            )
        }

        return request
            .execute()
            .fold(
                onSuccess = ::Ok,
                onError = { error ->
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
