package com.maksimowiczm.foodyou.feature.food.product.ui

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
import com.maksimowiczm.foodyou.feature.shared.ui.Saver
import com.maksimowiczm.foodyou.food.domain.entity.Product
import com.maksimowiczm.foodyou.food.domain.entity.RemoteProduct
import com.maksimowiczm.foodyou.shared.domain.food.FoodSource
import com.maksimowiczm.foodyou.shared.domain.food.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.shared.domain.food.NutrientsHelper
import com.maksimowiczm.foodyou.shared.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.shared.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.ui.form.FormField
import com.maksimowiczm.foodyou.shared.ui.form.nonBlankStringValidator
import com.maksimowiczm.foodyou.shared.ui.form.nonNegativeFloatValidator
import com.maksimowiczm.foodyou.shared.ui.form.nullableFloatParser
import com.maksimowiczm.foodyou.shared.ui.form.nullableStringParser
import com.maksimowiczm.foodyou.shared.ui.form.positiveFloatValidator
import com.maksimowiczm.foodyou.shared.ui.form.rememberFormField
import com.maksimowiczm.foodyou.shared.ui.form.stringParser
import com.maksimowiczm.foodyou.shared.ui.res.formatClipZeros
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

@Composable
internal fun rememberProductFormState(product: Product? = null): ProductFormState {
    val name =
        rememberFormField(
            initialValue = product?.name ?: "",
            parser = stringParser(),
            validator = nonBlankStringValidator(onEmpty = { ProductFormFieldError.Required }),
            textFieldState = rememberTextFieldState(product?.name ?: ""),
        )

    val brand =
        rememberFormField<String?, Nothing>(
            initialValue = product?.brand,
            parser = nullableStringParser(),
            textFieldState = rememberTextFieldState(product?.brand ?: ""),
        )

    val barcode =
        rememberFormField<String?, Nothing>(
            initialValue = product?.barcode ?: "",
            parser = nullableStringParser(),
            textFieldState = rememberTextFieldState(product?.barcode ?: ""),
        )

    val note =
        rememberFormField<String?, Nothing>(
            initialValue = product?.note ?: "",
            parser = nullableStringParser(),
            textFieldState = rememberTextFieldState(product?.note ?: ""),
        )

    val measurement =
        rememberSaveable(stateSaver = Measurement.Saver) { mutableStateOf(Measurement.Gram(100.0)) }

    val packageWeight =
        rememberFormField(
            initialValue = product?.packageWeight?.toFloat(),
            parser = nullableFloatParser(onNotANumber = { ProductFormFieldError.NotANumber }),
            validator =
                when {
                    measurement.value is Measurement.Package ->
                        positiveFloatValidator(
                            onNotPositive = { ProductFormFieldError.NotPositive },
                            onNull = { ProductFormFieldError.Required },
                        )

                    else ->
                        positiveFloatValidator(
                            onNotPositive = { ProductFormFieldError.NotPositive }
                        )
                },
            textFieldState = rememberTextFieldState(product?.packageWeight?.formatClipZeros() ?: ""),
        )

    val servingWeight =
        rememberFormField(
            initialValue = product?.servingWeight?.toFloat(),
            parser = nullableFloatParser(onNotANumber = { ProductFormFieldError.NotANumber }),
            validator =
                when {
                    measurement.value is Measurement.Serving ->
                        positiveFloatValidator(
                            onNotPositive = { ProductFormFieldError.NotPositive },
                            onNull = { ProductFormFieldError.Required },
                        )

                    else ->
                        positiveFloatValidator(
                            onNotPositive = { ProductFormFieldError.NotPositive }
                        )
                },
            textFieldState = rememberTextFieldState(product?.servingWeight?.formatClipZeros() ?: ""),
        )

    val proteins = rememberRequiredFormField(product?.nutritionFacts?.proteins?.value)
    val carbohydrates = rememberRequiredFormField(product?.nutritionFacts?.carbohydrates?.value)
    val fats = rememberRequiredFormField(product?.nutritionFacts?.fats?.value)
    val energy = rememberRequiredFormField(product?.nutritionFacts?.energy?.value)

    val autoCalculateEnergyState =
        rememberSaveable(product) {
            if (product == null) {
                mutableStateOf(true)
            } else {
                val energy = product.nutritionFacts.energy.value
                val proteins = product.nutritionFacts.proteins.value
                val carbohydrates = product.nutritionFacts.carbohydrates.value
                val fats = product.nutritionFacts.fats.value

                val initialState =
                    if (
                        energy == null || proteins == null || carbohydrates == null || fats == null
                    ) {
                        true
                    } else {
                        NutrientsHelper.calculateEnergy(
                            proteins = proteins,
                            carbohydrates = carbohydrates,
                            fats = fats,
                        ) == energy
                    }

                mutableStateOf(initialState)
            }
        }

    LaunchedEffect(autoCalculateEnergyState, proteins, carbohydrates, fats) {
        launch {
            snapshotFlow { autoCalculateEnergyState.value }
                .drop(1)
                .filter { it }
                .collectLatest {
                    val proteinsValue = proteins.value
                    val carbohydratesValue = carbohydrates.value
                    val fatsValue = fats.value

                    if (proteinsValue != null && carbohydratesValue != null && fatsValue != null) {
                        val kcal =
                            NutrientsHelper.calculateEnergy(
                                proteins = proteinsValue,
                                carbohydrates = carbohydratesValue,
                                fats = fatsValue,
                            )

                        val text = kcal.formatClipZeros()
                        energy.textFieldState.setTextAndPlaceCursorAtEnd(text)
                    }
                }
        }

        launch {
            val caloriesFlow =
                combine(
                        snapshotFlow { proteins.value },
                        snapshotFlow { carbohydrates.value },
                        snapshotFlow { fats.value },
                    ) {
                        it
                    }
                    .drop(1)
                    .mapNotNull {
                        val (proteins, carbohydrates, fats) = it
                        if (proteins == null || carbohydrates == null || fats == null) {
                            return@mapNotNull null
                        }

                        NutrientsHelper.calculateEnergy(
                            proteins = proteins,
                            carbohydrates = carbohydrates,
                            fats = fats,
                        )
                    }

            combine(snapshotFlow { autoCalculateEnergyState.value }, caloriesFlow) {
                    autoCalculateEnergy,
                    calories ->
                    if (!autoCalculateEnergy) {
                        return@combine null
                    }

                    calories
                }
                .filterNotNull()
                .collectLatest { kcal ->
                    val text = kcal.formatClipZeros()
                    energy.textFieldState.setTextAndPlaceCursorAtEnd(text)
                }
        }
    }

    val saturatedFats = rememberNotRequiredFormField(product?.nutritionFacts?.saturatedFats?.value)
    val transFats = rememberNotRequiredFormField(product?.nutritionFacts?.transFats?.value)
    val monounsaturatedFats =
        rememberNotRequiredFormField(product?.nutritionFacts?.monounsaturatedFats?.value)
    val polyunsaturatedFats =
        rememberNotRequiredFormField(product?.nutritionFacts?.polyunsaturatedFats?.value)
    val omega3 = rememberNotRequiredFormField(product?.nutritionFacts?.omega3?.value)
    val omega6 = rememberNotRequiredFormField(product?.nutritionFacts?.omega6?.value)

    val sugars = rememberNotRequiredFormField(product?.nutritionFacts?.sugars?.value)
    val addedSugars = rememberNotRequiredFormField(product?.nutritionFacts?.addedSugars?.value)
    val salt = rememberNotRequiredFormField(product?.nutritionFacts?.salt?.value)
    val dietaryFiber = rememberNotRequiredFormField(product?.nutritionFacts?.dietaryFiber?.value)
    val solubleFiber = rememberNotRequiredFormField(product?.nutritionFacts?.solubleFiber?.value)
    val insolubleFiber =
        rememberNotRequiredFormField(product?.nutritionFacts?.insolubleFiber?.value)
    val cholesterol =
        rememberNotRequiredFormField(product?.nutritionFacts?.cholesterol?.value?.times(1_000.0))
    val caffeine =
        rememberNotRequiredFormField(product?.nutritionFacts?.caffeine?.times(1_000.0)?.value)

    val vitaminA =
        rememberNotRequiredFormField(product?.nutritionFacts?.vitaminA?.times(1_000_000.0)?.value)
    val vitaminB1 =
        rememberNotRequiredFormField(product?.nutritionFacts?.vitaminB1?.times(1_000.0)?.value)
    val vitaminB2 =
        rememberNotRequiredFormField(product?.nutritionFacts?.vitaminB2?.times(1_000.0)?.value)
    val vitaminB3 =
        rememberNotRequiredFormField(product?.nutritionFacts?.vitaminB3?.times(1_000.0)?.value)
    val vitaminB5 =
        rememberNotRequiredFormField(product?.nutritionFacts?.vitaminB5?.times(1_000.0)?.value)
    val vitaminB6 =
        rememberNotRequiredFormField(product?.nutritionFacts?.vitaminB6?.times(1_000.0)?.value)
    val vitaminB7 =
        rememberNotRequiredFormField(product?.nutritionFacts?.vitaminB7?.times(1_000_000.0)?.value)
    val vitaminB9 =
        rememberNotRequiredFormField(product?.nutritionFacts?.vitaminB9?.times(1_000_000.0)?.value)
    val vitaminB12 =
        rememberNotRequiredFormField(product?.nutritionFacts?.vitaminB12?.times(1_000_000.0)?.value)
    val vitaminC =
        rememberNotRequiredFormField(product?.nutritionFacts?.vitaminC?.times(1_000.0)?.value)
    val vitaminD =
        rememberNotRequiredFormField(product?.nutritionFacts?.vitaminD?.times(1_000_000.0)?.value)
    val vitaminE =
        rememberNotRequiredFormField(product?.nutritionFacts?.vitaminE?.times(1_000.0)?.value)
    val vitaminK =
        rememberNotRequiredFormField(product?.nutritionFacts?.vitaminK?.times(1_000_000.0)?.value)

    val manganese =
        rememberNotRequiredFormField(product?.nutritionFacts?.manganese?.times(1_000.0)?.value)
    val magnesium =
        rememberNotRequiredFormField(product?.nutritionFacts?.magnesium?.times(1_000.0)?.value)
    val potassium =
        rememberNotRequiredFormField(product?.nutritionFacts?.potassium?.times(1_000.0)?.value)
    val calcium =
        rememberNotRequiredFormField(product?.nutritionFacts?.calcium?.times(1_000.0)?.value)
    val copper =
        rememberNotRequiredFormField(product?.nutritionFacts?.copper?.times(1_000.0)?.value)
    val zinc = rememberNotRequiredFormField(product?.nutritionFacts?.zinc?.times(1_000.0)?.value)
    val sodium =
        rememberNotRequiredFormField(product?.nutritionFacts?.sodium?.times(1_000.0)?.value)
    val iron = rememberNotRequiredFormField(product?.nutritionFacts?.iron?.times(1_000.0)?.value)
    val phosphorus =
        rememberNotRequiredFormField(product?.nutritionFacts?.phosphorus?.times(1_000.0)?.value)
    val selenium =
        rememberNotRequiredFormField(product?.nutritionFacts?.selenium?.times(1_000_000.0)?.value)
    val iodine =
        rememberNotRequiredFormField(product?.nutritionFacts?.iodine?.times(1_000_000.0)?.value)
    val chromium =
        rememberNotRequiredFormField(product?.nutritionFacts?.chromium?.times(1_000_000.0)?.value)

    val sourceType =
        rememberSaveable(product) { mutableStateOf(product?.source?.type ?: FoodSource.Type.User) }
    val sourceUrl =
        rememberFormField<String?, Nothing>(
            initialValue = product?.source?.url,
            parser = nullableStringParser(),
            textFieldState = rememberTextFieldState(product?.source?.url ?: ""),
        )
    val isLiquid = rememberSaveable(product) { mutableStateOf(product?.isLiquid ?: false) }

    val isModified =
        remember(product) {
            if (product != null) {
                derivedStateOf {
                    name.value != product.name ||
                        brand.value != product.brand ||
                        barcode.value != product.barcode ||
                        note.value != product.note ||
                        sourceType.value != product.source.type ||
                        sourceUrl.value != product.source.url ||
                        isLiquid.value != product.isLiquid ||
                        packageWeight.value != product.packageWeight?.toFloat() ||
                        servingWeight.value != product.servingWeight?.toFloat() ||
                        proteins.value != product.nutritionFacts.proteins.value?.toFloat() ||
                        carbohydrates.value !=
                            product.nutritionFacts.carbohydrates.value?.toFloat() ||
                        fats.value != product.nutritionFacts.fats.value?.toFloat() ||
                        energy.value != product.nutritionFacts.energy.value?.toFloat() ||
                        saturatedFats.value !=
                            product.nutritionFacts.saturatedFats.value?.toFloat() ||
                        transFats.value != product.nutritionFacts.transFats.value?.toFloat() ||
                        monounsaturatedFats.value !=
                            product.nutritionFacts.monounsaturatedFats.value?.toFloat() ||
                        polyunsaturatedFats.value !=
                            product.nutritionFacts.polyunsaturatedFats.value?.toFloat() ||
                        omega3.value != product.nutritionFacts.omega3.value?.toFloat() ||
                        omega6.value != product.nutritionFacts.omega6.value?.toFloat() ||
                        sugars.value != product.nutritionFacts.sugars.value?.toFloat() ||
                        addedSugars.value != product.nutritionFacts.addedSugars.value?.toFloat() ||
                        salt.value != product.nutritionFacts.salt.value?.toFloat() ||
                        dietaryFiber.value !=
                            product.nutritionFacts.dietaryFiber.value?.toFloat() ||
                        solubleFiber.value !=
                            product.nutritionFacts.solubleFiber.value?.toFloat() ||
                        insolubleFiber.value !=
                            product.nutritionFacts.insolubleFiber.value?.toFloat() ||
                        cholesterol.value !=
                            product.nutritionFacts.cholesterol.value?.times(1_000.0)?.toFloat() ||
                        caffeine.value !=
                            product.nutritionFacts.caffeine.value?.times(1_000.0)?.toFloat() ||
                        vitaminA.value !=
                            product.nutritionFacts.vitaminA.value?.times(1_000_000.0)?.toFloat() ||
                        vitaminB1.value !=
                            product.nutritionFacts.vitaminB1.value?.times(1_000.0)?.toFloat() ||
                        vitaminB2.value !=
                            product.nutritionFacts.vitaminB2.value?.times(1_000.0)?.toFloat() ||
                        vitaminB3.value !=
                            product.nutritionFacts.vitaminB3.value?.times(1_000.0)?.toFloat() ||
                        vitaminB5.value !=
                            product.nutritionFacts.vitaminB5.value?.times(1_000.0)?.toFloat() ||
                        vitaminB6.value !=
                            product.nutritionFacts.vitaminB6.value?.times(1_000.0)?.toFloat() ||
                        vitaminB7.value !=
                            product.nutritionFacts.vitaminB7.value?.times(1_000_000.0)?.toFloat() ||
                        vitaminB9.value !=
                            product.nutritionFacts.vitaminB9.value?.times(1_000_000.0)?.toFloat() ||
                        vitaminB12.value !=
                            product.nutritionFacts.vitaminB12.value
                                ?.times(1_000_000.0)
                                ?.toFloat() ||
                        vitaminC.value !=
                            product.nutritionFacts.vitaminC.value?.times(1_000.0)?.toFloat() ||
                        vitaminD.value !=
                            product.nutritionFacts.vitaminD.value?.times(1_000_000.0)?.toFloat() ||
                        vitaminE.value !=
                            product.nutritionFacts.vitaminE.value?.times(1_000.0)?.toFloat() ||
                        vitaminK.value !=
                            product.nutritionFacts.vitaminK.value?.times(1_000_000.0)?.toFloat() ||
                        manganese.value !=
                            product.nutritionFacts.manganese.value?.times(1_000.0)?.toFloat() ||
                        magnesium.value !=
                            product.nutritionFacts.magnesium.value?.times(1_000.0)?.toFloat() ||
                        potassium.value !=
                            product.nutritionFacts.potassium.value?.times(1_000.0)?.toFloat() ||
                        calcium.value !=
                            product.nutritionFacts.calcium.value?.times(1_000.0)?.toFloat() ||
                        copper.value !=
                            product.nutritionFacts.copper.value?.times(1_000.0)?.toFloat() ||
                        zinc.value !=
                            product.nutritionFacts.zinc.value?.times(1_000.0)?.toFloat() ||
                        sodium.value !=
                            product.nutritionFacts.sodium.value?.times(1_000.0)?.toFloat() ||
                        iron.value !=
                            product.nutritionFacts.iron.value?.times(1_000.0)?.toFloat() ||
                        phosphorus.value !=
                            product.nutritionFacts.phosphorus.value?.times(1_000.0)?.toFloat() ||
                        selenium.value !=
                            product.nutritionFacts.selenium.value?.times(1_000_000.0)?.toFloat() ||
                        iodine.value !=
                            product.nutritionFacts.iodine.value?.times(1_000_000.0)?.toFloat() ||
                        chromium.value !=
                            product.nutritionFacts.chromium.value?.times(1_000_000.0)?.toFloat()
                }
            } else {
                derivedStateOf {
                    name.value.isNotEmpty() ||
                        brand.value != null ||
                        barcode.value != null ||
                        note.value != null ||
                        sourceType.value != FoodSource.Type.User ||
                        sourceUrl.value != null ||
                        isLiquid.value ||
                        packageWeight.value != null ||
                        servingWeight.value != null ||
                        proteins.value != null ||
                        carbohydrates.value != null ||
                        fats.value != null ||
                        energy.value != null ||
                        saturatedFats.value != null ||
                        transFats.value != null ||
                        monounsaturatedFats.value != null ||
                        polyunsaturatedFats.value != null ||
                        omega3.value != null ||
                        omega6.value != null ||
                        sugars.value != null ||
                        addedSugars.value != null ||
                        salt.value != null ||
                        dietaryFiber.value != null ||
                        solubleFiber.value != null ||
                        insolubleFiber.value != null ||
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
                        chromium.value != null
                }
            }
        }

    return remember(
        name,
        brand,
        barcode,
        note,
        sourceType,
        sourceUrl,
        isLiquid,
        measurement,
        packageWeight,
        servingWeight,
        energy,
        proteins,
        fats,
        saturatedFats,
        transFats,
        monounsaturatedFats,
        polyunsaturatedFats,
        omega3,
        omega6,
        carbohydrates,
        sugars,
        addedSugars,
        dietaryFiber,
        solubleFiber,
        insolubleFiber,
        salt,
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
        iodine,
        chromium,
        isModified,
    ) {
        ProductFormState(
            name = name,
            brand = brand,
            barcode = barcode,
            note = note,
            sourceTypeState = sourceType,
            sourceUrl = sourceUrl,
            isLiquidState = isLiquid,
            measurementState = measurement,
            packageWeight = packageWeight,
            servingWeight = servingWeight,
            energy = energy,
            proteins = proteins,
            fats = fats,
            saturatedFats = saturatedFats,
            transFats = transFats,
            monounsaturatedFats = monounsaturatedFats,
            polyunsaturatedFats = polyunsaturatedFats,
            omega3 = omega3,
            omega6 = omega6,
            carbohydrates = carbohydrates,
            sugars = sugars,
            addedSugars = addedSugars,
            dietaryFiber = dietaryFiber,
            solubleFiber = solubleFiber,
            insolubleFiber = insolubleFiber,
            salt = salt,
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
            autoCalculateEnergyState = autoCalculateEnergyState,
        )
    }
}

