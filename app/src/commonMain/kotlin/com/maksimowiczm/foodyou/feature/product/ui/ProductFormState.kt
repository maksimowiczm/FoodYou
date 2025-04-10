package com.maksimowiczm.foodyou.feature.product.ui

import androidx.compose.runtime.Composable
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import pro.respawn.kmmutils.inputforms.Input
import pro.respawn.kmmutils.inputforms.Rule
import pro.respawn.kmmutils.inputforms.ValidationError
import pro.respawn.kmmutils.inputforms.default.Rules
import pro.respawn.kmmutils.inputforms.dsl.checks

internal interface ProductFormState {
    val name: Input
    val brand: Input
    val barcode: Input

    val proteins: Input
    val carbohydrates: Input
    val fats: Input
    val calories: Float?

    val sugars: Input
    val saturatedFats: Input
    val salt: Input
    val sodium: Input
    val fiber: Input

    val packageWeight: Input
    val servingWeight: Input

    val isModified: Boolean
    val isValid: Boolean
    val error: ProductFormError?
}

internal sealed interface ProductFormError {
    data object MacronutrientsSumExceeds100 : ProductFormError

    @Composable
    fun stringResource() = when (this) {
        MacronutrientsSumExceeds100 -> stringResource(
            Res.string.error_sum_of_macronutrients_cannot_exceed_100g
        )
    }
}

internal class NotANumber(value: String) : ValidationError.Generic(value)
internal class NegativeNumber(value: String) : ValidationError.Generic(value)
internal class NonPositiveNumber(value: String) : ValidationError.Generic(value)
internal class Exceeds100(value: String) : ValidationError.Generic(value)

internal val Rules.FloatNumber
    get() = Rule {
        { it.isEmpty() || it.toFloatOrNull() != null } checks { NotANumber(it) }
    }

internal val Rules.NonNegativeNumber
    get() = Rule {
        {
            val f = it.toFloatOrNull()
            when (f) {
                null -> true
                else -> f >= 0
            }
        } checks { NegativeNumber(it) }
    }

internal val Rules.PositiveNumber
    get() = Rule {
        {
            val f = it.toFloatOrNull()
            when (f) {
                null -> true
                else -> f > 0
            }
        } checks { NonPositiveNumber(it) }
    }

internal val Rules.Exceeds100
    get() = Rule {
        {
            val f = it.toFloatOrNull()
            when (f) {
                null -> true
                else -> f <= 100
            }
        } checks { Exceeds100(it) }
    }

@Composable
internal fun Iterable<ValidationError>.stringResource(): String {
    @Suppress("SimplifiableCallChain") // Can't call @Composable from lambda
    return map { it.stringResource() }.joinToString("\n")
}

// Handle only errors used in view models
@Composable
internal fun ValidationError.stringResource() = when (this) {
    is ValidationError.ContainsDigits -> error("Not supported")
    is ValidationError.ContainsLetters -> error("Not supported")
    is ValidationError.DoesNotContain -> error("Not supported")
    is ValidationError.DoesNotEndWith -> error("Not supported")
    is ValidationError.DoesNotMatch -> error("Not supported")
    is ValidationError.DoesNotStartWith -> error("Not supported")
    is ValidationError.Empty -> "* " + stringResource(Res.string.neutral_required)
    is ValidationError.HasNoDigits -> error("Not supported")
    is ValidationError.HasNoLetters -> error("Not supported")
    is ValidationError.HasWhitespace -> error("Not supported")
    is ValidationError.IsNotEqual -> error("Not supported")
    is ValidationError.LengthIsNotExactly -> error("Not supported")
    is ValidationError.NoUppercaseLetters -> error("Not supported")
    is ValidationError.NotAlphaNumeric -> error("Not supported")
    is ValidationError.NotAscii -> error("Not supported")
    is ValidationError.NotDigitsOnly -> error("Not supported")
    is ValidationError.NotInRange -> error("Not supported")
    is ValidationError.NotLettersOnly -> error("Not supported")
    is ValidationError.NotLowercase -> error("Not supported")
    is ValidationError.NotSingleline -> error("Not supported")
    is ValidationError.NotUppercase -> error("Not supported")
    is ValidationError.TooLong -> error("Not supported")
    is ValidationError.TooShort -> error("Not supported")
    is ValidationError.Generic -> when (this) {
        is NotANumber -> stringResource(Res.string.error_invalid_number)
        is NegativeNumber -> stringResource(Res.string.error_value_cannot_be_negative)
        is NonPositiveNumber -> stringResource(Res.string.error_value_must_be_positive)
        is Exceeds100 -> stringResource(Res.string.error_value_cannot_exceed_100)
        else -> error("Not supported")
    }
}
