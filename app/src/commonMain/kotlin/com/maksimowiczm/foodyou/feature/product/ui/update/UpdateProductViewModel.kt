package com.maksimowiczm.foodyou.feature.product.ui.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.domain.model.Product
import com.maksimowiczm.foodyou.core.input.AllowEmptyForm
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

internal class UpdateProductViewModel(
    private val productId: Long,
    private val productRepository: ProductRepository
) : ViewModel() {
    private var product: Product? = null
    private val _formState = MutableStateFlow<ProductFormState?>(null)
    val formState = _formState.asStateFlow()

    init {
        viewModelScope.launch {
            val product = productRepository.getProductById(productId) ?: return@launch

            this@UpdateProductViewModel.product = product

            _formState.value = ProductFormState(
                name = input(product.name),
                brand = input(product.brand ?: ""),
                barcode = input(product.barcode ?: ""),
                proteins = input(product.nutrients.proteins.value.formatClipZeros()),
                carbohydrates = input(product.nutrients.carbohydrates.value.formatClipZeros()),
                fats = input(product.nutrients.fats.value.formatClipZeros()),
                sugars = input(product.nutrients.sugars.value?.formatClipZeros() ?: ""),
                saturatedFats = input(
                    product.nutrients.saturatedFats.value?.formatClipZeros() ?: ""
                ),
                salt = input(product.nutrients.salt.value?.formatClipZeros() ?: ""),
                sodium = input(product.nutrients.sodium.value?.formatClipZeros() ?: ""),
                fiber = input(product.nutrients.fiber.value?.formatClipZeros() ?: ""),
                packageWeight = input(product.packageWeight?.weight?.formatClipZeros() ?: ""),
                servingWeight = input(product.servingWeight?.weight?.formatClipZeros() ?: ""),
                isModified = false
            )
        }
    }

    private val nameForm = Form<ProductFormFieldError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.NotEmpty
    )

    fun onNameChange(name: String) {
        _formState.update {
            val newState = it?.copy(
                name = nameForm.validate(name)
            )
            newState?.copy(
                isModified = isModified(newState)
            )
        }
    }

    private val brandForm = AllowEmptyForm<ProductFormFieldError>(ValidationStrategy.LazyEval)
    fun onBrandChange(brand: String) {
        _formState.update {
            val newState = it?.copy(
                brand = brandForm.validate(brand)
            )
            newState?.copy(
                isModified = isModified(newState)
            )
        }
    }

    private val barcodeForm = AllowEmptyForm<ProductFormFieldError>(ValidationStrategy.LazyEval)
    fun onBarcodeChange(barcode: String) {
        _formState.update {
            val newState = it?.copy(
                barcode = barcodeForm.validate(barcode)
            )
            newState?.copy(
                isModified = isModified(newState)
            )
        }
    }

    private val proteinsForm = Form<ProductFormFieldError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.NotEmpty,
        ProductFormRules.EmptyOrFloat,
        ProductFormRules.FloatBetween0and100
    )

    fun onProteinsChange(proteins: String) {
        _formState.update {
            val newState = it?.copy(
                proteins = proteinsForm.validate(proteins)
            )
            newState?.copy(
                isModified = isModified(newState)
            )
        }
    }

    private val carbohydratesForm = Form<ProductFormFieldError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.NotEmpty,
        ProductFormRules.EmptyOrFloat,
        ProductFormRules.FloatBetween0and100
    )

    fun onCarbohydratesChange(carbohydrates: String) {
        _formState.update {
            val newState = it?.copy(
                carbohydrates = carbohydratesForm.validate(carbohydrates)
            )
            newState?.copy(
                isModified = isModified(newState)
            )
        }
    }

    private val fatsForm = Form<ProductFormFieldError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.NotEmpty,
        ProductFormRules.EmptyOrFloat,
        ProductFormRules.FloatBetween0and100
    )

    fun onFatsChange(fats: String) {
        _formState.update {
            val newState = it?.copy(
                fats = fatsForm.validate(fats)
            )
            newState?.copy(
                isModified = isModified(newState)
            )
        }
    }

    private val sugarsForm = Form<ProductFormFieldError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.EmptyOrFloat,
        ProductFormRules.FloatBetween0and100
    )

    fun onSugarsChange(sugars: String) {
        _formState.update {
            val newState = it?.copy(
                sugars = sugarsForm.validate(sugars)
            )
            newState?.copy(
                isModified = isModified(newState)
            )
        }
    }

    private val saturatedFatsForm = AllowEmptyForm<ProductFormFieldError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.EmptyOrFloat,
        ProductFormRules.FloatBetween0and100
    )

    fun onSaturatedFatsChange(saturatedFats: String) {
        _formState.update {
            val newState = it?.copy(
                saturatedFats = saturatedFatsForm.validate(saturatedFats)
            )
            newState?.copy(
                isModified = isModified(newState)
            )
        }
    }

    private val saltForm = AllowEmptyForm<ProductFormFieldError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.EmptyOrFloat,
        ProductFormRules.FloatBetween0and100
    )

    fun onSaltChange(salt: String) {
        _formState.update {
            val newState = it?.copy(
                salt = saltForm.validate(salt)
            )
            newState?.copy(
                isModified = isModified(newState)
            )
        }
    }

    private val sodiumForm = AllowEmptyForm<ProductFormFieldError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.EmptyOrFloat,
        ProductFormRules.FloatBetween0and100
    )

    fun onSodiumChange(sodium: String) {
        _formState.update {
            val newState = it?.copy(
                sodium = sodiumForm.validate(sodium)
            )
            newState?.copy(
                isModified = isModified(newState)
            )
        }
    }

    private val fiberForm = AllowEmptyForm<ProductFormFieldError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.EmptyOrFloat,
        ProductFormRules.FloatBetween0and100
    )

    fun onFiberChange(fiber: String) {
        _formState.update {
            val newState = it?.copy(
                fiber = fiberForm.validate(fiber)
            )
            newState?.copy(
                isModified = isModified(newState)
            )
        }
    }

    private val packageWeightForm = AllowEmptyForm<ProductFormFieldError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.EmptyOrFloat,
        ProductFormRules.PositiveFloat
    )

    fun onPackageWeightChange(packageWeight: String) {
        _formState.update {
            val newState = it?.copy(
                packageWeight = packageWeightForm.validate(packageWeight)
            )
            newState?.copy(
                isModified = isModified(newState)
            )
        }
    }

    private val servingWeightForm = AllowEmptyForm<ProductFormFieldError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.EmptyOrFloat,
        ProductFormRules.PositiveFloat
    )

    fun onServingWeightChange(servingWeight: String) {
        _formState.update {
            val newState = it?.copy(
                servingWeight = servingWeightForm.validate(servingWeight)
            )
            newState?.copy(
                isModified = isModified(newState)
            )
        }
    }

    private fun Input<ProductFormFieldError>.float(): Float? = value.toFloatOrNull()

    private fun isModified(formState: ProductFormState) = when {
        formState.name.value != product?.name -> true
        formState.brand.value != product?.brand -> true
        formState.barcode.value != product?.barcode -> true
        formState.proteins.float() != product?.nutrients?.proteins?.value -> true
        formState.carbohydrates.float() != product?.nutrients?.carbohydrates?.value -> true
        formState.fats.float() != product?.nutrients?.fats?.value -> true
        formState.sugars.float() != product?.nutrients?.sugars?.value -> true
        formState.saturatedFats.float() != product?.nutrients?.saturatedFats?.value -> true
        formState.salt.float() != product?.nutrients?.salt?.value -> true
        formState.sodium.float() != product?.nutrients?.sodium?.value -> true
        formState.fiber.float() != product?.nutrients?.fiber?.value -> true
        formState.packageWeight.float() != product?.packageWeight?.weight -> true
        formState.servingWeight.float() != product?.servingWeight?.weight -> true
        else -> false
    }

    private val _eventBus = Channel<ProductFormEvent>()
    val eventBus = _eventBus.receiveAsFlow()

    fun onUpdate() {
        val formState = _formState.value ?: return

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
            _eventBus.send(ProductFormEvent.UpdatingProduct)

            productRepository.updateProduct(
                id = productId,
                name = name,
                brand = brand,
                barcode = barcode,
                calories = calories,
                proteins = proteins,
                carbohydrates = carbohydrates,
                sugars = sugars,
                fats = fats,
                saturatedFats = saturatedFats,
                salt = salt,
                sodium = sodium,
                fiber = fiber,
                packageWeight = packageWeight,
                servingWeight = servingWeight
            )

            _eventBus.send(ProductFormEvent.ProductUpdated(productId))
        }
    }
}
