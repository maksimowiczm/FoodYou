package com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts

import com.maksimowiczm.foodyou.common.domain.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.FluidOunces
import com.maksimowiczm.foodyou.common.domain.Grams
import com.maksimowiczm.foodyou.common.domain.Milliliters
import com.maksimowiczm.foodyou.common.domain.Ounces
import com.maksimowiczm.foodyou.common.domain.PackageQuantity
import com.maksimowiczm.foodyou.common.domain.Quantity
import com.maksimowiczm.foodyou.common.domain.ServingQuantity
import com.maksimowiczm.foodyou.food.domain.FoodImage
import com.maksimowiczm.foodyou.food.domain.FoodName
import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity
import com.maksimowiczm.foodyou.food.domain.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.food.domain.NutritionFacts
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.network.model.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.room.OpenFoodFactsProductEntity
import com.maksimowiczm.foodyou.food.search.domain.SearchableFoodDto
import kotlinx.serialization.json.Json

class OpenFoodFactsProductMapper {
    fun map(product: OpenFoodFactsProduct): OpenFoodFactsProductEntity =
        with(product) {
            return OpenFoodFactsProductEntity(
                barcode = barcode,
                names = Json.encodeToString(localizedNames),
                url = url,
                nutrients = nutritionFacts,
                brand = brand.takeIfNotBlank(),
                packageWeight = packageWeight,
                packageQuantityUnit = packageQuantityUnit,
                servingWeight = servingWeight,
                servingQuantityUnit = servingQuantityUnit,
                thumbnailUrl = thumbnailUrl,
                imageUrl = imageUrl,
            )
        }

