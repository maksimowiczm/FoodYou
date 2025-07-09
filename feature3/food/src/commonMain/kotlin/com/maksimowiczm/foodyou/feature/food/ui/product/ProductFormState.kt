package com.maksimowiczm.foodyou.feature.food.ui.product

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.maksimowiczm.foodyou.core.ui.form.FormField
import com.maksimowiczm.foodyou.core.ui.form.nonBlankStringValidator
import com.maksimowiczm.foodyou.core.ui.form.nonNegativeFloatValidator
import com.maksimowiczm.foodyou.core.ui.form.nullableFloatParser
import com.maksimowiczm.foodyou.core.ui.form.nullableStringParser
import com.maksimowiczm.foodyou.core.ui.form.positiveFloatValidator
import com.maksimowiczm.foodyou.core.ui.form.rememberFormField
import com.maksimowiczm.foodyou.core.ui.form.stringParser
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.core.util.NutrientsHelper
import com.maksimowiczm.foodyou.feature.food.data.Minerals
import com.maksimowiczm.foodyou.feature.food.data.Nutrients
import com.maksimowiczm.foodyou.feature.food.data.Product as ProductEntity
import com.maksimowiczm.foodyou.feature.food.data.Vitamins
import com.maksimowiczm.foodyou.feature.food.domain.Product
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import com.maksimowiczm.foodyou.feature.measurement.ui.Saver
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

