package com.maksimowiczm.foodyou.app.ui.food.product.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.common.domain.food.FoodSource
import com.maksimowiczm.foodyou.common.result.onError
import com.maksimowiczm.foodyou.common.result.onSuccess
import com.maksimowiczm.foodyou.food.domain.usecase.DownloadProductError
import com.maksimowiczm.foodyou.food.domain.usecase.DownloadProductUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class DownloadProductViewModel(
    text: String?,
    private val downloadProductUseCase: DownloadProductUseCase,
    private val downloadProductHolder: DownloadProductHolder,
) : ViewModel() {
    private val _isMutating = MutableStateFlow(false)
    val isMutating = _isMutating.asStateFlow()

    private val _error = MutableStateFlow<DownloadProductError?>(null)
    val error = _error.asStateFlow()

    private val productDownloadedEventBus = Channel<Unit>()

    val productEvent = productDownloadedEventBus.receiveAsFlow()

    init {
        if (text != null) {
            onDownload(text)
        }
    }

    fun onDownload(text: String) {
        viewModelScope.launch {
            _isMutating.emit(true)

            downloadProductUseCase
                .download(text)
                .onSuccess {
                    val product =
                        it.copy(
                            source =
                                FoodSource(type = FoodSource.Type.User, url = it.source.url ?: text)
                        )

                    downloadProductHolder.setProduct(product)
                    productDownloadedEventBus.send(Unit)
                }
                .onError { _error.value = it }

            _isMutating.emit(false)
        }
    }
}
