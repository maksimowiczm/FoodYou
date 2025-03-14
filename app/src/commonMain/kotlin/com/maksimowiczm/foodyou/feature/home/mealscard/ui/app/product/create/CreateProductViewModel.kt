package com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.product.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.github.michaelbull.result.mapBoth
import com.maksimowiczm.foodyou.data.ProductRepository
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.product.ProductFormState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateProductViewModel(private val productRepository: ProductRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<CreateProductState>(CreateProductState.Nothing)
    val uiState = _uiState.asStateFlow()

    fun onCreateProduct(formState: ProductFormState) {
        viewModelScope.launch {
            _uiState.value = CreateProductState.CreatingProduct

            if (
                formState.name.value == null ||
                formState.calories.value == null ||
                formState.proteins.value == null ||
                formState.carbohydrates.value == null ||
                formState.fats.value == null
            ) {
                Logger.w(TAG) { "Required fields are missing" }
                _uiState.value = CreateProductState.Error
                return@launch
            }

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
                    Logger.e(TAG) { "Failed to create product $it" }
                    _uiState.value = CreateProductState.Error
                }
            )
        }
    }

    private companion object {
        private const val TAG = "CreateProductViewModel"
    }
}
