package com.maksimowiczm.foodyou.feature.food.ui.product.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource
import com.maksimowiczm.foodyou.feature.food.domain.RemoteProduct
import com.maksimowiczm.foodyou.feature.food.domain.RemoteProductRequestFactory
import com.maksimowiczm.foodyou.feature.usda.USDAException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

internal class DownloadProductViewModel(
    text: String?,
    private val requestFactory: RemoteProductRequestFactory
) : ViewModel() {
    private val _isMutating = MutableStateFlow(false)
    val isMutating = _isMutating.asStateFlow()

    private val _error = MutableStateFlow<DownloadError?>(null)
    val error = _error.asStateFlow()

    private val _productEvent = MutableStateFlow<RemoteProduct?>(null)
    val productEvent = _productEvent.filterNotNull()

    private val linkRegex by lazy {
        Regex(
            """(?:https://)?[-a-zA-Z0-9@:%._+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)""",
            RegexOption.IGNORE_CASE
        )
    }

    init {
        if (text != null) {
            onDownload(text)
        }
    }

    fun onDownload(text: String) = withMutateGuard {
        _error.emit(null)

        val link = extractFirstLink(text)

        if (link == null) {
            _error.emit(urlNotFoundError())
            return@withMutateGuard
        }

        val request = requestFactory.createFromUrl(link)

        if (request == null) {
            _error.emit(urlNotSupportedError())
            return@withMutateGuard
        }

        val product = request.execute().getOrElse {
            when (it) {
                else -> _error.emit(customError(it.message))
            }

            return@withMutateGuard
        }

        if (product == null) {
            _error.emit(productNotFoundError())
            return@withMutateGuard
        }

        _productEvent.emit(
            product.copy(
                source = product.source.copy(
                    type = FoodSource.Type.User,
                    url = link
                )
            )
        )
    }

    private fun withMutateGuard(block: suspend () -> Unit) {
        viewModelScope.launch {
            _isMutating.emit(true)
            block()
            _isMutating.emit(false)
        }
    }

    private fun extractFirstLink(text: String): String? = linkRegex.find(text)?.value
}

private fun productNotFoundError() = DownloadError.GenericError.ProductNotFound
private fun urlNotFoundError() = DownloadError.GenericError.URLNotFound
private fun urlNotSupportedError() = DownloadError.GenericError.URLNotSupported
private fun customError(message: String?) = DownloadError.GenericError.Custom(message)
private fun usdaError(error: USDAException) = DownloadError.Usda(error)
