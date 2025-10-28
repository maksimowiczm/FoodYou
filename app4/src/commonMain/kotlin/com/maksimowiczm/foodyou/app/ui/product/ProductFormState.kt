package com.maksimowiczm.foodyou.app.ui.product

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.account.domain.EnergyFormat
import com.maksimowiczm.foodyou.app.ui.common.form.FormField
import com.maksimowiczm.foodyou.app.ui.common.form.Parser
import com.maksimowiczm.foodyou.app.ui.common.form.nonBlankStringValidator
import com.maksimowiczm.foodyou.app.ui.common.form.nonNegativeDoubleValidator
import com.maksimowiczm.foodyou.app.ui.common.form.nullableDoubleParser
import com.maksimowiczm.foodyou.app.ui.common.form.nullableStringParser
import com.maksimowiczm.foodyou.app.ui.common.form.numericStringValidator
import com.maksimowiczm.foodyou.app.ui.common.form.stringParser
import com.maksimowiczm.foodyou.food.domain.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.food.domain.NutritionFacts
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

enum class FormFieldError {
    NotANumber,
    NotABarcode,
    NegativeValue,
    Required;

    @Composable
    fun stringResource(): String {
        return when (this) {
            NotANumber -> stringResource(Res.string.error_invalid_number)
            NotABarcode -> stringResource(Res.string.error_not_a_barcode)
            NegativeValue -> stringResource(Res.string.error_value_cannot_be_negative)
            Required -> "* " + stringResource(Res.string.neutral_required)
        }
    }
}

enum class ValuesPer {
    Grams100,
    Milliliters100,
    Serving,
    Package;

    @Composable
    fun stringResource(): String =
        when (this) {
            Grams100 -> "100 " + stringResource(Res.string.unit_gram_short)
            Milliliters100 -> "100 " + stringResource(Res.string.unit_milliliter_short)
            Serving ->
                stringResource(
                    Res.string.x_times_y,
                    "1",
                    stringResource(Res.string.product_serving),
                )

            Package ->
                stringResource(
                    Res.string.x_times_y,
                    "1",
                    stringResource(Res.string.product_package),
                )
        }
}

enum class QuantityUnit {
    Gram,
    Milliliter,
    Ounce,
    FluidOunce,
}

