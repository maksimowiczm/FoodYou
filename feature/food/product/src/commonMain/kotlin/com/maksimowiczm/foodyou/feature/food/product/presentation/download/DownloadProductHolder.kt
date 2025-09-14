package com.maksimowiczm.foodyou.feature.food.product.presentation.download

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.food.domain.entity.RemoteProduct
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class DownloadProductHolder : ViewModel() {
    private val _product = MutableStateFlow<RemoteProduct?>(null)
    val product = _product.asStateFlow()

    fun setProduct(product: RemoteProduct?) {
        _product.value = product
    }
}
