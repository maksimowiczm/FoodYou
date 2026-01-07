package com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts

import com.maksimowiczm.foodyou.common.domain.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.FluidOunces
import com.maksimowiczm.foodyou.common.domain.Grams
import com.maksimowiczm.foodyou.common.domain.Milliliters
import com.maksimowiczm.foodyou.common.domain.Ounces
import com.maksimowiczm.foodyou.common.domain.PackageQuantity
import com.maksimowiczm.foodyou.common.domain.ServingQuantity
import com.maksimowiczm.foodyou.food.domain.Barcode
import com.maksimowiczm.foodyou.food.domain.FoodBrand
import com.maksimowiczm.foodyou.food.domain.FoodImage
import com.maksimowiczm.foodyou.food.domain.FoodName
import com.maksimowiczm.foodyou.food.domain.FoodProductDto
import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity
import com.maksimowiczm.foodyou.food.domain.FoodSource
import com.maksimowiczm.foodyou.food.domain.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.food.domain.NutritionFacts
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.network.model.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.room.OpenFoodFactsProductEntity
import com.maksimowiczm.foodyou.food.search.domain.SearchableFoodDto
import kotlinx.serialization.json.Json

class OpenFoodFactsProductMapper {
    fun openFoodFactsProductEntity(product: OpenFoodFactsProduct): OpenFoodFactsProductEntity =
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

    fun searchableFoodDto(entity: OpenFoodFactsProductEntity): SearchableFoodDto =
        buildFoodDto(entity) {
            name,
            brand,
            nutrients,
            servingQuantity,
            packageQuantity,
            image,
            isLiquid ->
            SearchableFoodDto(
                identity = FoodProductIdentity.OpenFoodFacts(entity.barcode),
                name = name,
                brand = brand,
                nutritionFacts = nutrients,
                servingQuantity = servingQuantity,
                packageQuantity = packageQuantity,
                suggestedQuantity =
                    servingQuantity?.let { ServingQuantity(1.0) }
                        ?: packageQuantity?.let { PackageQuantity(1.0) }
                        ?: if (isLiquid) AbsoluteQuantity.Volume(Milliliters(250.0))
                        else AbsoluteQuantity.Weight(Grams(100.0)),
                image = image,
                isLiquid = isLiquid,
            )
        }

    fun foodProductDto(entity: OpenFoodFactsProductEntity): FoodProductDto =
        buildFoodDto(entity) {
            name,
            brand,
            nutrients,
            servingQuantity,
            packageQuantity,
            image,
            isLiquid ->
            FoodProductDto(
                identity = FoodProductIdentity.OpenFoodFacts(entity.barcode),
                name = name,
                brand = brand,
                barcode = entity.barcode.takeIfNotBlank()?.let { Barcode(it) },
                note = null,
                image = image,
                source = entity.url.takeIfNotBlank()?.let { FoodSource.OpenFoodFacts(it) },
                nutritionFacts = nutrients,
                servingQuantity = servingQuantity,
                packageQuantity = packageQuantity,
                isLiquid = isLiquid,
            )
        }

    private inline fun <T> buildFoodDto(
        entity: OpenFoodFactsProductEntity,
        build:
            (
                FoodName,
                FoodBrand?,
                NutritionFacts,
                AbsoluteQuantity?,
                AbsoluteQuantity?,
                FoodImage.Remote?,
                isLiquid: Boolean,
            ) -> T,
    ): T =
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
                    indonesian = nameMap["id"].takeIfNotBlank() ?: nameMap["in"].takeIfNotBlank(),
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

            val servingQuantity = parseQuantity(servingWeight, servingQuantityUnit)
            val packageQuantity = parseQuantity(packageWeight, packageQuantityUnit)

            val image =
                if (thumbnailUrl != null || imageUrl != null) {
                    FoodImage.Remote(thumbnail = thumbnailUrl, fullSize = imageUrl)
                } else {
                    null
                }

            val brand = brand.takeIfNotBlank()?.let { FoodBrand(it) }

            val isLiquid =
                when {
                    servingQuantity is AbsoluteQuantity.Volume -> true
                    packageQuantity is AbsoluteQuantity.Volume -> true
                    else -> false
                }

            return build(name, brand, nutrients, servingQuantity, packageQuantity, image, isLiquid)
        }

    private fun parseQuantity(weight: Double?, unit: String?): AbsoluteQuantity? {
        val validWeight = weight?.takeIf { it > 0 } ?: return null
        val normalizedUnit = unit?.lowercase() ?: return null

        return when (normalizedUnit) {
            "g" -> AbsoluteQuantity.Weight(Grams(validWeight))
            "oz",
            "oz." -> AbsoluteQuantity.Weight(Ounces(validWeight))

            "ml" -> AbsoluteQuantity.Volume(Milliliters(validWeight))
            "fl",
            "fl.oz",
            "fl. oz",
            "fl.oz." -> AbsoluteQuantity.Volume(FluidOunces(validWeight))

            else -> null
        }
    }
}

private fun String?.takeIfNotBlank(): String? = this?.takeIf { it.isNotBlank() }
