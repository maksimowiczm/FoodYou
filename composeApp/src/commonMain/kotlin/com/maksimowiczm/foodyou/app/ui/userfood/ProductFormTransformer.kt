package com.maksimowiczm.foodyou.app.ui.userfood

import com.maksimowiczm.foodyou.account.domain.EnergyFormat
import com.maksimowiczm.foodyou.app.ui.common.form.FormField
import com.maksimowiczm.foodyou.common.domain.Image
import com.maksimowiczm.foodyou.common.domain.Language
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FluidOunces
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodNameSelector
import com.maksimowiczm.foodyou.common.domain.food.Grams
import com.maksimowiczm.foodyou.common.domain.food.Milliliters
import com.maksimowiczm.foodyou.common.domain.food.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Ounces
import com.maksimowiczm.foodyou.userfood.domain.UserFoodNote
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductBarcode
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductBrand

internal class ProductFormTransformer(
    private val getAppAccountEnergyFormatUseCase: GetAppAccountEnergyFormatUseCase,
    private val foodNameSelector: FoodNameSelector,
) {
    data class Result(
        val name: FoodName,
        val brand: UserProductBrand?,
        val barcode: UserProductBarcode?,
        val note: UserFoodNote?,
        val image: Image.Local?,
        val nutritionFacts: NutritionFacts,
        val servingQuantity: AbsoluteQuantity?,
        val packageQuantity: AbsoluteQuantity?,
        val isLiquid: Boolean,
    )

    suspend fun transform(form: ProductFormState): Result {
        require(form.isValid) { "Form is not valid" }

        val energyFormat = getAppAccountEnergyFormatUseCase.getAppAccountEnergyFormat()
        val language = foodNameSelector.select()

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
                        QuantityUnit.Ounce -> 100.0 / Ounces(servingQuantity).grams
                        QuantityUnit.FluidOunce -> 100.0 / FluidOunces(servingQuantity).milliliters
                    }
                }

                ValuesPer.Package -> {

                    requireNotNull(packageQuantity) { "Package quantity is required" }

                    when (form.packageUnit.value) {
                        QuantityUnit.Gram -> 100.0 / packageQuantity
                        QuantityUnit.Milliliter -> 100.0 / packageQuantity
                        QuantityUnit.Ounce -> 100.0 / Ounces(packageQuantity).grams
                        QuantityUnit.FluidOunce -> 100.0 / FluidOunces(packageQuantity).milliliters
                    }
                }
            }

        val nutritionFacts =
            form.toNutritionFacts(multiplier = multiplier, energyFormat = energyFormat)

        val boxedServingQuantity =
            servingQuantity?.let {
                when (form.servingUnit.value) {
                    QuantityUnit.Gram -> AbsoluteQuantity.Weight(Grams(it))
                    QuantityUnit.Milliliter -> AbsoluteQuantity.Volume(Milliliters(it))
                    QuantityUnit.Ounce -> AbsoluteQuantity.Weight(Ounces(it))
                    QuantityUnit.FluidOunce -> AbsoluteQuantity.Volume(FluidOunces(it))
                }
            }

        val boxedPackageQuantity =
            packageQuantity?.let {
                when (form.packageUnit.value) {
                    QuantityUnit.Gram -> AbsoluteQuantity.Weight(Grams(it))
                    QuantityUnit.Milliliter -> AbsoluteQuantity.Volume(Milliliters(it))
                    QuantityUnit.Ounce -> AbsoluteQuantity.Weight(Ounces(it))
                    QuantityUnit.FluidOunce -> AbsoluteQuantity.Volume(FluidOunces(it))
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
            image = form.imageUri.value?.let(Image::Local),
            isLiquid = isLiquid,
        )
    }

    private fun FormField.toDouble(): Double? =
        textFieldState.text.takeIf { it.isNotBlank() }?.toString()?.toDouble()

    private fun ProductFormState.toNutritionFacts(
        multiplier: Double,
        energyFormat: EnergyFormat,
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
            when (energyFormat) {
                EnergyFormat.Kilocalories -> energy
                EnergyFormat.Kilojoules -> energy?.let { it / 4.184 }
            }

        return NutritionFacts.requireAll(
            proteins = proteins?.times(multiplier).toNutrientValue(),
            carbohydrates = carbohydrates?.times(multiplier).toNutrientValue(),
            fats = fats?.times(multiplier).toNutrientValue(),
            energy = kcal?.times(multiplier).toNutrientValue(),
            saturatedFats = saturatedFats?.times(multiplier).toNutrientValue(),
            transFats = transFats?.times(multiplier).toNutrientValue(),
            monounsaturatedFats = monounsaturatedFats?.times(multiplier).toNutrientValue(),
            polyunsaturatedFats = polyunsaturatedFats?.times(multiplier).toNutrientValue(),
            omega3 = omega3?.times(multiplier).toNutrientValue(),
            omega6 = omega6?.times(multiplier).toNutrientValue(),
            sugars = sugars?.times(multiplier).toNutrientValue(),
            addedSugars = addedSugars?.times(multiplier).toNutrientValue(),
            dietaryFiber = dietaryFiber?.times(multiplier).toNutrientValue(),
            solubleFiber = solubleFiber?.times(multiplier).toNutrientValue(),
            insolubleFiber = insolubleFiber?.times(multiplier).toNutrientValue(),
            salt = salt?.times(multiplier).toNutrientValue(),
            cholesterol = (cholesterolMilli?.times(multiplier)?.div(1_000)).toNutrientValue(),
            caffeine = (caffeineMilli?.times(multiplier)?.div(1_000)).toNutrientValue(),
            vitaminA = (vitaminAMicro?.times(multiplier)?.div(1_000_000)).toNutrientValue(),
            vitaminB1 = (vitaminB1Milli?.times(multiplier)?.div(1_000)).toNutrientValue(),
            vitaminB2 = (vitaminB2Milli?.times(multiplier)?.div(1_000)).toNutrientValue(),
            vitaminB3 = (vitaminB3Milli?.times(multiplier)?.div(1_000)).toNutrientValue(),
            vitaminB5 = (vitaminB5Milli?.times(multiplier)?.div(1_000)).toNutrientValue(),
            vitaminB6 = (vitaminB6Milli?.times(multiplier)?.div(1_000)).toNutrientValue(),
            vitaminB7 = (vitaminB7Micro?.times(multiplier)?.div(1_000_000)).toNutrientValue(),
            vitaminB9 = (vitaminB9Micro?.times(multiplier)?.div(1_000_000)).toNutrientValue(),
            vitaminB12 = (vitaminB12Micro?.times(multiplier)?.div(1_000_000)).toNutrientValue(),
            vitaminC = (vitaminCMilli?.times(multiplier)?.div(1_000)).toNutrientValue(),
            vitaminD = (vitaminDMicro?.times(multiplier)?.div(1_000_000)).toNutrientValue(),
            vitaminE = (vitaminEMilli?.times(multiplier)?.div(1_000)).toNutrientValue(),
            vitaminK = (vitaminKMicro?.times(multiplier)?.div(1_000_000)).toNutrientValue(),
            manganese = (manganeseMilli?.times(multiplier)?.div(1_000)).toNutrientValue(),
            magnesium = (magnesiumMilli?.times(multiplier)?.div(1_000)).toNutrientValue(),
            potassium = (potassiumMilli?.times(multiplier)?.div(1_000)).toNutrientValue(),
            calcium = (calciumMilli?.times(multiplier)?.div(1_000)).toNutrientValue(),
            copper = (copperMilli?.times(multiplier)?.div(1_000)).toNutrientValue(),
            zinc = (zincMilli?.times(multiplier)?.div(1_000)).toNutrientValue(),
            sodium = (sodiumMilli?.times(multiplier)?.div(1_000)).toNutrientValue(),
            iron = (ironMilli?.times(multiplier)?.div(1_000)).toNutrientValue(),
            phosphorus = (phosphorusMilli?.times(multiplier)?.div(1_000)).toNutrientValue(),
            selenium = (seleniumMicro?.times(multiplier)?.div(1_000_000)).toNutrientValue(),
            iodine = (iodineMicro?.times(multiplier)?.div(1_000_000)).toNutrientValue(),
            chromium = (chromiumMicro?.times(multiplier)?.div(1_000_000)).toNutrientValue(),
        )
    }
}
