package com.maksimowiczm.foodyou.feature.food.product.presentation.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.food.application.command.DownloadProductCommand
import com.maksimowiczm.foodyou.business.food.application.command.DownloadProductError
import com.maksimowiczm.foodyou.business.food.domain.RemoteProduct
import com.maksimowiczm.foodyou.business.shared.application.command.CommandBus
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

internal class DownloadProductViewModel(text: String?, private val commandBus: CommandBus) :
    ViewModel() {
    private val _isMutating = MutableStateFlow(false)
    val isMutating = _isMutating.asStateFlow()

    private val _error = MutableStateFlow<DownloadProductError?>(null)
    val error = _error.asStateFlow()

    private val _productEvent = MutableStateFlow<RemoteProduct?>(null)
    val productEvent = _productEvent.filterNotNull()

    init {
        if (text != null) {
            onDownload(text)
        }
    }

    fun onDownload(text: String) {
        viewModelScope.launch {
            _isMutating.emit(true)

            commandBus
                .dispatch<RemoteProduct, DownloadProductError>(DownloadProductCommand(text))
                .fold(
                    onSuccess = {
                        _productEvent.emit(
                            it.copy(
                                source =
                                    FoodSource(
                                        type = FoodSource.Type.User,
                                        url = it.source.url ?: text,
                                    )
                            )
                        )
                    },
                    onFailure = { _error.value = it },
                )

            _isMutating.emit(false)
        }
    }
}
