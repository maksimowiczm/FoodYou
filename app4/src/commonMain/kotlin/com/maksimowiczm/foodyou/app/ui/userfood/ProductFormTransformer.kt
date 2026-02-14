package com.maksimowiczm.foodyou.app.ui.userfood

import com.maksimowiczm.foodyou.common.domain.Language
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FluidOunces
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodNameSelector
import com.maksimowiczm.foodyou.common.domain.food.Grams
import com.maksimowiczm.foodyou.common.domain.food.Milliliters
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Ounces
import com.maksimowiczm.foodyou.userfood.domain.UserFoodNote
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductBarcode
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductBrand
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductSource

class ProductFormTransformer(
    private val getAppAccountEnergyFormatUseCase: GetAppAccountEnergyFormatUseCase,
    private val foodNameSelector: FoodNameSelector,
) {
    data class Result(
        val foodName: FoodName,
        val brand: UserProductBrand?,
        val barcode: UserProductBarcode?,
        val note: UserFoodNote?,
        val source: UserProductSource?,
        val nutritionFacts: NutritionFacts,
        val servingQuantity: AbsoluteQuantity?,
        val packageQuantity: AbsoluteQuantity?,
        val isLiquid: Boolean,
    )

    suspend fun validate(form: ProductFormState): Result {
        require(form.isValid) { "Form is not valid" }

        val energyFormat = getAppAccountEnergyFormatUseCase.getAppAccountEnergyFormat()
        val language = foodNameSelector.select()

        val name = form.name.value
        requireNotNull(name) { "Name is required" }
        val foodName =
            FoodName.requireAll(
                english = if (language == Language.English) name else null,
                catalan = if (language == Language.Catalan) name else null,
                czech = if (language == Language.Czech) name else null,
                danish = if (language == Language.Danish) name else null,
                german = if (language == Language.German) name else null,
                spanish = if (language == Language.Spanish) name else null,
                french = if (language == Language.French) name else null,
                indonesian = if (language == Language.Indonesian) name else null,
                italian = if (language == Language.Italian) name else null,
                hungarian = if (language == Language.Hungarian) name else null,
                dutch = if (language == Language.Dutch) name else null,
                polish = if (language == Language.Polish) name else null,
                portugueseBrazil = if (language == Language.PortugueseBrazil) name else null,
                slovenian = if (language == Language.Slovenian) name else null,
                turkish = if (language == Language.Turkish) name else null,
                russian = if (language == Language.Russian) name else null,
                ukrainian = if (language == Language.Ukrainian) name else null,
                arabic = if (language == Language.Arabic) name else null,
                chineseSimplified = if (language == Language.ChineseSimplified) name else null,
                fallback = name,
            )

        val brand = form.brand.value?.let { UserProductBrand(it) }

        val barcode = form.barcode.value?.let { UserProductBarcode(it) }

        val note = form.note.value?.let { UserFoodNote(it) }

        val source = form.source.value?.let { UserProductSource(it) }

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
                        QuantityUnit.FluidOunce -> 100.0 / FluidOunces(servingQuantity).milliliters
                    }
                }

                ValuesPer.Package -> {
                    val packageQuantity = form.packageQuantity.value
                    requireNotNull(packageQuantity) { "Package quantity is required" }

                    when (form.packageUnit) {
                        QuantityUnit.Gram -> 100.0 / packageQuantity
                        QuantityUnit.Milliliter -> 100.0 / packageQuantity
                        QuantityUnit.Ounce -> 100.0 / Ounces(packageQuantity).grams
                        QuantityUnit.FluidOunce -> 100.0 / FluidOunces(packageQuantity).milliliters
                    }
                }
            }

        val nutritionFacts =
            form.toNutritionFacts(multiplier = multiplier, energyFormat = energyFormat)

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

        val possibleIsLiquid =
            when (form.valuesPer) {
                ValuesPer.Grams100 -> false
                ValuesPer.Milliliters100 -> true
                ValuesPer.Serving,
                ValuesPer.Package -> null
            }

        val isLiquid =
            when (form.servingUnit) {
                QuantityUnit.Gram if (form.servingQuantity.value != null) -> false
                QuantityUnit.Ounce if (form.servingQuantity.value != null) -> false
                QuantityUnit.Milliliter if (form.servingQuantity.value != null) -> true
                QuantityUnit.FluidOunce if (form.servingQuantity.value != null) -> true
                else -> null
            }
                ?: when (form.packageUnit) {
                    QuantityUnit.Gram if (form.packageQuantity.value != null) -> false
                    QuantityUnit.Ounce if (form.packageQuantity.value != null) -> false
                    QuantityUnit.Milliliter if (form.packageQuantity.value != null) -> true
                    QuantityUnit.FluidOunce if (form.packageQuantity.value != null) -> true
                    else -> null
                }
                ?: possibleIsLiquid
                ?: false

        return Result(
            foodName = foodName,
            brand = brand,
            barcode = barcode,
            note = note,
            source = source,
            nutritionFacts = nutritionFacts,
            servingQuantity = servingQuantity,
            packageQuantity = packageQuantity,
            isLiquid = isLiquid,
        )
    }
}
