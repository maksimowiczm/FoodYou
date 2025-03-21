package com.maksimowiczm.foodyou.feature.diary.ui.product.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.github.michaelbull.result.mapBoth
import com.maksimowiczm.foodyou.feature.diary.data.ProductRepository
import com.maksimowiczm.foodyou.feature.diary.ui.product.ProductFormState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class UpdateProductViewModel(
    private val productRepository: ProductRepository,
    private val productId: Long
) : ViewModel() {
    private val _uiState = MutableStateFlow<UpdateProductState>(UpdateProductState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        productRepository.observeProductById(productId).onEach {
            if (it == null) {
                Logger.e(TAG) { "Product not found" }
                return@onEach
            }

            if (
                _uiState.value is UpdateProductState.ProductReady ||
                _uiState.value is UpdateProductState.Loading
            ) {
                _uiState.value = UpdateProductState.ProductReady(it)
            }
        }.launchIn(viewModelScope)
    }

    fun updateProduct(formState: ProductFormState) {
        val state = _uiState.value
        if (state !is UpdateProductState.ProductReady) {
            Logger.e(TAG) { "Product not ready" }
            return
        }

        _uiState.value = UpdateProductState.UpdatingProduct(state.product)

        viewModelScope.launch {
            if (
                formState.name.value == null ||
                formState.calories.value == null ||
                formState.proteins.value == null ||
                formState.carbohydrates.value == null ||
                formState.fats.value == null
            ) {
                Logger.w(TAG) { "Required fields are missing" }
                return@launch
            }

            val result = productRepository.updateProduct(
                id = productId,
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
                success = {
                    val product = _uiState.value as? UpdateProductState.UpdatingProduct

                    if (product == null) {
                        Logger.e(TAG) { "Product not updating. Invalid state" }
                        return@mapBoth
                    }

                    _uiState.value = UpdateProductState.ProductUpdated(product.product)
                },
                failure = {
                    Logger.e(TAG) { "Failed to create product $it" }
                }
            )
        }
    }

    companion object {
        private const val TAG = "UpdateProductViewModel"
    }
}
