package com.maksimowiczm.foodyou.app.ui.userfood

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.account.domain.EnergyFormat
import com.maksimowiczm.foodyou.account.domain.testAccount
import com.maksimowiczm.foodyou.app.ui.common.form.FormField
import com.maksimowiczm.foodyou.common.domain.Language
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodNameSelector
import com.maksimowiczm.foodyou.common.domain.food.Grams
import com.maksimowiczm.foodyou.common.domain.food.Milliliters
import io.konform.validation.Validation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

class ProductFormTransformerTest {
    private fun createDefaultSelector(language: Language = Language.English): FoodNameSelector =
        object : FoodNameSelector {
            override fun select(foodName: FoodName): String = ""

            override fun select(): Language = language
        }

    private fun createProductFormTransformer(
        energyFormat: EnergyFormat = testAccount().settings.energyFormat,
        selector: FoodNameSelector = createDefaultSelector(),
    ): ProductFormTransformer =
        ProductFormTransformer(
            getAppAccountEnergyFormatUseCase = { energyFormat },
            foodNameSelector = selector,
        )

    private fun ProductFormState.fillRequiredFields() {
        name.textFieldState.setTextAndPlaceCursorAtEnd("Test Product")
        proteins.textFieldState.setTextAndPlaceCursorAtEnd("10")
        carbohydrates.textFieldState.setTextAndPlaceCursorAtEnd("20")
        fats.textFieldState.setTextAndPlaceCursorAtEnd("5")
        energy.textFieldState.setTextAndPlaceCursorAtEnd("200")
    }

    @Test
    fun should_transform_valid_form_to_result() = runTest {
        val transformer = createProductFormTransformer()
        val form = productFormState()
        form.fillRequiredFields()

        val result = transformer.transform(form)
        advanceUntilIdle()

        assertEquals("Test Product", result.name.english)
        assertNull(result.brand)
        assertNull(result.barcode)
        assertNull(result.note)
        assertEquals(200.0, result.nutritionFacts.energy.value)
        assertEquals(10.0, result.nutritionFacts.proteins.value)
        assertEquals(20.0, result.nutritionFacts.carbohydrates.value)
        assertEquals(5.0, result.nutritionFacts.fats.value)
        assertNull(result.servingQuantity)
        assertNull(result.packageQuantity)
        assertEquals(false, result.isLiquid)
    }

    @Test
    fun should_throw_exception_when_form_is_invalid() = runTest {
        val transformer = createProductFormTransformer()
        val form = productFormState()

        assertFailsWith<IllegalArgumentException> { transformer.transform(form) }
    }

    @Test
    fun should_throw_exception_when_name_is_missing() = runTest {
        val transformer = createProductFormTransformer()
        val form = productFormState()
        form.proteins.textFieldState.setTextAndPlaceCursorAtEnd("10")
        form.carbohydrates.textFieldState.setTextAndPlaceCursorAtEnd("20")
        form.fats.textFieldState.setTextAndPlaceCursorAtEnd("5")
        form.energy.textFieldState.setTextAndPlaceCursorAtEnd("200")

        assertFailsWith<IllegalArgumentException> { transformer.transform(form) }
    }

    @Test
    fun should_transform_form_with_brand() = runTest {
        val transformer = createProductFormTransformer()
        val form = productFormState()
        form.fillRequiredFields()
        form.brand.textFieldState.setTextAndPlaceCursorAtEnd("Test Brand")

        val result = transformer.transform(form)
        advanceUntilIdle()

        assertEquals("Test Brand", result.brand?.value)
    }

    @Test
    fun should_transform_form_with_barcode() = runTest {
        val transformer = createProductFormTransformer()
        val form = productFormState()
        form.fillRequiredFields()
        form.barcode.textFieldState.setTextAndPlaceCursorAtEnd("1234567890")

        val result = transformer.transform(form)
        advanceUntilIdle()

        assertEquals("1234567890", result.barcode?.value)
    }

    @Test
    fun should_transform_form_with_note() = runTest {
        val transformer = createProductFormTransformer()
        val form = productFormState()
        form.fillRequiredFields()
        form.note.textFieldState.setTextAndPlaceCursorAtEnd("Test note")

        val result = transformer.transform(form)
        advanceUntilIdle()

        assertEquals("Test note", result.note?.value)
    }

    @Test
    fun should_set_correct_language_for_Polish() = runTest {
        val transformer =
            createProductFormTransformer(selector = createDefaultSelector(Language.Polish))
        val form = productFormState()
        form.fillRequiredFields()

        val result = transformer.transform(form)
        advanceUntilIdle()

        assertEquals("Test Product", result.name.polish)
        assertNull(result.name.english)
    }