@Composable
internal fun rememberProductFormState(product: Product? = null): ProductFormState {
    val name = rememberFormField(
        initialValue = product?.name ?: "",
        parser = stringParser(),
        validator = nonBlankStringValidator(
            onEmpty = { ProductFormFieldError.Required }
        ),
        textFieldState = rememberTextFieldState(product?.name ?: "")
    )

    val brand = rememberFormField<String?, Nothing>(
        initialValue = product?.brand,
        parser = nullableStringParser(),
        textFieldState = rememberTextFieldState(product?.brand ?: "")
    )

    val barcode = rememberFormField<String?, Nothing>(
        initialValue = product?.barcode ?: "",
        parser = nullableStringParser(),
        textFieldState = rememberTextFieldState(product?.barcode ?: "")
    )

    val note = rememberFormField<String?, Nothing>(
        initialValue = product?.note ?: "",
        parser = nullableStringParser(),
        textFieldState = rememberTextFieldState(product?.note ?: "")
    )

    val measurement = rememberSaveable(
        stateSaver = Measurement.Saver
    ) {
        mutableStateOf(Measurement.Gram(100f))
    }

    val packageWeight = rememberFormField(
        initialValue = product?.packageWeight,
        parser = nullableFloatParser(
            onNotANumber = { ProductFormFieldError.NotANumber }
        ),
        validator = when {
            measurement.value is Measurement.Package -> positiveFloatValidator(
                onNotPositive = { ProductFormFieldError.NotPositive },
                onNull = { ProductFormFieldError.Required }
            )

            else -> positiveFloatValidator(
                onNotPositive = { ProductFormFieldError.NotPositive }
            )
        },
        textFieldState = rememberTextFieldState(product?.packageWeight?.toString() ?: "")
    )

    val servingWeight = rememberFormField(
        initialValue = product?.servingWeight,
        parser = nullableFloatParser(
            onNotANumber = { ProductFormFieldError.NotANumber }
        ),
        validator = when {
            measurement.value is Measurement.Serving -> positiveFloatValidator(
                onNotPositive = { ProductFormFieldError.NotPositive },
                onNull = { ProductFormFieldError.Required }
            )

            else -> positiveFloatValidator(
                onNotPositive = { ProductFormFieldError.NotPositive }
            )
        },
        textFieldState = rememberTextFieldState(product?.servingWeight?.toString() ?: "")
    )

    val proteins =
        rememberRequiredFormField(product?.nutritionFacts?.proteins?.value)
    val carbohydrates =
        rememberRequiredFormField(product?.nutritionFacts?.carbohydrates?.value)
    val fats =
        rememberRequiredFormField(product?.nutritionFacts?.fats?.value)
    val energy =
        rememberRequiredFormField(product?.nutritionFacts?.energy?.value)

    val autoCalculateEnergyState = rememberSaveable(product) {
        if (product == null) {
            mutableStateOf(true)
        } else {
            val energy = product.nutritionFacts.energy.value
            val proteins = product.nutritionFacts.proteins.value
            val carbohydrates = product.nutritionFacts.carbohydrates.value
            val fats = product.nutritionFacts.fats.value

            val initialState = NutrientsHelper.calculateEnergy(
                proteins = proteins,
                carbohydrates = carbohydrates,
                fats = fats
            ) == energy

            mutableStateOf(initialState)
        }
    }

    LaunchedEffect(autoCalculateEnergyState, proteins, carbohydrates, fats) {
        launch {
            snapshotFlow { autoCalculateEnergyState.value }.drop(1).filter { it }.collectLatest {
                val proteinsValue = proteins.value
                val carbohydratesValue = carbohydrates.value
                val fatsValue = fats.value

                if (proteinsValue != null && carbohydratesValue != null && fatsValue != null) {
                    val kcal = NutrientsHelper.calculateEnergy(
                        proteins = proteinsValue,
                        carbohydrates = carbohydratesValue,
                        fats = fatsValue
                    )

                    val text = kcal.formatClipZeros()
                    energy.textFieldState.setTextAndPlaceCursorAtEnd(text)
                }
            }
        }

        launch {
            val caloriesFlow = combine(
                snapshotFlow { proteins.value },
                snapshotFlow { carbohydrates.value },
                snapshotFlow { fats.value }
            ) { it }.drop(1).mapNotNull {
                val (proteins, carbohydrates, fats) = it
                if (proteins == null || carbohydrates == null || fats == null) {
                    return@mapNotNull null
                }

                NutrientsHelper.calculateEnergy(
                    proteins = proteins,
                    carbohydrates = carbohydrates,
                    fats = fats
                )
            }

            combine(
                snapshotFlow { autoCalculateEnergyState.value },
                caloriesFlow
            ) { autoCalculateEnergy, calories ->
                if (!autoCalculateEnergy) {
                    return@combine null
                }

                calories
            }.filterNotNull().collectLatest { kcal ->
                val text = kcal.formatClipZeros()
                energy.textFieldState.setTextAndPlaceCursorAtEnd(text)
            }
        }
    }

    val saturatedFats =
        rememberNotRequiredFormField(product?.nutritionFacts?.saturatedFats?.value)
    val monounsaturatedFats =
        rememberNotRequiredFormField(product?.nutritionFacts?.monounsaturatedFats?.value)
    val polyunsaturatedFats =
        rememberNotRequiredFormField(product?.nutritionFacts?.polyunsaturatedFats?.value)
    val omega3 =
        rememberNotRequiredFormField(product?.nutritionFacts?.omega3?.value)
    val omega6 =
        rememberNotRequiredFormField(product?.nutritionFacts?.omega6?.value)

    val sugars =
        rememberNotRequiredFormField(product?.nutritionFacts?.sugars?.value)
    val salt =
        rememberNotRequiredFormField(product?.nutritionFacts?.salt?.value)
    val fiber =
        rememberNotRequiredFormField(product?.nutritionFacts?.fiber?.value)
    val cholesterol =
        rememberNotRequiredFormField(product?.nutritionFacts?.cholesterolMilli?.value)
    val caffeine =
        rememberNotRequiredFormField(product?.nutritionFacts?.caffeineMilli?.value)

    val vitaminA =
        rememberNotRequiredFormField(product?.nutritionFacts?.vitaminAMicro?.value)
    val vitaminB1 =
        rememberNotRequiredFormField(product?.nutritionFacts?.vitaminB1Milli?.value)
    val vitaminB2 =
        rememberNotRequiredFormField(product?.nutritionFacts?.vitaminB2Milli?.value)
    val vitaminB3 =
        rememberNotRequiredFormField(product?.nutritionFacts?.vitaminB3Milli?.value)
    val vitaminB5 =
        rememberNotRequiredFormField(product?.nutritionFacts?.vitaminB5Milli?.value)
    val vitaminB6 =
        rememberNotRequiredFormField(product?.nutritionFacts?.vitaminB6Milli?.value)
    val vitaminB7 =
        rememberNotRequiredFormField(product?.nutritionFacts?.vitaminB7Micro?.value)
    val vitaminB9 =
        rememberNotRequiredFormField(product?.nutritionFacts?.vitaminB9Micro?.value)
    val vitaminB12 =
        rememberNotRequiredFormField(product?.nutritionFacts?.vitaminB12Micro?.value)
    val vitaminC =
        rememberNotRequiredFormField(product?.nutritionFacts?.vitaminCMilli?.value)
    val vitaminD =
        rememberNotRequiredFormField(product?.nutritionFacts?.vitaminDMicro?.value)
    val vitaminE =
        rememberNotRequiredFormField(product?.nutritionFacts?.vitaminEMilli?.value)
    val vitaminK =
        rememberNotRequiredFormField(product?.nutritionFacts?.vitaminKMicro?.value)

    val manganese =
        rememberNotRequiredFormField(product?.nutritionFacts?.manganeseMilli?.value)
    val magnesium =
        rememberNotRequiredFormField(product?.nutritionFacts?.magnesiumMilli?.value)
    val potassium =
        rememberNotRequiredFormField(product?.nutritionFacts?.potassiumMilli?.value)
    val calcium =
        rememberNotRequiredFormField(product?.nutritionFacts?.calciumMilli?.value)
    val copper =
        rememberNotRequiredFormField(product?.nutritionFacts?.copperMilli?.value)
    val zinc =
        rememberNotRequiredFormField(product?.nutritionFacts?.zincMilli?.value)
    val sodium =
        rememberNotRequiredFormField(product?.nutritionFacts?.sodiumMilli?.value)
    val iron =
        rememberNotRequiredFormField(product?.nutritionFacts?.ironMilli?.value)
    val phosphorus =
        rememberNotRequiredFormField(product?.nutritionFacts?.phosphorusMilli?.value)
    val selenium =
        rememberNotRequiredFormField(product?.nutritionFacts?.seleniumMicro?.value)
    val iodine =
        rememberNotRequiredFormField(product?.nutritionFacts?.iodineMicro?.value)
    val chromium =
        rememberNotRequiredFormField(product?.nutritionFacts?.chromiumMicro?.value)

    val isModified = remember(product) {
        if (product != null) {
            derivedStateOf {
                product.name != name.value ||
                    product.brand != brand.value ||
                    product.barcode != barcode.value ||
                    product.totalWeight != packageWeight.value ||
                    product.servingWeight != servingWeight.value ||
                    product.nutritionFacts.proteins.value != proteins.value ||
                    product.nutritionFacts.carbohydrates.value != carbohydrates.value ||
                    product.nutritionFacts.fats.value != fats.value ||
                    product.nutritionFacts.energy.value != energy.value ||
                    product.nutritionFacts.saturatedFats.value != saturatedFats.value ||
                    product.nutritionFacts.monounsaturatedFats.value !=
                    monounsaturatedFats.value ||
                    product.nutritionFacts.polyunsaturatedFats.value !=
                    polyunsaturatedFats.value ||
                    product.nutritionFacts.omega3.value != omega3.value ||
                    product.nutritionFacts.omega6.value != omega6.value ||
                    product.nutritionFacts.sugars.value != sugars.value ||
                    product.nutritionFacts.salt.value != salt.value ||
                    product.nutritionFacts.fiber.value != fiber.value ||
                    product.nutritionFacts.cholesterolMilli.value != cholesterol.value ||
                    product.nutritionFacts.caffeineMilli.value != caffeine.value ||
                    product.nutritionFacts.vitaminAMicro.value != vitaminA.value ||
                    product.nutritionFacts.vitaminB1Milli.value != vitaminB1.value ||
                    product.nutritionFacts.vitaminB2Milli.value != vitaminB2.value ||
                    product.nutritionFacts.vitaminB3Milli.value != vitaminB3.value ||
                    product.nutritionFacts.vitaminB5Milli.value != vitaminB5.value ||
                    product.nutritionFacts.vitaminB6Milli.value != vitaminB6.value ||
                    product.nutritionFacts.vitaminB7Micro.value != vitaminB7.value ||
                    product.nutritionFacts.vitaminB9Micro.value != vitaminB9.value ||
                    product.nutritionFacts.vitaminB12Micro.value != vitaminB12.value ||
                    product.nutritionFacts.vitaminCMilli.value != vitaminC.value ||
                    product.nutritionFacts.vitaminDMicro.value != vitaminD.value ||
                    product.nutritionFacts.vitaminEMilli.value != vitaminE.value ||
                    product.nutritionFacts.vitaminKMicro.value != vitaminK.value ||
                    product.nutritionFacts.manganeseMilli.value != manganese.value ||
                    product.nutritionFacts.magnesiumMilli.value != magnesium.value ||
                    product.nutritionFacts.potassiumMilli.value != potassium.value ||
                    product.nutritionFacts.calciumMilli.value != calcium.value ||
                    product.nutritionFacts.copperMilli.value != copper.value ||
                    product.nutritionFacts.zincMilli.value != zinc.value ||
                    product.nutritionFacts.sodiumMilli.value != sodium.value ||
                    product.nutritionFacts.ironMilli.value != iron.value ||
                    product.nutritionFacts.phosphorusMilli.value != phosphorus.value ||
                    product.nutritionFacts.seleniumMicro.value != selenium.value ||
                    product.nutritionFacts.iodineMicro.value != iodine.value ||
                    product.nutritionFacts.chromiumMicro.value != chromium.value ||
                    product.note != note.value ||
                    Measurement.notEqual(
                        measurement.value,
                        Measurement.Gram(100f)
                    )
            }
        } else {
            derivedStateOf {
                name.value.isNotBlank() ||
                    brand.value != null ||
                    barcode.value != null ||
                    note.value != null ||
                    packageWeight.value != null ||
                    servingWeight.value != null ||
                    proteins.value != null ||
                    carbohydrates.value != null ||
                    fats.value != null ||
                    energy.value != null ||
                    saturatedFats.value != null ||
                    monounsaturatedFats.value != null ||
                    polyunsaturatedFats.value != null ||
                    omega3.value != null ||
                    omega6.value != null ||
                    sugars.value != null ||
                    salt.value != null ||
                    fiber.value != null ||
                    cholesterol.value != null ||
                    caffeine.value != null ||
                    vitaminA.value != null ||
                    vitaminB1.value != null ||
                    vitaminB2.value != null ||
                    vitaminB3.value != null ||
                    vitaminB5.value != null ||
                    vitaminB6.value != null ||
                    vitaminB7.value != null ||
                    vitaminB9.value != null ||
                    vitaminB12.value != null ||
                    vitaminC.value != null ||
                    vitaminD.value != null ||
                    vitaminE.value != null ||
                    vitaminK.value != null ||
                    manganese.value != null ||
                    magnesium.value != null ||
                    potassium.value != null ||
                    calcium.value != null ||
                    copper.value != null ||
                    zinc.value != null ||
                    sodium.value != null ||
                    iron.value != null ||
                    phosphorus.value != null ||
                    selenium.value != null ||
                    iodine.value != null ||
                    chromium.value != null ||
                    Measurement.notEqual(
                        measurement.value,
                        Measurement.Gram(100f)
                    )
            }
        }
    }

    return remember {
        ProductFormState(
            name = name,
            brand = brand,
            barcode = barcode,
            note = note,
            packageWeight = packageWeight,
            servingWeight = servingWeight,
            measurementState = measurement,
            proteins = proteins,
            carbohydrates = carbohydrates,
            fats = fats,
            energy = energy,
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
            chromiumMicro = chromium,
            seleniumMicro = selenium,
            iodineMicro = iodine,
            isModifiedState = isModified,
            autoCalculateEnergyState = autoCalculateEnergyState
        )
    }
}

