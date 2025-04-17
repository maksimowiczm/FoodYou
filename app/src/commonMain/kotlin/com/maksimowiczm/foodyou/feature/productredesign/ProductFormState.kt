package com.maksimowiczm.foodyou.feature.productredesign

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
    val servingWeight: Input<ProductFormError> = input(),
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
}

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

    val NanOrBetween0and100 = Rule<ProductFormError> {
        { it.toFloatOrNull() } validates {
            when {
                it == null -> null
                it < 0f -> ProductFormError.NegativeNumber
                it > 100f -> ProductFormError.Exceeds100
                else -> null
            }
        }
    }

    val EmptyOrFloat = Rule<ProductFormError> {
        {
            when {
                it.isBlank() -> true
                else -> it.toFloatOrNull() != null
            }
        } checks { ProductFormError.NotANumber }
    }

    val PositiveFloat = Rule<ProductFormError> {
        { it.toFloatOrNull() } validates {
            when {
                it == null -> null
                it < 0f -> ProductFormError.NegativeNumber
                else -> null
            }
        }
    }
}

@Composable
internal fun Iterable<ProductFormError>.stringResource(): String {
    @Suppress("SimplifiableCallChain") // Can't call @Composable from lambda
    return map { it.stringResource() }.joinToString("\n")
}

@Composable
private fun ProductFormError.stringResource(): String = when (this) {
    ProductFormError.Empty -> stringResource(Res.string.error_this_field_is_required)
    ProductFormError.NotANumber -> stringResource(Res.string.error_invalid_number)
    ProductFormError.NegativeNumber -> stringResource(Res.string.error_value_cannot_be_negative)
    ProductFormError.Exceeds100 -> stringResource(Res.string.error_value_cannot_exceed_100)
}