@Composable
internal fun rememberProductFormState(product: RemoteProduct): ProductFormState {
    val name =
        rememberFormField(
            initialValue = product.name ?: "",
            parser = stringParser(),
            validator = nonBlankStringValidator(onEmpty = { ProductFormFieldError.Required }),
            textFieldState = rememberTextFieldState(product.name ?: ""),
        )

    val brand =
        rememberFormField<String?, Nothing>(
            initialValue = product.brand,
            parser = nullableStringParser(),
            textFieldState = rememberTextFieldState(product.brand ?: ""),
        )

    val barcode =
        rememberFormField<String?, Nothing>(
            initialValue = product.barcode ?: "",
            parser = nullableStringParser(),
            textFieldState = rememberTextFieldState(product.barcode ?: ""),
        )

    val note =
        rememberFormField<String?, Nothing>(
            initialValue = "",
            parser = nullableStringParser(),
            textFieldState = rememberTextFieldState(""),
        )

    val measurement =
        rememberSaveable(stateSaver = Measurement.Saver) { mutableStateOf(Measurement.Gram(100.0)) }

    val packageWeight =
        rememberFormField(
            initialValue = product.packageWeight?.toFloat(),
            parser = nullableFloatParser(onNotANumber = { ProductFormFieldError.NotANumber }),
            validator =
                when {
                    measurement.value is Measurement.Package ->
                        positiveFloatValidator(
                            onNotPositive = { ProductFormFieldError.NotPositive },
                            onNull = { ProductFormFieldError.Required },
                        )

                    else ->
                        positiveFloatValidator(
                            onNotPositive = { ProductFormFieldError.NotPositive }
                        )
                },
            textFieldState = rememberTextFieldState(product.packageWeight?.formatClipZeros() ?: ""),
        )

    val servingWeight =
        rememberFormField(
            initialValue = product.servingWeight?.toFloat(),
            parser = nullableFloatParser(onNotANumber = { ProductFormFieldError.NotANumber }),
            validator =
                when {
                    measurement.value is Measurement.Serving ->
                        positiveFloatValidator(
                            onNotPositive = { ProductFormFieldError.NotPositive },
                            onNull = { ProductFormFieldError.Required },
                        )

                    else ->
                        positiveFloatValidator(
                            onNotPositive = { ProductFormFieldError.NotPositive }
                        )
                },
            textFieldState = rememberTextFieldState(product.servingWeight?.formatClipZeros() ?: ""),
        )

    val proteins = rememberRequiredFormField(product.nutritionFacts?.proteins)
    val carbohydrates = rememberRequiredFormField(product.nutritionFacts?.carbohydrates)
    val fats = rememberRequiredFormField(product.nutritionFacts?.fats)
    val energy = rememberRequiredFormField(product.nutritionFacts?.energy)

    val autoCalculateEnergyState =
        rememberSaveable(product) {
            val energy = product.nutritionFacts?.energy
            val proteins = product.nutritionFacts?.proteins
            val carbohydrates = product.nutritionFacts?.carbohydrates
            val fats = product.nutritionFacts?.fats

            val initialState =
                if (energy == null || proteins == null || carbohydrates == null || fats == null) {
                    true
                } else {
                    NutrientsHelper.calculateEnergy(
                        proteins = proteins,
                        carbohydrates = carbohydrates,
                        fats = fats,
                    ) == energy
                }

            mutableStateOf(initialState)
        }

    LaunchedEffect(autoCalculateEnergyState, proteins, carbohydrates, fats) {
        launch {
            snapshotFlow { autoCalculateEnergyState.value }
                .drop(1)
                .filter { it }
                .collectLatest {
                    val proteinsValue = proteins.value
                    val carbohydratesValue = carbohydrates.value
                    val fatsValue = fats.value

                    if (proteinsValue != null && carbohydratesValue != null && fatsValue != null) {
                        val kcal =
                            NutrientsHelper.calculateEnergy(
                                proteins = proteinsValue,
                                carbohydrates = carbohydratesValue,
                                fats = fatsValue,
                            )

                        val text = kcal.formatClipZeros()
                        energy.textFieldState.setTextAndPlaceCursorAtEnd(text)
                    }
                }
        }

        launch {
            val caloriesFlow =
                combine(
                        snapshotFlow { proteins.value },
                        snapshotFlow { carbohydrates.value },
                        snapshotFlow { fats.value },
                    ) {
                        it
                    }
                    .drop(1)
                    .mapNotNull {
                        val (proteins, carbohydrates, fats) = it
                        if (proteins == null || carbohydrates == null || fats == null) {
                            return@mapNotNull null
                        }

                        NutrientsHelper.calculateEnergy(
                            proteins = proteins,
                            carbohydrates = carbohydrates,
                            fats = fats,
                        )
                    }

            combine(snapshotFlow { autoCalculateEnergyState.value }, caloriesFlow) {
                    autoCalculateEnergy,
                    calories ->
                    if (!autoCalculateEnergy) {
                        return@combine null
                    }

                    calories
                }
                .filterNotNull()
                .collectLatest { kcal ->
                    val text = kcal.formatClipZeros()
                    energy.textFieldState.setTextAndPlaceCursorAtEnd(text)
                }
        }
    }

    val saturatedFats = rememberNotRequiredFormField(product.nutritionFacts?.saturatedFats)
    val transFats = rememberNotRequiredFormField(product.nutritionFacts?.transFats)
    val monounsaturatedFats =
        rememberNotRequiredFormField(product.nutritionFacts?.monounsaturatedFats)
    val polyunsaturatedFats =
        rememberNotRequiredFormField(product.nutritionFacts?.polyunsaturatedFats)
    val omega3 = rememberNotRequiredFormField(product.nutritionFacts?.omega3)
    val omega6 = rememberNotRequiredFormField(product.nutritionFacts?.omega6)

    val sugars = rememberNotRequiredFormField(product.nutritionFacts?.sugars)
    val addedSugars = rememberNotRequiredFormField(product.nutritionFacts?.addedSugars)
    val salt = rememberNotRequiredFormField(product.nutritionFacts?.salt)
    val dietaryFiber = rememberNotRequiredFormField(product.nutritionFacts?.dietaryFiber)
    val solubleFiber = rememberNotRequiredFormField(product.nutritionFacts?.solubleFiber)
    val insolubleFiber = rememberNotRequiredFormField(product.nutritionFacts?.insolubleFiber)
    val cholesterol =
        rememberNotRequiredFormField(product.nutritionFacts?.cholesterol?.times(1_000.0))
    val caffeine = rememberNotRequiredFormField(product.nutritionFacts?.caffeine?.times(1_000.0))

    val vitaminA =
        rememberNotRequiredFormField(product.nutritionFacts?.vitaminA?.times(1_000_000.0))
    val vitaminB1 = rememberNotRequiredFormField(product.nutritionFacts?.vitaminB1?.times(1_000.0))
    val vitaminB2 = rememberNotRequiredFormField(product.nutritionFacts?.vitaminB2?.times(1_000.0))
    val vitaminB3 = rememberNotRequiredFormField(product.nutritionFacts?.vitaminB3?.times(1_000.0))
    val vitaminB5 = rememberNotRequiredFormField(product.nutritionFacts?.vitaminB5?.times(1_000.0))
    val vitaminB6 = rememberNotRequiredFormField(product.nutritionFacts?.vitaminB6?.times(1_000.0))
    val vitaminB7 =
        rememberNotRequiredFormField(product.nutritionFacts?.vitaminB7?.times(1_000_000.0))
    val vitaminB9 =
        rememberNotRequiredFormField(product.nutritionFacts?.vitaminB9?.times(1_000_000.0))
    val vitaminB12 =
        rememberNotRequiredFormField(product.nutritionFacts?.vitaminB12?.times(1_000_000.0))
    val vitaminC = rememberNotRequiredFormField(product.nutritionFacts?.vitaminC?.times(1_000.0))
    val vitaminD =
        rememberNotRequiredFormField(product.nutritionFacts?.vitaminD?.times(1_000_000.0))
    val vitaminE = rememberNotRequiredFormField(product.nutritionFacts?.vitaminE?.times(1_000.0))
    val vitaminK =
        rememberNotRequiredFormField(product.nutritionFacts?.vitaminK?.times(1_000_000.0))

    val manganese = rememberNotRequiredFormField(product.nutritionFacts?.manganese?.times(1_000.0))
    val magnesium = rememberNotRequiredFormField(product.nutritionFacts?.magnesium?.times(1_000.0))
    val potassium = rememberNotRequiredFormField(product.nutritionFacts?.potassium?.times(1_000.0))
    val calcium = rememberNotRequiredFormField(product.nutritionFacts?.calcium?.times(1_000.0))
    val copper = rememberNotRequiredFormField(product.nutritionFacts?.copper?.times(1_000.0))
    val zinc = rememberNotRequiredFormField(product.nutritionFacts?.zinc?.times(1_000.0))
    val sodium = rememberNotRequiredFormField(product.nutritionFacts?.sodium?.times(1_000.0))
    val iron = rememberNotRequiredFormField(product.nutritionFacts?.iron?.times(1_000.0))
    val phosphorus =
        rememberNotRequiredFormField(product.nutritionFacts?.phosphorus?.times(1_000.0))
    val selenium =
        rememberNotRequiredFormField(product.nutritionFacts?.selenium?.times(1_000_000.0))
    val iodine = rememberNotRequiredFormField(product.nutritionFacts?.iodine?.times(1_000_000.0))
    val chromium =
        rememberNotRequiredFormField(product.nutritionFacts?.chromium?.times(1_000_000.0))

    val sourceType = rememberSaveable(product) { mutableStateOf(product.source.type) }
    val sourceUrl =
        rememberFormField<String?, Nothing>(
            initialValue = product.source.url,
            parser = nullableStringParser(),
            textFieldState = rememberTextFieldState(product.source.url ?: ""),
        )
    val isLiquid = rememberSaveable(product) { mutableStateOf(product.isLiquid) }

    val isModified =
        remember(product) {
            derivedStateOf {
                name.value != (product.name ?: "") ||
                    brand.value != product.brand ||
                    barcode.value != product.barcode ||
                    note.value.isNullOrEmpty() ||
                    sourceType.value != product.source.type ||
                    sourceUrl.value != product.source.url ||
                    isLiquid.value != product.isLiquid ||
                    packageWeight.value != product.packageWeight?.toFloat() ||
                    servingWeight.value != product.servingWeight?.toFloat() ||
                    proteins.value != product.nutritionFacts?.proteins?.toFloat() ||
                    carbohydrates.value != product.nutritionFacts?.carbohydrates?.toFloat() ||
                    fats.value != product.nutritionFacts?.fats?.toFloat() ||
                    energy.value != product.nutritionFacts?.energy?.toFloat() ||
                    saturatedFats.value != product.nutritionFacts?.saturatedFats?.toFloat() ||
                    transFats.value != product.nutritionFacts?.transFats?.toFloat() ||
                    monounsaturatedFats.value !=
                        product.nutritionFacts?.monounsaturatedFats?.toFloat() ||
                    polyunsaturatedFats.value !=
                        product.nutritionFacts?.polyunsaturatedFats?.toFloat() ||
                    omega3.value != product.nutritionFacts?.omega3?.toFloat() ||
                    omega6.value != product.nutritionFacts?.omega6?.toFloat() ||
                    sugars.value != product.nutritionFacts?.sugars?.toFloat() ||
                    addedSugars.value != product.nutritionFacts?.addedSugars?.toFloat() ||
                    salt.value != product.nutritionFacts?.salt?.toFloat() ||
                    dietaryFiber.value != product.nutritionFacts?.dietaryFiber?.toFloat() ||
                    solubleFiber.value != product.nutritionFacts?.solubleFiber?.toFloat() ||
                    insolubleFiber.value != product.nutritionFacts?.insolubleFiber?.toFloat() ||
                    cholesterol.value !=
                        product.nutritionFacts?.cholesterol?.times(1_000.0)?.toFloat() ||
                    caffeine.value != product.nutritionFacts?.caffeine?.times(1_000.0)?.toFloat() ||
                    vitaminA.value !=
                        product.nutritionFacts?.vitaminA?.times(1_000_000.0)?.toFloat() ||
                    vitaminB1.value !=
                        product.nutritionFacts?.vitaminB1?.times(1_000.0)?.toFloat() ||
                    vitaminB2.value !=
                        product.nutritionFacts?.vitaminB2?.times(1_000.0)?.toFloat() ||
                    vitaminB3.value !=
                        product.nutritionFacts?.vitaminB3?.times(1_000.0)?.toFloat() ||
                    vitaminB5.value !=
                        product.nutritionFacts?.vitaminB5?.times(1_000.0)?.toFloat() ||
                    vitaminB6.value !=
                        product.nutritionFacts?.vitaminB6?.times(1_000.0)?.toFloat() ||
                    vitaminB7.value !=
                        product.nutritionFacts?.vitaminB7?.times(1_000_000.0)?.toFloat() ||
                    vitaminB9.value !=
                        product.nutritionFacts?.vitaminB9?.times(1_000_000.0)?.toFloat() ||
                    vitaminB12.value !=
                        product.nutritionFacts?.vitaminB12?.times(1_000_000.0)?.toFloat() ||
                    vitaminC.value != product.nutritionFacts?.vitaminC?.times(1_000.0)?.toFloat() ||
                    vitaminD.value !=
                        product.nutritionFacts?.vitaminD?.times(1_000_000.0)?.toFloat() ||
                    vitaminE.value != product.nutritionFacts?.vitaminE?.times(1_000.0)?.toFloat() ||
                    vitaminK.value !=
                        product.nutritionFacts?.vitaminK?.times(1_000_000.0)?.toFloat() ||
                    manganese.value !=
                        product.nutritionFacts?.manganese?.times(1_000.0)?.toFloat() ||
                    magnesium.value !=
                        product.nutritionFacts?.magnesium?.times(1_000.0)?.toFloat() ||
                    potassium.value !=
                        product.nutritionFacts?.potassium?.times(1_000.0)?.toFloat() ||
                    calcium.value != product.nutritionFacts?.calcium?.times(1_000.0)?.toFloat() ||
                    copper.value != product.nutritionFacts?.copper?.times(1_000.0)?.toFloat() ||
                    zinc.value != product.nutritionFacts?.zinc?.times(1_000.0)?.toFloat() ||
                    sodium.value != product.nutritionFacts?.sodium?.times(1_000.0)?.toFloat() ||
                    iron.value != product.nutritionFacts?.iron?.times(1_000.0)?.toFloat() ||
                    phosphorus.value !=
                        product.nutritionFacts?.phosphorus?.times(1_000.0)?.toFloat() ||
                    selenium.value !=
                        product.nutritionFacts?.selenium?.times(1_000_000.0)?.toFloat() ||
                    iodine.value != product.nutritionFacts?.iodine?.times(1_000_000.0)?.toFloat() ||
                    chromium.value !=
                        product.nutritionFacts?.chromium?.times(1_000_000.0)?.toFloat()
            }
        }

    return remember(
        name,
        brand,
        barcode,
        note,
        sourceType,
        sourceUrl,
        isLiquid,
        measurement,
        packageWeight,
        servingWeight,
        energy,
        proteins,
        fats,
        saturatedFats,
        transFats,
        monounsaturatedFats,
        polyunsaturatedFats,
        omega3,
        omega6,
        carbohydrates,
        sugars,
        addedSugars,
        dietaryFiber,
        solubleFiber,
        insolubleFiber,
        salt,
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
        iodine,
        chromium,
    ) {
        ProductFormState(
            name = name,
            brand = brand,
            barcode = barcode,
            note = note,
            sourceTypeState = sourceType,
            sourceUrl = sourceUrl,
            isLiquidState = isLiquid,
            measurementState = measurement,
            packageWeight = packageWeight,
            servingWeight = servingWeight,
            energy = energy,
            proteins = proteins,
            fats = fats,
            saturatedFats = saturatedFats,
            transFats = transFats,
            monounsaturatedFats = monounsaturatedFats,
            polyunsaturatedFats = polyunsaturatedFats,
            omega3 = omega3,
            omega6 = omega6,
            carbohydrates = carbohydrates,
            sugars = sugars,
            addedSugars = addedSugars,
            dietaryFiber = dietaryFiber,
            solubleFiber = solubleFiber,
            insolubleFiber = insolubleFiber,
            salt = salt,
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
            autoCalculateEnergyState = autoCalculateEnergyState,
        )
    }
}

