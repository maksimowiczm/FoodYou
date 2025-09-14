package com.maksimowiczm.foodyou.feature.food.product.presentation.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.app.business.opensource.domain.food.User
import com.maksimowiczm.foodyou.food.domain.entity.RemoteProduct
import com.maksimowiczm.foodyou.food.domain.usecase.DownloadProductError
import com.maksimowiczm.foodyou.food.domain.usecase.DownloadProductUseCase
import com.maksimowiczm.foodyou.shared.domain.food.FoodSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

internal class DownloadProductViewModel(
    text: String?,
    private val downloadProductUseCase: DownloadProductUseCase,
) : ViewModel() {
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

            downloadProductUseCase
                .download(text)
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