@Composable
private fun rememberNotRequiredFormField(initialValue: Float? = null) = rememberFormField(
    initialValue = initialValue,
    parser = nullableFloatParser(
        onNotANumber = { ProductFormFieldError.NotANumber }
    ),
    validator = nonNegativeFloatValidator(
        onNegative = { ProductFormFieldError.NotPositive }
    ),
    textFieldState = rememberTextFieldState(initialValue?.formatClipZeros("%.4f") ?: "")
)

@Composable
private fun rememberRequiredFormField(initialValue: Float? = null) = rememberFormField(
    initialValue = initialValue,
    parser = nullableFloatParser(
        onNotANumber = { ProductFormFieldError.NotANumber },
        onNull = { ProductFormFieldError.Required }
    ),
    validator = nonNegativeFloatValidator(
        onNegative = { ProductFormFieldError.Negative },
        onNull = { ProductFormFieldError.Required }
    ),
    textFieldState = rememberTextFieldState(initialValue?.formatClipZeros("%.4f") ?: "")
)

@Stable
internal class ProductFormState(
    // General
    val name: FormField<String, ProductFormFieldError>,
    val brand: FormField<String?, Nothing>,
    val barcode: FormField<String?, Nothing>,
    val note: FormField<String?, Nothing>,
    // Weight
    measurementState: MutableState<Measurement>,
    val packageWeight: FormField<Float?, ProductFormFieldError>,
    val servingWeight: FormField<Float?, ProductFormFieldError>,
    // Macronutrients
    val proteins: FormField<Float?, ProductFormFieldError>,
    val carbohydrates: FormField<Float?, ProductFormFieldError>,
    val fats: FormField<Float?, ProductFormFieldError>,
    val energy: FormField<Float?, ProductFormFieldError>,
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
    val iodineMicro: FormField<Float?, ProductFormFieldError>,
    // Extra
    isModifiedState: State<Boolean>,
    autoCalculateEnergyState: MutableState<Boolean>
) {
    val isValid: Boolean
        get() = name.error == null &&
            packageWeight.error == null &&
            servingWeight.error == null &&
            proteins.error == null &&
            carbohydrates.error == null &&
            fats.error == null &&
            energy.error == null &&
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
            iodineMicro.error == null &&
            chromiumMicro.error == null

    var measurement: Measurement by measurementState
    val isModified: Boolean by isModifiedState
    var autoCalculateEnergy: Boolean by autoCalculateEnergyState
}