@Composable
private fun rememberNotRequiredFormField(initialValue: Double? = null) =
    rememberFormField(
        initialValue = initialValue?.toFloat(),
        parser = nullableFloatParser(onNotANumber = { ProductFormFieldError.NotANumber }),
        validator = nonNegativeFloatValidator(onNegative = { ProductFormFieldError.NotPositive }),
        textFieldState = rememberTextFieldState(initialValue?.formatClipZeros("%.4f") ?: ""),
    )

@Composable
private fun rememberRequiredFormField(initialValue: Double? = null) =
    rememberFormField(
        initialValue = initialValue?.toFloat(),
        parser =
            nullableFloatParser(
                onNotANumber = { ProductFormFieldError.NotANumber },
                onNull = { ProductFormFieldError.Required },
            ),
        validator =
            nonNegativeFloatValidator(
                onNegative = { ProductFormFieldError.Negative },
                onNull = { ProductFormFieldError.Required },
            ),
        textFieldState = rememberTextFieldState(initialValue?.formatClipZeros("%.4f") ?: ""),
    )

@Stable
internal class ProductFormState(
    // General
    val name: FormField<String, ProductFormFieldError>,
    val brand: FormField<String?, Nothing>,
    val barcode: FormField<String?, Nothing>,
    val note: FormField<String?, Nothing>,
    sourceTypeState: MutableState<FoodSource.Type>,
    val sourceUrl: FormField<String?, Nothing>,
    isLiquidState: MutableState<Boolean>,
    // Weight
    measurementState: MutableState<Measurement>,
    val packageWeight: FormField<Float?, ProductFormFieldError>,
    val servingWeight: FormField<Float?, ProductFormFieldError>,
    // Nutrients
    val energy: FormField<Float?, ProductFormFieldError>,
    // Proteins
    val proteins: FormField<Float?, ProductFormFieldError>,
    // Fats
    val fats: FormField<Float?, ProductFormFieldError>,
    val saturatedFats: FormField<Float?, ProductFormFieldError>,
    val transFats: FormField<Float?, ProductFormFieldError>,
    val monounsaturatedFats: FormField<Float?, ProductFormFieldError>,
    val polyunsaturatedFats: FormField<Float?, ProductFormFieldError>,
    val omega3: FormField<Float?, ProductFormFieldError>,
    val omega6: FormField<Float?, ProductFormFieldError>,
    // Carbohydrates
    val carbohydrates: FormField<Float?, ProductFormFieldError>,
    val sugars: FormField<Float?, ProductFormFieldError>,
    val addedSugars: FormField<Float?, ProductFormFieldError>,
    val dietaryFiber: FormField<Float?, ProductFormFieldError>,
    val solubleFiber: FormField<Float?, ProductFormFieldError>,
    val insolubleFiber: FormField<Float?, ProductFormFieldError>,
    // Other
    val salt: FormField<Float?, ProductFormFieldError>,
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
    autoCalculateEnergyState: MutableState<Boolean>,
) {
    val isValid: Boolean
        get() =
            name.error == null &&
                packageWeight.error == null &&
                servingWeight.error == null &&
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
                salt.error == null &&
                dietaryFiber.error == null &&
                solubleFiber.error == null &&
                insolubleFiber.error == null &&
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

    var sourceType: FoodSource.Type by sourceTypeState
    var isLiquid: Boolean by isLiquidState
    var measurement: Measurement by measurementState
    val isModified: Boolean by isModifiedState
    var autoCalculateEnergy: Boolean by autoCalculateEnergyState
}

