package com.maksimowiczm.foodyou.feature.productredesign

import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.ui.res.Saver
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.core.ui.simpleform.FormField
import com.maksimowiczm.foodyou.core.ui.simpleform.ParseResult
import com.maksimowiczm.foodyou.core.ui.simpleform.rememberFormField
import com.maksimowiczm.foodyou.core.util.NutrientsHelper

@Composable
fun rememberProductFormState(): ProductFormState {
    val name = rememberFormField(
        initialValue = "",
        parser = { ParseResult.Success(it) },
        validator = { if (it.isBlank()) ProductFormFieldError.Required else null }
    )

    val brand = rememberFormField<String, ProductFormFieldError>(
        initialValue = "",
        parser = { ParseResult.Success(it) }
    )

    val barcode = rememberFormField<String, ProductFormFieldError>(
        initialValue = "",
        parser = { ParseResult.Success(it) }
    )

    val measurement = rememberSaveable(
        stateSaver = Measurement.Saver
    ) {
        mutableStateOf(Measurement.Gram(100f))
    }

    val packageWeight = rememberFormField(
        initialValue = null,
        parser = nullableFloatParser,
        validator = when {
            measurement.value is Measurement.Package -> valueRequiredValidator()
            else -> allowAllValidator()
        }
    )

    val servingWeight = rememberFormField(
        initialValue = null,
        parser = nullableFloatParser,
        validator = when {
            measurement.value is Measurement.Serving -> valueRequiredValidator()
            else -> allowAllValidator()
        }
    )

    val proteins = rememberRequiredFormField()
    val carbohydrates = rememberRequiredFormField()
    val fats = rememberRequiredFormField()
    val calories = rememberRequiredFormField()

    LaunchedEffect(proteins.value, carbohydrates.value, fats.value) {
        val proteins = proteins.value
        val carbohydrates = carbohydrates.value
        val fats = fats.value

        if (proteins == null || carbohydrates == null || fats == null) {
            return@LaunchedEffect
        }

        val kcal = NutrientsHelper.calculateCalories(proteins, carbohydrates, fats)
        val text = kcal.formatClipZeros()
        calories.textFieldState.setTextAndPlaceCursorAtEnd(text)
    }

    val saturatedFats = rememberNotRequiredFormField()
    val monounsaturatedFats = rememberNotRequiredFormField()
    val polyunsaturatedFats = rememberNotRequiredFormField()
    val omega3 = rememberNotRequiredFormField()
    val omega6 = rememberNotRequiredFormField()

    val sugars = rememberNotRequiredFormField()
    val salt = rememberNotRequiredFormField()
    val fiber = rememberNotRequiredFormField()
    val cholesterol = rememberNotRequiredFormField()
    val caffeine = rememberNotRequiredFormField()

    val vitaminA = rememberNotRequiredFormField()
    val vitaminB1 = rememberNotRequiredFormField()
    val vitaminB2 = rememberNotRequiredFormField()
    val vitaminB3 = rememberNotRequiredFormField()
    val vitaminB5 = rememberNotRequiredFormField()
    val vitaminB6 = rememberNotRequiredFormField()
    val vitaminB7 = rememberNotRequiredFormField()
    val vitaminB9 = rememberNotRequiredFormField()
    val vitaminB12 = rememberNotRequiredFormField()
    val vitaminC = rememberNotRequiredFormField()
    val vitaminD = rememberNotRequiredFormField()
    val vitaminE = rememberNotRequiredFormField()
    val vitaminK = rememberNotRequiredFormField()

    val manganese = rememberNotRequiredFormField()
    val magnesium = rememberNotRequiredFormField()
    val potassium = rememberNotRequiredFormField()
    val calcium = rememberNotRequiredFormField()
    val copper = rememberNotRequiredFormField()
    val zinc = rememberNotRequiredFormField()
    val sodium = rememberNotRequiredFormField()
    val iron = rememberNotRequiredFormField()
    val phosphorus = rememberNotRequiredFormField()
    val selenium = rememberNotRequiredFormField()
    val iodine = rememberNotRequiredFormField()

    return remember(
        name,
        brand,
        barcode,
        measurement,
        packageWeight,
        servingWeight,
        proteins,
        carbohydrates,
        fats,
        calories,
        saturatedFats,
        monounsaturatedFats,
        polyunsaturatedFats,
        omega3,
        omega6,
        sugars,
        salt,
        fiber,
        cholesterol,
        caffeine,
        vitaminA,
        vitaminB1,
        vitaminB2,
        vitaminB3,
        vitaminB5,
        vitaminB6,
        vitaminB7,
        vitaminB9,
        vitaminB12,
        vitaminC,
        vitaminD,
        vitaminE,
        vitaminK,
        manganese,
        magnesium,
        potassium,
        calcium,
        copper,
        zinc,
        sodium,
        iron,
        phosphorus,
        selenium,
        iodine
    ) {
        ProductFormState(
            name = name,
            brand = brand,
            barcode = barcode,
            measurementState = measurement,
            packageWeight = packageWeight,
            servingWeight = servingWeight,
            proteins = proteins,
            carbohydrates = carbohydrates,
            fats = fats,
            calories = calories,
            saturatedFats = saturatedFats,
            monounsaturatedFats = monounsaturatedFats,
            polyunsaturatedFats = polyunsaturatedFats,
            omega3 = omega3,
            omega6 = omega6,
            sugars = sugars,
            salt = salt,
            fiber = fiber,
            cholesterolMilli = cholesterol,
            caffeineMilli = caffeine,
            vitaminAMicro = vitaminA,
            vitaminB1Milli = vitaminB1,
            vitaminB2Milli = vitaminB2,
            vitaminB3Milli = vitaminB3,
            vitaminB5Milli = vitaminB5,
            vitaminB6Milli = vitaminB6,
            vitaminB7Micro = vitaminB7,
            vitaminB9Micro = vitaminB9,
            vitaminB12Micro = vitaminB12,
            vitaminCMilli = vitaminC,
            vitaminDMicro = vitaminD,
            vitaminEMilli = vitaminE,
            vitaminKMicro = vitaminK,
            manganeseMilli = manganese,
            magnesiumMilli = magnesium,
            potassiumMilli = potassium,
            calciumMilli = calcium,
            copperMilli = copper,
            zincMilli = zinc,
            sodiumMilli = sodium,
            ironMilli = iron,
            phosphorusMilli = phosphorus,
            seleniumMicro = selenium,
            iodineMicro = iodine
        )
    }
}

