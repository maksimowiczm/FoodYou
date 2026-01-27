package com.maksimowiczm.foodyou.app.ui.userfood

import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import com.maksimowiczm.foodyou.account.domain.Account
import com.maksimowiczm.foodyou.account.domain.AccountSettings
import com.maksimowiczm.foodyou.account.domain.EnergyFormat
import com.maksimowiczm.foodyou.account.domain.testAccount
import com.maksimowiczm.foodyou.common.domain.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.FoodName
import com.maksimowiczm.foodyou.common.domain.FoodNameSelector
import com.maksimowiczm.foodyou.common.domain.Grams
import com.maksimowiczm.foodyou.common.domain.Language
import com.maksimowiczm.foodyou.common.domain.Milliliters
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

class ProductFormTransformerTest {
    private fun createDefaultSelector(language: Language = Language.English): FoodNameSelector =
        object : FoodNameSelector {
            override fun select(foodName: FoodName): String = ""

            override fun select(): Language = language
        }

    private fun createProductFormTransformer(
        account: Account = testAccount(),
        selector: FoodNameSelector = createDefaultSelector(),
    ): ProductFormTransformer =
        ProductFormTransformer(
            observePrimaryAccountUseCase = { flowOf(account) },
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
    fun `should transform valid form to result`() = runTest {
        val transformer = createProductFormTransformer()
        val form = ProductFormState()
        form.fillRequiredFields()

        val result = transformer.validate(form)
        advanceUntilIdle()

        assertEquals("Test Product", result.foodName.english)
        assertNull(result.brand)
        assertNull(result.barcode)
        assertNull(result.note)
        assertNull(result.source)
        assertEquals(200.0, result.nutritionFacts.energy.value)
        assertEquals(10.0, result.nutritionFacts.proteins.value)
        assertEquals(20.0, result.nutritionFacts.carbohydrates.value)
        assertEquals(5.0, result.nutritionFacts.fats.value)
        assertNull(result.servingQuantity)
        assertNull(result.packageQuantity)
        assertEquals(false, result.isLiquid)
    }

    @Test
    fun `should throw exception when form is invalid`() = runTest {
        val transformer = createProductFormTransformer()
        val form = ProductFormState()

        assertFailsWith<IllegalArgumentException> { transformer.validate(form) }
    }

    @Test
    fun `should throw exception when name is missing`() = runTest {
        val transformer = createProductFormTransformer()
        val form = ProductFormState()
        form.proteins.textFieldState.setTextAndPlaceCursorAtEnd("10")
        form.carbohydrates.textFieldState.setTextAndPlaceCursorAtEnd("20")
        form.fats.textFieldState.setTextAndPlaceCursorAtEnd("5")
        form.energy.textFieldState.setTextAndPlaceCursorAtEnd("200")

        assertFailsWith<IllegalArgumentException> { transformer.validate(form) }
    }

    @Test
    fun `should transform form with brand`() = runTest {
        val transformer = createProductFormTransformer()
        val form = ProductFormState()
        form.fillRequiredFields()
        form.brand.textFieldState.setTextAndPlaceCursorAtEnd("Test Brand")

        val result = transformer.validate(form)
        advanceUntilIdle()

        assertEquals("Test Brand", result.brand?.value)
    }

    @Test
    fun `should transform form with barcode`() = runTest {
        val transformer = createProductFormTransformer()
        val form = ProductFormState()
        form.fillRequiredFields()
        form.barcode.textFieldState.setTextAndPlaceCursorAtEnd("1234567890")

        val result = transformer.validate(form)
        advanceUntilIdle()

        assertEquals("1234567890", result.barcode?.value)
    }

    @Test
    fun `should transform form with note`() = runTest {
        val transformer = createProductFormTransformer()
        val form = ProductFormState()
        form.fillRequiredFields()
        form.note.textFieldState.setTextAndPlaceCursorAtEnd("Test note")

        val result = transformer.validate(form)
        advanceUntilIdle()

        assertEquals("Test note", result.note?.value)
    }

    @Test
    fun `should transform form with source`() = runTest {
        val transformer = createProductFormTransformer()
        val form = ProductFormState()
        form.fillRequiredFields()
        form.source.textFieldState.setTextAndPlaceCursorAtEnd("Test source")

        val result = transformer.validate(form)
        advanceUntilIdle()

        assertEquals("Test source", result.source?.value)
    }

    @Test
    fun `should set correct language for Polish`() = runTest {
        val transformer =
            createProductFormTransformer(selector = createDefaultSelector(Language.Polish))
        val form = ProductFormState()
        form.fillRequiredFields()

        val result = transformer.validate(form)
        advanceUntilIdle()

        assertEquals("Test Product", result.foodName.polish)
        assertNull(result.foodName.english)
    }

    @Test
    fun `should set correct language for German`() = runTest {
        val transformer =
            createProductFormTransformer(selector = createDefaultSelector(Language.German))
        val form = ProductFormState()
        form.fillRequiredFields()

        val result = transformer.validate(form)
        advanceUntilIdle()

        assertEquals("Test Product", result.foodName.german)
        assertNull(result.foodName.english)
    }

    @Test
    fun `should convert kilojoules to kilocalories`() = runTest {
        val account =
            testAccount(
                settings = AccountSettings.default.copy(energyFormat = EnergyFormat.Kilojoules)
            )
        val transformer = createProductFormTransformer(account = account)
        val form = ProductFormState()
        form.fillRequiredFields()
        form.energy.textFieldState.setTextAndPlaceCursorAtEnd("836.8") // 836.8 kJ = ~200 kcal

        val result = transformer.validate(form)
        advanceUntilIdle()

        assertEquals(200.0, result.nutritionFacts.energy.value!!, 0.1)
    }

    @Test
    fun `should calculate multiplier for serving size in grams`() = runTest {
        val transformer = createProductFormTransformer()
        val form = ProductFormState(valuesPer = ValuesPer.Serving, servingUnit = QuantityUnit.Gram)
        form.fillRequiredFields()
        form.servingQuantity.textFieldState.setTextAndPlaceCursorAtEnd("50")

        // Values are per 50g, so should be doubled for 100g
        form.proteins.textFieldState.setTextAndPlaceCursorAtEnd("5")
        form.carbohydrates.textFieldState.setTextAndPlaceCursorAtEnd("10")
        form.fats.textFieldState.setTextAndPlaceCursorAtEnd("2.5")
        form.energy.textFieldState.setTextAndPlaceCursorAtEnd("100")

        val result = transformer.validate(form)
        advanceUntilIdle()

        assertEquals(10.0, result.nutritionFacts.proteins.value)
        assertEquals(20.0, result.nutritionFacts.carbohydrates.value)
        assertEquals(5.0, result.nutritionFacts.fats.value)
        assertEquals(200.0, result.nutritionFacts.energy.value)
        assertEquals(false, result.isLiquid)
    }

    @Test
    fun `should calculate multiplier for serving size in milliliters`() = runTest {
        val transformer = createProductFormTransformer()
        val form =
            ProductFormState(valuesPer = ValuesPer.Serving, servingUnit = QuantityUnit.Milliliter)
        form.fillRequiredFields()
        form.servingQuantity.textFieldState.setTextAndPlaceCursorAtEnd("200")

        // Values are per 200ml, so should be halved for 100ml
        form.proteins.textFieldState.setTextAndPlaceCursorAtEnd("20")
        form.carbohydrates.textFieldState.setTextAndPlaceCursorAtEnd("40")
        form.fats.textFieldState.setTextAndPlaceCursorAtEnd("10")
        form.energy.textFieldState.setTextAndPlaceCursorAtEnd("400")

        val result = transformer.validate(form)
        advanceUntilIdle()

        assertEquals(10.0, result.nutritionFacts.proteins.value)
        assertEquals(20.0, result.nutritionFacts.carbohydrates.value)
        assertEquals(5.0, result.nutritionFacts.fats.value)
        assertEquals(200.0, result.nutritionFacts.energy.value)
        assertEquals(true, result.isLiquid)
    }

    @Test
    fun `should calculate multiplier for package size`() = runTest {
        val transformer = createProductFormTransformer()
        val form = ProductFormState(valuesPer = ValuesPer.Package, packageUnit = QuantityUnit.Gram)
        form.fillRequiredFields()
        form.packageQuantity.textFieldState.setTextAndPlaceCursorAtEnd("250")

        // Values are per 250g package
        form.proteins.textFieldState.setTextAndPlaceCursorAtEnd("25")
        form.carbohydrates.textFieldState.setTextAndPlaceCursorAtEnd("50")
        form.fats.textFieldState.setTextAndPlaceCursorAtEnd("12.5")
        form.energy.textFieldState.setTextAndPlaceCursorAtEnd("500")

        val result = transformer.validate(form)
        advanceUntilIdle()

        assertEquals(10.0, result.nutritionFacts.proteins.value)
        assertEquals(20.0, result.nutritionFacts.carbohydrates.value)
        assertEquals(5.0, result.nutritionFacts.fats.value)
        assertEquals(200.0, result.nutritionFacts.energy.value)
        assertEquals(false, result.isLiquid)
    }

    @Test
    fun `should throw exception when serving quantity is missing for serving values`() = runTest {
        val transformer = createProductFormTransformer()
        val form = ProductFormState(valuesPer = ValuesPer.Serving)
        form.fillRequiredFields()

        assertFailsWith<IllegalArgumentException> { transformer.validate(form) }
    }

    @Test
    fun `should throw exception when package quantity is missing for package values`() = runTest {
        val transformer = createProductFormTransformer()
        val form = ProductFormState(valuesPer = ValuesPer.Package)
        form.fillRequiredFields()

        assertFailsWith<IllegalArgumentException> { transformer.validate(form) }
    }

    @Test
    fun `should store serving quantity in grams`() = runTest {
        val transformer = createProductFormTransformer()
        val form = ProductFormState(servingUnit = QuantityUnit.Gram)
        form.fillRequiredFields()
        form.servingQuantity.textFieldState.setTextAndPlaceCursorAtEnd("150")

        val result = transformer.validate(form)
        advanceUntilIdle()

        assertEquals(AbsoluteQuantity.Weight(Grams(150.0)), result.servingQuantity)
        assertEquals(false, result.isLiquid)
    }

    @Test
    fun `should store serving quantity in milliliters`() = runTest {
        val transformer = createProductFormTransformer()
        val form = ProductFormState(servingUnit = QuantityUnit.Milliliter)
        form.fillRequiredFields()
        form.servingQuantity.textFieldState.setTextAndPlaceCursorAtEnd("250")

        val result = transformer.validate(form)
        advanceUntilIdle()

        assertEquals(AbsoluteQuantity.Volume(Milliliters(250.0)), result.servingQuantity)
        assertEquals(true, result.isLiquid)
    }

    @Test
    fun `should store package quantity in grams`() = runTest {
        val transformer = createProductFormTransformer()
        val form = ProductFormState(packageUnit = QuantityUnit.Gram)
        form.fillRequiredFields()
        form.packageQuantity.textFieldState.setTextAndPlaceCursorAtEnd("500")

        val result = transformer.validate(form)
        advanceUntilIdle()

        assertEquals(AbsoluteQuantity.Weight(Grams(500.0)), result.packageQuantity)
        assertEquals(false, result.isLiquid)
    }

    @Test
    fun `should store package quantity in milliliters`() = runTest {
        val transformer = createProductFormTransformer()
        val form = ProductFormState(packageUnit = QuantityUnit.Milliliter)
        form.fillRequiredFields()
        form.packageQuantity.textFieldState.setTextAndPlaceCursorAtEnd("750")

        val result = transformer.validate(form)
        advanceUntilIdle()

        assertEquals(AbsoluteQuantity.Volume(Milliliters(750.0)), result.packageQuantity)
        assertEquals(true, result.isLiquid)
    }

    @Test
    fun `should handle ounces for serving size`() = runTest {
        val transformer = createProductFormTransformer()
        val form = ProductFormState(valuesPer = ValuesPer.Serving, servingUnit = QuantityUnit.Ounce)
        form.fillRequiredFields()
        form.servingQuantity.textFieldState.setTextAndPlaceCursorAtEnd("3.5274") // ~100g

        form.proteins.textFieldState.setTextAndPlaceCursorAtEnd("10")
        form.energy.textFieldState.setTextAndPlaceCursorAtEnd("200")

        val result = transformer.validate(form)
        advanceUntilIdle()

        assertEquals(10.0, result.nutritionFacts.proteins.value!!, 0.1)
        assertEquals(false, result.isLiquid)
    }

    @Test
    fun `should handle fluid ounces for serving size`() = runTest {
        val transformer = createProductFormTransformer()
        val form =
            ProductFormState(valuesPer = ValuesPer.Serving, servingUnit = QuantityUnit.FluidOunce)
        form.fillRequiredFields()
        form.servingQuantity.textFieldState.setTextAndPlaceCursorAtEnd("3.3814") // ~100ml

        form.proteins.textFieldState.setTextAndPlaceCursorAtEnd("10")
        form.energy.textFieldState.setTextAndPlaceCursorAtEnd("200")

        val result = transformer.validate(form)
        advanceUntilIdle()

        assertEquals(10.0, result.nutritionFacts.proteins.value!!, 0.1)
        assertEquals(true, result.isLiquid)
    }

    @Test
    fun `should always set fallback name`() = runTest {
        val transformer = createProductFormTransformer()
        val form = ProductFormState()
        form.fillRequiredFields()

        val result = transformer.validate(form)
        advanceUntilIdle()

        assertEquals("Test Product", result.foodName.fallback)
    }

    @Test
    fun `should handle all optional fields together`() = runTest {
        val transformer = createProductFormTransformer()
        val form = ProductFormState()
        form.fillRequiredFields()
        form.brand.textFieldState.setTextAndPlaceCursorAtEnd("Brand")
        form.barcode.textFieldState.setTextAndPlaceCursorAtEnd("123456")
        form.note.textFieldState.setTextAndPlaceCursorAtEnd("Note")
        form.source.textFieldState.setTextAndPlaceCursorAtEnd("Source")
        form.servingQuantity.textFieldState.setTextAndPlaceCursorAtEnd("100")
        form.packageQuantity.textFieldState.setTextAndPlaceCursorAtEnd("500")

        val result = transformer.validate(form)
        advanceUntilIdle()

        assertEquals("Brand", result.brand?.value)
        assertEquals("123456", result.barcode?.value)
        assertEquals("Note", result.note?.value)
        assertEquals("Source", result.source?.value)
        assertEquals(AbsoluteQuantity.Weight(Grams(100.0)), result.servingQuantity)
        assertEquals(AbsoluteQuantity.Weight(Grams(500.0)), result.packageQuantity)
        assertEquals(false, result.isLiquid)
    }
}