    fun map(entity: OpenFoodFactsProductEntity): SearchableFoodDto =
        with(entity) {
            val nameMap = Json.decodeFromString<Map<String, String>>(names)

            val name =
                FoodName(
                    english = nameMap["en"].takeIfNotBlank(),
                    catalan = nameMap["ca"].takeIfNotBlank(),
                    danish = nameMap["da"].takeIfNotBlank(),
                    german = nameMap["de"].takeIfNotBlank(),
                    spanish = nameMap["es"].takeIfNotBlank(),
                    french = nameMap["fr"].takeIfNotBlank(),
                    italian = nameMap["it"].takeIfNotBlank(),
                    hungarian = nameMap["hu"].takeIfNotBlank(),
                    dutch = nameMap["nl"].takeIfNotBlank(),
                    polish = nameMap["pl"].takeIfNotBlank(),
                    portugueseBrazil =
                        nameMap["pt_br"].takeIfNotBlank() ?: nameMap["pt"].takeIfNotBlank(),
                    turkish = nameMap["tr"].takeIfNotBlank(),
                    russian = nameMap["ru"].takeIfNotBlank(),
                    ukrainian = nameMap["uk"].takeIfNotBlank(),
                    arabic = nameMap["ar"].takeIfNotBlank(),
                    chineseSimplified = nameMap["zh"].takeIfNotBlank(),
                    fallback = nameMap.values.firstOrNull { it.isNotBlank() } ?: barcode,
                )

            val nutrients =
                NutritionFacts.requireAll(
                    proteins = nutrients?.proteins.toNutrientValue(),
                    carbohydrates = nutrients?.carbohydrates.toNutrientValue(),
                    energy = nutrients?.energy.toNutrientValue(),
                    fats = nutrients?.fats.toNutrientValue(),
                    saturatedFats = nutrients?.saturatedFats.toNutrientValue(),
                    transFats = nutrients?.transFats.toNutrientValue(),
                    monounsaturatedFats = nutrients?.monounsaturatedFats.toNutrientValue(),
                    polyunsaturatedFats = nutrients?.polyunsaturatedFats.toNutrientValue(),
                    omega3 = nutrients?.omega3Fats.toNutrientValue(),
                    omega6 = nutrients?.omega6Fats.toNutrientValue(),
                    sugars = nutrients?.sugars.toNutrientValue(),
                    addedSugars = nutrients?.addedSugars.toNutrientValue(),
                    dietaryFiber = nutrients?.fiber.toNutrientValue(),
                    solubleFiber = nutrients?.solubleFiber.toNutrientValue(),
                    insolubleFiber = nutrients?.insolubleFiber.toNutrientValue(),
                    salt = nutrients?.salt.toNutrientValue(),
                    cholesterol = nutrients?.cholesterol.toNutrientValue(),
                    caffeine = nutrients?.caffeine.toNutrientValue(),
                    vitaminA = nutrients?.vitaminA.toNutrientValue(),
                    vitaminB1 = nutrients?.vitaminB1.toNutrientValue(),
                    vitaminB2 = nutrients?.vitaminB2.toNutrientValue(),
                    vitaminB3 = nutrients?.vitaminB3.toNutrientValue(),
                    vitaminB5 = nutrients?.vitaminB5.toNutrientValue(),
                    vitaminB6 = nutrients?.vitaminB6.toNutrientValue(),
                    vitaminB7 = nutrients?.vitaminB7.toNutrientValue(),
                    vitaminB9 = nutrients?.vitaminB9.toNutrientValue(),
                    vitaminB12 = nutrients?.vitaminB12.toNutrientValue(),
                    vitaminC = nutrients?.vitaminC.toNutrientValue(),
                    vitaminD = nutrients?.vitaminD.toNutrientValue(),
                    vitaminE = nutrients?.vitaminE.toNutrientValue(),
                    vitaminK = nutrients?.vitaminK.toNutrientValue(),
                    manganese = nutrients?.manganese.toNutrientValue(),
                    magnesium = nutrients?.magnesium.toNutrientValue(),
                    potassium = nutrients?.potassium.toNutrientValue(),
                    calcium = nutrients?.calcium.toNutrientValue(),
                    copper = nutrients?.copper.toNutrientValue(),
                    zinc = nutrients?.zinc.toNutrientValue(),
                    sodium = nutrients?.sodium.toNutrientValue(),
                    iron = nutrients?.iron.toNutrientValue(),
                    phosphorus = nutrients?.phosphorus.toNutrientValue(),
                    selenium = nutrients?.selenium.toNutrientValue(),
                    iodine = nutrients?.iodine.toNutrientValue(),
                    chromium = nutrients?.chromium.toNutrientValue(),
                )

            val servingQuantity = run {
                val weight = this.servingWeight?.takeIf { it > 0 } ?: return@run null
                val unit = this.servingQuantityUnit?.lowercase() ?: return@run null
                when (unit) {
                    "g" -> AbsoluteQuantity.Weight(Grams(weight.toDouble()))
                    "oz",
                    "oz." -> AbsoluteQuantity.Weight(Ounces(weight.toDouble()))

                    "ml" -> AbsoluteQuantity.Volume(Milliliters(weight.toDouble()))
                    "fl",
                    "fl.oz",
                    "fl. oz",
                    "fl.oz." -> AbsoluteQuantity.Volume(FluidOunces(weight.toDouble()))

                    else -> null
                }
            }
            val packageQuantity = run {
                val weight = this.packageWeight?.takeIf { it > 0 } ?: return@run null
                val unit = this.packageQuantityUnit?.lowercase() ?: return@run null
                when (unit) {
                    "g" -> AbsoluteQuantity.Weight(Grams(weight.toDouble()))
                    "oz",
                    "oz." -> AbsoluteQuantity.Weight(Ounces(weight.toDouble()))

                    "ml" -> AbsoluteQuantity.Volume(Milliliters(weight.toDouble()))
                    "fl",
                    "fl.oz",
                    "fl. oz",
                    "fl.oz." -> AbsoluteQuantity.Volume(FluidOunces(weight.toDouble()))

                    else -> null
                }
            }

            // Probably move it to domain service
            val suggestedQuantity: Quantity =
                when {
                    servingQuantity != null -> ServingQuantity(1.0)
                    packageQuantity != null -> PackageQuantity(1.0)
                    else -> AbsoluteQuantity.Weight(Grams(100.0))
                }

            val image =
                if (thumbnailUrl != null || imageUrl != null) {
                    FoodImage.Remote(thumbnail = thumbnailUrl, fullSize = imageUrl)
                } else {
                    null
                }

            return SearchableFoodDto(
                identity = FoodProductIdentity.OpenFoodFacts(barcode),
                name = name,
                nutritionFacts = nutrients,
                servingQuantity = servingQuantity,
                packageQuantity = packageQuantity,
                suggestedQuantity = suggestedQuantity,
                image = image,
            )
        }
}

private fun String?.takeIfNotBlank(): String? = this?.takeIf { it.isNotBlank() }