    @Test
    fun should_set_correct_language_for_German() = runTest {
        val transformer =
            createProductFormTransformer(selector = createDefaultSelector(Language.German))
        val form = productFormState()
        form.fillRequiredFields()

        val result = transformer.transform(form)
        advanceUntilIdle()

        assertEquals("Test Product", result.name.german)
        assertNull(result.name.english)
    }

    @Test
    fun should_convert_kilojoules_to_kilocalories() = runTest {
        val transformer = createProductFormTransformer(energyFormat = EnergyFormat.Kilojoules)
        val form = productFormState()
        form.fillRequiredFields()
        form.energy.textFieldState.setTextAndPlaceCursorAtEnd("836.8") // 836.8 kJ = ~200 kcal

        val result = transformer.transform(form)
        advanceUntilIdle()

        assertEquals(200.0, result.nutritionFacts.energy.value!!, 0.1)
    }

    @Test
    fun should_calculate_multiplier_for_serving_size_in_grams() = runTest {
        val transformer = createProductFormTransformer()
        val form = productFormState(valuesPer = ValuesPer.Serving, servingUnit = QuantityUnit.Gram)
        form.fillRequiredFields()
        form.servingQuantity.textFieldState.setTextAndPlaceCursorAtEnd("50")

        // Values are per 50g, so should be doubled for 100g
        form.proteins.textFieldState.setTextAndPlaceCursorAtEnd("5")
        form.carbohydrates.textFieldState.setTextAndPlaceCursorAtEnd("10")
        form.fats.textFieldState.setTextAndPlaceCursorAtEnd("2.5")
        form.energy.textFieldState.setTextAndPlaceCursorAtEnd("100")

        val result = transformer.transform(form)
        advanceUntilIdle()

        assertEquals(10.0, result.nutritionFacts.proteins.value)
        assertEquals(20.0, result.nutritionFacts.carbohydrates.value)
        assertEquals(5.0, result.nutritionFacts.fats.value)
        assertEquals(200.0, result.nutritionFacts.energy.value)
        assertEquals(false, result.isLiquid)
    }

    @Test
    fun should_calculate_multiplier_for_serving_size_in_milliliters() = runTest {
        val transformer = createProductFormTransformer()
        val form =
            productFormState(valuesPer = ValuesPer.Serving, servingUnit = QuantityUnit.Milliliter)
        form.fillRequiredFields()
        form.servingQuantity.textFieldState.setTextAndPlaceCursorAtEnd("200")

        // Values are per 200ml, so should be halved for 100ml
        form.proteins.textFieldState.setTextAndPlaceCursorAtEnd("20")
        form.carbohydrates.textFieldState.setTextAndPlaceCursorAtEnd("40")
        form.fats.textFieldState.setTextAndPlaceCursorAtEnd("10")
        form.energy.textFieldState.setTextAndPlaceCursorAtEnd("400")

        val result = transformer.transform(form)
        advanceUntilIdle()

        assertEquals(10.0, result.nutritionFacts.proteins.value)
        assertEquals(20.0, result.nutritionFacts.carbohydrates.value)
        assertEquals(5.0, result.nutritionFacts.fats.value)
        assertEquals(200.0, result.nutritionFacts.energy.value)
        assertEquals(true, result.isLiquid)
    }

    @Test
    fun should_calculate_multiplier_for_package_size() = runTest {
        val transformer = createProductFormTransformer()
        val form = productFormState(valuesPer = ValuesPer.Package, packageUnit = QuantityUnit.Gram)
        form.fillRequiredFields()
        form.packageQuantity.textFieldState.setTextAndPlaceCursorAtEnd("250")

        // Values are per 250g package
        form.proteins.textFieldState.setTextAndPlaceCursorAtEnd("25")
        form.carbohydrates.textFieldState.setTextAndPlaceCursorAtEnd("50")
        form.fats.textFieldState.setTextAndPlaceCursorAtEnd("12.5")
        form.energy.textFieldState.setTextAndPlaceCursorAtEnd("500")

        val result = transformer.transform(form)
        advanceUntilIdle()

        assertEquals(10.0, result.nutritionFacts.proteins.value)
        assertEquals(20.0, result.nutritionFacts.carbohydrates.value)
        assertEquals(5.0, result.nutritionFacts.fats.value)
        assertEquals(200.0, result.nutritionFacts.energy.value)
        assertEquals(false, result.isLiquid)
    }

    @Test
    fun should_throw_exception_when_serving_quantity_is_missing_for_serving_values() = runTest {
        val transformer = createProductFormTransformer()
        val form = productFormState(valuesPer = ValuesPer.Serving)
        form.fillRequiredFields()

        assertFailsWith<IllegalArgumentException> { transformer.transform(form) }
    }