/**
 * Converts the [ProductFormState] to a [ProductEntity].
 *
 * @param multiplier The multiplier to apply to the nutrient values.
 * @return A [Result] containing the [ProductEntity] or an error if conversion fails.
 */
internal fun ProductFormState.toProductEntity(multiplier: Float): Result<ProductEntity> =
    runCatching {
        val proteins = proteins.value
        checkNotNull(proteins) { "Proteins cannot be null" }

        val carbohydrates = carbohydrates.value
        checkNotNull(carbohydrates) { "Carbohydrates cannot be null" }

        val fats = fats.value
        checkNotNull(fats) { "Fats cannot be null" }

        val energy = energy.value
        checkNotNull(energy) { "Energy cannot be null" }

        return Result.success(
            ProductEntity(
                name = name.value,
                brand = brand.value,
                barcode = barcode.value,
                nutrients = Nutrients(
                    proteins = proteins * multiplier,
                    carbohydrates = carbohydrates * multiplier,
                    fats = fats * multiplier,
                    energy = energy * multiplier,
                    saturatedFats = saturatedFats.value.applyMultiplier(multiplier),
                    monounsaturatedFats = monounsaturatedFats.value.applyMultiplier(multiplier),
                    polyunsaturatedFats = polyunsaturatedFats.value.applyMultiplier(multiplier),
                    omega3 = omega3.value.applyMultiplier(multiplier),
                    omega6 = omega6.value.applyMultiplier(multiplier),
                    sugars = sugars.value.applyMultiplier(multiplier),
                    salt = salt.value.applyMultiplier(multiplier),
                    fiber = fiber.value.applyMultiplier(multiplier),
                    cholesterolMilli = cholesterolMilli.value.applyMultiplier(multiplier),
                    caffeineMilli = caffeineMilli.value.applyMultiplier(multiplier)
                ),
                vitamins = Vitamins(
                    vitaminAMicro = vitaminAMicro.value.applyMultiplier(multiplier),
                    vitaminB1Milli = vitaminB1Milli.value.applyMultiplier(multiplier),
                    vitaminB2Milli = vitaminB2Milli.value.applyMultiplier(multiplier),
                    vitaminB3Milli = vitaminB3Milli.value.applyMultiplier(multiplier),
                    vitaminB5Milli = vitaminB5Milli.value.applyMultiplier(multiplier),
                    vitaminB6Milli = vitaminB6Milli.value.applyMultiplier(multiplier),
                    vitaminB7Micro = vitaminB7Micro.value.applyMultiplier(multiplier),
                    vitaminB9Micro = vitaminB9Micro.value.applyMultiplier(multiplier),
                    vitaminB12Micro = vitaminB12Micro.value.applyMultiplier(multiplier),
                    vitaminCMilli = vitaminCMilli.value.applyMultiplier(multiplier),
                    vitaminDMicro = vitaminDMicro.value.applyMultiplier(multiplier),
                    vitaminEMilli = vitaminEMilli.value.applyMultiplier(multiplier),
                    vitaminKMicro = vitaminKMicro.value.applyMultiplier(multiplier)
                ),
                minerals = Minerals(
                    manganeseMilli = manganeseMilli.value.applyMultiplier(multiplier),
                    magnesiumMilli = magnesiumMilli.value.applyMultiplier(multiplier),
                    potassiumMilli = potassiumMilli.value.applyMultiplier(multiplier),
                    calciumMilli = calciumMilli.value.applyMultiplier(multiplier),
                    copperMilli = copperMilli.value.applyMultiplier(multiplier),
                    zincMilli = zincMilli.value.applyMultiplier(multiplier),
                    sodiumMilli = sodiumMilli.value.applyMultiplier(multiplier),
                    ironMilli = ironMilli.value.applyMultiplier(multiplier),
                    phosphorusMilli = phosphorusMilli.value.applyMultiplier(multiplier),
                    seleniumMicro = seleniumMicro.value.applyMultiplier(multiplier),
                    iodineMicro = iodineMicro.value.applyMultiplier(multiplier),
                    chromiumMicro = chromiumMicro.value.applyMultiplier(multiplier)

                ),
                packageWeight = packageWeight.value,
                servingWeight = servingWeight.value,
                note = note.value
            )
        )
    }

private fun Float?.applyMultiplier(multiplier: Float): Float? = this?.let { it * multiplier }
