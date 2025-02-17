package com.maksimowiczm.foodyou.core.feature.addfood.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.feature.product.data.ProductRepository
import kotlinx.coroutines.launch

class AddFoodViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {
    fun onProductDelete(productId: Long) {
        viewModelScope.launch {
            productRepository.deleteProduct(productId)
        }
    }
}
