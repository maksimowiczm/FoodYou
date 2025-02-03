package com.maksimowiczm.foodyou.core.feature.product.ui.create

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.mapBoth
import com.maksimowiczm.foodyou.core.feature.product.data.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateProductViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<CreateProductState>(CreateProductState.Nothing)
    val uiState = _uiState.asStateFlow()

    fun onCreateProduct(formState: ProductFormState) {
        viewModelScope.launch {
            _uiState.value = CreateProductState.CreatingProduct

            val result = productRepository.createUserProduct(
                name = formState.name.value,
                brand = formState.brand.value,
                barcode = formState.barcode.value,
                calories = formState.calories.value,
                proteins = formState.proteins.value,
                carbohydrates = formState.carbohydrates.value,
                sugars = formState.sugars.value,
                fats = formState.fats.value,
                saturatedFats = formState.saturatedFats.value,
                salt = formState.salt.value,
                sodium = formState.sodium.value,
                fiber = formState.fiber.value,
                packageWeight = formState.packageWeight.value,
                servingWeight = formState.servingWeight.value,
                weightUnit = formState.weightUnit
            )

            result.mapBoth(
                success = { productId ->
                    _uiState.value = CreateProductState.ProductCreated(productId)
                },
                failure = {
                    Log.e(TAG, "Failed to create product $it")
                }
            )
        }
    }

    private companion object {
        private const val TAG = "CreateProductViewModel"
    }
}
