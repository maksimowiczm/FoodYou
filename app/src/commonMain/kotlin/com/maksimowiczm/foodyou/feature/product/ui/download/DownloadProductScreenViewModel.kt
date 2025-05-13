package com.maksimowiczm.foodyou.feature.product.ui.download

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.core.ext.launch
import com.maksimowiczm.foodyou.feature.product.domain.RemoteProductRequestFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class DownloadProductScreenViewModel(
    private val requestFactory: RemoteProductRequestFactory
) : ViewModel() {
    private val _isMutating = MutableStateFlow(false)
    val isMutating = _isMutating.asStateFlow()

    fun onDownload(text: String) = withMutateGuard {
        delay(2000)
        val link = extractFirstLink(text)

        if (link == null) {
            // TODO handle error
            return@withMutateGuard
        }

        val request = requestFactory.createFromUrl(link)

        if (request == null) {
            // TODO handle error
            return@withMutateGuard
        }

        val product = request.getProduct().getOrElse {
            // TODO handle error
            return@withMutateGuard
        }

        // TODO handle product
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
