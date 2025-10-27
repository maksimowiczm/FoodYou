package com.maksimowiczm.foodyou.app.ui.product.create

import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import com.maksimowiczm.foodyou.account.domain.Account
import com.maksimowiczm.foodyou.account.domain.AccountSettings
import com.maksimowiczm.foodyou.account.domain.EnergyFormat
import com.maksimowiczm.foodyou.account.domain.testAccount
import com.maksimowiczm.foodyou.app.ui.product.ProductFormState
import com.maksimowiczm.foodyou.app.ui.product.QuantityUnit
import com.maksimowiczm.foodyou.app.ui.product.ValuesPer
import com.maksimowiczm.foodyou.common.domain.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.Language
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.food.domain.Barcode
import com.maksimowiczm.foodyou.food.domain.FoodBrand
import com.maksimowiczm.foodyou.food.domain.FoodName
import com.maksimowiczm.foodyou.food.domain.FoodNameSelector
import com.maksimowiczm.foodyou.food.domain.FoodNote
import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity
import com.maksimowiczm.foodyou.food.domain.FoodSource
import com.maksimowiczm.foodyou.food.domain.NutrientValue
import com.maksimowiczm.foodyou.food.domain.NutritionFacts
import com.maksimowiczm.foodyou.food.domain.UserFoodRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

class CreateProductViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(
        account: Account = testAccount(),
        repository: UserFoodRepository = createDefaultRepository(),
        selector: FoodNameSelector = createDefaultSelector(),
    ): CreateProductViewModel =
        CreateProductViewModel(
            observePrimaryAccountUseCase = { flowOf(account) },
            userFoodRepository = repository,
            foodNameSelector = selector,
        )

    private fun createDefaultRepository(
        identity: FoodProductIdentity.Local = FoodProductIdentity.Local("test-id")
    ): UserFoodRepository =
        object : UserFoodRepository {
            override suspend fun create(
                name: FoodName,
                brand: FoodBrand?,
                barcode: Barcode?,
                note: FoodNote?,
                imageUri: String?,
                source: FoodSource.UserAdded?,
                nutritionFacts: NutritionFacts,
                servingQuantity: AbsoluteQuantity?,
                packageQuantity: AbsoluteQuantity?,
                accountId: LocalAccountId,
            ): FoodProductIdentity.Local = identity
        }

    private fun createDefaultSelector(language: Language = Language.English): FoodNameSelector =
        object : FoodNameSelector {
            override fun select(foodName: FoodName): String = ""

            override fun select(): Language = language
        }

    private fun ProductFormState.fillRequiredFields() {
        name.textFieldState.setTextAndPlaceCursorAtEnd("Test Product")
        proteins.textFieldState.setTextAndPlaceCursorAtEnd("10")
        carbohydrates.textFieldState.setTextAndPlaceCursorAtEnd("20")
        fats.textFieldState.setTextAndPlaceCursorAtEnd("5")
        energy.textFieldState.setTextAndPlaceCursorAtEnd("200")
    }

    @Test
    fun `create() should emit Created event when form is valid`() = runTest {
        val account = testAccount()
        val foodIdentity = FoodProductIdentity.Local("test-id")
        val viewModel =
            createViewModel(account = account, repository = createDefaultRepository(foodIdentity))

        viewModel.productFormState.value.fillRequiredFields()

        val eventJob = async { viewModel.uiEvents.first() }
        viewModel.create()
        advanceUntilIdle()

        val event = eventJob.await()
        assertEquals(CreateProductEvent.Created(foodIdentity), event)
    }

    @Test
    fun `create() should throw error when form is invalid`() = runTest {
        val viewModel = createViewModel()

        // Leave form empty (invalid)

        assertFailsWith<IllegalArgumentException> { viewModel.create() }
    }

    @Test
    fun `create() should prevent concurrent creation when locked`() = runTest {
        var createCallCount = 0
        val repository =
            object : UserFoodRepository {
                override suspend fun create(
                    name: FoodName,
                    brand: FoodBrand?,
                    barcode: Barcode?,
                    note: FoodNote?,
                    imageUri: String?,
                    source: FoodSource.UserAdded?,
                    nutritionFacts: NutritionFacts,
                    servingQuantity: AbsoluteQuantity?,
                    packageQuantity: AbsoluteQuantity?,
                    accountId: LocalAccountId,
                ): FoodProductIdentity.Local {
                    createCallCount++
                    delay(100) // Simulate async work
                    return FoodProductIdentity.Local("test-id")
                }
            }

        val viewModel = createViewModel(repository = repository)
        viewModel.productFormState.value.fillRequiredFields()

        // Launch both creates concurrently
        launch { viewModel.create() }
        launch { viewModel.create() }

        advanceUntilIdle()

        // Only one should have executed
        assertEquals(1, createCallCount)
    }

    @Test
    fun `isLocked should be true during creation`() = runTest {
        val repository =
            object : UserFoodRepository {
                override suspend fun create(
                    name: FoodName,
                    brand: FoodBrand?,
                    barcode: Barcode?,
                    note: FoodNote?,
                    imageUri: String?,
                    source: FoodSource.UserAdded?,
                    nutritionFacts: NutritionFacts,
                    servingQuantity: AbsoluteQuantity?,
                    packageQuantity: AbsoluteQuantity?,
                    accountId: LocalAccountId,
                ): FoodProductIdentity.Local {
                    delay(100)
                    return FoodProductIdentity.Local("test-id")
                }
            }

        val viewModel = createViewModel(repository = repository)
        viewModel.productFormState.value.fillRequiredFields()

        assertFalse(viewModel.isLocked.value)

        launch { viewModel.create() }
        advanceTimeBy(50)

        assertTrue(viewModel.isLocked.value)

        advanceUntilIdle()

        // Stays locked after creation
        assertTrue(viewModel.isLocked.value)
    }

    @Test
    fun `setImage should update imageUri in state`() {
        val viewModel = createViewModel()

        assertNull(viewModel.productFormState.value.imageUri)

        viewModel.setImage("content://test-uri")

        assertEquals("content://test-uri", viewModel.productFormState.value.imageUri)
    }

    @Test
    fun `setImage should handle null uri`() {
        val viewModel = createViewModel()

        viewModel.setImage("content://test-uri")
        viewModel.setImage(null)

        assertNull(viewModel.productFormState.value.imageUri)
    }

    @Test
    fun `setValuesPer Grams100 should make servingQuantity optional`() {
        val viewModel = createViewModel()

        viewModel.setValuesPer(ValuesPer.Grams100)

        assertEquals(ValuesPer.Grams100, viewModel.productFormState.value.valuesPer)
        // servingQuantity should be optional (can be empty)
    }

    @Test
    fun `setValuesPer Milliliters100 should make servingQuantity optional`() {
        val viewModel = createViewModel()

        viewModel.setValuesPer(ValuesPer.Milliliters100)

        assertEquals(ValuesPer.Milliliters100, viewModel.productFormState.value.valuesPer)
    }

    @Test
    fun `setValuesPer Serving should make servingQuantity required`() {
        val viewModel = createViewModel()

        viewModel.setValuesPer(ValuesPer.Serving)

        val state = viewModel.productFormState.value
        assertEquals(ValuesPer.Serving, state.valuesPer)

        // servingQuantity should now be required
        assertFalse(state.servingQuantity.isValid)
    }

    @Test
    fun `setValuesPer Package should make packageQuantity required`() {
        val viewModel = createViewModel()

        viewModel.setValuesPer(ValuesPer.Package)

        val state = viewModel.productFormState.value
        assertEquals(ValuesPer.Package, state.valuesPer)

        // packageQuantity should now be required
        assertFalse(state.packageQuantity.isValid)
    }

    @Test
    fun `setValuesPer should preserve textFieldState when switching`() {
        val viewModel = createViewModel()

        viewModel.productFormState.value.servingQuantity.textFieldState.setTextAndPlaceCursorAtEnd(
            "50"
        )

        viewModel.setValuesPer(ValuesPer.Serving)

        // Text should be preserved
        assertEquals(
            "50",
            viewModel.productFormState.value.servingQuantity.textFieldState.text.toString(),
        )
    }

    @Test
    fun `setServingUnit should update serving unit`() {
        val viewModel = createViewModel()

        assertEquals(QuantityUnit.Gram, viewModel.productFormState.value.servingUnit)

        viewModel.setServingUnit(QuantityUnit.Ounce)

        assertEquals(QuantityUnit.Ounce, viewModel.productFormState.value.servingUnit)
    }

    @Test
    fun `setPackageUnit should update package unit`() {
        val viewModel = createViewModel()

        assertEquals(QuantityUnit.Gram, viewModel.productFormState.value.packageUnit)

        viewModel.setPackageUnit(QuantityUnit.Milliliter)

        assertEquals(QuantityUnit.Milliliter, viewModel.productFormState.value.packageUnit)
    }

    @Test
    fun `create should use correct language for FoodName - English`() = runTest {
        var capturedName: FoodName? = null
        val repository =
            object : UserFoodRepository {
                override suspend fun create(
                    name: FoodName,
                    brand: FoodBrand?,
                    barcode: Barcode?,
                    note: FoodNote?,
                    imageUri: String?,
                    source: FoodSource.UserAdded?,
                    nutritionFacts: NutritionFacts,
                    servingQuantity: AbsoluteQuantity?,
                    packageQuantity: AbsoluteQuantity?,
                    accountId: LocalAccountId,
                ): FoodProductIdentity.Local {
                    capturedName = name
                    return FoodProductIdentity.Local("test-id")
                }
            }

        val viewModel =
            createViewModel(
                repository = repository,
                selector = createDefaultSelector(Language.English),
            )

        viewModel.productFormState.value.fillRequiredFields()
        viewModel.create()
        advanceUntilIdle()

        assertNotNull(capturedName)
        assertEquals("Test Product", capturedName.english)
        assertNull(capturedName.spanish)
        assertNull(capturedName.french)
        assertEquals("Test Product", capturedName.fallback)
    }

    @Test
    fun `create should use correct language for FoodName - Spanish`() = runTest {
        var capturedName: FoodName? = null
        val repository =
            object : UserFoodRepository {
                override suspend fun create(
                    name: FoodName,
                    brand: FoodBrand?,
                    barcode: Barcode?,
                    note: FoodNote?,
                    imageUri: String?,
                    source: FoodSource.UserAdded?,
                    nutritionFacts: NutritionFacts,
                    servingQuantity: AbsoluteQuantity?,
                    packageQuantity: AbsoluteQuantity?,
                    accountId: LocalAccountId,
                ): FoodProductIdentity.Local {
                    capturedName = name
                    return FoodProductIdentity.Local("test-id")
                }
            }

        val viewModel =
            createViewModel(
                repository = repository,
                selector = createDefaultSelector(Language.Spanish),
            )

        viewModel.productFormState.value.fillRequiredFields()
        viewModel.create()
        advanceUntilIdle()

        assertNotNull(capturedName)
        assertNull(capturedName.english)
        assertEquals("Test Product", capturedName.spanish)
        assertEquals("Test Product", capturedName.fallback)
    }

    @Test
    fun `create should convert kilojoules to kilocalories`() = runTest {
        var capturedNutrition: NutritionFacts? = null
        val repository =
            object : UserFoodRepository {
                override suspend fun create(
                    name: FoodName,
                    brand: FoodBrand?,
                    barcode: Barcode?,
                    note: FoodNote?,
                    imageUri: String?,
                    source: FoodSource.UserAdded?,
                    nutritionFacts: NutritionFacts,
                    servingQuantity: AbsoluteQuantity?,
                    packageQuantity: AbsoluteQuantity?,
                    accountId: LocalAccountId,
                ): FoodProductIdentity.Local {
                    capturedNutrition = nutritionFacts
                    return FoodProductIdentity.Local("test-id")
                }
            }

        val account = testAccount(energyFormat = EnergyFormat.Kilojoules)
        val viewModel = createViewModel(account = account, repository = repository)

        val form = viewModel.productFormState.value
        form.fillRequiredFields()
        form.energy.textFieldState.setTextAndPlaceCursorAtEnd("418.4") // 418.4 kJ = ~100 kcal

        viewModel.create()
        advanceUntilIdle()

        assertNotNull(capturedNutrition)
        val energy = (capturedNutrition.energy as NutrientValue.Complete).value
        assertNotNull(energy)
        assertEquals(100.0, energy, 0.1)
    }

    @Test
    fun `create should not convert when using kilocalories`() = runTest {
        var capturedNutrition: NutritionFacts? = null
        val repository =
            object : UserFoodRepository {
                override suspend fun create(
                    name: FoodName,
                    brand: FoodBrand?,
                    barcode: Barcode?,
                    note: FoodNote?,
                    imageUri: String?,
                    source: FoodSource.UserAdded?,
                    nutritionFacts: NutritionFacts,
                    servingQuantity: AbsoluteQuantity?,
                    packageQuantity: AbsoluteQuantity?,
                    accountId: LocalAccountId,
                ): FoodProductIdentity.Local {
                    capturedNutrition = nutritionFacts
                    return FoodProductIdentity.Local("test-id")
                }
            }

        val account = testAccount(energyFormat = EnergyFormat.Kilocalories)
        val viewModel = createViewModel(account = account, repository = repository)

        val form = viewModel.productFormState.value
        form.fillRequiredFields()
        form.energy.textFieldState.setTextAndPlaceCursorAtEnd("200")

        viewModel.create()
        advanceUntilIdle()

        assertNotNull(capturedNutrition)
        val energy = (capturedNutrition.energy as NutrientValue.Complete).value
        assertEquals(200.0, energy)
    }

    @Test
    fun `create should calculate multiplier 1_0 for Grams100`() = runTest {
        var capturedNutrition: NutritionFacts? = null
        val repository =
            object : UserFoodRepository {
                override suspend fun create(
                    name: FoodName,
                    brand: FoodBrand?,
                    barcode: Barcode?,
                    note: FoodNote?,
                    imageUri: String?,
                    source: FoodSource.UserAdded?,
                    nutritionFacts: NutritionFacts,
                    servingQuantity: AbsoluteQuantity?,
                    packageQuantity: AbsoluteQuantity?,
                    accountId: LocalAccountId,
                ): FoodProductIdentity.Local {
                    capturedNutrition = nutritionFacts
                    return FoodProductIdentity.Local("test-id")
                }
            }

        val viewModel = createViewModel(repository = repository)

        val form = viewModel.productFormState.value
        form.fillRequiredFields()
        form.proteins.textFieldState.setTextAndPlaceCursorAtEnd("10")
        viewModel.setValuesPer(ValuesPer.Grams100)

        viewModel.create()
        advanceUntilIdle()

        val proteins = (capturedNutrition?.proteins as? NutrientValue.Complete)?.value
        assertEquals(10.0, proteins) // No multiplication
    }

    @Test
    fun `create should calculate multiplier for Serving with grams`() = runTest {
        var capturedNutrition: NutritionFacts? = null
        val repository =
            object : UserFoodRepository {
                override suspend fun create(
                    name: FoodName,
                    brand: FoodBrand?,
                    barcode: Barcode?,
                    note: FoodNote?,
                    imageUri: String?,
                    source: FoodSource.UserAdded?,
                    nutritionFacts: NutritionFacts,
                    servingQuantity: AbsoluteQuantity?,
                    packageQuantity: AbsoluteQuantity?,
                    accountId: LocalAccountId,
                ): FoodProductIdentity.Local {
                    capturedNutrition = nutritionFacts
                    return FoodProductIdentity.Local("test-id")
                }
            }

        val viewModel = createViewModel(repository = repository)

        val form = viewModel.productFormState.value
        form.fillRequiredFields()
        viewModel.setValuesPer(ValuesPer.Serving)
        form.servingQuantity.textFieldState.setTextAndPlaceCursorAtEnd("50") // 50g serving
        viewModel.setServingUnit(QuantityUnit.Gram)
        form.proteins.textFieldState.setTextAndPlaceCursorAtEnd("10") // 10g per 50g serving

        viewModel.create()
        advanceUntilIdle()

        // Should be multiplied by 100/50 = 2.0, so 20g per 100g
        val proteins = (capturedNutrition?.proteins as NutrientValue.Complete).value
        assertEquals(20.0, proteins, 0.001)
    }

    @Test
    fun `create should calculate multiplier for Serving with ounces`() = runTest {
        var capturedNutrition: NutritionFacts? = null
        val repository =
            object : UserFoodRepository {
                override suspend fun create(
                    name: FoodName,
                    brand: FoodBrand?,
                    barcode: Barcode?,
                    note: FoodNote?,
                    imageUri: String?,
                    source: FoodSource.UserAdded?,
                    nutritionFacts: NutritionFacts,
                    servingQuantity: AbsoluteQuantity?,
                    packageQuantity: AbsoluteQuantity?,
                    accountId: LocalAccountId,
                ): FoodProductIdentity.Local {
                    capturedNutrition = nutritionFacts
                    return FoodProductIdentity.Local("test-id")
                }
            }

        val viewModel = createViewModel(repository = repository)

        val form = viewModel.productFormState.value
        form.fillRequiredFields()
        viewModel.setValuesPer(ValuesPer.Serving)
        form.servingQuantity.textFieldState.setTextAndPlaceCursorAtEnd("1") // 1 oz serving
        viewModel.setServingUnit(QuantityUnit.Ounce)
        form.proteins.textFieldState.setTextAndPlaceCursorAtEnd("10")

        viewModel.create()
        advanceUntilIdle()

        // 1 oz = ~28.35g, multiplier = 100/28.35 = ~3.527
        // 10 * 3.527 = ~35.27g per 100g
        val proteins = (capturedNutrition?.proteins as NutrientValue.Complete).value
        assertEquals(35.27, proteins, 0.1)
    }

    @Test
    fun `create should calculate multiplier for Serving with fluid ounces`() = runTest {
        var capturedNutrition: NutritionFacts? = null
        val repository =
            object : UserFoodRepository {
                override suspend fun create(
                    name: FoodName,
                    brand: FoodBrand?,
                    barcode: Barcode?,
                    note: FoodNote?,
                    imageUri: String?,
                    source: FoodSource.UserAdded?,
                    nutritionFacts: NutritionFacts,
                    servingQuantity: AbsoluteQuantity?,
                    packageQuantity: AbsoluteQuantity?,
                    accountId: LocalAccountId,
                ): FoodProductIdentity.Local {
                    capturedNutrition = nutritionFacts
                    return FoodProductIdentity.Local("test-id")
                }
            }

        val viewModel = createViewModel(repository = repository)

        val form = viewModel.productFormState.value
        form.fillRequiredFields()
        viewModel.setValuesPer(ValuesPer.Serving)
        form.servingQuantity.textFieldState.setTextAndPlaceCursorAtEnd("1") // 1 fl. oz serving
        viewModel.setServingUnit(QuantityUnit.FluidOunce)
        form.proteins.textFieldState.setTextAndPlaceCursorAtEnd("10")

        viewModel.create()
        advanceUntilIdle()

        // 1 fl oz = ~29.57ml, multiplier = 100/29.57 = ~3.383
        // 10 * 3.383 = ~33.83g per 100g
        val proteins = (capturedNutrition?.proteins as NutrientValue.Complete).value
        assertEquals(33.83, proteins, 0.1)
    }

    @Test
    fun `create should calculate multiplier for Package`() = runTest {
        var capturedNutrition: NutritionFacts? = null
        val repository =
            object : UserFoodRepository {
                override suspend fun create(
                    name: FoodName,
                    brand: FoodBrand?,
                    barcode: Barcode?,
                    note: FoodNote?,
                    imageUri: String?,
                    source: FoodSource.UserAdded?,
                    nutritionFacts: NutritionFacts,
                    servingQuantity: AbsoluteQuantity?,
                    packageQuantity: AbsoluteQuantity?,
                    accountId: LocalAccountId,
                ): FoodProductIdentity.Local {
                    capturedNutrition = nutritionFacts
                    return FoodProductIdentity.Local("test-id")
                }
            }

        val viewModel = createViewModel(repository = repository)

        val form = viewModel.productFormState.value
        form.fillRequiredFields()
        viewModel.setValuesPer(ValuesPer.Package)
        form.packageQuantity.textFieldState.setTextAndPlaceCursorAtEnd("250") // 250g package
        viewModel.setPackageUnit(QuantityUnit.Gram)
        form.proteins.textFieldState.setTextAndPlaceCursorAtEnd("25") // 25g per package

        viewModel.create()
        advanceUntilIdle()

        // Should be multiplied by 100/250 = 0.4, so 10g per 100g
        val proteins = (capturedNutrition?.proteins as NutrientValue.Complete).value
        assertEquals(10.0, proteins, 0.001)
    }

    @Test
    fun `create should include brand when provided`() = runTest {
        var capturedBrand: FoodBrand? = null
        val repository =
            object : UserFoodRepository {
                override suspend fun create(
                    name: FoodName,
                    brand: FoodBrand?,
                    barcode: Barcode?,
                    note: FoodNote?,
                    imageUri: String?,
                    source: FoodSource.UserAdded?,
                    nutritionFacts: NutritionFacts,
                    servingQuantity: AbsoluteQuantity?,
                    packageQuantity: AbsoluteQuantity?,
                    accountId: LocalAccountId,
                ): FoodProductIdentity.Local {
                    capturedBrand = brand
                    return FoodProductIdentity.Local("test-id")
                }
            }

        val viewModel = createViewModel(repository = repository)

        val form = viewModel.productFormState.value
        form.fillRequiredFields()
        form.brand.textFieldState.setTextAndPlaceCursorAtEnd("Test Brand")

        viewModel.create()
        advanceUntilIdle()

        assertNotNull(capturedBrand)
        assertEquals("Test Brand", capturedBrand?.value)
    }

    @Test
    fun `create should include barcode when provided`() = runTest {
        var capturedBarcode: Barcode? = null
        val repository =
            object : UserFoodRepository {
                override suspend fun create(
                    name: FoodName,
                    brand: FoodBrand?,
                    barcode: Barcode?,
                    note: FoodNote?,
                    imageUri: String?,
                    source: FoodSource.UserAdded?,
                    nutritionFacts: NutritionFacts,
                    servingQuantity: AbsoluteQuantity?,
                    packageQuantity: AbsoluteQuantity?,
                    accountId: LocalAccountId,
                ): FoodProductIdentity.Local {
                    capturedBarcode = barcode
                    return FoodProductIdentity.Local("test-id")
                }
            }

        val viewModel = createViewModel(repository = repository)

        val form = viewModel.productFormState.value
        form.fillRequiredFields()
        form.barcode.textFieldState.setTextAndPlaceCursorAtEnd("1234567890")

        viewModel.create()
        advanceUntilIdle()

        assertNotNull(capturedBarcode)
        assertEquals("1234567890", capturedBarcode?.value)
    }

    @Test
    fun `create should include note when provided`() = runTest {
        var capturedNote: FoodNote? = null
        val repository =
            object : UserFoodRepository {
                override suspend fun create(
                    name: FoodName,
                    brand: FoodBrand?,
                    barcode: Barcode?,
                    note: FoodNote?,
                    imageUri: String?,
                    source: FoodSource.UserAdded?,
                    nutritionFacts: NutritionFacts,
                    servingQuantity: AbsoluteQuantity?,
                    packageQuantity: AbsoluteQuantity?,
                    accountId: LocalAccountId,
                ): FoodProductIdentity.Local {
                    capturedNote = note
                    return FoodProductIdentity.Local("test-id")
                }
            }

        val viewModel = createViewModel(repository = repository)

        val form = viewModel.productFormState.value
        form.fillRequiredFields()
        form.note.textFieldState.setTextAndPlaceCursorAtEnd("Test note")

        viewModel.create()
        advanceUntilIdle()

        assertNotNull(capturedNote)
        assertEquals("Test note", capturedNote?.value)
    }

    @Test
    fun `create should include source when provided`() = runTest {
        var capturedSource: FoodSource.UserAdded? = null
        val repository =
            object : UserFoodRepository {
                override suspend fun create(
                    name: FoodName,
                    brand: FoodBrand?,
                    barcode: Barcode?,
                    note: FoodNote?,
                    imageUri: String?,
                    source: FoodSource.UserAdded?,
                    nutritionFacts: NutritionFacts,
                    servingQuantity: AbsoluteQuantity?,
                    packageQuantity: AbsoluteQuantity?,
                    accountId: LocalAccountId,
                ): FoodProductIdentity.Local {
                    capturedSource = source
                    return FoodProductIdentity.Local("test-id")
                }
            }

        val viewModel = createViewModel(repository = repository)

        val form = viewModel.productFormState.value
        form.fillRequiredFields()
        form.source.textFieldState.setTextAndPlaceCursorAtEnd("Test source")

        viewModel.create()
        advanceUntilIdle()

        assertNotNull(capturedSource)
        assertEquals("Test source", capturedSource?.value)
    }

    @Test
    fun `create should include imageUri when provided`() = runTest {
        var capturedImageUri: String? = null
        val repository =
            object : UserFoodRepository {
                override suspend fun create(
                    name: FoodName,
                    brand: FoodBrand?,
                    barcode: Barcode?,
                    note: FoodNote?,
                    imageUri: String?,
                    source: FoodSource.UserAdded?,
                    nutritionFacts: NutritionFacts,
                    servingQuantity: AbsoluteQuantity?,
                    packageQuantity: AbsoluteQuantity?,
                    accountId: LocalAccountId,
                ): FoodProductIdentity.Local {
                    capturedImageUri = imageUri
                    return FoodProductIdentity.Local("test-id")
                }
            }

        val viewModel = createViewModel(repository = repository)

        val form = viewModel.productFormState.value
        form.fillRequiredFields()
        viewModel.setImage("content://test-image")

        viewModel.create()
        advanceUntilIdle()

        assertEquals("content://test-image", capturedImageUri)
    }

    @Test
    fun `create should convert serving quantity to correct AbsoluteQuantity`() = runTest {
        var capturedServingQuantity: AbsoluteQuantity? = null
        val repository =
            object : UserFoodRepository {
                override suspend fun create(
                    name: FoodName,
                    brand: FoodBrand?,
                    barcode: Barcode?,
                    note: FoodNote?,
                    imageUri: String?,
                    source: FoodSource.UserAdded?,
                    nutritionFacts: NutritionFacts,
                    servingQuantity: AbsoluteQuantity?,
                    packageQuantity: AbsoluteQuantity?,
                    accountId: LocalAccountId,
                ): FoodProductIdentity.Local {
                    capturedServingQuantity = servingQuantity
                    return FoodProductIdentity.Local("test-id")
                }
            }

        val viewModel = createViewModel(repository = repository)

        val form = viewModel.productFormState.value
        form.fillRequiredFields()
        form.servingQuantity.textFieldState.setTextAndPlaceCursorAtEnd("50")
        viewModel.setServingUnit(QuantityUnit.Gram)

        viewModel.create()
        advanceUntilIdle()

        assertNotNull(capturedServingQuantity)
        assertTrue(capturedServingQuantity is AbsoluteQuantity.Weight)
        assertEquals(50.0, (capturedServingQuantity as AbsoluteQuantity.Weight).weight.grams)
    }

    @Test
    fun `create should convert package quantity to correct AbsoluteQuantity`() = runTest {
        var capturedPackageQuantity: AbsoluteQuantity? = null
        val repository =
            object : UserFoodRepository {
                override suspend fun create(
                    name: FoodName,
                    brand: FoodBrand?,
                    barcode: Barcode?,
                    note: FoodNote?,
                    imageUri: String?,
                    source: FoodSource.UserAdded?,
                    nutritionFacts: NutritionFacts,
                    servingQuantity: AbsoluteQuantity?,
                    packageQuantity: AbsoluteQuantity?,
                    accountId: LocalAccountId,
                ): FoodProductIdentity.Local {
                    capturedPackageQuantity = packageQuantity
                    return FoodProductIdentity.Local("test-id")
                }
            }

        val viewModel = createViewModel(repository = repository)

        val form = viewModel.productFormState.value
        form.fillRequiredFields()
        form.packageQuantity.textFieldState.setTextAndPlaceCursorAtEnd("250")
        viewModel.setPackageUnit(QuantityUnit.Milliliter)

        viewModel.create()
        advanceUntilIdle()

        assertNotNull(capturedPackageQuantity)
        assertTrue(capturedPackageQuantity is AbsoluteQuantity.Volume)
        assertEquals(250.0, (capturedPackageQuantity as AbsoluteQuantity.Volume).volume.milliliters)
    }

    @Test
    fun `create should use correct accountId`() = runTest {
        var capturedAccountId: LocalAccountId? = null
        val repository =
            object : UserFoodRepository {
                override suspend fun create(
                    name: FoodName,
                    brand: FoodBrand?,
                    barcode: Barcode?,
                    note: FoodNote?,
                    imageUri: String?,
                    source: FoodSource.UserAdded?,
                    nutritionFacts: NutritionFacts,
                    servingQuantity: AbsoluteQuantity?,
                    packageQuantity: AbsoluteQuantity?,
                    accountId: LocalAccountId,
                ): FoodProductIdentity.Local {
                    capturedAccountId = accountId
                    return FoodProductIdentity.Local("test-id")
                }
            }

        val account = testAccount(accountId = LocalAccountId("custom-account-id"))
        val viewModel = createViewModel(account = account, repository = repository)

        val form = viewModel.productFormState.value
        form.fillRequiredFields()

        viewModel.create()
        advanceUntilIdle()

        assertEquals(LocalAccountId("custom-account-id"), capturedAccountId)
    }
}

// Helper function to create test account
private fun testAccount(
    accountId: LocalAccountId = LocalAccountId("test-account"),
    energyFormat: EnergyFormat = EnergyFormat.Kilocalories,
): Account =
    testAccount(
        localAccountId = accountId,
        settings = AccountSettings.default.copy(energyFormat = energyFormat),
    )
