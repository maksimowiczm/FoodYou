package com.maksimowiczm.foodyou.feature.product.ui.download

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.core.ext.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class DownloadProductScreenViewModel : ViewModel() {
    private val _isMutating = MutableStateFlow(false)
    val isMutating = _isMutating.asStateFlow()

    fun onDownload(text: String) = launch {
        _isMutating.emit(true)
        delay(2000)
        _isMutating.emit(false)
    }
}