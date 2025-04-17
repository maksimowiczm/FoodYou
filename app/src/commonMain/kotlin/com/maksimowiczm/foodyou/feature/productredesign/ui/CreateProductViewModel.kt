package com.maksimowiczm.foodyou.feature.productredesign.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.domain.model.openfoodfacts.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.core.domain.source.OpenFoodFactsRemoteDataSource
import com.maksimowiczm.foodyou.core.input.Form
import com.maksimowiczm.foodyou.core.input.Input
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

    private val nameForm = Form<ProductFormFieldError>(
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

    private val brandForm = Form<ProductFormFieldError>(ValidationStrategy.LazyEval)
    fun onBrandChange(brand: String) {
        _formState.update {
            val newState = it.copy(
                brand = brandForm.validate(brand)
            )
            newState.copy(
                isModified = isModified(newState)
            )
        }
    }

    private val barcodeForm = Form<ProductFormFieldError>(ValidationStrategy.LazyEval)
    fun onBarcodeChange(barcode: String) {
        _formState.update {
            val newState = it.copy(
                barcode = barcodeForm.validate(barcode)
            )
            newState.copy(
                isModified = isModified(newState)
            )
        }
    }

    private val proteinsForm = Form<ProductFormFieldError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.NotEmpty,
        ProductFormRules.EmptyOrFloat,
        ProductFormRules.NanOrBetween0and100
    )

    fun onProteinsChange(proteins: String) {
        _formState.update {
            val newState = it.copy(
                proteins = proteinsForm.validate(proteins)
            )
            newState.copy(
                isModified = isModified(newState)
            )
        }
    }

    private val carbohydratesForm = Form<ProductFormFieldError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.NotEmpty,
        ProductFormRules.EmptyOrFloat,
        ProductFormRules.NanOrBetween0and100
    )

    fun onCarbohydratesChange(carbohydrates: String) {
        _formState.update {
            val newState = it.copy(
                carbohydrates = carbohydratesForm.validate(carbohydrates)
            )
            newState.copy(
                isModified = isModified(newState)
            )
        }
    }

    private val fatsForm = Form<ProductFormFieldError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.NotEmpty,
        ProductFormRules.EmptyOrFloat,
        ProductFormRules.NanOrBetween0and100
    )

    fun onFatsChange(fats: String) {
        _formState.update {
            val newState = it.copy(
                fats = fatsForm.validate(fats)
            )
            newState.copy(
                isModified = isModified(newState)
            )
        }
    }

    private val sugarsForm = Form<ProductFormFieldError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.EmptyOrFloat,
        ProductFormRules.NanOrBetween0and100
    )

    fun onSugarsChange(sugars: String) {
        _formState.update {
            val newState = it.copy(
                sugars = sugarsForm.validate(sugars)
            )
            newState.copy(
                isModified = isModified(newState)
            )
        }
    }

    private val saturatedFatsForm = Form<ProductFormFieldError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.EmptyOrFloat,
        ProductFormRules.NanOrBetween0and100
    )

    fun onSaturatedFatsChange(saturatedFats: String) {
        _formState.update {
            val newState = it.copy(
                saturatedFats = saturatedFatsForm.validate(saturatedFats)
            )
            newState.copy(
                isModified = isModified(newState)
            )
        }
    }

    private val saltForm = Form<ProductFormFieldError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.EmptyOrFloat,
        ProductFormRules.NanOrBetween0and100
    )

    fun onSaltChange(salt: String) {
        _formState.update {
            val newState = it.copy(
                salt = saltForm.validate(salt)
            )
            newState.copy(
                isModified = isModified(newState)
            )
        }
    }

    private val sodiumForm = Form<ProductFormFieldError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.EmptyOrFloat,
        ProductFormRules.NanOrBetween0and100
    )

    fun onSodiumChange(sodium: String) {
        _formState.update {
            val newState = it.copy(
                sodium = sodiumForm.validate(sodium)
            )
            newState.copy(
                isModified = isModified(newState)
            )
        }
    }

    private val fiberForm = Form<ProductFormFieldError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.EmptyOrFloat,
        ProductFormRules.NanOrBetween0and100
    )

    fun onFiberChange(fiber: String) {
        _formState.update {
            val newState = it.copy(
                fiber = fiberForm.validate(fiber)
            )
            newState.copy(
                isModified = isModified(newState)
            )
        }
    }

    private val packageWeightForm = Form<ProductFormFieldError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.EmptyOrFloat,
        ProductFormRules.PositiveFloat
    )

    fun onPackageWeightChange(packageWeight: String) {
        _formState.update {
            val newState = it.copy(
                packageWeight = packageWeightForm.validate(packageWeight)
            )
            newState.copy(
                isModified = isModified(newState)
            )
        }
    }

    private val servingWeightForm = Form<ProductFormFieldError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.EmptyOrFloat,
        ProductFormRules.PositiveFloat
    )

    fun onServingWeightChange(servingWeight: String) {
        _formState.update {
            val newState = it.copy(
                servingWeight = servingWeightForm.validate(servingWeight)
            )
            newState.copy(
                isModified = isModified(newState)
            )
        }
    }

    private fun isModified(formState: ProductFormState) = when {
        formState.name !is Input.Empty -> true
        formState.brand !is Input.Empty -> true
        formState.barcode !is Input.Empty -> true
        formState.proteins !is Input.Empty -> true
        formState.carbohydrates !is Input.Empty -> true
        formState.fats !is Input.Empty -> true
        formState.sugars !is Input.Empty -> true
        formState.saturatedFats !is Input.Empty -> true
        formState.salt !is Input.Empty -> true
        formState.sodium !is Input.Empty -> true
        formState.fiber !is Input.Empty -> true
        formState.packageWeight !is Input.Empty -> true
        formState.servingWeight !is Input.Empty -> true
        else -> false
    }

    fun onCreateProduct() {
        // TODO
    }

    private val _openFoodFactsErrorBus = Channel<OpenFoodFactsError?>()
    val openFoodFactsErrorBus = _openFoodFactsErrorBus.receiveAsFlow()
    private val openFoodFactsLinkHelper by lazy { OpenFoodFactsLinkHelper() }
    fun onDownloadOpenFoodFacts(url: String) {
        viewModelScope.launch {
            _isDownloading.emit(true)
            _openFoodFactsErrorBus.send(null)

            val code = when (val code = openFoodFactsLinkHelper.extractCode(url)) {
                null -> {
                    _openFoodFactsErrorBus.send(OpenFoodFactsError.InvalidUrl)
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
                    _formState.update {
                        val newState = remote.asState()
                        newState.copy(
                            isModified = isModified(newState)
                        )
                    }
                    _eventBus.send(ProductFormEvent.DownloadedProductSuccessfully)
                }
                .onFailure {
                    _openFoodFactsErrorBus.send(OpenFoodFactsError.DownloadProductFailed(it))
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
