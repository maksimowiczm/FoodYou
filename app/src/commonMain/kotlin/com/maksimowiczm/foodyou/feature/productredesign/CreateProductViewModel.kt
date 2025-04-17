package com.maksimowiczm.foodyou.feature.productredesign

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.domain.model.openfoodfacts.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.core.domain.source.OpenFoodFactsRemoteDataSource
import com.maksimowiczm.foodyou.core.input.Form
import com.maksimowiczm.foodyou.core.input.ValidationStrategy
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class CreateProductViewModel(
    private val openFoodFactsRemoteDataSource: OpenFoodFactsRemoteDataSource
) : ViewModel() {
    private val _formState = MutableStateFlow<ProductFormState>(ProductFormState())
    val formState = _formState.asStateFlow()

    private val _isDownloading = MutableStateFlow(false)
    val isDownloading = _isDownloading.asStateFlow()

    private val _eventBus = Channel<ProductFormEvent>()
    val eventBus = _eventBus.receiveAsFlow()

    private val nameForm = Form<ProductFormError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.NotEmpty
    )

    fun onNameChange(name: String) {
        _formState.update {
            it.copy(
                name = nameForm.validate(name)
            )
        }
    }

    private val brandForm = Form<ProductFormError>(ValidationStrategy.LazyEval)
    fun onBrandChange(brand: String) {
        _formState.update {
            it.copy(
                brand = brandForm.validate(brand)
            )
        }
    }

    private val barcodeForm = Form<ProductFormError>(ValidationStrategy.LazyEval)
    fun onBarcodeChange(barcode: String) {
        _formState.update {
            it.copy(
                barcode = barcodeForm.validate(barcode)
            )
        }
    }

    private val proteinsForm = Form<ProductFormError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.NotEmpty,
        ProductFormRules.between0and100
    )

    fun onProteinsChange(proteins: String) {
        _formState.update {
            it.copy(
                proteins = proteinsForm.validate(proteins)
            )
        }
    }

    private val carbohydratesForm = Form<ProductFormError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.between0and100
    )

    fun onCarbohydratesChange(carbohydrates: String) {
        _formState.update {
            it.copy(
                carbohydrates = carbohydratesForm.validate(carbohydrates)
            )
        }
    }

    private val fatsForm = Form<ProductFormError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.between0and100
    )

    fun onFatsChange(fats: String) {
        _formState.update {
            it.copy(
                fats = fatsForm.validate(fats)
            )
        }
    }

    private val sugarsForm = Form<ProductFormError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.between0and100
    )

    fun onSugarsChange(sugars: String) {
        _formState.update {
            it.copy(
                sugars = sugarsForm.validate(sugars)
            )
        }
    }

    private val saturatedFatsForm = Form<ProductFormError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.between0and100
    )

    fun onSaturatedFatsChange(saturatedFats: String) {
        _formState.update {
            it.copy(
                saturatedFats = saturatedFatsForm.validate(saturatedFats)
            )
        }
    }

    private val saltForm = Form<ProductFormError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.between0and100
    )

    fun onSaltChange(salt: String) {
        _formState.update {
            it.copy(
                salt = saltForm.validate(salt)
            )
        }
    }

    private val sodiumForm = Form<ProductFormError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.between0and100
    )

    fun onSodiumChange(sodium: String) {
        _formState.update {
            it.copy(
                sodium = sodiumForm.validate(sodium)
            )
        }
    }

    private val fiberForm = Form<ProductFormError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.between0and100
    )

    fun onFiberChange(fiber: String) {
        _formState.update {
            it.copy(
                fiber = fiberForm.validate(fiber)
            )
        }
    }

    private val packageWeightForm = Form<ProductFormError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.positiveFloat
    )

    fun onPackageWeightChange(packageWeight: String) {
        _formState.update {
            it.copy(
                packageWeight = packageWeightForm.validate(packageWeight)
            )
        }
    }

    private val servingWeightForm = Form<ProductFormError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.positiveFloat
    )

    fun onServingWeightChange(servingWeight: String) {
        _formState.update {
            it.copy(
                servingWeight = servingWeightForm.validate(servingWeight)
            )
        }
    }

    private val _openFoodFactsError = MutableStateFlow<OpenFoodFactsError?>(null)
    val openFoodFactsError = _openFoodFactsError.asStateFlow()
    private val openFoodFactsLinkHelper by lazy { OpenFoodFactsLinkHelper() }
    fun onDownloadOpenFoodFacts(url: String) {
        viewModelScope.launch {
            _isDownloading.emit(true)

            val code = when (val code = openFoodFactsLinkHelper.extractCode(url)) {
                null -> {
                    _openFoodFactsError.emit(OpenFoodFactsError.InvalidUrl)
                    _isDownloading.emit(false)
                    return@launch
                }

                else -> code
            }

            val product = runCatching {
                openFoodFactsRemoteDataSource.getProduct(code, null) ?: error("Product not found")
            }

            product
                .onSuccess { remote ->
                    _formState.update { remote.asState() }
                    _eventBus.send(ProductFormEvent.DownloadedProductSuccessfully)
                }
                .onFailure {
                    _openFoodFactsError.emit(OpenFoodFactsError.DownloadProductFailed(it))
                }

            _isDownloading.emit(false)
        }
    }

    private fun OpenFoodFactsProduct.asState(): ProductFormState = ProductFormState(
        name = nameForm(productName ?: ""),
        brand = brandForm(brands ?: ""),
        barcode = barcodeForm(code ?: ""),
        proteins = proteinsForm(nutrients?.proteins100g?.formatClipZeros() ?: ""),
        carbohydrates = carbohydratesForm(nutrients?.carbohydrates100g?.formatClipZeros() ?: ""),
        fats = fatsForm(nutrients?.fat100g?.formatClipZeros() ?: ""),
        sugars = sugarsForm(nutrients?.sugars100g?.formatClipZeros() ?: ""),
        saturatedFats = saturatedFatsForm(nutrients?.saturatedFat100g?.formatClipZeros() ?: ""),
        salt = saltForm(nutrients?.salt100g?.formatClipZeros() ?: ""),
        sodium = sodiumForm(nutrients?.sodium100g?.formatClipZeros() ?: ""),
        fiber = fiberForm(nutrients?.fiber100g?.formatClipZeros() ?: ""),
        packageWeight = packageWeightForm(packageQuantity.toString()),
        servingWeight = servingWeightForm(servingQuantity.toString())
    )
}

private class OpenFoodFactsLinkHelper {
    fun extractCode(url: String): String? {
        // Extract barcode from product URL
        val regex = "openfoodfacts\\.org/product/(\\d+)".toRegex()
        val barcode = regex.find(url)?.groupValues?.getOrNull(1)
        return barcode
    }
}