@Stable
data class ProductFormState(
    val name: FormField<String, FormFieldError> =
        FormField(
            parser = stringParser(),
            validator = nonBlankStringValidator(onEmpty = { FormFieldError.Required }),
        ),
    val defaultName: String = "",
    val brand: FormField<String?, Unit> = FormField(parser = nullableStringParser()),
    val defaultBrand: String? = null,
    val barcode: FormField<String?, FormFieldError> =
        FormField(
            parser = nullableStringParser(),
            validator = numericStringValidator(onNotNumeric = { FormFieldError.NotABarcode }),
        ),
    val defaultBarcode: String? = null,
    val note: FormField<String?, Unit> = FormField(parser = nullableStringParser()),
    val defaultNote: String? = null,
    val source: FormField<String?, Unit> = FormField(parser = nullableStringParser()),
    val defaultSource: String? = null,
    val proteins: FormField<Double?, FormFieldError> = optionalField(),
    val defaultProteins: Double? = null,
    val fats: FormField<Double?, FormFieldError> = optionalField(),
    val defaultFats: Double? = null,
    val carbohydrates: FormField<Double?, FormFieldError> = optionalField(),
    val defaultCarbohydrates: Double? = null,
    val energy: FormField<Double?, FormFieldError> = optionalField(),
    val defaultEnergy: Double? = null,
    val saturatedFats: FormField<Double?, FormFieldError> = optionalField(),
    val defaultSaturatedFats: Double? = null,
    val transFats: FormField<Double?, FormFieldError> = optionalField(),
    val defaultTransFats: Double? = null,
    val monounsaturatedFats: FormField<Double?, FormFieldError> = optionalField(),
    val defaultMonounsaturatedFats: Double? = null,
    val polyunsaturatedFats: FormField<Double?, FormFieldError> = optionalField(),
    val defaultPolyunsaturatedFats: Double? = null,
    val omega3: FormField<Double?, FormFieldError> = optionalField(),
    val defaultOmega3: Double? = null,
    val omega6: FormField<Double?, FormFieldError> = optionalField(),
    val defaultOmega6: Double? = null,
    val sugars: FormField<Double?, FormFieldError> = optionalField(),
    val defaultSugars: Double? = null,
    val addedSugars: FormField<Double?, FormFieldError> = optionalField(),
    val defaultAddedSugars: Double? = null,
    val dietaryFiber: FormField<Double?, FormFieldError> = optionalField(),
    val defaultDietaryFiber: Double? = null,
    val solubleFiber: FormField<Double?, FormFieldError> = optionalField(),
    val defaultSolubleFiber: Double? = null,
    val insolubleFiber: FormField<Double?, FormFieldError> = optionalField(),
    val defaultInsolubleFiber: Double? = null,
    val salt: FormField<Double?, FormFieldError> = optionalField(),
    val defaultSalt: Double? = null,
    val cholesterolMilli: FormField<Double?, FormFieldError> = optionalField(),
    val defaultCholesterolMilli: Double? = null,
    val caffeineMilli: FormField<Double?, FormFieldError> = optionalField(),
    val defaultCaffeineMilli: Double? = null,
    val vitaminAMicro: FormField<Double?, FormFieldError> = optionalField(),
    val defaultVitaminAMicro: Double? = null,
    val vitaminB1Milli: FormField<Double?, FormFieldError> = optionalField(),
    val defaultVitaminB1Milli: Double? = null,
    val vitaminB2Milli: FormField<Double?, FormFieldError> = optionalField(),
    val defaultVitaminB2Milli: Double? = null,
    val vitaminB3Milli: FormField<Double?, FormFieldError> = optionalField(),
    val defaultVitaminB3Milli: Double? = null,
    val vitaminB5Milli: FormField<Double?, FormFieldError> = optionalField(),
    val defaultVitaminB5Milli: Double? = null,
    val vitaminB6Milli: FormField<Double?, FormFieldError> = optionalField(),
    val defaultVitaminB6Milli: Double? = null,
    val vitaminB7Micro: FormField<Double?, FormFieldError> = optionalField(),
    val defaultVitaminB7Micro: Double? = null,
    val vitaminB9Micro: FormField<Double?, FormFieldError> = optionalField(),
    val defaultVitaminB9Micro: Double? = null,
    val vitaminB12Micro: FormField<Double?, FormFieldError> = optionalField(),
    val defaultVitaminB12Micro: Double? = null,
    val vitaminCMilli: FormField<Double?, FormFieldError> = optionalField(),
    val defaultVitaminCMilli: Double? = null,
    val vitaminDMicro: FormField<Double?, FormFieldError> = optionalField(),
    val defaultVitaminDMicro: Double? = null,
    val vitaminEMilli: FormField<Double?, FormFieldError> = optionalField(),
    val defaultVitaminEMilli: Double? = null,
    val vitaminKMicro: FormField<Double?, FormFieldError> = optionalField(),
    val defaultVitaminKMicro: Double? = null,
    val manganeseMilli: FormField<Double?, FormFieldError> = optionalField(),
    val defaultManganeseMilli: Double? = null,
    val magnesiumMilli: FormField<Double?, FormFieldError> = optionalField(),
    val defaultMagnesiumMilli: Double? = null,
    val potassiumMilli: FormField<Double?, FormFieldError> = optionalField(),
    val defaultPotassiumMilli: Double? = null,
    val calciumMilli: FormField<Double?, FormFieldError> = optionalField(),
    val defaultCalciumMilli: Double? = null,
    val copperMilli: FormField<Double?, FormFieldError> = optionalField(),
    val defaultCopperMilli: Double? = null,
    val zincMilli: FormField<Double?, FormFieldError> = optionalField(),
    val defaultZincMilli: Double? = null,
    val sodiumMilli: FormField<Double?, FormFieldError> = optionalField(),
    val defaultSodiumMilli: Double? = null,
    val ironMilli: FormField<Double?, FormFieldError> = optionalField(),
    val defaultIronMilli: Double? = null,
    val phosphorusMilli: FormField<Double?, FormFieldError> = optionalField(),
    val defaultPhosphorusMilli: Double? = null,
    val seleniumMicro: FormField<Double?, FormFieldError> = optionalField(),
    val defaultSeleniumMicro: Double? = null,
    val iodineMicro: FormField<Double?, FormFieldError> = optionalField(),
    val defaultIodineMicro: Double? = null,
    val chromiumMicro: FormField<Double?, FormFieldError> = optionalField(),
    val defaultChromiumMicro: Double? = null,
    val imageUri: String? = null,
    val defaultImageUri: String? = null,
    val valuesPer: ValuesPer = defaultValuesPer,
    val servingQuantity: FormField<Double?, FormFieldError> = optionalField(),
    val defaultServingQuantity: Double? = null,
    val servingUnit: QuantityUnit = QuantityUnit.Gram,
    val defaultServingUnit: QuantityUnit = QuantityUnit.Gram,
    val packageQuantity: FormField<Double?, FormFieldError> = optionalField(),
    val defaultPackageQuantity: Double? = null,
    val packageUnit: QuantityUnit = QuantityUnit.Gram,
    val defaultPackageUnit: QuantityUnit = QuantityUnit.Gram,
) {
    private val fieldsWithDefaults =
        listOf(
            name to defaultName,
            brand to defaultBrand,
            barcode to defaultBarcode,
            note to defaultNote,
            source to defaultSource,
            proteins to defaultProteins,
            fats to defaultFats,
            carbohydrates to defaultCarbohydrates,
            energy to defaultEnergy,
            saturatedFats to defaultSaturatedFats,
            transFats to defaultTransFats,
            monounsaturatedFats to defaultMonounsaturatedFats,
            polyunsaturatedFats to defaultPolyunsaturatedFats,
            omega3 to defaultOmega3,
            omega6 to defaultOmega6,
            sugars to defaultSugars,
            addedSugars to defaultAddedSugars,
            dietaryFiber to defaultDietaryFiber,
            solubleFiber to defaultSolubleFiber,
            insolubleFiber to defaultInsolubleFiber,
            salt to defaultSalt,
            cholesterolMilli to defaultCholesterolMilli,
            caffeineMilli to defaultCaffeineMilli,
            vitaminAMicro to defaultVitaminAMicro,
            vitaminB1Milli to defaultVitaminB1Milli,
            vitaminB2Milli to defaultVitaminB2Milli,
            vitaminB3Milli to defaultVitaminB3Milli,
            vitaminB5Milli to defaultVitaminB5Milli,
            vitaminB6Milli to defaultVitaminB6Milli,
            vitaminB7Micro to defaultVitaminB7Micro,
            vitaminB9Micro to defaultVitaminB9Micro,
            vitaminB12Micro to defaultVitaminB12Micro,
            vitaminCMilli to defaultVitaminCMilli,
            vitaminDMicro to defaultVitaminDMicro,
            vitaminEMilli to defaultVitaminEMilli,
            vitaminKMicro to defaultVitaminKMicro,
            manganeseMilli to defaultManganeseMilli,
            magnesiumMilli to defaultMagnesiumMilli,
            potassiumMilli to defaultPotassiumMilli,
            calciumMilli to defaultCalciumMilli,
            copperMilli to defaultCopperMilli,
            zincMilli to defaultZincMilli,
            sodiumMilli to defaultSodiumMilli,
            ironMilli to defaultIronMilli,
            phosphorusMilli to defaultPhosphorusMilli,
            seleniumMicro to defaultSeleniumMicro,
            iodineMicro to defaultIodineMicro,
            chromiumMicro to defaultChromiumMicro,
            servingQuantity to defaultServingQuantity,
            packageQuantity to defaultPackageQuantity,
        )

    private val fields = fieldsWithDefaults.map { it.first }

    val isValid by derivedStateOf { fields.all { it.isValid } }

    val isModified by derivedStateOf {
        fieldsWithDefaults.any { (field, defaultValue) -> field.value != defaultValue } ||
            imageUri != defaultImageUri ||
            valuesPer != defaultValuesPer ||
            servingUnit != defaultServingUnit ||
            packageUnit != defaultPackageUnit
    }

    companion object {
        private val defaultValuesPer = ValuesPer.Grams100

        fun optionalField(
            textFieldState: TextFieldState = TextFieldState(),
            parser: Parser<Double?, FormFieldError> =
                nullableDoubleParser(onNotANumber = { FormFieldError.NotANumber }),
            validator: (Double?) -> FormFieldError? =
                nonNegativeDoubleValidator(onNegative = { FormFieldError.NegativeValue }),
        ): FormField<Double?, FormFieldError> =
            FormField(textFieldState = textFieldState, parser = parser, validator = validator)

        fun requiredField(
            textFieldState: TextFieldState = TextFieldState(),
            parser: Parser<Double?, FormFieldError> =
                nullableDoubleParser(onNotANumber = { FormFieldError.NotANumber }),
            validator: (Double?) -> FormFieldError? =
                nonNegativeDoubleValidator(
                    onNegative = { FormFieldError.NegativeValue },
                    onNull = { FormFieldError.Required },
                ),
        ): FormField<Double?, FormFieldError> =
            FormField(textFieldState = textFieldState, parser = parser, validator = validator)
    }
}