internal fun ProductFormState.nutritionFacts(multiplier: Float) =
    NutritionFacts(
        proteins = proteins.value.applyMultiplier(multiplier).toNutrientValue(),
        carbohydrates = carbohydrates.value.applyMultiplier(multiplier).toNutrientValue(),
        energy = energy.value.applyMultiplier(multiplier).toNutrientValue(),
        fats = fats.value.applyMultiplier(multiplier).toNutrientValue(),
        saturatedFats = saturatedFats.value.applyMultiplier(multiplier).toNutrientValue(),
        transFats = transFats.value.applyMultiplier(multiplier).toNutrientValue(),
        monounsaturatedFats =
            monounsaturatedFats.value.applyMultiplier(multiplier).toNutrientValue(),
        polyunsaturatedFats =
            polyunsaturatedFats.value.applyMultiplier(multiplier).toNutrientValue(),
        omega3 = omega3.value.applyMultiplier(multiplier).toNutrientValue(),
        omega6 = omega6.value.applyMultiplier(multiplier).toNutrientValue(),
        sugars = sugars.value.applyMultiplier(multiplier).toNutrientValue(),
        addedSugars = addedSugars.value.applyMultiplier(multiplier).toNutrientValue(),
        dietaryFiber = dietaryFiber.value.applyMultiplier(multiplier).toNutrientValue(),
        solubleFiber = solubleFiber.value.applyMultiplier(multiplier).toNutrientValue(),
        insolubleFiber = insolubleFiber.value.applyMultiplier(multiplier).toNutrientValue(),
        salt = salt.value.applyMultiplier(multiplier).toNutrientValue(),
        cholesterol =
            cholesterolMilli.value.applyMultiplier(multiplier)?.div(1_000).toNutrientValue(),
        caffeine = caffeineMilli.value.applyMultiplier(multiplier)?.div(1_000).toNutrientValue(),
        vitaminA =
            vitaminAMicro.value.applyMultiplier(multiplier)?.div(1_000_000).toNutrientValue(),
        vitaminB1 = vitaminB1Milli.value.applyMultiplier(multiplier)?.div(1_000).toNutrientValue(),
        vitaminB2 = vitaminB2Milli.value.applyMultiplier(multiplier)?.div(1_000).toNutrientValue(),
        vitaminB3 = vitaminB3Milli.value.applyMultiplier(multiplier)?.div(1_000).toNutrientValue(),
        vitaminB5 = vitaminB5Milli.value.applyMultiplier(multiplier)?.div(1_000).toNutrientValue(),
        vitaminB6 = vitaminB6Milli.value.applyMultiplier(multiplier)?.div(1_000).toNutrientValue(),
        vitaminB7 =
            vitaminB7Micro.value.applyMultiplier(multiplier)?.div(1_000_000).toNutrientValue(),
        vitaminB9 =
            vitaminB9Micro.value.applyMultiplier(multiplier)?.div(1_000_000).toNutrientValue(),
        vitaminB12 =
            vitaminB12Micro.value.applyMultiplier(multiplier)?.div(1_000_000).toNutrientValue(),
        vitaminC = vitaminCMilli.value.applyMultiplier(multiplier)?.div(1_000).toNutrientValue(),
        vitaminD =
            vitaminDMicro.value.applyMultiplier(multiplier)?.div(1_000_000).toNutrientValue(),
        vitaminE = vitaminEMilli.value.applyMultiplier(multiplier)?.div(1_000).toNutrientValue(),
        vitaminK =
            vitaminKMicro.value.applyMultiplier(multiplier)?.div(1_000_000).toNutrientValue(),
        manganese = manganeseMilli.value.applyMultiplier(multiplier)?.div(1_000).toNutrientValue(),
        magnesium = magnesiumMilli.value.applyMultiplier(multiplier)?.div(1_000).toNutrientValue(),
        potassium = potassiumMilli.value.applyMultiplier(multiplier)?.div(1_000).toNutrientValue(),
        calcium = calciumMilli.value.applyMultiplier(multiplier)?.div(1_000).toNutrientValue(),
        copper = copperMilli.value.applyMultiplier(multiplier)?.div(1_000).toNutrientValue(),
        zinc = zincMilli.value.applyMultiplier(multiplier)?.div(1_000).toNutrientValue(),
        sodium = sodiumMilli.value.applyMultiplier(multiplier)?.div(1_000).toNutrientValue(),
        iron = ironMilli.value.applyMultiplier(multiplier)?.div(1_000).toNutrientValue(),
        phosphorus =
            phosphorusMilli.value.applyMultiplier(multiplier)?.div(1_000).toNutrientValue(),
        selenium =
            seleniumMicro.value.applyMultiplier(multiplier)?.div(1_000_000).toNutrientValue(),
        iodine = iodineMicro.value.applyMultiplier(multiplier)?.div(1_000_000).toNutrientValue(),
        chromium = chromiumMicro.value.applyMultiplier(multiplier)?.div(1_000_000).toNutrientValue(),
    )

private fun Float?.applyMultiplier(multiplier: Float): Float? = this?.let { it * multiplier }