    @Test
    fun should_throw_exception_when_package_quantity_is_missing_for_package_values() = runTest {
        val transformer = createProductFormTransformer()
        val form = productFormState(valuesPer = ValuesPer.Package)
        form.fillRequiredFields()

        assertFailsWith<IllegalArgumentException> { transformer.transform(form) }
    }

    @Test
    fun should_store_serving_quantity_in_grams() = runTest {
        val transformer = createProductFormTransformer()
        val form = productFormState(servingUnit = QuantityUnit.Gram)
        form.fillRequiredFields()
        form.servingQuantity.textFieldState.setTextAndPlaceCursorAtEnd("150")

        val result = transformer.transform(form)
        advanceUntilIdle()

        assertEquals(AbsoluteQuantity.Weight(Grams(150.0)), result.servingQuantity)
        assertEquals(false, result.isLiquid)
    }

    @Test
    fun should_store_serving_quantity_in_milliliters() = runTest {
        val transformer = createProductFormTransformer()
        val form = productFormState(servingUnit = QuantityUnit.Milliliter)
        form.fillRequiredFields()
        form.servingQuantity.textFieldState.setTextAndPlaceCursorAtEnd("250")

        val result = transformer.transform(form)
        advanceUntilIdle()

        assertEquals(AbsoluteQuantity.Volume(Milliliters(250.0)), result.servingQuantity)
        assertEquals(true, result.isLiquid)
    }

    @Test
    fun should_store_package_quantity_in_grams() = runTest {
        val transformer = createProductFormTransformer()
        val form = productFormState(packageUnit = QuantityUnit.Gram)
        form.fillRequiredFields()
        form.packageQuantity.textFieldState.setTextAndPlaceCursorAtEnd("500")

        val result = transformer.transform(form)
        advanceUntilIdle()

        assertEquals(AbsoluteQuantity.Weight(Grams(500.0)), result.packageQuantity)
        assertEquals(false, result.isLiquid)
    }

    @Test
    fun should_store_package_quantity_in_milliliters() = runTest {
        val transformer = createProductFormTransformer()
        val form = productFormState(packageUnit = QuantityUnit.Milliliter)
        form.fillRequiredFields()
        form.packageQuantity.textFieldState.setTextAndPlaceCursorAtEnd("750")

        val result = transformer.transform(form)
        advanceUntilIdle()

        assertEquals(AbsoluteQuantity.Volume(Milliliters(750.0)), result.packageQuantity)
        assertEquals(true, result.isLiquid)
    }

    @Test
    fun should_handle_ounces_for_serving_size() = runTest {
        val transformer = createProductFormTransformer()
        val form = productFormState(valuesPer = ValuesPer.Serving, servingUnit = QuantityUnit.Ounce)
        form.fillRequiredFields()
        form.servingQuantity.textFieldState.setTextAndPlaceCursorAtEnd("3.5274") // ~100g

        form.proteins.textFieldState.setTextAndPlaceCursorAtEnd("10")
        form.energy.textFieldState.setTextAndPlaceCursorAtEnd("200")

        val result = transformer.transform(form)
        advanceUntilIdle()

        assertEquals(10.0, result.nutritionFacts.proteins.value!!, 0.1)
        assertEquals(false, result.isLiquid)
    }

    @Test
    fun should_handle_fluid_ounces_for_serving_size() = runTest {
        val transformer = createProductFormTransformer()
        val form =
            productFormState(valuesPer = ValuesPer.Serving, servingUnit = QuantityUnit.FluidOunce)
        form.fillRequiredFields()
        form.servingQuantity.textFieldState.setTextAndPlaceCursorAtEnd("3.3814") // ~100ml

        form.proteins.textFieldState.setTextAndPlaceCursorAtEnd("10")
        form.energy.textFieldState.setTextAndPlaceCursorAtEnd("200")

        val result = transformer.transform(form)
        advanceUntilIdle()

        assertEquals(10.0, result.nutritionFacts.proteins.value!!, 0.1)
        assertEquals(true, result.isLiquid)
    }

    @Test
    fun should_always_set_fallback_name() = runTest {
        val transformer = createProductFormTransformer()
        val form = productFormState()
        form.fillRequiredFields()

        val result = transformer.transform(form)
        advanceUntilIdle()

        assertEquals("Test Product", result.name.fallback)
    }

