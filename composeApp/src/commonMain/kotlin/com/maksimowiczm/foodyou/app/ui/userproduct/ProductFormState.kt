package com.maksimowiczm.foodyou.app.ui.userproduct

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.maksimowiczm.foodyou.app.ui.common.form.FormField
import com.maksimowiczm.foodyou.app.ui.common.form.rememberFormField
import com.maksimowiczm.foodyou.app.ui.common.form.validateDouble
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalEnergyUnit
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.app.ui.common.utility.formatCompact
import com.maksimowiczm.foodyou.app.ui.common.utility.resolveBlob
import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import com.maksimowiczm.foodyou.common.domain.VolumeUnit
import com.maksimowiczm.foodyou.common.domain.WeightUnit
import com.maksimowiczm.foodyou.common.domain.fluidOunces
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.kilojoules
import com.maksimowiczm.foodyou.common.domain.micrograms
import com.maksimowiczm.foodyou.common.domain.milligrams
import com.maksimowiczm.foodyou.common.domain.ounces
import com.maksimowiczm.foodyou.userproduct.domain.UserProduct
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
internal fun rememberProductFormState(
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
    val brand = rememberFormField(product?.brand)
    val barcode =
        rememberFormField(product?.barcode?.value) {
            ifPresent { constrain(notABarcode) { it.all(Char::isDigit) } }
        }

    val imageUri = product?.image?.let { resolveBlob(it).value }
    val imageUriState = rememberSaveable(imageUri) { mutableStateOf(imageUri) }
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
            defaultValue = defaultServingQuantity?.formatCompact(),
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
            defaultValue = defaultPackageQuantity?.formatCompact(),
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

    val note = rememberFormField(product?.note)

    val proteins =
        rememberDoubleFormField(product?.nutritionFacts?.proteins?.value?.grams?.formatCompact())
    val carbs =
        rememberDoubleFormField(
            product?.nutritionFacts?.carbohydrates?.value?.grams?.formatCompact()
        )
    val fats = rememberDoubleFormField(product?.nutritionFacts?.fats?.value?.grams?.formatCompact())
    val energy =
        rememberDoubleFormField(
            product?.nutritionFacts?.energy?.value?.let {
                when (defaultEnergyUnit) {
                    EnergyUnit.Kilocalories -> it.kilocalories
                    EnergyUnit.Kilojoules -> it.kilojoules
                }.formatCompact()
            }
        )
    val energyUnit = rememberSaveable(defaultEnergyUnit) { mutableStateOf(defaultEnergyUnit) }

    val saturatedFats =
        rememberDoubleFormField(
            product?.nutritionFacts?.saturatedFats?.value?.grams?.formatCompact()
        )
    val transFats =
        rememberDoubleFormField(product?.nutritionFacts?.transFats?.value?.grams?.formatCompact())
    val monounsaturatedFats =
        rememberDoubleFormField(
            product?.nutritionFacts?.monounsaturatedFats?.value?.grams?.formatCompact()
        )
    val polyunsaturatedFats =
        rememberDoubleFormField(
            product?.nutritionFacts?.polyunsaturatedFats?.value?.grams?.formatCompact()
        )
    val omega3 =
        rememberDoubleFormField(product?.nutritionFacts?.omega3?.value?.grams?.formatCompact())
    val omega6 =
        rememberDoubleFormField(product?.nutritionFacts?.omega6?.value?.grams?.formatCompact())

    val sugars =
        rememberDoubleFormField(product?.nutritionFacts?.sugars?.value?.grams?.formatCompact())
    val addedSugars =
        rememberDoubleFormField(product?.nutritionFacts?.addedSugars?.value?.grams?.formatCompact())
    val dietaryFiber =
        rememberDoubleFormField(
            product?.nutritionFacts?.dietaryFiber?.value?.grams?.formatCompact()
        )
    val solubleFiber =
        rememberDoubleFormField(
            product?.nutritionFacts?.solubleFiber?.value?.grams?.formatCompact()
        )
    val insolubleFiber =
        rememberDoubleFormField(
            product?.nutritionFacts?.insolubleFiber?.value?.grams?.formatCompact()
        )

    val salt = rememberDoubleFormField(product?.nutritionFacts?.salt?.value?.grams?.formatCompact())
    val cholesterolMilli =
        rememberDoubleFormField(
            product?.nutritionFacts?.cholesterol?.value?.milligrams?.formatCompact()
        )
    val caffeineMilli =
        rememberDoubleFormField(
            product?.nutritionFacts?.caffeine?.value?.milligrams?.formatCompact()
        )

    val vitaminAMicro =
        rememberDoubleFormField(
            product?.nutritionFacts?.vitaminA?.value?.micrograms?.formatCompact()
        )
    val vitaminB1Milli =
        rememberDoubleFormField(
            product?.nutritionFacts?.vitaminB1?.value?.milligrams?.formatCompact()
        )
    val vitaminB2Milli =
        rememberDoubleFormField(
            product?.nutritionFacts?.vitaminB2?.value?.milligrams?.formatCompact()
        )
    val vitaminB3Milli =
        rememberDoubleFormField(
            product?.nutritionFacts?.vitaminB3?.value?.milligrams?.formatCompact()
        )
    val vitaminB5Milli =
        rememberDoubleFormField(
            product?.nutritionFacts?.vitaminB5?.value?.milligrams?.formatCompact()
        )
    val vitaminB6Milli =
        rememberDoubleFormField(
            product?.nutritionFacts?.vitaminB6?.value?.milligrams?.formatCompact()
        )
    val vitaminB7Micro =
        rememberDoubleFormField(
            product?.nutritionFacts?.vitaminB7?.value?.micrograms?.formatCompact()
        )
    val vitaminB9Micro =
        rememberDoubleFormField(
            product?.nutritionFacts?.vitaminB9?.value?.micrograms?.formatCompact()
        )
    val vitaminB12Micro =
        rememberDoubleFormField(
            product?.nutritionFacts?.vitaminB12?.value?.micrograms?.formatCompact()
        )
    val vitaminCMilli =
        rememberDoubleFormField(
            product?.nutritionFacts?.vitaminC?.value?.milligrams?.formatCompact()
        )
    val vitaminDMicro =
        rememberDoubleFormField(
            product?.nutritionFacts?.vitaminD?.value?.micrograms?.formatCompact()
        )
    val vitaminEMilli =
        rememberDoubleFormField(
            product?.nutritionFacts?.vitaminE?.value?.milligrams?.formatCompact()
        )
    val vitaminKMicro =
        rememberDoubleFormField(
            product?.nutritionFacts?.vitaminK?.value?.micrograms?.formatCompact()
        )

    val manganeseMilli =
        rememberDoubleFormField(
            product?.nutritionFacts?.manganese?.value?.milligrams?.formatCompact()
        )
    val magnesiumMilli =
        rememberDoubleFormField(
            product?.nutritionFacts?.magnesium?.value?.milligrams?.formatCompact()
        )
    val potassiumMilli =
        rememberDoubleFormField(
            product?.nutritionFacts?.potassium?.value?.milligrams?.formatCompact()
        )
    val calciumMilli =
        rememberDoubleFormField(
            product?.nutritionFacts?.calcium?.value?.milligrams?.formatCompact()
        )
    val copperMilli =
        rememberDoubleFormField(product?.nutritionFacts?.copper?.value?.milligrams?.formatCompact())
    val zincMilli =
        rememberDoubleFormField(product?.nutritionFacts?.zinc?.value?.milligrams?.formatCompact())
    val sodiumMilli =
        rememberDoubleFormField(product?.nutritionFacts?.sodium?.value?.milligrams?.formatCompact())
    val ironMilli =
        rememberDoubleFormField(product?.nutritionFacts?.iron?.value?.milligrams?.formatCompact())
    val phosphorusMilli =
        rememberDoubleFormField(
            product?.nutritionFacts?.phosphorus?.value?.milligrams?.formatCompact()
        )
    val seleniumMicro =
        rememberDoubleFormField(
            product?.nutritionFacts?.selenium?.value?.micrograms?.formatCompact()
        )
    val iodineMicro =
        rememberDoubleFormField(product?.nutritionFacts?.iodine?.value?.micrograms?.formatCompact())
    val chromiumMicro =
        rememberDoubleFormField(
            product?.nutritionFacts?.chromium?.value?.micrograms?.formatCompact()
        )

    return remember(
        name,
        brand,
        barcode,
        product,
        imageUriState,
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
            defaultImageUri = imageUri,
            imageUri = imageUriState,
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
private fun rememberDoubleFormField(defaultValue: String?): FormField {
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
