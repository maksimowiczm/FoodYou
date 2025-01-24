package com.maksimowiczm.foodyou.feature.product.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.feature.product.data.model.Product
import com.maksimowiczm.foodyou.feature.product.data.model.WeightUnit

@Composable
fun rememberProductFormState(
    name: String = "",
    nameError: FormFieldError? = null,
    brand: String? = null,
    brandError: FormFieldError? = null,
    barcode: String? = null,
    barcodeError: FormFieldError? = null,
    calories: Float = 0f,
    caloriesError: FormFieldError? = null,
    proteins: Float = 0f,
    proteinsError: FormFieldError? = null,
    carbohydrates: Float = 0f,
    carbohydratesError: FormFieldError? = null,
    sugars: Float? = null,
    sugarsError: FormFieldError? = null,
    fats: Float = 0f,
    fatsError: FormFieldError? = null,
    saturatedFats: Float? = null,
    saturatedFatsError: FormFieldError? = null,
    salt: Float? = null,
    saltError: FormFieldError? = null,
    sodium: Float? = null,
    sodiumError: FormFieldError? = null,
    fiber: Float? = null,
    fiberError: FormFieldError? = null,
    packageWeight: Float? = null,
    packageWeightError: FormFieldError? = null,
    servingWeight: Float? = null,
    servingWeightError: FormFieldError? = null,
    weightUnit: WeightUnit = WeightUnit.Gram
): ProductFormState = rememberSaveable(saver = ProductFormSaver) {
    ProductFormState(
        name,
        nameError,
        brand,
        brandError,
        barcode,
        barcodeError,
        calories,
        caloriesError,
        proteins,
        proteinsError,
        carbohydrates,
        carbohydratesError,
        sugars,
        sugarsError,
        fats,
        fatsError,
        saturatedFats,
        saturatedFatsError,
        salt,
        saltError,
        sodium,
        sodiumError,
        fiber,
        fiberError,
        packageWeight,
        packageWeightError,
        servingWeight,
        servingWeightError,
        weightUnit
    )
}

@Composable
fun rememberProductFormState(product: Product): ProductFormState = rememberProductFormState(
    name = product.name,
    brand = product.brand,
    barcode = product.barcode,
    calories = product.calories,
    proteins = product.proteins,
    carbohydrates = product.carbohydrates,
    sugars = product.sugars,
    fats = product.fats,
    saturatedFats = product.saturatedFats,
    salt = product.salt,
    sodium = product.sodium,
    fiber = product.fiber,
    packageWeight = product.packageWeight,
    servingWeight = product.servingWeight,
    weightUnit = product.weightUnit
)

@Composable
fun FormFieldError.stringResource(): String = when (this) {
    FormFieldError.Empty -> stringResource(R.string.error_this_field_cannot_be_empty)
    FormFieldError.Invalid -> stringResource(R.string.error_invalid)
    FormFieldError.InvalidNumber -> stringResource(R.string.error_invalid_number)
    FormFieldError.Negative -> stringResource(R.string.error_value_cannot_be_negative)
}

enum class FormFieldError {
    Empty,
    Invalid,
    InvalidNumber,
    Negative
}

sealed interface ParserResult<T> {
    data class Success<T>(val value: T) : ParserResult<T>
    data class Error<T>(val error: FormFieldError) : ParserResult<T>
}

fun interface Validator<T> {
    operator fun invoke(value: T): ParserResult<T>
}

fun interface Parser<T> {
    operator fun invoke(value: String): ParserResult<T>
}

class FormField<T>(
    private var dirty: Boolean,
    initialValue: T,
    initialError: FormFieldError?,
    private val parser: Parser<T>,
    private vararg val validators: Validator<T>
) {
    var value by mutableStateOf(initialValue)
        private set

    var error by mutableStateOf<FormFieldError?>(initialError)
        private set

    val isValid by derivedStateOf {
        error == null && validate(value) is ParserResult.Success
    }

    private fun validate(value: T): ParserResult<T> = validators
        .map { it(value) }
        .firstOrNull { it is ParserResult.Error }
        ?: ParserResult.Success(value)

    fun onValueChange(newValue: String) {
        dirty = true

        when (val parsed = parser(newValue)) {
            is ParserResult.Success -> {
                error = validate(parsed.value).let {
                    when (it) {
                        is ParserResult.Error -> it.error
                        is ParserResult.Success -> null
                    }
                }

                if (error == null) {
                    value = parsed.value
                }
            }

            is ParserResult.Error -> error = parsed.error
        }
    }

    private var hadFocus by mutableStateOf(false)

    fun onEnterFocus() {
        hadFocus = true
    }

    fun onExitFocus(value: String) {
        if (hadFocus) {
            onValueChange(value)
        }
    }

    override fun toString(): String = if (dirty) {
        value.toString()
    } else {
        ""
    }
}