fun ProductFormState.toNutritionFacts(
    multiplier: Double,
    energyFormat: EnergyFormat,
): NutritionFacts {
    // Energy MUST be in kilocalories internally
    val kcal =
        when (energyFormat) {
            EnergyFormat.Kilocalories -> energy.value
            EnergyFormat.Kilojoules -> energy.value?.let { it / 4.184 }
        }

    return NutritionFacts.requireAll(
        proteins = proteins.value?.multiplier(multiplier).toNutrientValue(),
        carbohydrates = carbohydrates.value?.multiplier(multiplier).toNutrientValue(),
        fats = fats.value?.multiplier(multiplier).toNutrientValue(),
        energy = kcal?.multiplier(multiplier).toNutrientValue(),
        saturatedFats = saturatedFats.value?.multiplier(multiplier).toNutrientValue(),
        transFats = transFats.value?.multiplier(multiplier).toNutrientValue(),
        monounsaturatedFats = monounsaturatedFats.value?.multiplier(multiplier).toNutrientValue(),
        polyunsaturatedFats = polyunsaturatedFats.value?.multiplier(multiplier).toNutrientValue(),
        omega3 = omega3.value?.multiplier(multiplier).toNutrientValue(),
        omega6 = omega6.value?.multiplier(multiplier).toNutrientValue(),
        sugars = sugars.value?.multiplier(multiplier).toNutrientValue(),
        addedSugars = addedSugars.value?.multiplier(multiplier).toNutrientValue(),
        dietaryFiber = dietaryFiber.value?.multiplier(multiplier).toNutrientValue(),
        solubleFiber = solubleFiber.value?.multiplier(multiplier).toNutrientValue(),
        insolubleFiber = insolubleFiber.value?.multiplier(multiplier).toNutrientValue(),
        salt = salt.value?.multiplier(multiplier).toNutrientValue(),
        cholesterol =
            (cholesterolMilli.value?.multiplier(multiplier)?.div(1_000)).toNutrientValue(),
        caffeine = (caffeineMilli.value?.multiplier(multiplier)?.div(1_000)).toNutrientValue(),
        vitaminA = (vitaminAMicro.value?.multiplier(multiplier)?.div(1_000_000)).toNutrientValue(),
        vitaminB1 = (vitaminB1Milli.value?.multiplier(multiplier)?.div(1_000)).toNutrientValue(),
        vitaminB2 = (vitaminB2Milli.value?.multiplier(multiplier)?.div(1_000)).toNutrientValue(),
        vitaminB3 = (vitaminB3Milli.value?.multiplier(multiplier)?.div(1_000)).toNutrientValue(),
        vitaminB5 = (vitaminB5Milli.value?.multiplier(multiplier)?.div(1_000)).toNutrientValue(),
        vitaminB6 = (vitaminB6Milli.value?.multiplier(multiplier)?.div(1_000)).toNutrientValue(),
        vitaminB7 =
            (vitaminB7Micro.value?.multiplier(multiplier)?.div(1_000_000)).toNutrientValue(),
        vitaminB9 =
            (vitaminB9Micro.value?.multiplier(multiplier)?.div(1_000_000)).toNutrientValue(),
        vitaminB12 =
            (vitaminB12Micro.value?.multiplier(multiplier)?.div(1_000_000)).toNutrientValue(),
        vitaminC = (vitaminCMilli.value?.multiplier(multiplier)?.div(1_000)).toNutrientValue(),
        vitaminD = (vitaminDMicro.value?.multiplier(multiplier)?.div(1_000_000)).toNutrientValue(),
        vitaminE = (vitaminEMilli.value?.multiplier(multiplier)?.div(1_000)).toNutrientValue(),
        vitaminK = (vitaminKMicro.value?.multiplier(multiplier)?.div(1_000_000)).toNutrientValue(),
        manganese = (manganeseMilli.value?.multiplier(multiplier)?.div(1_000)).toNutrientValue(),
        magnesium = (magnesiumMilli.value?.multiplier(multiplier)?.div(1_000)).toNutrientValue(),
        potassium = (potassiumMilli.value?.multiplier(multiplier)?.div(1_000)).toNutrientValue(),
        calcium = (calciumMilli.value?.multiplier(multiplier)?.div(1_000)).toNutrientValue(),
        copper = (copperMilli.value?.multiplier(multiplier)?.div(1_000)).toNutrientValue(),
        zinc = (zincMilli.value?.multiplier(multiplier)?.div(1_000)).toNutrientValue(),
        sodium = (sodiumMilli.value?.multiplier(multiplier)?.div(1_000)).toNutrientValue(),
        iron = (ironMilli.value?.multiplier(multiplier)?.div(1_000)).toNutrientValue(),
        phosphorus = (phosphorusMilli.value?.multiplier(multiplier)?.div(1_000)).toNutrientValue(),
        selenium = (seleniumMicro.value?.multiplier(multiplier)?.div(1_000_000)).toNutrientValue(),
        iodine = (iodineMicro.value?.multiplier(multiplier)?.div(1_000_000)).toNutrientValue(),
        chromium = (chromiumMicro.value?.multiplier(multiplier)?.div(1_000_000)).toNutrientValue(),
    )
}

private fun Double.multiplier(multiplier: Double): Double? = times(multiplier)
