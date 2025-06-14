package com.maksimowiczm.foodyou.feature.product.ui

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.model.Product
import com.maksimowiczm.foodyou.core.model.Saver
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.core.ui.simpleform.FormField
import com.maksimowiczm.foodyou.core.ui.simpleform.ParseResult
import com.maksimowiczm.foodyou.core.ui.simpleform.rememberFormField
import com.maksimowiczm.foodyou.core.util.NutrientsHelper
import com.maksimowiczm.foodyou.feature.product.domain.RemoteProduct
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop

@Composable
internal fun rememberProductFormState(product: Product? = null): ProductFormState {
    val name = rememberFormField(
        initialValue = product?.name ?: "",
        parser = { ParseResult.Success(it) },
        validator = { if (it.isBlank()) ProductFormFieldError.Required else null },
        textFieldState = rememberTextFieldState(product?.name ?: "")
    )

    val brand = rememberFormField<String, ProductFormFieldError>(
        initialValue = product?.brand ?: "",
        parser = { ParseResult.Success(it) },
        textFieldState = rememberTextFieldState(product?.brand ?: "")
    )

    val barcode = rememberFormField<String, ProductFormFieldError>(
        initialValue = product?.barcode ?: "",
        parser = { ParseResult.Success(it) },
        textFieldState = rememberTextFieldState(product?.barcode ?: "")
    )

    var isLiquidState = rememberSaveable { mutableStateOf(product?.isLiquid ?: false) }

    val measurement = rememberSaveable(
        stateSaver = Measurement.Saver
    ) {
        mutableStateOf(Measurement.Gram(100f))
    }

    val packageWeight = rememberFormField(
        initialValue = product?.totalWeight,
        parser = nullableFloatParser,
        validator = when {
            measurement.value is Measurement.Package -> requirePositiveFloatValidator()
            else -> positiveFloatValidator()
        },
        textFieldState = rememberTextFieldState(
            product?.totalWeight?.formatClipZeros() ?: ""
        )
    )

    val servingWeight = rememberFormField(
        initialValue = product?.servingWeight,
        parser = nullableFloatParser,
        validator = when {
            measurement.value is Measurement.Serving -> requirePositiveFloatValidator()
            else -> positiveFloatValidator()
        },
        textFieldState = rememberTextFieldState(
            product?.servingWeight?.formatClipZeros() ?: ""
        )
    )

    val proteins = rememberRequiredFormField(product?.nutritionFacts?.proteins?.value)
    val carbohydrates = rememberRequiredFormField(product?.nutritionFacts?.carbohydrates?.value)
    val fats = rememberRequiredFormField(product?.nutritionFacts?.fats?.value)
    val calories = rememberRequiredFormField(product?.nutritionFacts?.calories?.value)

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

    val saturatedFats = rememberNotRequiredFormField(product?.nutritionFacts?.saturatedFats?.value)
    val monounsaturatedFats =
        rememberNotRequiredFormField(product?.nutritionFacts?.monounsaturatedFats?.value)
    val polyunsaturatedFats =
        rememberNotRequiredFormField(product?.nutritionFacts?.polyunsaturatedFats?.value)
    val omega3 = rememberNotRequiredFormField(product?.nutritionFacts?.omega3?.value)
    val omega6 = rememberNotRequiredFormField(product?.nutritionFacts?.omega6?.value)

    val sugars = rememberNotRequiredFormField(product?.nutritionFacts?.sugars?.value)
    val salt = rememberNotRequiredFormField(product?.nutritionFacts?.salt?.value)
    val fiber = rememberNotRequiredFormField(product?.nutritionFacts?.fiber?.value)
    val cholesterol = rememberNotRequiredFormField(product?.nutritionFacts?.cholesterolMilli?.value)
    val caffeine = rememberNotRequiredFormField(product?.nutritionFacts?.caffeineMilli?.value)

    val vitaminA = rememberNotRequiredFormField(product?.nutritionFacts?.vitaminAMicro?.value)
    val vitaminB1 = rememberNotRequiredFormField(product?.nutritionFacts?.vitaminB1Milli?.value)
    val vitaminB2 = rememberNotRequiredFormField(product?.nutritionFacts?.vitaminB2Milli?.value)
    val vitaminB3 = rememberNotRequiredFormField(product?.nutritionFacts?.vitaminB3Milli?.value)
    val vitaminB5 = rememberNotRequiredFormField(product?.nutritionFacts?.vitaminB5Milli?.value)
    val vitaminB6 = rememberNotRequiredFormField(product?.nutritionFacts?.vitaminB6Milli?.value)
    val vitaminB7 = rememberNotRequiredFormField(product?.nutritionFacts?.vitaminB7Micro?.value)
    val vitaminB9 = rememberNotRequiredFormField(product?.nutritionFacts?.vitaminB9Micro?.value)
    val vitaminB12 = rememberNotRequiredFormField(product?.nutritionFacts?.vitaminB12Micro?.value)
    val vitaminC = rememberNotRequiredFormField(product?.nutritionFacts?.vitaminCMilli?.value)
    val vitaminD = rememberNotRequiredFormField(product?.nutritionFacts?.vitaminDMicro?.value)
    val vitaminE = rememberNotRequiredFormField(product?.nutritionFacts?.vitaminEMilli?.value)
    val vitaminK = rememberNotRequiredFormField(product?.nutritionFacts?.vitaminKMicro?.value)

    val manganese = rememberNotRequiredFormField(product?.nutritionFacts?.manganeseMilli?.value)
    val magnesium = rememberNotRequiredFormField(product?.nutritionFacts?.magnesiumMilli?.value)
    val potassium = rememberNotRequiredFormField(product?.nutritionFacts?.potassiumMilli?.value)
    val calcium = rememberNotRequiredFormField(product?.nutritionFacts?.calciumMilli?.value)
    val copper = rememberNotRequiredFormField(product?.nutritionFacts?.copperMilli?.value)
    val zinc = rememberNotRequiredFormField(product?.nutritionFacts?.zincMilli?.value)
    val sodium = rememberNotRequiredFormField(product?.nutritionFacts?.sodiumMilli?.value)
    val iron = rememberNotRequiredFormField(product?.nutritionFacts?.ironMilli?.value)
    val phosphorus = rememberNotRequiredFormField(product?.nutritionFacts?.phosphorusMilli?.value)
    val selenium = rememberNotRequiredFormField(product?.nutritionFacts?.seleniumMicro?.value)
    val iodine = rememberNotRequiredFormField(product?.nutritionFacts?.iodineMicro?.value)
    val chromium = rememberNotRequiredFormField(product?.nutritionFacts?.chromiumMicro?.value)

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
            isLiquidState = isLiquidState,
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
            iodineMicro = iodine,
            chromiumMicro = chromium
        )
    }
}