private object Parsers {
    val stringNotBlank = Parser {
        if (it.isBlank()) {
            ParserResult.Error(FormFieldError.Empty)
        } else {
            ParserResult.Success(it)
        }
    }

    val string = Parser {
        if (it.isBlank()) {
            ParserResult.Success(null)
        } else {
            ParserResult.Success(it)
        }
    }

    val float = Parser {
        if (it.isBlank()) {
            ParserResult.Error(FormFieldError.Empty)
        } else {
            val value = it.toFloatOrNull()
            if (value == null) {
                ParserResult.Error(FormFieldError.InvalidNumber)
            } else {
                ParserResult.Success(value)
            }
        }
    }

    val nullableFloat = Parser {
        if (it.isBlank()) {
            ParserResult.Success(null)
        } else {
            val value = it.toFloatOrNull()
            if (value == null) {
                ParserResult.Error(FormFieldError.InvalidNumber)
            } else {
                ParserResult.Success(value)
            }
        }
    }
}

private object Validators {
    val nonBlankAllowNull = Validator<String?> {
        if (it == null || it.isNotBlank()) {
            ParserResult.Success(it)
        } else {
            ParserResult.Error(FormFieldError.Empty)
        }
    }

    val nonBlank = Validator<String> {
        if (it.isNotBlank()) {
            ParserResult.Success(it)
        } else {
            ParserResult.Error(FormFieldError.Empty)
        }
    }

    val blankOrNumbersOnly = Validator<String?> {
        if (it.isNullOrBlank() || it.all { char -> char.isDigit() }) {
            ParserResult.Success(it)
        } else {
            ParserResult.Error(FormFieldError.Invalid)
        }
    }

    val nonNegativeFloat = Validator<Float> {
        if (it >= 0f) {
            ParserResult.Success(it)
        } else {
            ParserResult.Error(FormFieldError.Negative)
        }
    }

    val nonNegativeFloatOrNull = Validator<Float?> {
        if (it == null || it >= 0f) {
            ParserResult.Success(it)
        } else {
            ParserResult.Error(FormFieldError.Negative)
        }
    }
}

