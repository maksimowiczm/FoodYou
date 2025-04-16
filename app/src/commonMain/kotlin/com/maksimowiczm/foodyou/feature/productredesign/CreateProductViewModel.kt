package com.maksimowiczm.foodyou.feature.productredesign

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.core.input.Form
import com.maksimowiczm.foodyou.core.input.Input
import com.maksimowiczm.foodyou.core.input.Rule
import com.maksimowiczm.foodyou.core.input.ValidationStrategy
import com.maksimowiczm.foodyou.core.input.dsl.checks
import com.maksimowiczm.foodyou.core.input.dsl.input
import com.maksimowiczm.foodyou.core.input.dsl.validates
import com.maksimowiczm.foodyou.core.util.NutrientsHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal sealed interface ProductFormError {
    object Empty : ProductFormError
    object NotANumber : ProductFormError
    object NegativeNumber : ProductFormError
    object Exceeds100 : ProductFormError
}

internal object ProductFormRules {
    val NotEmpty = Rule<ProductFormError> {
        { it.isNotBlank() } checks { ProductFormError.Empty }
    }

    val between0and100 = Rule<ProductFormError> {
        { it.toFloatOrNull() } validates {
            when (it) {
                null -> ProductFormError.NotANumber
                else if (it < 0) -> ProductFormError.NegativeNumber
                else if (it > 100) -> ProductFormError.Exceeds100
                else -> null
            }
        }
    }

    val positiveFloat = Rule<ProductFormError> {
        { it.toFloatOrNull() } validates {
            when (it) {
                null -> ProductFormError.NotANumber
                else if (it < 0) -> ProductFormError.NegativeNumber
                else -> null
            }
        }
    }
}

internal data class ProductFormState(
    val name: Input<ProductFormError> = input(),
    val brand: Input<ProductFormError> = input(),
    val barcode: Input<ProductFormError> = input(),
    val proteins: Input<ProductFormError> = input(),
    val carbohydrates: Input<ProductFormError> = input(),
    val fats: Input<ProductFormError> = input(),
    val sugars: Input<ProductFormError> = input(),
    val saturatedFats: Input<ProductFormError> = input(),
    val salt: Input<ProductFormError> = input(),
    val sodium: Input<ProductFormError> = input(),
    val fiber: Input<ProductFormError> = input(),
    val packageWeight: Input<ProductFormError> = input(),
    val servingWeight: Input<ProductFormError> = input()
) {
    private val proteinsValue
        get() = proteins.value.toFloatOrNull()
    private val carbohydratesValue
        get() = carbohydrates.value.toFloatOrNull()
    private val fatsValue
        get() = fats.value.toFloatOrNull()

    val calories: Float?
        get() {
            val proteins = proteinsValue ?: return null
            val carbohydrates = carbohydratesValue ?: return null
            val fats = fatsValue ?: return null

            return NutrientsHelper.calculateCalories(proteins, carbohydrates, fats)
        }
}

internal class CreateProductViewModel : ViewModel() {
    private val _state = MutableStateFlow<ProductFormState>(ProductFormState())
    val state = _state.asStateFlow()

    private val nameForm = Form<ProductFormError>(
        ValidationStrategy.LazyEval,
        ProductFormRules.NotEmpty
    )

    fun onNameChange(name: String) {
        _state.update {
            it.copy(
                name = nameForm.validate(name)
            )
        }
    }

    private val brandForm = Form<ProductFormError>(ValidationStrategy.LazyEval)
    fun onBrandChange(brand: String) {
        _state.update {
            it.copy(
                brand = brandForm.validate(brand)
            )
        }
    }

    private val barcodeForm = Form<ProductFormError>(ValidationStrategy.LazyEval)
    fun onBarcodeChange(barcode: String) {
        _state.update {
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
        _state.update {
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
        _state.update {
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
        _state.update {
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
        _state.update {
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
        _state.update {
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
        _state.update {
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
        _state.update {
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
        _state.update {
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
        _state.update {
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
        _state.update {
            it.copy(
                servingWeight = servingWeightForm.validate(servingWeight)
            )
        }
    }
}
