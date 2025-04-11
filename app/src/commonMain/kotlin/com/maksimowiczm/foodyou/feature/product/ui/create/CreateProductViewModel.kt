package com.maksimowiczm.foodyou.feature.product.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.product.data.ProductRepository
import com.maksimowiczm.foodyou.feature.product.ui.Exceeds100
import com.maksimowiczm.foodyou.feature.product.ui.FloatNumber
import com.maksimowiczm.foodyou.feature.product.ui.NonNegativeNumber
import com.maksimowiczm.foodyou.feature.product.ui.PositiveNumber
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pro.respawn.kmmutils.inputforms.Form
import pro.respawn.kmmutils.inputforms.ValidationStrategy
import pro.respawn.kmmutils.inputforms.default.Rules
import pro.respawn.kmmutils.inputforms.dsl.isValid
import pro.respawn.kmmutils.inputforms.dsl.isValidOrEmpty

internal class CreateProductViewModel(private val productRepository: ProductRepository) :
    ViewModel() {
    private val _idState = MutableStateFlow<CreateState>(CreateState.Nothing)
    val idState = _idState.asStateFlow()

    private val nutrientsRules = arrayOf(
        Rules.FloatNumber,
        Rules.NonNegativeNumber,
        Rules.Exceeds100
    )

    private val nameForm = Form(ValidationStrategy.LazyEval, Rules.NonEmpty)
    private val brandForm = Form(ValidationStrategy.FailFast)
    private val barcodeForm = Form(ValidationStrategy.FailFast)

    private val proteinsForm = Form(
        ValidationStrategy.LazyEval,
        Rules.NonEmpty,
        *nutrientsRules
    )
    private val carbohydratesForm = Form(
        ValidationStrategy.LazyEval,
        Rules.NonEmpty,
        *nutrientsRules
    )
    private val fatsForm = Form(
        ValidationStrategy.LazyEval,
        Rules.NonEmpty,
        *nutrientsRules
    )

    private val sugarsForm = Form(
        ValidationStrategy.FailFast,
        *nutrientsRules
    )
    private val saturatedFatsForm = Form(
        ValidationStrategy.FailFast,
        *nutrientsRules
    )
    private val saltForm = Form(
        ValidationStrategy.FailFast,
        *nutrientsRules
    )
    private val sodiumForm = Form(
        ValidationStrategy.FailFast,
        *nutrientsRules
    )
    private val fiberForm = Form(
        ValidationStrategy.FailFast,
        *nutrientsRules
    )

    private val packageWeightForm = Form(
        ValidationStrategy.FailFast,
        Rules.FloatNumber,
        Rules.PositiveNumber
    )
    private val servingWeightForm = Form(
        ValidationStrategy.FailFast,
        Rules.FloatNumber,
        Rules.PositiveNumber
    )

    private val _state = MutableStateFlow(CreateProductState())
    val state = _state.asStateFlow()

    fun onNameChange(name: String) {
        _state.update {
            it.copy(
                name = nameForm(name),
                isModified = it.isModified || name.isNotBlank()
            )
        }
    }

    fun onBrandChange(brand: String) {
        _state.update {
            it.copy(
                brand = brandForm(brand),
                isModified = it.isModified || brand.isNotBlank()
            )
        }
    }

    fun onBarcodeChange(barcode: String) {
        _state.update {
            it.copy(
                barcode = barcodeForm(barcode),
                isModified = it.isModified || barcode.isNotBlank()
            )
        }
    }

    fun onProteinsChange(proteins: String) {
        _state.update {
            it.copy(
                proteins = proteinsForm(proteins),
                isModified = it.isModified || proteins.isNotBlank()
            )
        }
    }

    fun onCarbohydratesChange(carbohydrates: String) {
        _state.update {
            it.copy(
                carbohydrates = carbohydratesForm(carbohydrates),
                isModified = it.isModified || carbohydrates.isNotBlank()
            )
        }
    }

    fun onFatsChange(fats: String) {
        _state.update {
            it.copy(
                fats = fatsForm(fats),
                isModified = it.isModified || fats.isNotBlank()
            )
        }
    }

    fun onSugarsChange(sugars: String) {
        _state.update {
            it.copy(
                sugars = sugarsForm(sugars),
                isModified = it.isModified || sugars.isNotBlank()
            )
        }
    }

    fun onSaturatedFatsChange(saturatedFats: String) {
        _state.update {
            it.copy(
                saturatedFats = saturatedFatsForm(saturatedFats),
                isModified = it.isModified || saturatedFats.isNotBlank()
            )
        }
    }

    fun onSaltChange(salt: String) {
        _state.update {
            it.copy(
                salt = saltForm(salt),
                isModified = it.isModified || salt.isNotBlank()
            )
        }
    }

    fun onSodiumChange(sodium: String) {
        _state.update {
            it.copy(
                sodium = sodiumForm(sodium),
                isModified = it.isModified || sodium.isNotBlank()
            )
        }
    }

    fun onFiberChange(fiber: String) {
        _state.update {
            it.copy(
                fiber = fiberForm(fiber),
                isModified = it.isModified || fiber.isNotBlank()
            )
        }
    }

    fun onPackageWeightChange(packageWeight: String) {
        _state.update {
            it.copy(
                packageWeight = packageWeightForm(packageWeight),
                isModified = it.isModified || packageWeight.isNotBlank()
            )
        }
    }

    fun onServingWeightChange(servingWeight: String) {
        _state.update {
            it.copy(
                servingWeight = servingWeightForm(servingWeight),
                isModified = it.isModified || servingWeight.isNotBlank()
            )
        }
    }

    fun onCreate() {
        val formState = _state.value

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
            _idState.value = CreateState.CreatingProduct

            val id = productRepository.createUserProduct(
                name = name,
                brand = brand,
                barcode = barcode,
                calories = formState.calories ?: return@launch,
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

            _idState.value = CreateState.Created(id)
        }
    }
}
