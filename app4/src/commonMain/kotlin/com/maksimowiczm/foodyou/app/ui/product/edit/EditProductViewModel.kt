package com.maksimowiczm.foodyou.app.ui.product.edit

import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.application.ObservePrimaryAccountUseCase
import com.maksimowiczm.foodyou.app.ui.common.form.FormField
import com.maksimowiczm.foodyou.app.ui.common.utility.formatClipZeros
import com.maksimowiczm.foodyou.app.ui.product.ProductFormState
import com.maksimowiczm.foodyou.app.ui.product.ProductFormState.Companion.optionalField
import com.maksimowiczm.foodyou.app.ui.product.ProductFormState.Companion.requiredField
import com.maksimowiczm.foodyou.app.ui.product.ProductFormTransformer
import com.maksimowiczm.foodyou.app.ui.product.QuantityUnit
import com.maksimowiczm.foodyou.app.ui.product.ValuesPer
import com.maksimowiczm.foodyou.common.domain.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.FluidOunces
import com.maksimowiczm.foodyou.common.domain.Grams
import com.maksimowiczm.foodyou.common.domain.Milliliters
import com.maksimowiczm.foodyou.common.domain.Ounces
import com.maksimowiczm.foodyou.food.domain.FoodImage
import com.maksimowiczm.foodyou.food.domain.FoodNameSelector
import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity
import com.maksimowiczm.foodyou.food.domain.FoodSource
import com.maksimowiczm.foodyou.food.domain.NutrientValue
import com.maksimowiczm.foodyou.food.domain.UserFoodRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditProductViewModel(
    private val observePrimaryAccountUseCase: ObservePrimaryAccountUseCase,
    private val userFoodRepository: UserFoodRepository,
    private val foodNameSelector: FoodNameSelector,
    private val productFormTransformer: ProductFormTransformer,
    private val identity: FoodProductIdentity.Local,
) : ViewModel() {
    private val eventBus = Channel<EditProductEvent>()
    val uiEvents = eventBus.receiveAsFlow()

    private val _productFormState = MutableStateFlow(ProductFormState())
    val productFormState = _productFormState.asStateFlow()

    private val _isLocked = MutableStateFlow(true)
    val isLocked = _isLocked.asStateFlow()

    init {
        viewModelScope.launch {
            val account = observePrimaryAccountUseCase.observe().first()
            val product =
                userFoodRepository
                    .observe(identity = identity, accountId = account.localAccountId)
                    .first()

            requireNotNull(product) { "Product not found: $identity" }

            val name = foodNameSelector.select(product.name)
            val source = (product.source as? FoodSource.UserAdded)?.value
            val (servingQuantity, servingUnit) =
                run {
                    if (product.servingQuantity == null) {
                        return@run null to QuantityUnit.Gram
                    }

                    when (product.servingQuantity) {
                        is AbsoluteQuantity.Volume ->
                            when (product.servingQuantity.volume) {
                                is FluidOunces ->
                                    product.servingQuantity.volume.fluidOunces to
                                        QuantityUnit.FluidOunce

                                is Milliliters ->
                                    product.servingQuantity.volume.milliliters to
                                        QuantityUnit.Milliliter
                            }

                        is AbsoluteQuantity.Weight ->
                            when (product.servingQuantity.weight) {
                                is Grams ->
                                    product.servingQuantity.weight.grams to QuantityUnit.Gram

                                is Ounces ->
                                    product.servingQuantity.weight.ounces to QuantityUnit.Ounce
                            }
                    }
                }
            val (packageQuantity, packageUnit) =
                run {
                    if (product.packageQuantity == null) {
                        return@run null to QuantityUnit.Gram
                    }

                    when (product.packageQuantity) {
                        is AbsoluteQuantity.Volume ->
                            when (product.packageQuantity.volume) {
                                is FluidOunces ->
                                    product.packageQuantity.volume.fluidOunces to
                                        QuantityUnit.FluidOunce

                                is Milliliters ->
                                    product.packageQuantity.volume.milliliters to
                                        QuantityUnit.Milliliter
                            }

                        is AbsoluteQuantity.Weight ->
                            when (product.packageQuantity.weight) {
                                is Grams ->
                                    product.packageQuantity.weight.grams to QuantityUnit.Gram

                                is Ounces ->
                                    product.packageQuantity.weight.ounces to QuantityUnit.Ounce
                            }
                    }
                }
            val image = (product.image as? FoodImage.Local)?.uri

            _productFormState.update {
                it.copy(
                        defaultName = name,
                        defaultBrand = product.brand?.value,
                        defaultBarcode = product.barcode?.value,
                        defaultNote = product.note?.value,
                        defaultSource = source,
                        defaultProteins = product.nutritionFacts.proteins.value,
                        defaultFats = product.nutritionFacts.fats.value,
                        defaultCarbohydrates = product.nutritionFacts.carbohydrates.value,
                        defaultEnergy = product.nutritionFacts.energy.value,
                        defaultSaturatedFats = product.nutritionFacts.saturatedFats.value,
                        defaultTransFats = product.nutritionFacts.transFats.value,
                        defaultMonounsaturatedFats =
                            product.nutritionFacts.monounsaturatedFats.value,
                        defaultPolyunsaturatedFats =
                            product.nutritionFacts.polyunsaturatedFats.value,
                        defaultOmega3 = product.nutritionFacts.omega3.value,
                        defaultOmega6 = product.nutritionFacts.omega6.value,
                        defaultSugars = product.nutritionFacts.sugars.value,
                        defaultAddedSugars = product.nutritionFacts.addedSugars.value,
                        defaultDietaryFiber = product.nutritionFacts.dietaryFiber.value,
                        defaultSolubleFiber = product.nutritionFacts.solubleFiber.value,
                        defaultInsolubleFiber = product.nutritionFacts.insolubleFiber.value,
                        defaultSalt = product.nutritionFacts.salt.value,
                        defaultCholesterolMilli =
                            product.nutritionFacts.cholesterol.value?.times(1_000),
                        defaultCaffeineMilli = product.nutritionFacts.caffeine.value?.times(1_000),
                        defaultVitaminAMicro =
                            product.nutritionFacts.vitaminA.value?.times(1_000_000),
                        defaultVitaminB1Milli =
                            product.nutritionFacts.vitaminB1.value?.times(1_000),
                        defaultVitaminB2Milli =
                            product.nutritionFacts.vitaminB2.value?.times(1_000),
                        defaultVitaminB3Milli =
                            product.nutritionFacts.vitaminB3.value?.times(1_000),
                        defaultVitaminB5Milli =
                            product.nutritionFacts.vitaminB5.value?.times(1_000),
                        defaultVitaminB6Milli =
                            product.nutritionFacts.vitaminB6.value?.times(1_000),
                        defaultVitaminB7Micro =
                            product.nutritionFacts.vitaminB7.value?.times(1_000_000),
                        defaultVitaminB9Micro =
                            product.nutritionFacts.vitaminB9.value?.times(1_000_000),
                        defaultVitaminB12Micro =
                            product.nutritionFacts.vitaminB12.value?.times(1_000_000),
                        defaultVitaminCMilli = product.nutritionFacts.vitaminC.value?.times(1_000),
                        defaultVitaminDMicro =
                            product.nutritionFacts.vitaminD.value?.times(1_000_000),
                        defaultVitaminEMilli = product.nutritionFacts.vitaminE.value?.times(1_000),
                        defaultVitaminKMicro =
                            product.nutritionFacts.vitaminK.value?.times(1_000_000),
                        defaultManganeseMilli =
                            product.nutritionFacts.manganese.value?.times(1_000),
                        defaultMagnesiumMilli =
                            product.nutritionFacts.magnesium.value?.times(1_000),
                        defaultPotassiumMilli =
                            product.nutritionFacts.potassium.value?.times(1_000),
                        defaultCalciumMilli = product.nutritionFacts.calcium.value?.times(1_000),
                        defaultCopperMilli = product.nutritionFacts.copper.value?.times(1_000),
                        defaultZincMilli = product.nutritionFacts.zinc.value?.times(1_000),
                        defaultSodiumMilli = product.nutritionFacts.sodium.value?.times(1_000),
                        defaultIronMilli = product.nutritionFacts.iron.value?.times(1_000),
                        defaultPhosphorusMilli =
                            product.nutritionFacts.phosphorus.value?.times(1_000),
                        defaultSeleniumMicro =
                            product.nutritionFacts.selenium.value?.times(1_000_000),
                        defaultIodineMicro = product.nutritionFacts.iodine.value?.times(1_000_000),
                        defaultChromiumMicro =
                            product.nutritionFacts.chromium.value?.times(1_000_000),
                        defaultServingQuantity = servingQuantity,
                        defaultServingUnit = servingUnit,
                        defaultPackageQuantity = packageQuantity,
                        defaultPackageUnit = packageUnit,
                        imageUri = image,
                        defaultImageUri = image,
                        servingUnit = servingUnit,
                        packageUnit = packageUnit,
                    )
                    .apply {
                        this.name.textFieldState.setTextAndPlaceCursorAtEnd(name)
                        this.brand.textFieldState.setTextAndPlaceCursorAtEnd(
                            product.brand?.value.orEmpty()
                        )
                        this.barcode.textFieldState.setTextAndPlaceCursorAtEnd(
                            product.barcode?.value.orEmpty()
                        )
                        this.note.textFieldState.setTextAndPlaceCursorAtEnd(
                            product.note?.value.orEmpty()
                        )
                        this.source.textFieldState.setTextAndPlaceCursorAtEnd(source.orEmpty())
                        this.servingQuantity.fill(servingQuantity)
                        this.packageQuantity.fill(packageQuantity)
                        this.proteins.fill(product.nutritionFacts.proteins)
                        this.fats.fill(product.nutritionFacts.fats)
                        this.carbohydrates.fill(product.nutritionFacts.carbohydrates)
                        this.energy.fill(product.nutritionFacts.energy)
                        this.saturatedFats.fill(product.nutritionFacts.saturatedFats)
                        this.transFats.fill(product.nutritionFacts.transFats)
                        this.monounsaturatedFats.fill(product.nutritionFacts.monounsaturatedFats)
                        this.polyunsaturatedFats.fill(product.nutritionFacts.polyunsaturatedFats)
                        this.omega3.fill(product.nutritionFacts.omega3)
                        this.omega6.fill(product.nutritionFacts.omega6)
                        this.sugars.fill(product.nutritionFacts.sugars)
                        this.addedSugars.fill(product.nutritionFacts.addedSugars)
                        this.dietaryFiber.fill(product.nutritionFacts.dietaryFiber)
                        this.solubleFiber.fill(product.nutritionFacts.solubleFiber)
                        this.insolubleFiber.fill(product.nutritionFacts.insolubleFiber)
                        this.salt.fill(product.nutritionFacts.salt)
                        this.cholesterolMilli.fill(
                            product.nutritionFacts.cholesterol.value?.times(1_000)
                        )
                        this.caffeineMilli.fill(product.nutritionFacts.caffeine.value?.times(1_000))
                        this.vitaminAMicro.fill(
                            product.nutritionFacts.vitaminA.value?.times(1_000_000)
                        )
                        this.vitaminB1Milli.fill(
                            product.nutritionFacts.vitaminB1.value?.times(1_000)
                        )
                        this.vitaminB2Milli.fill(
                            product.nutritionFacts.vitaminB2.value?.times(1_000)
                        )
                        this.vitaminB3Milli.fill(
                            product.nutritionFacts.vitaminB3.value?.times(1_000)
                        )
                        this.vitaminB5Milli.fill(
                            product.nutritionFacts.vitaminB5.value?.times(1_000)
                        )
                        this.vitaminB6Milli.fill(
                            product.nutritionFacts.vitaminB6.value?.times(1_000)
                        )
                        this.vitaminB7Micro.fill(
                            product.nutritionFacts.vitaminB7.value?.times(1_000_000)
                        )
                        this.vitaminB9Micro.fill(
                            product.nutritionFacts.vitaminB9.value?.times(1_000_000)
                        )
                        this.vitaminB12Micro.fill(
                            product.nutritionFacts.vitaminB12.value?.times(1_000_000)
                        )
                        this.vitaminCMilli.fill(product.nutritionFacts.vitaminC.value?.times(1_000))
                        this.vitaminDMicro.fill(
                            product.nutritionFacts.vitaminD.value?.times(1_000_000)
                        )
                        this.vitaminEMilli.fill(product.nutritionFacts.vitaminE.value?.times(1_000))
                        this.vitaminKMicro.fill(
                            product.nutritionFacts.vitaminK.value?.times(1_000_000)
                        )
                        this.manganeseMilli.fill(
                            product.nutritionFacts.manganese.value?.times(1_000)
                        )
                        this.magnesiumMilli.fill(
                            product.nutritionFacts.magnesium.value?.times(1_000)
                        )
                        this.potassiumMilli.fill(
                            product.nutritionFacts.potassium.value?.times(1_000)
                        )
                        this.calciumMilli.fill(product.nutritionFacts.calcium.value?.times(1_000))
                        this.copperMilli.fill(product.nutritionFacts.copper.value?.times(1_000))
                        this.zincMilli.fill(product.nutritionFacts.zinc.value?.times(1_000))
                        this.sodiumMilli.fill(product.nutritionFacts.sodium.value?.times(1_000))
                        this.ironMilli.fill(product.nutritionFacts.iron.value?.times(1_000))
                        this.phosphorusMilli.fill(
                            product.nutritionFacts.phosphorus.value?.times(1_000)
                        )
                        this.seleniumMicro.fill(
                            product.nutritionFacts.selenium.value?.times(1_000_000)
                        )
                        this.iodineMicro.fill(product.nutritionFacts.iodine.value?.times(1_000_000))
                        this.chromiumMicro.fill(
                            product.nutritionFacts.chromium.value?.times(1_000_000)
                        )
                    }
            }

            _isLocked.value = false
        }
    }

    fun setImage(uri: String?) {
        _productFormState.value = _productFormState.value.copy(imageUri = uri)
    }

    fun setValuesPer(valuesPer: ValuesPer) {
        _productFormState.update {
            val servingQuantity =
                when (valuesPer) {
                    ValuesPer.Serving -> requiredField(it.servingQuantity.textFieldState)
                    else -> optionalField(it.servingQuantity.textFieldState)
                }

            val packageQuantity =
                when (valuesPer) {
                    ValuesPer.Package -> requiredField(it.packageQuantity.textFieldState)
                    else -> optionalField(it.packageQuantity.textFieldState)
                }

            it.copy(
                valuesPer = valuesPer,
                servingQuantity = servingQuantity,
                packageQuantity = packageQuantity,
            )
        }
    }

    fun setServingUnit(unit: QuantityUnit) {
        _productFormState.update { it.copy(servingUnit = unit) }
    }

    fun setPackageUnit(unit: QuantityUnit) {
        _productFormState.update { it.copy(packageUnit = unit) }
    }

    fun save() {
        val isLocked = _isLocked.value
        if (isLocked) return
        _isLocked.value = true

        val form = _productFormState.value.copy()
        require(form.isValid) { "Form is not valid" }

        viewModelScope.launch {
            val (
                foodName,
                brand,
                barcode,
                note,
                source,
                nutritionFacts,
                servingQuantity,
                packageQuantity,
                isLiquid) =
                productFormTransformer.validate(form)

            val accountId = observePrimaryAccountUseCase.observe().first().localAccountId

            userFoodRepository.edit(
                identity = identity,
                name = foodName,
                brand = brand,
                barcode = barcode,
                note = note,
                imageUri = form.imageUri,
                source = source,
                nutritionFacts = nutritionFacts,
                servingQuantity = servingQuantity,
                packageQuantity = packageQuantity,
                accountId = accountId,
                isLiquid = isLiquid,
            )

            eventBus.send(EditProductEvent.Edited)
        }
    }
}

private fun FormField<Double?, *>.fill(nutrient: NutrientValue) {
    fill(nutrient.value)
}

private fun FormField<Double?, *>.fill(value: Double?) {
    this.textFieldState.setTextAndPlaceCursorAtEnd(value?.formatClipZeros().orEmpty())
}
