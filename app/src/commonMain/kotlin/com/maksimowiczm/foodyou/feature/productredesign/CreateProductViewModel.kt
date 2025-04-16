package com.maksimowiczm.foodyou.feature.productredesign

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.core.input.Form
import com.maksimowiczm.foodyou.core.input.ValidationStrategy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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
