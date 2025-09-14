package com.maksimowiczm.foodyou.feature.food.product.presentation.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.app.business.opensource.domain.food.User
import com.maksimowiczm.foodyou.food.domain.usecase.DownloadProductError
import com.maksimowiczm.foodyou.food.domain.usecase.DownloadProductUseCase
import com.maksimowiczm.foodyou.shared.domain.food.FoodSource
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
                .fold(
                    onSuccess = {
                        val product =
                            it.copy(
                                source =
                                    FoodSource(
                                        type = FoodSource.Type.User,
                                        url = it.source.url ?: text,
                                    )
                            )

                        downloadProductHolder.setProduct(product)
                        productDownloadedEventBus.send(Unit)
                    },
                    onFailure = { _error.value = it },
                )

            _isMutating.emit(false)
        }
    }
}