@Composable
internal fun rememberProductFormState(product: RemoteProduct): ProductFormState {
    val name = rememberFormField(
        initialValue = product.name ?: "",
        parser = { ParseResult.Success(it) },
        validator = { if (it.isBlank()) ProductFormFieldError.Required else null },
        textFieldState = rememberTextFieldState(product.name ?: "")
    )

    val brand = rememberFormField<String, ProductFormFieldError>(
        initialValue = product.brand ?: "",
        parser = { ParseResult.Success(it) },
        textFieldState = rememberTextFieldState(product.brand ?: "")
    )

    val barcode = rememberFormField<String, ProductFormFieldError>(
        initialValue = product.barcode ?: "",
        parser = { ParseResult.Success(it) },
        textFieldState = rememberTextFieldState(product.barcode ?: "")
    )

    var isLiquidState = rememberSaveable { mutableStateOf(false) }

    val measurement = rememberSaveable(
        stateSaver = Measurement.Saver
    ) {
        mutableStateOf(Measurement.Gram(100f))
    }

    val packageWeight = rememberFormField(
        initialValue = product.packageWeight,
        parser = nullableFloatParser,
        validator = when {
            measurement.value is Measurement.Package -> requirePositiveFloatValidator()
            else -> positiveFloatValidator()
        },
        textFieldState = rememberTextFieldState(
            product.packageWeight?.formatClipZeros() ?: ""
        )
    )

    val servingWeight = rememberFormField(
        initialValue = product.servingWeight,
        parser = nullableFloatParser,
        validator = when {
            measurement.value is Measurement.Serving -> requirePositiveFloatValidator()
            else -> positiveFloatValidator()
        },
        textFieldState = rememberTextFieldState(
            product.servingWeight?.formatClipZeros() ?: ""
        )
    )

    val proteins = rememberRequiredFormField(product.nutritionFacts.proteins)
    val carbohydrates = rememberRequiredFormField(product.nutritionFacts.carbohydrates)
    val fats = rememberRequiredFormField(product.nutritionFacts.fats)
    val calories = rememberRequiredFormField(product.nutritionFacts.calories)

    LaunchedEffect(proteins, carbohydrates, fats) {
        combine(
            snapshotFlow { proteins.value },
            snapshotFlow { carbohydrates.value },
            snapshotFlow { fats.value }
        ) { it }.drop(1).collectLatest { (proteins, carbohydrates, fats) ->
            if (proteins == null || carbohydrates == null || fats == null) {
                return@collectLatest
            }

            val kcal = NutrientsHelper.calculateCalories(proteins, carbohydrates, fats)
            val text = kcal.formatClipZeros()
            calories.textFieldState.setTextAndPlaceCursorAtEnd(text)
        }
    }

    val saturatedFats = rememberNotRequiredFormField(product.nutritionFacts.saturatedFats)
    val monounsaturatedFats =
        rememberNotRequiredFormField(product.nutritionFacts.monounsaturatedFats)
    val polyunsaturatedFats =
        rememberNotRequiredFormField(product.nutritionFacts.polyunsaturatedFats)
    val omega3 = rememberNotRequiredFormField(product.nutritionFacts.omega3)
    val omega6 = rememberNotRequiredFormField(product.nutritionFacts.omega6)
    val sugars = rememberNotRequiredFormField(product.nutritionFacts.sugars)
    val salt = rememberNotRequiredFormField(product.nutritionFacts.salt)
    val fiber = rememberNotRequiredFormField(product.nutritionFacts.fiber)
    val cholesterol = rememberNotRequiredFormField(product.nutritionFacts.cholesterolMilli)
    val caffeine = rememberNotRequiredFormField(product.nutritionFacts.caffeineMilli)

    val vitaminA = rememberNotRequiredFormField(product.nutritionFacts.vitaminAMicro)
    val vitaminB1 = rememberNotRequiredFormField(product.nutritionFacts.vitaminB1Milli)
    val vitaminB2 = rememberNotRequiredFormField(product.nutritionFacts.vitaminB2Milli)
    val vitaminB3 = rememberNotRequiredFormField(product.nutritionFacts.vitaminB3Milli)
    val vitaminB5 = rememberNotRequiredFormField(product.nutritionFacts.vitaminB5Milli)
    val vitaminB6 = rememberNotRequiredFormField(product.nutritionFacts.vitaminB6Milli)
    val vitaminB7 = rememberNotRequiredFormField(product.nutritionFacts.vitaminB7Micro)
    val vitaminB9 = rememberNotRequiredFormField(product.nutritionFacts.vitaminB9Micro)
    val vitaminB12 = rememberNotRequiredFormField(product.nutritionFacts.vitaminB12Micro)
    val vitaminC = rememberNotRequiredFormField(product.nutritionFacts.vitaminCMilli)
    val vitaminD = rememberNotRequiredFormField(product.nutritionFacts.vitaminDMicro)
    val vitaminE = rememberNotRequiredFormField(product.nutritionFacts.vitaminEMilli)
    val vitaminK = rememberNotRequiredFormField(product.nutritionFacts.vitaminKMicro)

    val manganese = rememberNotRequiredFormField(product.nutritionFacts.manganeseMilli)
    val magnesium = rememberNotRequiredFormField(product.nutritionFacts.magnesiumMilli)
    val potassium = rememberNotRequiredFormField(product.nutritionFacts.potassiumMilli)
    val calcium = rememberNotRequiredFormField(product.nutritionFacts.calciumMilli)
    val copper = rememberNotRequiredFormField(product.nutritionFacts.copperMilli)
    val zinc = rememberNotRequiredFormField(product.nutritionFacts.zincMilli)
    val sodium = rememberNotRequiredFormField(product.nutritionFacts.sodiumMilli)
    val iron = rememberNotRequiredFormField(product.nutritionFacts.ironMilli)
    val phosphorus = rememberNotRequiredFormField(product.nutritionFacts.phosphorusMilli)
    val selenium = rememberNotRequiredFormField(product.nutritionFacts.seleniumMicro)
    val iodine = rememberNotRequiredFormField(product.nutritionFacts.iodineMicro)
    val chromium = rememberNotRequiredFormField(product.nutritionFacts.chromiumMicro)

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
            isLiquidState = isLiquidState,
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
            iodineMicro = iodine,
            chromiumMicro = chromium
        )
    }
}

