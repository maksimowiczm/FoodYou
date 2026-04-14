package com.maksimowiczm.foodyou.app.ui.userfood

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.maksimowiczm.foodyou.app.ui.common.form.FormField
import com.maksimowiczm.foodyou.app.ui.common.form.rememberFormField
import com.maksimowiczm.foodyou.app.ui.common.form.validateDouble
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalEnergyUnit
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.app.ui.common.utility.formatClipZeros
import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import com.maksimowiczm.foodyou.common.domain.VolumeUnit
import com.maksimowiczm.foodyou.common.domain.WeightUnit
import com.maksimowiczm.foodyou.common.domain.fluidOunces
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.kilojoules
import com.maksimowiczm.foodyou.common.domain.micrograms
import com.maksimowiczm.foodyou.common.domain.milligrams
import com.maksimowiczm.foodyou.common.domain.ounces
import com.maksimowiczm.foodyou.userfood.domain.product.UserProduct
import foodyou.app.generated.resources.*
import io.konform.validation.ifPresent
import org.jetbrains.compose.resources.stringResource

@Stable
internal class ProductFormState(
    val name: FormField,
    val brand: FormField,
    val barcode: FormField,
    private val defaultImageUri: String?,
    val imageUri: MutableState<String?>,
    private val defaultValuesPer: ValuesPer,
    val valuesPer: MutableState<ValuesPer>,
    val servingQuantity: FormField,
    private val defaultServingUnit: QuantityUnit,
    val servingUnit: MutableState<QuantityUnit>,
    val packageQuantity: FormField,
    private val defaultPackageUnit: QuantityUnit,
    val packageUnit: MutableState<QuantityUnit>,
    val note: FormField,
    val proteins: FormField,
    val carbohydrates: FormField,
    val fats: FormField,
    val energy: FormField,
    private val defaultEnergyUnit: EnergyUnit,
    val energyUnit: MutableState<EnergyUnit>,
    val saturatedFats: FormField,
    val transFats: FormField,
    val monounsaturatedFats: FormField,
    val polyunsaturatedFats: FormField,
    val omega3: FormField,
    val omega6: FormField,
    val sugars: FormField,
    val addedSugars: FormField,
    val solubleFiber: FormField,
    val insolubleFiber: FormField,
    val dietaryFiber: FormField,
    val salt: FormField,
    val cholesterolMilli: FormField,
    val caffeineMilli: FormField,
    val vitaminAMicro: FormField,
    val vitaminB1Milli: FormField,
    val vitaminB2Milli: FormField,
    val vitaminB3Milli: FormField,
    val vitaminB5Milli: FormField,
    val vitaminB6Milli: FormField,
    val vitaminB7Micro: FormField,
    val vitaminB9Micro: FormField,
    val vitaminB12Micro: FormField,
    val vitaminCMilli: FormField,
    val vitaminDMicro: FormField,
    val vitaminEMilli: FormField,
    val vitaminKMicro: FormField,
    val manganeseMilli: FormField,
    val magnesiumMilli: FormField,
    val potassiumMilli: FormField,
    val calciumMilli: FormField,
    val copperMilli: FormField,
    val zincMilli: FormField,
    val sodiumMilli: FormField,
    val ironMilli: FormField,
    val phosphorusMilli: FormField,
    val seleniumMicro: FormField,
    val iodineMicro: FormField,
    val chromiumMicro: FormField,
) {
    val isModified: Boolean by derivedStateOf {
        name.isModified ||
            brand.isModified ||
            barcode.isModified ||
            imageUri.value != defaultImageUri ||
            valuesPer.value != defaultValuesPer ||
            servingQuantity.isModified ||
            servingUnit.value != defaultServingUnit ||
            packageQuantity.isModified ||
            packageUnit.value != defaultPackageUnit ||
            note.isModified ||
            proteins.isModified ||
            carbohydrates.isModified ||
            fats.isModified ||
            energy.isModified ||
            energyUnit.value != defaultEnergyUnit ||
            saturatedFats.isModified ||
            transFats.isModified ||
            monounsaturatedFats.isModified ||
            polyunsaturatedFats.isModified ||
            omega3.isModified ||
            omega6.isModified ||
            sugars.isModified ||
            addedSugars.isModified ||
            dietaryFiber.isModified ||
            solubleFiber.isModified ||
            insolubleFiber.isModified ||
            salt.isModified ||
            cholesterolMilli.isModified ||
            caffeineMilli.isModified ||
            vitaminAMicro.isModified ||
            vitaminB1Milli.isModified ||
            vitaminB2Milli.isModified ||
            vitaminB3Milli.isModified ||
            vitaminB5Milli.isModified ||
            vitaminB6Milli.isModified ||
            vitaminB7Micro.isModified ||
            vitaminB9Micro.isModified ||
            vitaminB12Micro.isModified ||
            vitaminCMilli.isModified ||
            vitaminDMicro.isModified ||
            vitaminEMilli.isModified ||
            vitaminKMicro.isModified ||
            manganeseMilli.isModified ||
            magnesiumMilli.isModified ||
            potassiumMilli.isModified ||
            calciumMilli.isModified ||
            copperMilli.isModified ||
            zincMilli.isModified ||
            sodiumMilli.isModified ||
            ironMilli.isModified ||
            phosphorusMilli.isModified ||
            seleniumMicro.isModified ||
            iodineMicro.isModified ||
            chromiumMicro.isModified
    }

    val isValid: Boolean by derivedStateOf {
        name.error == null &&
            name.textFieldState.text.isNotBlank() &&
            brand.error == null &&
            barcode.error == null &&
            servingQuantity.error == null &&
            packageQuantity.error == null &&
            note.error == null &&
            proteins.error == null &&
            carbohydrates.error == null &&
            fats.error == null &&
            energy.error == null &&
            saturatedFats.error == null &&
            transFats.error == null &&
            monounsaturatedFats.error == null &&
            polyunsaturatedFats.error == null &&
            omega3.error == null &&
            omega6.error == null &&
            sugars.error == null &&
            addedSugars.error == null &&
            dietaryFiber.error == null &&
            solubleFiber.error == null &&
            insolubleFiber.error == null &&
            salt.error == null &&
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
            iodineMicro.error == null &&
            chromiumMicro.error == null
    }

    val hasSuggestedFieldsFilled: Boolean by derivedStateOf {
        proteins.textFieldState.text.isNotBlank() &&
            carbohydrates.textFieldState.text.isNotBlank() &&
            fats.textFieldState.text.isNotBlank() &&
            energy.textFieldState.text.isNotBlank()
    }
}

