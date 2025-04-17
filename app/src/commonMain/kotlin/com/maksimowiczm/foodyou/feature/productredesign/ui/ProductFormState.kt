package com.maksimowiczm.foodyou.feature.productredesign.ui

import androidx.compose.runtime.Composable
import com.maksimowiczm.foodyou.core.input.Input
import com.maksimowiczm.foodyou.core.input.Rule
import com.maksimowiczm.foodyou.core.input.dsl.checks
import com.maksimowiczm.foodyou.core.input.dsl.input
import com.maksimowiczm.foodyou.core.input.dsl.validates
import com.maksimowiczm.foodyou.core.util.NutrientsHelper
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

internal data class ProductFormState(
    val name: Input<ProductFormFieldError> = input(),
    val brand: Input<ProductFormFieldError> = input(),
    val barcode: Input<ProductFormFieldError> = input(),
    val proteins: Input<ProductFormFieldError> = input(),
    val carbohydrates: Input<ProductFormFieldError> = input(),
    val fats: Input<ProductFormFieldError> = input(),
    val sugars: Input<ProductFormFieldError> = input(),
    val saturatedFats: Input<ProductFormFieldError> = input(),
    val salt: Input<ProductFormFieldError> = input(),
    val sodium: Input<ProductFormFieldError> = input(),
    val fiber: Input<ProductFormFieldError> = input(),
    val packageWeight: Input<ProductFormFieldError> = input(),
    val servingWeight: Input<ProductFormFieldError> = input(),
    val isModified: Boolean = false
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

    val isValid = name.isValid &&
        !name.isEmptyValue &&
        brand.isValidOrEmpty &&
        barcode.isValidOrEmpty &&
        proteins.isValid &&
        carbohydrates.isValid &&
        fats.isValid &&
        sugars.isValidOrEmpty &&
        saturatedFats.isValidOrEmpty &&
        salt.isValidOrEmpty &&
        sodium.isValidOrEmpty &&
        fiber.isValidOrEmpty &&
        packageWeight.isValidOrEmpty &&
        servingWeight.isValidOrEmpty

    val error: ProductFormError?
        get() {
            val proteins = proteinsValue ?: return null
            val carbohydrates = carbohydratesValue ?: return null
            val fats = fatsValue ?: return null

            val total = proteins + carbohydrates + fats
            return if (total > 100f) {
                ProductFormError.MacronutrientsExceeds100
            } else {
                null
            }
        }
}

internal sealed interface ProductFormFieldError {
    object Empty : ProductFormFieldError
    object NotANumber : ProductFormFieldError
    object NegativeNumber : ProductFormFieldError
    object Exceeds100 : ProductFormFieldError
}

internal sealed interface ProductFormError {
    object MacronutrientsExceeds100 : ProductFormError
}

internal object ProductFormRules {
    val NotEmpty = Rule<ProductFormFieldError> {
        { it.isNotBlank() } checks { ProductFormFieldError.Empty }
    }

    val NanOrBetween0and100 = Rule<ProductFormFieldError> {
        { it.toFloatOrNull() } validates {
            when {
                it == null -> null
                it < 0f -> ProductFormFieldError.NegativeNumber
                it > 100f -> ProductFormFieldError.Exceeds100
                else -> null
            }
        }
    }

    val EmptyOrFloat = Rule<ProductFormFieldError> {
        {
            when {
                it.isBlank() -> true
                else -> it.toFloatOrNull() != null
            }
        } checks { ProductFormFieldError.NotANumber }
    }

    val PositiveFloat = Rule<ProductFormFieldError> {
        { it.toFloatOrNull() } validates {
            when {
                it == null -> null
                it < 0f -> ProductFormFieldError.NegativeNumber
                else -> null
            }
        }
    }
}

@Composable
internal fun Iterable<ProductFormFieldError>.stringResource(): String {
    @Suppress("SimplifiableCallChain") // Can't call @Composable from lambda
    return map { it.stringResource() }.joinToString("\n")
}

@Composable
private fun ProductFormFieldError.stringResource(): String = when (this) {
    ProductFormFieldError.Empty -> stringResource(Res.string.error_this_field_is_required)
    ProductFormFieldError.NotANumber -> stringResource(Res.string.error_invalid_number)
    ProductFormFieldError.NegativeNumber -> stringResource(
        Res.string.error_value_cannot_be_negative
    )
    ProductFormFieldError.Exceeds100 -> stringResource(Res.string.error_value_cannot_exceed_100)
}
