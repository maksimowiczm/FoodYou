package com.maksimowiczm.foodyou.feature.search.ui

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.feature.search.domain.ObserveProductUseCase

class ProductViewModel(private val observeProductUseCase: ObserveProductUseCase) : ViewModel() {
    fun observeProduct(id: Long) = observeProductUseCase.observeProduct(id)
}