@Composable
internal fun rememberProductForm2State(
    product: UserProduct? = null,
    defaultEnergyUnit: EnergyUnit = LocalEnergyUnit.current,
): ProductFormState {
    val required = stringResource(Res.string.neutral_required)
    val notABarcode = stringResource(Res.string.error_not_a_barcode)
    val invalidNumber = stringResource(Res.string.error_invalid_number)
    val valueMustBePositive = stringResource(Res.string.error_value_must_be_positive)

    val nameSelector = LocalFoodNameSelector.current

    val name =
        rememberFormField(defaultValue = product?.name?.let(nameSelector::select)) {
            constrain(required) { !it.isNullOrBlank() }
        }
    val brand = rememberFormField(product?.brand?.value)
    val barcode =
        rememberFormField(product?.barcode?.value) {
            ifPresent { constrain(notABarcode) { it.all(Char::isDigit) } }
        }
    val imageUri = rememberSaveable(product) { mutableStateOf(product?.image?.value) }
    val defaultValuesPer =
        if (product?.isLiquid == true) ValuesPer.Milliliters100 else ValuesPer.Grams100
    val valuesPer = rememberSaveable(defaultValuesPer) { mutableStateOf(defaultValuesPer) }

    val (defaultServingQuantity, defaultServingUnit) =
        remember(product) {
            if (product?.servingQuantity == null) {
                return@remember null to QuantityUnit.Gram
            }

            when (product.servingQuantity) {
                is AbsoluteQuantity.Volume ->
                    when (product.servingQuantity.volume.unit) {
                        VolumeUnit.Milliliters ->
                            product.servingQuantity.volume.milliliters to QuantityUnit.Milliliter

                        VolumeUnit.FluidOunces ->
                            product.servingQuantity.volume.fluidOunces to QuantityUnit.FluidOunce
                    }

                is AbsoluteQuantity.Weight ->
                    when (product.servingQuantity.weight.unit) {
                        WeightUnit.Grams ->
                            product.servingQuantity.weight.grams to QuantityUnit.Gram

                        WeightUnit.Ounces ->
                            product.servingQuantity.weight.ounces to QuantityUnit.Ounce

                        else -> error("Not supported")
                    }
            }
        }
    val servingQuantity =
        rememberFormField(
            valuesPer.value,
            defaultValue = defaultServingQuantity?.formatClipZeros(),
        ) {
            dynamic {
                if (valuesPer.value == ValuesPer.Serving) {
                    constrain(required) { !it.isNullOrBlank() }
                }
            }
            ifPresent {
                validateDouble {
                    constrain(invalidNumber) { it != null }
                    constrain(valueMustBePositive) { it?.let { it > 0 } ?: true }
                }
            }
        }
    val servingUnit = rememberSaveable(defaultServingUnit) { mutableStateOf(defaultServingUnit) }

    val (defaultPackageQuantity, defaultPackageUnit) =
        remember(product) {
            if (product?.packageQuantity == null) {
                return@remember null to QuantityUnit.Gram
            }

            when (product.packageQuantity) {
                is AbsoluteQuantity.Volume ->
                    when (product.packageQuantity.volume.unit) {
                        VolumeUnit.Milliliters ->
                            product.packageQuantity.volume.milliliters to QuantityUnit.Milliliter

                        VolumeUnit.FluidOunces ->
                            product.packageQuantity.volume.fluidOunces to QuantityUnit.FluidOunce
                    }

                is AbsoluteQuantity.Weight ->
                    when (product.packageQuantity.weight.unit) {
                        WeightUnit.Grams ->
                            product.packageQuantity.weight.grams to QuantityUnit.Gram

                        WeightUnit.Ounces ->
                            product.packageQuantity.weight.ounces to QuantityUnit.Ounce

                        else -> error("Not supported")
                    }
            }
        }
    val packageQuantity =
        rememberFormField(
            valuesPer.value,
            defaultValue = defaultPackageQuantity?.formatClipZeros(),
        ) {
            dynamic {
                if (valuesPer.value == ValuesPer.Package) {
                    constrain(required) { !it.isNullOrBlank() }
                }
            }
            ifPresent {
                validateDouble {
                    constrain(invalidNumber) { it != null }
                    constrain(valueMustBePositive) { it?.let { it > 0 } ?: true }
                }
            }
        }
    val packageUnit = rememberSaveable(defaultPackageUnit) { mutableStateOf(defaultPackageUnit) }

    val note = rememberFormField()

    val proteins =
        rememberDoubleFormField2(product?.nutritionFacts?.proteins?.value?.grams?.formatClipZeros())
    val carbs =
        rememberDoubleFormField2(
            product?.nutritionFacts?.carbohydrates?.value?.grams?.formatClipZeros()
        )
    val fats =
        rememberDoubleFormField2(product?.nutritionFacts?.fats?.value?.grams?.formatClipZeros())
    val energy =
        rememberDoubleFormField2(
            product?.nutritionFacts?.energy?.value?.let {
                when (defaultEnergyUnit) {
                    EnergyUnit.Kilocalories -> it.kilocalories
                    EnergyUnit.Kilojoules -> it.kilojoules
                }.formatClipZeros()
            }
        )
    val energyUnit = rememberSaveable(defaultEnergyUnit) { mutableStateOf(defaultEnergyUnit) }

    val saturatedFats =
        rememberDoubleFormField2(
            product?.nutritionFacts?.saturatedFats?.value?.grams?.formatClipZeros()
        )
    val transFats =
        rememberDoubleFormField2(
            product?.nutritionFacts?.transFats?.value?.grams?.formatClipZeros()
        )
    val monounsaturatedFats =
        rememberDoubleFormField2(
            product?.nutritionFacts?.monounsaturatedFats?.value?.grams?.formatClipZeros()
        )
    val polyunsaturatedFats =
        rememberDoubleFormField2(
            product?.nutritionFacts?.polyunsaturatedFats?.value?.grams?.formatClipZeros()
        )
    val omega3 =
        rememberDoubleFormField2(product?.nutritionFacts?.omega3?.value?.grams?.formatClipZeros())
    val omega6 =
        rememberDoubleFormField2(product?.nutritionFacts?.omega6?.value?.grams?.formatClipZeros())

    val sugars =
        rememberDoubleFormField2(product?.nutritionFacts?.sugars?.value?.grams?.formatClipZeros())
    val addedSugars =
        rememberDoubleFormField2(
            product?.nutritionFacts?.addedSugars?.value?.grams?.formatClipZeros()
        )
    val dietaryFiber =
        rememberDoubleFormField2(
            product?.nutritionFacts?.dietaryFiber?.value?.grams?.formatClipZeros()
        )
    val solubleFiber =
        rememberDoubleFormField2(
            product?.nutritionFacts?.solubleFiber?.value?.grams?.formatClipZeros()
        )
    val insolubleFiber =
        rememberDoubleFormField2(
            product?.nutritionFacts?.insolubleFiber?.value?.grams?.formatClipZeros()
        )

    val salt =
        rememberDoubleFormField2(product?.nutritionFacts?.salt?.value?.grams?.formatClipZeros())
    val cholesterolMilli =
        rememberDoubleFormField2(
            product?.nutritionFacts?.cholesterol?.value?.milligrams?.formatClipZeros()
        )
    val caffeineMilli =
        rememberDoubleFormField2(
            product?.nutritionFacts?.caffeine?.value?.milligrams?.formatClipZeros()
        )

    val vitaminAMicro =
        rememberDoubleFormField2(
            product?.nutritionFacts?.vitaminA?.value?.micrograms?.formatClipZeros()
        )
    val vitaminB1Milli =
        rememberDoubleFormField2(
            product?.nutritionFacts?.vitaminB1?.value?.milligrams?.formatClipZeros()
        )
    val vitaminB2Milli =
        rememberDoubleFormField2(
            product?.nutritionFacts?.vitaminB2?.value?.milligrams?.formatClipZeros()
        )
    val vitaminB3Milli =
        rememberDoubleFormField2(
            product?.nutritionFacts?.vitaminB3?.value?.milligrams?.formatClipZeros()
        )
    val vitaminB5Milli =
        rememberDoubleFormField2(
            product?.nutritionFacts?.vitaminB5?.value?.milligrams?.formatClipZeros()
        )
    val vitaminB6Milli =
        rememberDoubleFormField2(
            product?.nutritionFacts?.vitaminB6?.value?.milligrams?.formatClipZeros()
        )
    val vitaminB7Micro =
        rememberDoubleFormField2(
            product?.nutritionFacts?.vitaminB7?.value?.micrograms?.formatClipZeros()
        )
    val vitaminB9Micro =
        rememberDoubleFormField2(
            product?.nutritionFacts?.vitaminB9?.value?.micrograms?.formatClipZeros()
        )
    val vitaminB12Micro =
        rememberDoubleFormField2(
            product?.nutritionFacts?.vitaminB12?.value?.micrograms?.formatClipZeros()
        )
    val vitaminCMilli =
        rememberDoubleFormField2(
            product?.nutritionFacts?.vitaminC?.value?.milligrams?.formatClipZeros()
        )
    val vitaminDMicro =
        rememberDoubleFormField2(
            product?.nutritionFacts?.vitaminD?.value?.micrograms?.formatClipZeros()
        )
    val vitaminEMilli =
        rememberDoubleFormField2(
            product?.nutritionFacts?.vitaminE?.value?.milligrams?.formatClipZeros()
        )
    val vitaminKMicro =
        rememberDoubleFormField2(
            product?.nutritionFacts?.vitaminK?.value?.micrograms?.formatClipZeros()
        )

    val manganeseMilli =
        rememberDoubleFormField2(
            product?.nutritionFacts?.manganese?.value?.milligrams?.formatClipZeros()
        )
    val magnesiumMilli =
        rememberDoubleFormField2(
            product?.nutritionFacts?.magnesium?.value?.milligrams?.formatClipZeros()
        )
    val potassiumMilli =
        rememberDoubleFormField2(
            product?.nutritionFacts?.potassium?.value?.milligrams?.formatClipZeros()
        )
    val calciumMilli =
        rememberDoubleFormField2(
            product?.nutritionFacts?.calcium?.value?.milligrams?.formatClipZeros()
        )
    val copperMilli =
        rememberDoubleFormField2(
            product?.nutritionFacts?.copper?.value?.milligrams?.formatClipZeros()
        )
    val zincMilli =
        rememberDoubleFormField2(
            product?.nutritionFacts?.zinc?.value?.milligrams?.formatClipZeros()
        )
    val sodiumMilli =
        rememberDoubleFormField2(
            product?.nutritionFacts?.sodium?.value?.milligrams?.formatClipZeros()
        )
    val ironMilli =
        rememberDoubleFormField2(
            product?.nutritionFacts?.iron?.value?.milligrams?.formatClipZeros()
        )
    val phosphorusMilli =
        rememberDoubleFormField2(
            product?.nutritionFacts?.phosphorus?.value?.milligrams?.formatClipZeros()
        )
    val seleniumMicro =
        rememberDoubleFormField2(
            product?.nutritionFacts?.selenium?.value?.micrograms?.formatClipZeros()
        )
    val iodineMicro =
        rememberDoubleFormField2(
            product?.nutritionFacts?.iodine?.value?.micrograms?.formatClipZeros()
        )
    val chromiumMicro =
        rememberDoubleFormField2(
            product?.nutritionFacts?.chromium?.value?.micrograms?.formatClipZeros()
        )

    return remember(
        name,
        brand,
        barcode,
        product,
        imageUri,
        defaultValuesPer,
        valuesPer,
        servingQuantity,
        defaultServingUnit,
        servingUnit,
        packageQuantity,
        defaultPackageUnit,
        packageUnit,
        note,
        proteins,
        carbs,
        fats,
        energy,
        defaultEnergyUnit,
        energyUnit,
        saturatedFats,
        transFats,
        monounsaturatedFats,
        polyunsaturatedFats,
        omega3,
        omega6,
        sugars,
        addedSugars,
        solubleFiber,
        insolubleFiber,
        dietaryFiber,
        salt,
        cholesterolMilli,
        caffeineMilli,
        vitaminAMicro,
        vitaminB1Milli,
        vitaminB2Milli,
        vitaminB3Milli,
        vitaminB5Milli,
        vitaminB6Milli,
        vitaminB7Micro,
        vitaminB9Micro,
        vitaminB12Micro,
        vitaminCMilli,
        vitaminDMicro,
        vitaminEMilli,
        vitaminKMicro,
        manganeseMilli,
        magnesiumMilli,
        potassiumMilli,
        calciumMilli,
        copperMilli,
        zincMilli,
        sodiumMilli,
        ironMilli,
        phosphorusMilli,
        seleniumMicro,
        iodineMicro,
        chromiumMicro,
    ) {
        ProductFormState(
            name = name,
            brand = brand,
            barcode = barcode,
            defaultImageUri = product?.image?.value,
            imageUri = imageUri,
            defaultValuesPer = defaultValuesPer,
            valuesPer = valuesPer,
            servingQuantity = servingQuantity,
            defaultServingUnit = defaultServingUnit,
            servingUnit = servingUnit,
            packageQuantity = packageQuantity,
            defaultPackageUnit = defaultPackageUnit,
            packageUnit = packageUnit,
            note = note,
            proteins = proteins,
            carbohydrates = carbs,
            fats = fats,
            energy = energy,
            defaultEnergyUnit = defaultEnergyUnit,
            energyUnit = energyUnit,
            saturatedFats = saturatedFats,
            transFats = transFats,
            monounsaturatedFats = monounsaturatedFats,
            polyunsaturatedFats = polyunsaturatedFats,
            omega3 = omega3,
            omega6 = omega6,
            sugars = sugars,
            addedSugars = addedSugars,
            solubleFiber = solubleFiber,
            insolubleFiber = insolubleFiber,
            dietaryFiber = dietaryFiber,
            salt = salt,
            cholesterolMilli = cholesterolMilli,
            caffeineMilli = caffeineMilli,
            vitaminAMicro = vitaminAMicro,
            vitaminB1Milli = vitaminB1Milli,
            vitaminB2Milli = vitaminB2Milli,
            vitaminB3Milli = vitaminB3Milli,
            vitaminB5Milli = vitaminB5Milli,
            vitaminB6Milli = vitaminB6Milli,
            vitaminB7Micro = vitaminB7Micro,
            vitaminB9Micro = vitaminB9Micro,
            vitaminB12Micro = vitaminB12Micro,
            vitaminCMilli = vitaminCMilli,
            vitaminDMicro = vitaminDMicro,
            vitaminEMilli = vitaminEMilli,
            vitaminKMicro = vitaminKMicro,
            manganeseMilli = manganeseMilli,
            magnesiumMilli = magnesiumMilli,
            potassiumMilli = potassiumMilli,
            calciumMilli = calciumMilli,
            copperMilli = copperMilli,
            zincMilli = zincMilli,
            sodiumMilli = sodiumMilli,
            ironMilli = ironMilli,
            phosphorusMilli = phosphorusMilli,
            seleniumMicro = seleniumMicro,
            iodineMicro = iodineMicro,
            chromiumMicro = chromiumMicro,
        )
    }
}

@Composable
private fun rememberDoubleFormField2(defaultValue: String?): FormField {
    val invalidNumber = stringResource(Res.string.error_invalid_number)
    val valueMustBePositive = stringResource(Res.string.error_value_must_be_positive)

    return rememberFormField(defaultValue = defaultValue) {
        ifPresent {
            validateDouble {
                constrain(invalidNumber) { it != null }
                constrain(valueMustBePositive) { it?.let { it > 0 } ?: true }
            }
        }
    }
}
