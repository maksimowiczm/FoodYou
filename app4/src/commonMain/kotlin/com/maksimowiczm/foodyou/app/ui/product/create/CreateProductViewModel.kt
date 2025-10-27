package com.maksimowiczm.foodyou.app.ui.product.create

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.application.ObservePrimaryAccountUseCase
import com.maksimowiczm.foodyou.account.domain.EnergyFormat
import com.maksimowiczm.foodyou.app.ui.common.form.FormField
import com.maksimowiczm.foodyou.app.ui.common.form.Parser
import com.maksimowiczm.foodyou.app.ui.common.form.nonBlankStringValidator
import com.maksimowiczm.foodyou.app.ui.common.form.nonNegativeDoubleValidator
import com.maksimowiczm.foodyou.app.ui.common.form.nullableDoubleParser
import com.maksimowiczm.foodyou.app.ui.common.form.nullableStringParser
import com.maksimowiczm.foodyou.app.ui.common.form.numericStringValidator
import com.maksimowiczm.foodyou.app.ui.common.form.stringParser
import com.maksimowiczm.foodyou.app.ui.product.FormFieldError
import com.maksimowiczm.foodyou.app.ui.product.ProductFormState
import com.maksimowiczm.foodyou.app.ui.product.QuantityUnit
import com.maksimowiczm.foodyou.app.ui.product.ValuesPer
import com.maksimowiczm.foodyou.common.domain.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.FluidOunces
import com.maksimowiczm.foodyou.common.domain.Grams
import com.maksimowiczm.foodyou.common.domain.Language
import com.maksimowiczm.foodyou.common.domain.Milliliters
import com.maksimowiczm.foodyou.common.domain.Ounces
import com.maksimowiczm.foodyou.food.domain.Barcode
import com.maksimowiczm.foodyou.food.domain.FoodBrand
import com.maksimowiczm.foodyou.food.domain.FoodName
import com.maksimowiczm.foodyou.food.domain.FoodNameSelector
import com.maksimowiczm.foodyou.food.domain.FoodNote
import com.maksimowiczm.foodyou.food.domain.FoodSource
import com.maksimowiczm.foodyou.food.domain.NutrientValue
import com.maksimowiczm.foodyou.food.domain.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.food.domain.NutritionFacts
import com.maksimowiczm.foodyou.food.domain.UserFoodRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateProductViewModel(
    private val observePrimaryAccountUseCase: ObservePrimaryAccountUseCase,
    private val userFoodRepository: UserFoodRepository,
    private val foodNameSelector: FoodNameSelector,
) : ViewModel() {

    private val eventBus = Channel<CreateProductEvent>()
    val uiEvents = eventBus.receiveAsFlow()

    private val _productFormState =
        MutableStateFlow(
            ProductFormState(
                name =
                    FormField(
                        parser = stringParser(),
                        validator = nonBlankStringValidator(onEmpty = { FormFieldError.Required }),
                    ),
                defaultName = "",
                brand = FormField(parser = nullableStringParser()),
                defaultBrand = null,
                barcode =
                    FormField(
                        parser = nullableStringParser(),
                        validator =
                            numericStringValidator(onNotNumeric = { FormFieldError.NotABarcode }),
                    ),
                defaultBarcode = null,
                note = FormField(parser = nullableStringParser()),
                defaultNote = null,
                source = FormField(parser = nullableStringParser()),
                defaultSource = null,
                proteins = requiredField(),
                defaultProteins = null,
                fats = requiredField(),
                defaultFats = null,
                carbohydrates = requiredField(),
                defaultCarbohydrates = null,
                energy = requiredField(),
                defaultEnergy = null,
                saturatedFats = optionalField(),
                defaultSaturatedFats = null,
                transFats = optionalField(),
                defaultTransFats = null,
                monounsaturatedFats = optionalField(),
                defaultMonounsaturatedFats = null,
                polyunsaturatedFats = optionalField(),
                defaultPolyunsaturatedFats = null,
                omega3 = optionalField(),
                defaultOmega3 = null,
                omega6 = optionalField(),
                defaultOmega6 = null,
                sugars = optionalField(),
                defaultSugars = null,
                addedSugars = optionalField(),
                defaultAddedSugars = null,
                dietaryFiber = optionalField(),
                defaultDietaryFiber = null,
                solubleFiber = optionalField(),
                defaultSolubleFiber = null,
                insolubleFiber = optionalField(),
                defaultInsolubleFiber = null,
                salt = optionalField(),
                defaultSalt = null,
                cholesterolMilli = optionalField(),
                defaultCholesterolMilli = null,
                caffeineMilli = optionalField(),
                defaultCaffeineMilli = null,
                vitaminAMicro = optionalField(),
                defaultVitaminAMicro = null,
                vitaminB1Milli = optionalField(),
                defaultVitaminB1Milli = null,
                vitaminB2Milli = optionalField(),
                defaultVitaminB2Milli = null,
                vitaminB3Milli = optionalField(),
                defaultVitaminB3Milli = null,
                vitaminB5Milli = optionalField(),
                defaultVitaminB5Milli = null,
                vitaminB6Milli = optionalField(),
                defaultVitaminB6Milli = null,
                vitaminB7Micro = optionalField(),
                defaultVitaminB7Micro = null,
                vitaminB9Micro = optionalField(),
                defaultVitaminB9Micro = null,
                vitaminB12Micro = optionalField(),
                defaultVitaminB12Micro = null,
                vitaminCMilli = optionalField(),
                defaultVitaminCMilli = null,
                vitaminDMicro = optionalField(),
                defaultVitaminDMicro = null,
                vitaminEMilli = optionalField(),
                defaultVitaminEMilli = null,
                vitaminKMicro = optionalField(),
                defaultVitaminKMicro = null,
                manganeseMilli = optionalField(),
                defaultManganeseMilli = null,
                magnesiumMilli = optionalField(),
                defaultMagnesiumMilli = null,
                potassiumMilli = optionalField(),
                defaultPotassiumMilli = null,
                calciumMilli = optionalField(),
                defaultCalciumMilli = null,
                copperMilli = optionalField(),
                defaultCopperMilli = null,
                zincMilli = optionalField(),
                defaultZincMilli = null,
                sodiumMilli = optionalField(),
                defaultSodiumMilli = null,
                ironMilli = optionalField(),
                defaultIronMilli = null,
                phosphorusMilli = optionalField(),
                defaultPhosphorusMilli = null,
                seleniumMicro = optionalField(),
                defaultSeleniumMicro = null,
                iodineMicro = optionalField(),
                defaultIodineMicro = null,
                chromiumMicro = optionalField(),
                defaultChromiumMicro = null,
                imageUri = null,
                defaultImageUri = null,
                valuesPer = ValuesPer.Grams100,
                servingQuantity = optionalField(),
                defaultServingQuantity = null,
                servingUnit = QuantityUnit.Gram,
                defaultServingUnit = QuantityUnit.Gram,
                packageQuantity = optionalField(),
                packageUnit = QuantityUnit.Gram,
                defaultPackageUnit = QuantityUnit.Gram,
                defaultPackageQuantity = null,
            )
        )
    val productFormState = _productFormState.asStateFlow()

    private val _isLocked = MutableStateFlow(false)
    val isLocked = _isLocked.asStateFlow()

    fun create() {
        val isLocked = _isLocked.value
        if (isLocked) return
        _isLocked.value = true

        val form = _productFormState.value.copy()
        require(form.isValid) { "Form is not valid" }

        viewModelScope.launch {
            val energyFormat = observePrimaryAccountUseCase.observe().first().settings.energyFormat
            val language = foodNameSelector.select()

            requireNotNull(form.name.value) { "Name is required" }
            val foodName =
                FoodName(
                    english = if (language == Language.English) form.name.value else null,
                    catalan = if (language == Language.Catalan) form.name.value else null,
                    danish = if (language == Language.Danish) form.name.value else null,
                    german = if (language == Language.German) form.name.value else null,
                    spanish = if (language == Language.Spanish) form.name.value else null,
                    french = if (language == Language.French) form.name.value else null,
                    italian = if (language == Language.Italian) form.name.value else null,
                    hungarian = if (language == Language.Hungarian) form.name.value else null,
                    dutch = if (language == Language.Dutch) form.name.value else null,
                    polish = if (language == Language.Polish) form.name.value else null,
                    portugueseBrazil =
                        if (language == Language.PortugueseBrazil) form.name.value else null,
                    turkish = if (language == Language.Turkish) form.name.value else null,
                    russian = if (language == Language.Russian) form.name.value else null,
                    ukrainian = if (language == Language.Ukrainian) form.name.value else null,
                    arabic = if (language == Language.Arabic) form.name.value else null,
                    chineseSimplified =
                        if (language == Language.ChineseSimplified) form.name.value else null,
                    fallback = form.name.value!!,
                )

            val brand = form.brand.value?.let { FoodBrand(it) }

            val barcode = form.barcode.value?.let { Barcode(it) }

            val note = form.note.value?.let { FoodNote(it) }

            val source = form.source.value?.let { FoodSource.UserAdded(it) }

            // Multiplier is 1.0 for 100g/ml, serving size for serving, and package size for package
            // but needs to be adjusted to match that nutrition facts MUST be per 100g/ml
            val multiplier =
                when (form.valuesPer) {
                    ValuesPer.Grams100 -> 1.0
                    ValuesPer.Milliliters100 -> 1.0
                    ValuesPer.Serving -> {
                        val servingQuantity = form.servingQuantity.value
                        requireNotNull(servingQuantity) { "Serving quantity is required" }

                        when (form.servingUnit) {
                            QuantityUnit.Gram -> 100.0 / servingQuantity
                            QuantityUnit.Milliliter -> 100.0 / servingQuantity
                            QuantityUnit.Ounce -> 100.0 / Ounces(servingQuantity).grams
                            QuantityUnit.FluidOunce ->
                                100.0 / FluidOunces(servingQuantity).milliliters
                        }
                    }

                    ValuesPer.Package -> {
                        val packageQuantity = form.packageQuantity.value
                        requireNotNull(packageQuantity) { "Package quantity is required" }

                        when (form.packageUnit) {
                            QuantityUnit.Gram -> 100.0 / packageQuantity
                            QuantityUnit.Milliliter -> 100.0 / packageQuantity
                            QuantityUnit.Ounce -> 100.0 / Ounces(packageQuantity).grams
                            QuantityUnit.FluidOunce ->
                                100.0 / FluidOunces(packageQuantity).milliliters
                        }
                    }
                }

            // Energy MUST be in kilocalories internally
            val kcal =
                when (energyFormat) {
                    EnergyFormat.Kilocalories -> form.energy.value
                    EnergyFormat.Kilojoules -> form.energy.value?.let { it / 4.184 }
                }
            requireNotNull(kcal) { "Energy is required" }

            val nutritionFacts =
                form.toNutritionFacts(
                    multiplier = multiplier,
                    energy = NutrientValue.Complete(kcal),
                )

            val servingQuantity =
                form.servingQuantity.value?.let {
                    when (form.servingUnit) {
                        QuantityUnit.Gram -> AbsoluteQuantity.Weight(Grams(it))
                        QuantityUnit.Milliliter -> AbsoluteQuantity.Volume(Milliliters(it))
                        QuantityUnit.Ounce -> AbsoluteQuantity.Weight(Ounces(it))
                        QuantityUnit.FluidOunce -> AbsoluteQuantity.Volume(FluidOunces(it))
                    }
                }

            val packageQuantity =
                form.packageQuantity.value?.let {
                    when (form.packageUnit) {
                        QuantityUnit.Gram -> AbsoluteQuantity.Weight(Grams(it))
                        QuantityUnit.Milliliter -> AbsoluteQuantity.Volume(Milliliters(it))
                        QuantityUnit.Ounce -> AbsoluteQuantity.Weight(Ounces(it))
                        QuantityUnit.FluidOunce -> AbsoluteQuantity.Volume(FluidOunces(it))
                    }
                }

            val accountId = observePrimaryAccountUseCase.observe().first().localAccountId

            val id =
                userFoodRepository.create(
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
                )

            eventBus.send(CreateProductEvent.Created(id))
        }
    }

    fun setImage(uri: String?) {
        _productFormState.value = _productFormState.value.copy(imageUri = uri)
    }

    fun setValuesPer(valuesPer: ValuesPer) {
        _productFormState.update {
            val servingQuantity =
                when (valuesPer) {
                    ValuesPer.Serving ->
                        requiredField(textFieldState = it.servingQuantity.textFieldState)

                    else -> optionalField(textFieldState = it.servingQuantity.textFieldState)
                }

            val packageQuantity =
                when (valuesPer) {
                    ValuesPer.Package ->
                        requiredField(textFieldState = it.packageQuantity.textFieldState)

                    else -> optionalField(textFieldState = it.packageQuantity.textFieldState)
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
}

private fun optionalField(
    textFieldState: TextFieldState = TextFieldState(),
    parser: Parser<Double?, FormFieldError> =
        nullableDoubleParser(onNotANumber = { FormFieldError.NotANumber }),
    validator: (Double?) -> FormFieldError? =
        nonNegativeDoubleValidator(onNegative = { FormFieldError.NegativeValue }),
): FormField<Double?, FormFieldError> =
    FormField(textFieldState = textFieldState, parser = parser, validator = validator)

private fun requiredField(
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

private fun ProductFormState.toNutritionFacts(
    multiplier: Double,
    energy: NutrientValue,
): NutritionFacts =
    NutritionFacts.requireAll(
        proteins = proteins.value?.multiplier(multiplier).toNutrientValue(),
        carbohydrates = carbohydrates.value?.multiplier(multiplier).toNutrientValue(),
        fats = fats.value?.multiplier(multiplier).toNutrientValue(),
        energy = energy.value?.multiplier(multiplier).toNutrientValue(),
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

private fun Double.multiplier(multiplier: Double): Double? = times(multiplier)