@Stable
internal class ProductFormState(
    // General
    val name: FormField<String, ProductFormFieldError>,
    val brand: FormField<String, ProductFormFieldError>,
    val barcode: FormField<String, ProductFormFieldError>,
    // Weight
    private val isLiquidState: MutableState<Boolean>,
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
    val chromiumMicro: FormField<Float?, ProductFormFieldError>,
    val seleniumMicro: FormField<Float?, ProductFormFieldError>,
    val iodineMicro: FormField<Float?, ProductFormFieldError>
) {
    val isValid: Boolean
        get() = name.error == null &&
            brand.error == null &&
            barcode.error == null &&
            measurementState.value != null &&
            packageWeight.error == null &&
            servingWeight.error == null &&
            proteins.error == null &&
            carbohydrates.error == null &&
            fats.error == null &&
            calories.error == null &&
            saturatedFats.error == null &&
            monounsaturatedFats.error == null &&
            polyunsaturatedFats.error == null &&
            omega3.error == null &&
            omega6.error == null &&
            sugars.error == null &&
            salt.error == null &&
            fiber.error == null &&
            cholesterolMilli.error == null &&
            caffeineMilli.error == null &&
            vitaminAMicro.error == null &&
            vitaminB1Milli.error == null &&
            vitaminB2Milli.error == null &&
            vitaminB3Milli.error == null &&
            vitaminB5Milli.error == null &&
            vitaminB6Milli.error == null &&
            vitaminB7Micro.error == null &&
            vitaminB9Micro.error == null &&
            vitaminB12Micro.error == null &&
            vitaminCMilli.error == null &&
            vitaminDMicro.error == null &&
            vitaminEMilli.error == null &&
            vitaminKMicro.error == null &&
            manganeseMilli.error == null &&
            magnesiumMilli.error == null &&
            potassiumMilli.error == null &&
            calciumMilli.error == null &&
            copperMilli.error == null &&
            zincMilli.error == null &&
            sodiumMilli.error == null &&
            ironMilli.error == null &&
            phosphorusMilli.error == null &&
            seleniumMicro.error == null &&
            iodineMicro.error == null

    var isLiquid: Boolean by isLiquidState

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
        } else {
            ParseResult.Success(value)
        }
    }
}