class ProductFormState(
    name: String,
    nameError: FormFieldError?,
    brand: String?,
    brandError: FormFieldError?,
    barcode: String?,
    barcodeError: FormFieldError?,
    calories: Float,
    caloriesError: FormFieldError?,
    proteins: Float,
    proteinsError: FormFieldError?,
    carbohydrates: Float,
    carbohydratesError: FormFieldError?,
    sugars: Float?,
    sugarsError: FormFieldError?,
    fats: Float,
    fatsError: FormFieldError?,
    saturatedFats: Float?,
    saturatedFatsError: FormFieldError?,
    salt: Float?,
    saltError: FormFieldError?,
    sodium: Float?,
    sodiumError: FormFieldError?,
    fiber: Float?,
    fiberError: FormFieldError?,
    packageWeight: Float?,
    packageWeightError: FormFieldError?,
    servingWeight: Float?,
    servingWeightError: FormFieldError?,
    weightUnit: WeightUnit
) {
    val name = FormField(
        dirty = name.isNotBlank(),
        initialValue = name,
        initialError = nameError,
        parser = Parsers.stringNotBlank,
        Validators.nonBlank
    )

    val brand = FormField(
        dirty = brand != null,
        initialValue = brand,
        initialError = brandError,
        parser = Parsers.string,
        Validators.nonBlankAllowNull
    )

    val barcode = FormField(
        dirty = barcode != null,
        initialValue = barcode,
        initialError = barcodeError,
        parser = Parsers.string,
        Validators.blankOrNumbersOnly
    )

    val calories = FormField(
        dirty = calories != 0f,
        initialValue = calories,
        initialError = caloriesError,
        parser = Parsers.float,
        Validators.nonNegativeFloat
    )

    val proteins = FormField(
        dirty = proteins != 0f,
        initialValue = proteins,
        initialError = proteinsError,
        parser = Parsers.float,
        Validators.nonNegativeFloat
    )

    val carbohydrates = FormField(
        dirty = carbohydrates != 0f,
        initialValue = carbohydrates,
        initialError = carbohydratesError,
        parser = Parsers.float,
        Validators.nonNegativeFloat
    )

    val sugars = FormField(
        dirty = sugars != null,
        initialValue = sugars,
        initialError = sugarsError,
        parser = Parsers.nullableFloat,
        Validators.nonNegativeFloatOrNull
    )

    val fats = FormField(
        dirty = fats != 0f,
        initialValue = fats,
        initialError = fatsError,
        parser = Parsers.float,
        Validators.nonNegativeFloat
    )

    val saturatedFats = FormField(
        dirty = saturatedFats != null,
        initialValue = saturatedFats,
        initialError = saturatedFatsError,
        parser = Parsers.nullableFloat,
        Validators.nonNegativeFloatOrNull
    )

    val salt = FormField(
        dirty = salt != null,
        initialValue = salt,
        initialError = saltError,
        parser = Parsers.nullableFloat,
        Validators.nonNegativeFloatOrNull
    )

    val sodium = FormField(
        dirty = sodium != null,
        initialValue = sodium,
        initialError = sodiumError,
        parser = Parsers.nullableFloat,
        Validators.nonNegativeFloatOrNull
    )

    val fiber = FormField(
        dirty = fiber != null,
        initialValue = fiber,
        initialError = fiberError,
        parser = Parsers.nullableFloat,
        Validators.nonNegativeFloatOrNull
    )

    val packageWeight = FormField(
        dirty = packageWeight != null,
        initialValue = packageWeight,
        initialError = packageWeightError,
        parser = Parsers.nullableFloat,
        Validators.nonNegativeFloatOrNull
    )

    val servingWeight = FormField(
        dirty = servingWeight != null,
        initialValue = servingWeight,
        initialError = servingWeightError,
        parser = Parsers.nullableFloat,
        Validators.nonNegativeFloatOrNull
    )

    var weightUnit by mutableStateOf(weightUnit)
        private set

    fun onWeightUnitChange(value: WeightUnit) {
        weightUnit = value
    }

    val isValid by derivedStateOf {
        this.name.isValid &&
            this.brand.isValid &&
            this.barcode.isValid &&
            this.calories.isValid &&
            this.proteins.isValid &&
            this.carbohydrates.isValid &&
            this.sugars.isValid &&
            this.fats.isValid &&
            this.saturatedFats.isValid &&
            this.salt.isValid &&
            this.sodium.isValid &&
            this.packageWeight.isValid &&
            this.servingWeight.isValid
    }
}

private val ProductFormSaver = Saver<ProductFormState, ArrayList<Any?>>(
    save = { state ->
        arrayListOf(
            state.name.value,
            state.name.error,
            state.brand.value,
            state.brand.error,
            state.barcode.value,
            state.barcode.error,
            state.calories.value,
            state.calories.error,
            state.proteins.value,
            state.proteins.error,
            state.carbohydrates.value,
            state.carbohydrates.error,
            state.sugars.value,
            state.sugars.error,
            state.fats.value,
            state.fats.error,
            state.saturatedFats.value,
            state.saturatedFats.error,
            state.salt.value,
            state.salt.error,
            state.sodium.value,
            state.sodium.error,
            state.fiber.value,
            state.fiber.error,
            state.packageWeight.value,
            state.packageWeight.error,
            state.servingWeight.value,
            state.servingWeight.error,
            state.weightUnit
        )
    },
    restore = { values ->
        ProductFormState(
            name = values[0] as String,
            nameError = values[1] as FormFieldError?,
            brand = values[2] as String?,
            brandError = values[3] as FormFieldError?,
            barcode = values[4] as String?,
            barcodeError = values[5] as FormFieldError?,
            calories = values[6] as Float,
            caloriesError = values[7] as FormFieldError?,
            proteins = values[8] as Float,
            proteinsError = values[9] as FormFieldError?,
            carbohydrates = values[10] as Float,
            carbohydratesError = values[11] as FormFieldError?,
            sugars = values[12] as Float?,
            sugarsError = values[13] as FormFieldError?,
            fats = values[14] as Float,
            fatsError = values[15] as FormFieldError?,
            saturatedFats = values[16] as Float?,
            saturatedFatsError = values[17] as FormFieldError?,
            salt = values[18] as Float?,
            saltError = values[19] as FormFieldError?,
            sodium = values[20] as Float?,
            sodiumError = values[21] as FormFieldError?,
            fiber = values[22] as Float?,
            fiberError = values[23] as FormFieldError?,
            packageWeight = values[24] as Float?,
            packageWeightError = values[25] as FormFieldError?,
            servingWeight = values[26] as Float?,
            servingWeightError = values[27] as FormFieldError?,
            weightUnit = values[28] as WeightUnit
        )
    }
)
