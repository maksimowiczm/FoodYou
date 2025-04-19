package com.maksimowiczm.foodyou.feature.product.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.domain.model.openfoodfacts.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.core.domain.source.OpenFoodFactsRemoteDataSource
import com.maksimowiczm.foodyou.core.input.Form
import com.maksimowiczm.foodyou.core.input.Input
import com.maksimowiczm.foodyou.core.input.ValidationStrategy
import com.maksimowiczm.foodyou.core.input.dsl.input
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.feature.product.data.ProductRepository
import com.maksimowiczm.foodyou.feature.product.ui.ProductFormFieldError
import com.maksimowiczm.foodyou.feature.product.ui.ProductFormRules
import com.maksimowiczm.foodyou.feature.product.ui.ProductFormState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class CreateProductViewModel(
    private val productRepository: ProductRepository,
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
            val newState = it.copy(
                name = nameForm.validate(name)
            )
            newState.copy(
                isModified = isModified(newState)
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
        val formState = _formState.value

        if (formState.error != null || !formState.isValid) {
            return
        }

        val name = formState.name.takeIf { it.isValid }?.value ?: return
        val brand = formState.brand.takeIf { it.isValidOrEmpty }?.value
        val barcode = formState.barcode.takeIf { it.isValidOrEmpty }?.value

        val proteins = formState.proteins.takeIf { it.isValid }?.value?.toFloatOrNull() ?: return
        val carbohydrates =
            formState.carbohydrates.takeIf { it.isValid }?.value?.toFloatOrNull() ?: return
        val fats = formState.fats.takeIf { it.isValid }?.value?.toFloatOrNull() ?: return
        val calories = formState.calories ?: return

        val sugars = formState.sugars.takeIf { it.isValidOrEmpty }?.value?.toFloatOrNull()
        val saturatedFats =
            formState.saturatedFats.takeIf { it.isValidOrEmpty }?.value?.toFloatOrNull()
        val salt = formState.salt.takeIf { it.isValidOrEmpty }?.value?.toFloatOrNull()
        val sodium = formState.sodium.takeIf { it.isValidOrEmpty }?.value?.toFloatOrNull()
        val fiber = formState.fiber.takeIf { it.isValidOrEmpty }?.value?.toFloatOrNull()

        val packageWeight =
            formState.packageWeight.takeIf { it.isValidOrEmpty }?.value?.toFloatOrNull()
        val servingWeight =
            formState.servingWeight.takeIf { it.isValidOrEmpty }?.value?.toFloatOrNull()

        viewModelScope.launch {
            _eventBus.send(ProductFormEvent.CreatingProduct)

            val id = productRepository.createProduct(
                name = name,
                brand = brand,
                barcode = barcode,
                calories = calories,
                proteins = proteins,
                carbohydrates = carbohydrates,
                fats = fats,
                sugars = sugars,
                saturatedFats = saturatedFats,
                salt = salt,
                sodium = sodium,
                fiber = fiber,
                packageWeight = packageWeight,
                servingWeight = servingWeight
            )

            _eventBus.send(ProductFormEvent.ProductCreated(id))
        }
    }

    private val _openFoodFactsLink = MutableStateFlow(input<OpenFoodFactsLinkError>())
    val openFoodFactsLink = _openFoodFactsLink.asStateFlow()
    private val openFoodFactsLinkForm = Form<OpenFoodFactsLinkError>(
        strategy = ValidationStrategy.FailFast,
        OpenFoodFactsLinkRules.NotEmpty,
        OpenFoodFactsLinkRules.ValidUrl
    )

    fun onDownloadLinkChange(link: String) {
        _openFoodFactsLink.update {
            openFoodFactsLinkForm.validate(link)
        }
    }

    private val openFoodFactsLinkHelper by lazy { OpenFoodFactsLinkHelper() }
    private val _openFoodFactsError = MutableStateFlow<DownloadProductFailed?>(null)
    val openFoodFactsError = _openFoodFactsError.asStateFlow()
    fun onDownloadOpenFoodFacts() {
        val url = _openFoodFactsLink.value.value

        if (url.isEmpty()) {
            _openFoodFactsLink.update {
                Input.Invalid(it.value, listOf(OpenFoodFactsLinkError.Empty))
            }
            return
        }

        viewModelScope.launch {
            _openFoodFactsError.emit(null)

            val code = when (val code = openFoodFactsLinkHelper.extractCode(url)) {
                null -> {
                    _openFoodFactsLink.update {
                        Input.Invalid(it.value, listOf(OpenFoodFactsLinkError.InvalidUrl))
                    }
                    return@launch
                }

                else -> code
            }

            _isDownloading.emit(true)

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
                    _openFoodFactsLink.update { input() }
                    _openFoodFactsError.emit(null)
                }
                .onFailure {
                    _openFoodFactsError.emit(DownloadProductFailed(it))
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
        packageWeight = packageWeightForm(packageQuantity?.formatClipZeros() ?: ""),
        servingWeight = servingWeightForm(servingQuantity?.formatClipZeros() ?: "")
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