private fun nonNegativeFloatValidator(): (Float?) -> ProductFormFieldError? = {
    when {
        it == null -> null
        it < 0f -> ProductFormFieldError.Negative
        else -> null
    }
}

private fun requireNonNegativeFloatValidator(): (Float?) -> ProductFormFieldError? = {
    when {
        it == null -> ProductFormFieldError.Required
        it < 0f -> ProductFormFieldError.Negative
        else -> null
    }
}

private fun positiveFloatValidator(): (Float?) -> ProductFormFieldError? = {
    when {
        it == null -> null
        it <= 0f -> ProductFormFieldError.NotPositive
        else -> null
    }
}

private fun requirePositiveFloatValidator(): (Float?) -> ProductFormFieldError? = {
    when {
        it == null -> ProductFormFieldError.Required
        it <= 0f -> ProductFormFieldError.NotPositive
        else -> null
    }
}

@Composable
private fun rememberNotRequiredFormField(initialValue: Float? = null) =
    rememberFormField<Float?, ProductFormFieldError>(
        initialValue = initialValue,
        parser = nullableFloatParser,
        validator = nonNegativeFloatValidator(),
        textFieldState = rememberTextFieldState(initialValue?.formatClipZeros("%.4f") ?: "")
    )

@Composable
private fun rememberRequiredFormField(initialValue: Float? = null) = rememberFormField(
    initialValue = initialValue,
    parser = nullableFloatParser,
    validator = requireNonNegativeFloatValidator(),
    textFieldState = rememberTextFieldState(initialValue?.formatClipZeros("%.4f") ?: "")
)
