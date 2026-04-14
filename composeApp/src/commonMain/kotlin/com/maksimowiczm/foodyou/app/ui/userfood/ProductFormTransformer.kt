package com.maksimowiczm.foodyou.app.ui.userfood

import com.maksimowiczm.foodyou.app.ui.common.form.FormField
import com.maksimowiczm.foodyou.app.ui.common.utility.FoodNameSelector
import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import com.maksimowiczm.foodyou.common.domain.ImageUri
import com.maksimowiczm.foodyou.common.domain.Language
import com.maksimowiczm.foodyou.common.domain.fluidOunces
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.kilocalories
import com.maksimowiczm.foodyou.common.domain.kilojoules
import com.maksimowiczm.foodyou.common.domain.micrograms
import com.maksimowiczm.foodyou.common.domain.milligrams
import com.maksimowiczm.foodyou.common.domain.milliliters
import com.maksimowiczm.foodyou.common.domain.ounces
import com.maksimowiczm.foodyou.userfood.domain.UserFoodNote
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductBarcode
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductBrand
import kotlinx.coroutines.flow.first

internal class ProductFormTransformer(
    private val getAppAccountEnergyUnitUseCase: GetAppAccountEnergyUnitUseCase,
    private val foodNameSelector: FoodNameSelector,
) {
    data class Result(
        val name: FoodName,
        val brand: UserProductBrand?,
        val barcode: UserProductBarcode?,
        val note: UserFoodNote?,
        val image: ImageUri?,
        val nutritionFacts: NutritionFacts,
        val servingQuantity: AbsoluteQuantity?,
        val packageQuantity: AbsoluteQuantity?,
        val isLiquid: Boolean,
    )

    suspend fun transform(form: ProductFormState): Result {
        require(form.isValid) { "Form is not valid" }

        val energyFormat = getAppAccountEnergyUnitUseCase.getAppAccountEnergyUnit()
        val language = foodNameSelector.observeLanguage().first()

        val nameStr = form.name.textFieldState.text.toString()
        val name =
            FoodName.requireAll(
                english = if (language == Language.English) nameStr else null,
                catalan = if (language == Language.Catalan) nameStr else null,
                czech = if (language == Language.Czech) nameStr else null,
                danish = if (language == Language.Danish) nameStr else null,
                german = if (language == Language.German) nameStr else null,
                spanish = if (language == Language.Spanish) nameStr else null,
                french = if (language == Language.French) nameStr else null,
                indonesian = if (language == Language.Indonesian) nameStr else null,
                italian = if (language == Language.Italian) nameStr else null,
                hungarian = if (language == Language.Hungarian) nameStr else null,
                dutch = if (language == Language.Dutch) nameStr else null,
                polish = if (language == Language.Polish) nameStr else null,
                portugueseBrazil = if (language == Language.PortugueseBrazil) nameStr else null,
                slovenian = if (language == Language.Slovenian) nameStr else null,
                turkish = if (language == Language.Turkish) nameStr else null,
                russian = if (language == Language.Russian) nameStr else null,
                ukrainian = if (language == Language.Ukrainian) nameStr else null,
                arabic = if (language == Language.Arabic) nameStr else null,
                chineseSimplified = if (language == Language.ChineseSimplified) nameStr else null,
                fallback = nameStr,
            )

        val brand =
            form.brand.textFieldState.text
                .takeIf { it.isNotBlank() }
                ?.toString()
                ?.let(::UserProductBrand)

        val barcode =
            form.barcode.textFieldState.text
                .takeIf { it.isNotBlank() }
                ?.toString()
                ?.let(::UserProductBarcode)

        val note =
            form.note.textFieldState.text
                .takeIf { it.isNotBlank() }
                ?.toString()
                ?.let(::UserFoodNote)

        val servingQuantity = form.servingQuantity.toDouble()
        val packageQuantity = form.packageQuantity.toDouble()

        // Multiplier is 1.0 for 100g/ml, serving size for serving, and package size for package
        // but needs to be adjusted to match that nutrition facts MUST be per 100g/ml
        val multiplier =
            when (form.valuesPer.value) {
                ValuesPer.Grams100 -> 1.0
                ValuesPer.Milliliters100 -> 1.0
                ValuesPer.Serving -> {
                    requireNotNull(servingQuantity) { "Serving quantity is required" }

                    when (form.servingUnit.value) {
                        QuantityUnit.Gram -> 100.0 / servingQuantity
                        QuantityUnit.Milliliter -> 100.0 / servingQuantity
                        QuantityUnit.Ounce -> 100.0 / servingQuantity.ounces.grams
                        QuantityUnit.FluidOunce -> 100.0 / servingQuantity.fluidOunces.milliliters
                    }
                }

                ValuesPer.Package -> {

                    requireNotNull(packageQuantity) { "Package quantity is required" }

                    when (form.packageUnit.value) {
                        QuantityUnit.Gram -> 100.0 / packageQuantity
                        QuantityUnit.Milliliter -> 100.0 / packageQuantity
                        QuantityUnit.Ounce -> 100.0 / packageQuantity.ounces.grams
                        QuantityUnit.FluidOunce -> 100.0 / packageQuantity.fluidOunces.milliliters
                    }
                }
            }

        val nutritionFacts =
            form.toNutritionFacts(multiplier = multiplier, energyUnit = energyFormat)

        val boxedServingQuantity =
            servingQuantity?.let {
                when (form.servingUnit.value) {
                    QuantityUnit.Gram -> AbsoluteQuantity.Weight(it.grams)
                    QuantityUnit.Milliliter -> AbsoluteQuantity.Volume(it.milliliters)
                    QuantityUnit.Ounce -> AbsoluteQuantity.Weight(it.ounces)
                    QuantityUnit.FluidOunce -> AbsoluteQuantity.Volume(it.fluidOunces)
                }
            }

        val boxedPackageQuantity =
            packageQuantity?.let {
                when (form.packageUnit.value) {
                    QuantityUnit.Gram -> AbsoluteQuantity.Weight(it.grams)
                    QuantityUnit.Milliliter -> AbsoluteQuantity.Volume(it.milliliters)
                    QuantityUnit.Ounce -> AbsoluteQuantity.Weight(it.ounces)
                    QuantityUnit.FluidOunce -> AbsoluteQuantity.Volume(it.fluidOunces)
                }
            }

        val possibleIsLiquid =
            when (form.valuesPer.value) {
                ValuesPer.Grams100 -> false
                ValuesPer.Milliliters100 -> true
                ValuesPer.Serving,
                ValuesPer.Package -> null
            }

        val isLiquid =
            when (form.servingUnit.value) {
                QuantityUnit.Gram if (servingQuantity != null) -> false
                QuantityUnit.Ounce if (servingQuantity != null) -> false
                QuantityUnit.Milliliter if (servingQuantity != null) -> true
                QuantityUnit.FluidOunce if (servingQuantity != null) -> true
                else -> null
            }
                ?: when (form.packageUnit.value) {
                    QuantityUnit.Gram if (packageQuantity != null) -> false
                    QuantityUnit.Ounce if (packageQuantity != null) -> false
                    QuantityUnit.Milliliter if (packageQuantity != null) -> true
                    QuantityUnit.FluidOunce if (packageQuantity != null) -> true
                    else -> null
                }
                ?: possibleIsLiquid
                ?: false

        return Result(
            name = name,
            brand = brand,
            barcode = barcode,
            note = note,
            nutritionFacts = nutritionFacts,
            servingQuantity = boxedServingQuantity,
            packageQuantity = boxedPackageQuantity,
            image = form.imageUri.value?.let(::ImageUri),
            isLiquid = isLiquid,
        )
    }

    private fun FormField.toDouble(): Double? =
        textFieldState.text.takeIf { it.isNotBlank() }?.toString()?.toDouble()

    private fun ProductFormState.toNutritionFacts(
        multiplier: Double,
        energyUnit: EnergyUnit,
    ): NutritionFacts {
        val energy = energy.toDouble()
        val proteins = proteins.toDouble()
        val carbohydrates = carbohydrates.toDouble()
        val fats = fats.toDouble()
        val saturatedFats = saturatedFats.toDouble()
        val transFats = transFats.toDouble()
        val monounsaturatedFats = monounsaturatedFats.toDouble()
        val polyunsaturatedFats = polyunsaturatedFats.toDouble()
        val omega3 = omega3.toDouble()
        val omega6 = omega6.toDouble()
        val sugars = sugars.toDouble()
        val addedSugars = addedSugars.toDouble()
        val dietaryFiber = dietaryFiber.toDouble()
        val solubleFiber = solubleFiber.toDouble()
        val insolubleFiber = insolubleFiber.toDouble()
        val salt = salt.toDouble()
        val cholesterolMilli = cholesterolMilli.toDouble()
        val caffeineMilli = caffeineMilli.toDouble()
        val vitaminAMicro = vitaminAMicro.toDouble()
        val vitaminB1Milli = vitaminB1Milli.toDouble()
        val vitaminB2Milli = vitaminB2Milli.toDouble()
        val vitaminB3Milli = vitaminB3Milli.toDouble()
        val vitaminB5Milli = vitaminB5Milli.toDouble()
        val vitaminB6Milli = vitaminB6Milli.toDouble()
        val vitaminB7Micro = vitaminB7Micro.toDouble()
        val vitaminB9Micro = vitaminB9Micro.toDouble()
        val vitaminB12Micro = vitaminB12Micro.toDouble()
        val vitaminCMilli = vitaminCMilli.toDouble()
        val vitaminDMicro = vitaminDMicro.toDouble()
        val vitaminEMilli = vitaminEMilli.toDouble()
        val vitaminKMicro = vitaminKMicro.toDouble()
        val manganeseMilli = manganeseMilli.toDouble()
        val magnesiumMilli = magnesiumMilli.toDouble()
        val potassiumMilli = potassiumMilli.toDouble()
        val calciumMilli = calciumMilli.toDouble()
        val copperMilli = copperMilli.toDouble()
        val zincMilli = zincMilli.toDouble()
        val sodiumMilli = sodiumMilli.toDouble()
        val ironMilli = ironMilli.toDouble()
        val phosphorusMilli = phosphorusMilli.toDouble()
        val seleniumMicro = seleniumMicro.toDouble()
        val iodineMicro = iodineMicro.toDouble()
        val chromiumMicro = chromiumMicro.toDouble()

        // Energy MUST be in kilocalories internally
        val kcal =
            when (energyUnit) {
                EnergyUnit.Kilocalories -> energy?.kilocalories
                EnergyUnit.Kilojoules -> energy?.kilojoules
            }

        return NutritionFacts.requireAll(
            proteins = proteins?.times(multiplier)?.grams.toNutrientValue(),
            carbohydrates = carbohydrates?.times(multiplier)?.grams.toNutrientValue(),
            fats = fats?.times(multiplier)?.grams.toNutrientValue(),
            energy = kcal?.times(multiplier).toNutrientValue(),
            saturatedFats = saturatedFats?.times(multiplier)?.grams.toNutrientValue(),
            transFats = transFats?.times(multiplier)?.grams.toNutrientValue(),
            monounsaturatedFats = monounsaturatedFats?.times(multiplier)?.grams.toNutrientValue(),
            polyunsaturatedFats = polyunsaturatedFats?.times(multiplier)?.grams.toNutrientValue(),
            omega3 = omega3?.times(multiplier)?.grams.toNutrientValue(),
            omega6 = omega6?.times(multiplier)?.grams.toNutrientValue(),
            sugars = sugars?.times(multiplier)?.grams.toNutrientValue(),
            addedSugars = addedSugars?.times(multiplier)?.grams.toNutrientValue(),
            dietaryFiber = dietaryFiber?.times(multiplier)?.grams.toNutrientValue(),
            solubleFiber = solubleFiber?.times(multiplier)?.grams.toNutrientValue(),
            insolubleFiber = insolubleFiber?.times(multiplier)?.grams.toNutrientValue(),
            salt = salt?.times(multiplier)?.grams.toNutrientValue(),
            cholesterol = cholesterolMilli?.times(multiplier)?.milligrams.toNutrientValue(),
            caffeine = caffeineMilli?.times(multiplier)?.milligrams.toNutrientValue(),
            vitaminA = vitaminAMicro?.times(multiplier)?.micrograms.toNutrientValue(),
            vitaminB1 = vitaminB1Milli?.times(multiplier)?.milligrams.toNutrientValue(),
            vitaminB2 = vitaminB2Milli?.times(multiplier)?.milligrams.toNutrientValue(),
            vitaminB3 = vitaminB3Milli?.times(multiplier)?.milligrams.toNutrientValue(),
            vitaminB5 = vitaminB5Milli?.times(multiplier)?.milligrams.toNutrientValue(),
            vitaminB6 = vitaminB6Milli?.times(multiplier)?.milligrams.toNutrientValue(),
            vitaminB7 = vitaminB7Micro?.times(multiplier)?.micrograms.toNutrientValue(),
            vitaminB9 = vitaminB9Micro?.times(multiplier)?.micrograms.toNutrientValue(),
            vitaminB12 = vitaminB12Micro?.times(multiplier)?.micrograms.toNutrientValue(),
            vitaminC = vitaminCMilli?.times(multiplier)?.milligrams.toNutrientValue(),
            vitaminD = vitaminDMicro?.times(multiplier)?.micrograms.toNutrientValue(),
            vitaminE = vitaminEMilli?.times(multiplier)?.milligrams.toNutrientValue(),
            vitaminK = vitaminKMicro?.times(multiplier)?.micrograms.toNutrientValue(),
            manganese = manganeseMilli?.times(multiplier)?.milligrams.toNutrientValue(),
            magnesium = magnesiumMilli?.times(multiplier)?.milligrams.toNutrientValue(),
            potassium = potassiumMilli?.times(multiplier)?.milligrams.toNutrientValue(),
            calcium = calciumMilli?.times(multiplier)?.milligrams.toNutrientValue(),
            copper = copperMilli?.times(multiplier)?.milligrams.toNutrientValue(),
            zinc = zincMilli?.times(multiplier)?.milligrams.toNutrientValue(),
            sodium = sodiumMilli?.times(multiplier)?.milligrams.toNutrientValue(),
            iron = ironMilli?.times(multiplier)?.milligrams.toNutrientValue(),
            phosphorus = phosphorusMilli?.times(multiplier)?.milligrams.toNutrientValue(),
            selenium = seleniumMicro?.times(multiplier)?.micrograms.toNutrientValue(),
            iodine = iodineMicro?.times(multiplier)?.micrograms.toNutrientValue(),
            chromium = chromiumMicro?.times(multiplier)?.micrograms.toNutrientValue(),
        )
    }
}
