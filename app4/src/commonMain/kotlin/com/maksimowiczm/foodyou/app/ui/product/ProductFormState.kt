package com.maksimowiczm.foodyou.app.ui.product

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.app.ui.common.form.FormField
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
    val name: FormField<String, FormFieldError>,
    val defaultName: String,
    val brand: FormField<String?, Unit>,
    val defaultBrand: String?,
    val barcode: FormField<String?, FormFieldError>,
    val defaultBarcode: String?,
    val note: FormField<String?, Unit>,
    val defaultNote: String?,
    val source: FormField<String?, Unit>,
    val defaultSource: String?,
    val proteins: FormField<Double?, FormFieldError>,
    val defaultProteins: Double?,
    val fats: FormField<Double?, FormFieldError>,
    val defaultFats: Double?,
    val carbohydrates: FormField<Double?, FormFieldError>,
    val defaultCarbohydrates: Double?,
    val energy: FormField<Double?, FormFieldError>,
    val defaultEnergy: Double?,
    val saturatedFats: FormField<Double?, FormFieldError>,
    val defaultSaturatedFats: Double?,
    val transFats: FormField<Double?, FormFieldError>,
    val defaultTransFats: Double?,
    val monounsaturatedFats: FormField<Double?, FormFieldError>,
    val defaultMonounsaturatedFats: Double?,
    val polyunsaturatedFats: FormField<Double?, FormFieldError>,
    val defaultPolyunsaturatedFats: Double?,
    val omega3: FormField<Double?, FormFieldError>,
    val defaultOmega3: Double?,
    val omega6: FormField<Double?, FormFieldError>,
    val defaultOmega6: Double?,
    val sugars: FormField<Double?, FormFieldError>,
    val defaultSugars: Double?,
    val addedSugars: FormField<Double?, FormFieldError>,
    val defaultAddedSugars: Double?,
    val dietaryFiber: FormField<Double?, FormFieldError>,
    val defaultDietaryFiber: Double?,
    val solubleFiber: FormField<Double?, FormFieldError>,
    val defaultSolubleFiber: Double?,
    val insolubleFiber: FormField<Double?, FormFieldError>,
    val defaultInsolubleFiber: Double?,
    val salt: FormField<Double?, FormFieldError>,
    val defaultSalt: Double?,
    val cholesterolMilli: FormField<Double?, FormFieldError>,
    val defaultCholesterolMilli: Double?,
    val caffeineMilli: FormField<Double?, FormFieldError>,
    val defaultCaffeineMilli: Double?,
    val vitaminAMicro: FormField<Double?, FormFieldError>,
    val defaultVitaminAMicro: Double?,
    val vitaminB1Milli: FormField<Double?, FormFieldError>,
    val defaultVitaminB1Milli: Double?,
    val vitaminB2Milli: FormField<Double?, FormFieldError>,
    val defaultVitaminB2Milli: Double?,
    val vitaminB3Milli: FormField<Double?, FormFieldError>,
    val defaultVitaminB3Milli: Double?,
    val vitaminB5Milli: FormField<Double?, FormFieldError>,
    val defaultVitaminB5Milli: Double?,
    val vitaminB6Milli: FormField<Double?, FormFieldError>,
    val defaultVitaminB6Milli: Double?,
    val vitaminB7Micro: FormField<Double?, FormFieldError>,
    val defaultVitaminB7Micro: Double?,
    val vitaminB9Micro: FormField<Double?, FormFieldError>,
    val defaultVitaminB9Micro: Double?,
    val vitaminB12Micro: FormField<Double?, FormFieldError>,
    val defaultVitaminB12Micro: Double?,
    val vitaminCMilli: FormField<Double?, FormFieldError>,
    val defaultVitaminCMilli: Double?,
    val vitaminDMicro: FormField<Double?, FormFieldError>,
    val defaultVitaminDMicro: Double?,
    val vitaminEMilli: FormField<Double?, FormFieldError>,
    val defaultVitaminEMilli: Double?,
    val vitaminKMicro: FormField<Double?, FormFieldError>,
    val defaultVitaminKMicro: Double?,
    val manganeseMilli: FormField<Double?, FormFieldError>,
    val defaultManganeseMilli: Double?,
    val magnesiumMilli: FormField<Double?, FormFieldError>,
    val defaultMagnesiumMilli: Double?,
    val potassiumMilli: FormField<Double?, FormFieldError>,
    val defaultPotassiumMilli: Double?,
    val calciumMilli: FormField<Double?, FormFieldError>,
    val defaultCalciumMilli: Double?,
    val copperMilli: FormField<Double?, FormFieldError>,
    val defaultCopperMilli: Double?,
    val zincMilli: FormField<Double?, FormFieldError>,
    val defaultZincMilli: Double?,
    val sodiumMilli: FormField<Double?, FormFieldError>,
    val defaultSodiumMilli: Double?,
    val ironMilli: FormField<Double?, FormFieldError>,
    val defaultIronMilli: Double?,
    val phosphorusMilli: FormField<Double?, FormFieldError>,
    val defaultPhosphorusMilli: Double?,
    val seleniumMicro: FormField<Double?, FormFieldError>,
    val defaultSeleniumMicro: Double?,
    val iodineMicro: FormField<Double?, FormFieldError>,
    val defaultIodineMicro: Double?,
    val chromiumMicro: FormField<Double?, FormFieldError>,
    val defaultChromiumMicro: Double?,
    val imageUri: String?,
    val defaultImageUri: String?,
    val valuesPer: ValuesPer,
    val servingQuantity: FormField<Double?, FormFieldError>,
    val defaultServingQuantity: Double?,
    val servingUnit: QuantityUnit,
    val defaultServingUnit: QuantityUnit,
    val packageQuantity: FormField<Double?, FormFieldError>,
    val defaultPackageQuantity: Double?,
    val packageUnit: QuantityUnit,
    val defaultPackageUnit: QuantityUnit,
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

    private companion object {
        private val defaultValuesPer = ValuesPer.Grams100
    }
}
