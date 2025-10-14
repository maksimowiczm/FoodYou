package com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts

import com.maksimowiczm.foodyou.food.domain.FoodName
import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity
import com.maksimowiczm.foodyou.food.domain.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.food.domain.NutritionFacts
import com.maksimowiczm.foodyou.food.domain.Weight
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
            )
        }

    fun map(entity: OpenFoodFactsProductEntity): SearchableFoodDto =
        with(entity) {
            val nameMap = Json.decodeFromString<Map<String, String>>(names).toMutableMap()

            val name =
                try {
                    FoodName(
                        english = nameMap.remove("en").takeIfNotBlank(),
                        catalan = nameMap.remove("ca").takeIfNotBlank(),
                        danish = nameMap.remove("da").takeIfNotBlank(),
                        german = nameMap.remove("de").takeIfNotBlank(),
                        spanish = nameMap.remove("es").takeIfNotBlank(),
                        french = nameMap.remove("fr").takeIfNotBlank(),
                        italian = nameMap.remove("it").takeIfNotBlank(),
                        hungarian = nameMap.remove("hu").takeIfNotBlank(),
                        dutch = nameMap.remove("nl").takeIfNotBlank(),
                        polish = nameMap.remove("pl").takeIfNotBlank(),
                        portugueseBrazil =
                            nameMap.remove("pt_br").takeIfNotBlank()
                                ?: nameMap.remove("pt").takeIfNotBlank(),
                        turkish = nameMap.remove("tr").takeIfNotBlank(),
                        russian = nameMap.remove("ru").takeIfNotBlank(),
                        ukrainian = nameMap.remove("uk").takeIfNotBlank(),
                        arabic = nameMap.remove("ar").takeIfNotBlank(),
                        chineseSimplified = nameMap.remove("zh").takeIfNotBlank(),
                    )
                } catch (_: IllegalArgumentException) {
                    val other = nameMap.values.firstOrNull().takeIfNotBlank()

                    if (other != null) FoodName(other = other)
                    // Fallback to barcode as name if all names are blank
                    else FoodName(other = barcode)
                }

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

            val servingWeight = run {
                val weight = this.servingWeight?.takeIf { it > 0 } ?: return@run null
                val unit = this.servingQuantityUnit?.lowercase() ?: return@run null
                when (unit) {
                    "g",
                    "ml" -> Weight(weight.toDouble())

                    else -> null
                }
            }
            val packageWeight = run {
                val weight = this.packageWeight?.takeIf { it > 0 } ?: return@run null
                val unit = this.packageQuantityUnit?.lowercase() ?: return@run null
                when (unit) {
                    "g",
                    "ml" -> Weight(weight.toDouble())

                    else -> null
                }
            }

            return SearchableFoodDto(
                identity = FoodProductIdentity.OpenFoodFacts(barcode),
                name = name,
                nutritionFacts = nutrients,
                servingWeight = servingWeight,
                totalWeight = packageWeight,
            )
        }
}

private fun String?.takeIfNotBlank(): String? = this?.takeIf { it.isNotBlank() }
