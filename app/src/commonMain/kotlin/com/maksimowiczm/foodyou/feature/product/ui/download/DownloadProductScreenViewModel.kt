package com.maksimowiczm.foodyou.feature.product.ui.download

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.core.ext.launch
import com.maksimowiczm.foodyou.feature.product.domain.RemoteProduct
import com.maksimowiczm.foodyou.feature.product.domain.RemoteProductRequestFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull

internal class DownloadProductScreenViewModel(
    private val requestFactory: RemoteProductRequestFactory
) : ViewModel() {
    private val _isMutating = MutableStateFlow(false)
    val isMutating = _isMutating.asStateFlow()

    private val _error = MutableStateFlow<DownloadError?>(null)
    val error = _error.asStateFlow()

    private val _productEvent = MutableStateFlow<RemoteProduct?>(null)
    val productEvent = _productEvent.filterNotNull()

    fun onDownload(text: String) = withMutateGuard {
        _error.emit(null)

        val link = extractFirstLink(text)

        if (link == null) {
            _error.emit(DownloadError.URLNotFound)
            return@withMutateGuard
        }

        val request = requestFactory.createFromUrl(link)

        if (request == null) {
            _error.emit(DownloadError.URLNotSupported)
            return@withMutateGuard
        }

        val product = request.execute().getOrElse {
            _error.emit(DownloadError.Custom(it.message?.toString()))
            return@withMutateGuard
        }

        _productEvent.emit(product)
    }

    private fun withMutateGuard(block: suspend () -> Unit) = launch {
        _isMutating.emit(true)
        block()
        _isMutating.emit(false)
    }

    private val linkRegex by lazy {
        Regex(
            "(https?://[\\w-]+(\\.[\\w-]+)+(/\\S*)?)",
            RegexOption.IGNORE_CASE
        )
    }

    private fun extractFirstLink(text: String): String? = linkRegex.find(text)?.value
}
