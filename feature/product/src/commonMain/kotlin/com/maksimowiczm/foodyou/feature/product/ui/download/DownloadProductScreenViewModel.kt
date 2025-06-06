package com.maksimowiczm.foodyou.feature.product.ui.download

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.core.ext.launch
import com.maksimowiczm.foodyou.core.ext.set
import com.maksimowiczm.foodyou.core.ext.setNull
import com.maksimowiczm.foodyou.feature.product.data.ProductNotFoundException
import com.maksimowiczm.foodyou.feature.product.data.network.usda.USDAException
import com.maksimowiczm.foodyou.feature.product.data.network.usda.USDAPreferences
import com.maksimowiczm.foodyou.feature.product.domain.RemoteProduct
import com.maksimowiczm.foodyou.feature.product.domain.RemoteProductRequestFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull

internal class DownloadProductScreenViewModel(
    text: String?,
    private val requestFactory: RemoteProductRequestFactory,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    private val _isMutating = MutableStateFlow(false)
    val isMutating = _isMutating.asStateFlow()

    private val _error = MutableStateFlow<DownloadError?>(null)
    val error = _error.asStateFlow()

    private val _productEvent = MutableStateFlow<RemoteProduct?>(null)
    val productEvent = _productEvent.filterNotNull()

    private val linkRegex by lazy {
        Regex(
            """[-a-zA-Z0-9@:%._+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)""",
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
                is ProductNotFoundException -> _error.emit(productNotFoundError())
                is USDAException -> _error.emit(usdaApiKeyError(it))
                else -> _error.emit(customError(it.message))
            }

            return@withMutateGuard
        }

        _productEvent.emit(product)
    }

    private fun withMutateGuard(block: suspend () -> Unit) = launch {
        _isMutating.emit(true)
        block()
        _isMutating.emit(false)
    }

    private fun extractFirstLink(text: String): String? = linkRegex.find(text)?.value

    fun setUsdaApiKey(key: String) = launch {
        if (key.isBlank()) {
            dataStore.setNull(USDAPreferences.apiKeyPreferenceKey)
        } else {
            dataStore.set(USDAPreferences.apiKeyPreferenceKey to key)
        }
    }
}

private fun productNotFoundError() = DownloadError.GenericError.ProductNotFound
private fun urlNotFoundError() = DownloadError.GenericError.URLNotFound
private fun urlNotSupportedError() = DownloadError.GenericError.URLNotSupported
private fun customError(message: String?) = DownloadError.GenericError.Custom(message)
private fun usdaApiKeyError(error: USDAException) = DownloadError.UsdaApiKeyError(error)