@Stable
class ProductFormState(
    // General
    val name: FormField<String, ProductFormFieldError>,
    val brand: FormField<String, ProductFormFieldError>,
    val barcode: FormField<String, ProductFormFieldError>,
    // Weight
    private val measurementState: MutableState<Measurement?>,
    val packageWeight: FormField<Float?, ProductFormFieldError>,
    val servingWeight: FormField<Float?, ProductFormFieldError>,
    // Macronutrients
    val proteins: FormField<Float?, ProductFormFieldError>,
    val carbohydrates: FormField<Float?, ProductFormFieldError>,
    val fats: FormField<Float?, ProductFormFieldError>,
    val calories: FormField<Float?, ProductFormFieldError>,
    // Fats
    val saturatedFats: FormField<Float?, ProductFormFieldError>,
    val monounsaturatedFats: FormField<Float?, ProductFormFieldError>,
    val polyunsaturatedFats: FormField<Float?, ProductFormFieldError>,
    val omega3: FormField<Float?, ProductFormFieldError>,
    val omega6: FormField<Float?, ProductFormFieldError>,
    // Other
    val sugars: FormField<Float?, ProductFormFieldError>,
    val salt: FormField<Float?, ProductFormFieldError>,
    val fiber: FormField<Float?, ProductFormFieldError>,
    val cholesterolMilli: FormField<Float?, ProductFormFieldError>,
    val caffeineMilli: FormField<Float?, ProductFormFieldError>,
    // Vitamins
    val vitaminAMicro: FormField<Float?, ProductFormFieldError>,
    val vitaminB1Milli: FormField<Float?, ProductFormFieldError>,
    val vitaminB2Milli: FormField<Float?, ProductFormFieldError>,
    val vitaminB3Milli: FormField<Float?, ProductFormFieldError>,
    val vitaminB5Milli: FormField<Float?, ProductFormFieldError>,
    val vitaminB6Milli: FormField<Float?, ProductFormFieldError>,
    val vitaminB7Micro: FormField<Float?, ProductFormFieldError>,
    val vitaminB9Micro: FormField<Float?, ProductFormFieldError>,
    val vitaminB12Micro: FormField<Float?, ProductFormFieldError>,
    val vitaminCMilli: FormField<Float?, ProductFormFieldError>,
    val vitaminDMicro: FormField<Float?, ProductFormFieldError>,
    val vitaminEMilli: FormField<Float?, ProductFormFieldError>,
    val vitaminKMicro: FormField<Float?, ProductFormFieldError>,
    // Minerals
    val manganeseMilli: FormField<Float?, ProductFormFieldError>,
    val magnesiumMilli: FormField<Float?, ProductFormFieldError>,
    val potassiumMilli: FormField<Float?, ProductFormFieldError>,
    val calciumMilli: FormField<Float?, ProductFormFieldError>,
    val copperMilli: FormField<Float?, ProductFormFieldError>,
    val zincMilli: FormField<Float?, ProductFormFieldError>,
    val sodiumMilli: FormField<Float?, ProductFormFieldError>,
    val ironMilli: FormField<Float?, ProductFormFieldError>,
    val phosphorusMilli: FormField<Float?, ProductFormFieldError>,
    val seleniumMicro: FormField<Float?, ProductFormFieldError>,
    val iodineMicro: FormField<Float?, ProductFormFieldError>
) {
    var measurement: Measurement
        get() = measurementState.value!!
        set(value) {
            measurementState.value = value
        }
}

private val nullableFloatParser: (String) -> ParseResult<Float?, ProductFormFieldError> = { input ->
    if (input.isBlank()) {
        ParseResult.Success(null)
    } else {
        val value = input.toFloatOrNull()
        if (value == null) {
            ParseResult.Failure(ProductFormFieldError.NotANumber)
        } else if (value <= 0) {
            ParseResult.Failure(ProductFormFieldError.MustBePositive)
        } else {
            ParseResult.Success(value)
        }
    }
}

private fun <T> valueRequiredValidator(): (T) -> ProductFormFieldError? =
    { if (it == null) ProductFormFieldError.Required else null }

private fun <T> allowAllValidator(): (T) -> ProductFormFieldError? = { null }

@Composable
private fun rememberNotRequiredFormField(initialValue: Float? = null) =
    rememberFormField<Float?, ProductFormFieldError>(
        initialValue = initialValue,
        parser = nullableFloatParser,
        validator = allowAllValidator()
    )

@Composable
private fun rememberRequiredFormField(initialValue: Float? = null) = rememberFormField(
    initialValue = initialValue,
    parser = nullableFloatParser,
    validator = valueRequiredValidator()
)