    @Test
    fun should_handle_all_optional_fields_together() = runTest {
        val transformer = createProductFormTransformer()
        val form = productFormState()
        form.fillRequiredFields()
        form.brand.textFieldState.setTextAndPlaceCursorAtEnd("Brand")
        form.barcode.textFieldState.setTextAndPlaceCursorAtEnd("123456")
        form.note.textFieldState.setTextAndPlaceCursorAtEnd("Note")
        form.servingQuantity.textFieldState.setTextAndPlaceCursorAtEnd("100")
        form.packageQuantity.textFieldState.setTextAndPlaceCursorAtEnd("500")

        val result = transformer.transform(form)
        advanceUntilIdle()

        assertEquals("Brand", result.brand?.value)
        assertEquals("123456", result.barcode?.value)
        assertEquals("Note", result.note?.value)
        assertEquals(AbsoluteQuantity.Weight(Grams(100.0)), result.servingQuantity)
        assertEquals(AbsoluteQuantity.Weight(Grams(500.0)), result.packageQuantity)
        assertEquals(false, result.isLiquid)
    }
}

private fun formField2(
    textFieldState: TextFieldState = TextFieldState(),
    defaultValue: String? = null,
    validation: Validation<String?> = Validation {},
) = FormField(textFieldState, defaultValue, validation)

private fun productFormState(
    name: FormField = formField2(),
    brand: FormField = formField2(),
    barcode: FormField = formField2(),
    defaultImageUri: String? = null,
    imageUri: String? = null,
    valuesPer: ValuesPer = ValuesPer.Grams100,
    servingQuantity: FormField = formField2(),
    servingUnit: QuantityUnit = QuantityUnit.Gram,
    packageQuantity: FormField = formField2(),
    packageUnit: QuantityUnit = QuantityUnit.Gram,
    note: FormField = formField2(),
    proteins: FormField = formField2(),
    carbohydrates: FormField = formField2(),
    fats: FormField = formField2(),
    energy: FormField = formField2(),
    energyFormat: EnergyFormat = EnergyFormat.Kilocalories,
    saturatedFats: FormField = formField2(),
    transFats: FormField = formField2(),
    monounsaturatedFats: FormField = formField2(),
    polyunsaturatedFats: FormField = formField2(),
    omega3: FormField = formField2(),
    omega6: FormField = formField2(),
    sugars: FormField = formField2(),
    addedSugars: FormField = formField2(),
    solubleFiber: FormField = formField2(),
    insolubleFiber: FormField = formField2(),
    dietaryFiber: FormField = formField2(),
    salt: FormField = formField2(),
    cholesterolMilli: FormField = formField2(),
    caffeineMilli: FormField = formField2(),
    vitaminAMicro: FormField = formField2(),
    vitaminB1Milli: FormField = formField2(),
    vitaminB2Milli: FormField = formField2(),
    vitaminB3Milli: FormField = formField2(),
    vitaminB5Milli: FormField = formField2(),
    vitaminB6Milli: FormField = formField2(),
    vitaminB7Micro: FormField = formField2(),
    vitaminB9Micro: FormField = formField2(),
    vitaminB12Micro: FormField = formField2(),
    vitaminCMilli: FormField = formField2(),
    vitaminDMicro: FormField = formField2(),
    vitaminEMilli: FormField = formField2(),
    vitaminKMicro: FormField = formField2(),
    manganeseMilli: FormField = formField2(),
    magnesiumMilli: FormField = formField2(),
    potassiumMilli: FormField = formField2(),
    calciumMilli: FormField = formField2(),
    copperMilli: FormField = formField2(),
    zincMilli: FormField = formField2(),
    sodiumMilli: FormField = formField2(),
    ironMilli: FormField = formField2(),
    phosphorusMilli: FormField = formField2(),
    seleniumMicro: FormField = formField2(),
    iodineMicro: FormField = formField2(),
    chromiumMicro: FormField = formField2(),
) =
    ProductFormState(
        name = name,
        brand = brand,
        barcode = barcode,
        defaultImageUri = defaultImageUri,
        imageUri = mutableStateOf(imageUri),
        defaultValuesPer = valuesPer,
        valuesPer = mutableStateOf(valuesPer),
        servingQuantity = servingQuantity,
        defaultServingUnit = servingUnit,
        servingUnit = mutableStateOf(servingUnit),
        packageQuantity = packageQuantity,
        defaultPackageUnit = packageUnit,
        packageUnit = mutableStateOf(packageUnit),
        note = note,
        proteins = proteins,
        carbohydrates = carbohydrates,
        fats = fats,
        energy = energy,
        defaultEnergyFormat = energyFormat,
        energyFormat = mutableStateOf(energyFormat),
        saturatedFats = saturatedFats,
        transFats = transFats,
        monounsaturatedFats = monounsaturatedFats,
        polyunsaturatedFats = polyunsaturatedFats,
        omega3 = omega3,
        omega6 = omega6,
        sugars = sugars,
        addedSugars = addedSugars,
        solubleFiber = dietaryFiber,
        insolubleFiber = solubleFiber,
        dietaryFiber = insolubleFiber,
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
