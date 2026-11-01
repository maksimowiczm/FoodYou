package com.maksimowiczm.foodyou.openfoodfacts.infrastructure

import com.maksimowiczm.foodyou.common.domain.FileUri
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutrientValue.Companion.toNormalizedNutrientValue
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.kilocalories
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductId
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.Nutriments
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.OpenFoodFactsProductNetwork
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room.OpenFoodFactsProductEntity
import kotlinx.serialization.json.Json

internal class OpenFoodFactsProductMapper {
    fun toEntity(model: OpenFoodFactsProductNetwork) =
        OpenFoodFactsProductEntity(barcode = model.code, rawJson = Json.encodeToString(model))

    fun toModel(entity: OpenFoodFactsProductEntity): OpenFoodFactsProduct {
        val network = Json.decodeFromString<OpenFoodFactsProductNetwork>(entity.rawJson)
        return toModel(network)
    }

    fun toModel(network: OpenFoodFactsProductNetwork): OpenFoodFactsProduct = network.toModel()
}

private fun OpenFoodFactsProductNetwork.toModel(): OpenFoodFactsProduct {
    fun String?.sanitized(): String? = this?.sanitizeHtml()?.takeIf { it.isNotBlank() }

    val name =
        FoodName.requireAll(
            english = localizedNames["en"].sanitized() ?: localizedGenericNames["en"].sanitized(),
            catalan = localizedNames["ca"].sanitized() ?: localizedGenericNames["ca"].sanitized(),
            czech = localizedNames["cs"].sanitized() ?: localizedGenericNames["cs"].sanitized(),
            danish = localizedNames["da"].sanitized() ?: localizedGenericNames["da"].sanitized(),
            german = localizedNames["de"].sanitized() ?: localizedGenericNames["de"].sanitized(),
            spanish = localizedNames["es"].sanitized() ?: localizedGenericNames["es"].sanitized(),
            french = localizedNames["fr"].sanitized() ?: localizedGenericNames["fr"].sanitized(),
            indonesian =
                localizedNames["id"].sanitized()
                    ?: localizedNames["in"].sanitized()
                    ?: localizedGenericNames["id"].sanitized()
                    ?: localizedGenericNames["in"].sanitized(),
            italian = localizedNames["it"].sanitized() ?: localizedGenericNames["it"].sanitized(),
            hungarian = localizedNames["hu"].sanitized() ?: localizedGenericNames["hu"].sanitized(),
            dutch = localizedNames["nl"].sanitized() ?: localizedGenericNames["nl"].sanitized(),
            polish = localizedNames["pl"].sanitized() ?: localizedGenericNames["pl"].sanitized(),
            portugueseBrazil =
                localizedNames["pt_br"].sanitized()
                    ?: localizedGenericNames["pt_br"].sanitized()
                    ?: localizedNames["pt"].sanitized()
                    ?: localizedGenericNames["pt"].sanitized(),
            portuguesePortugal =
                localizedNames["pt_pt"].sanitized()
                    ?: localizedGenericNames["pt_pt"].sanitized()
                    ?: localizedNames["pt"].sanitized()
                    ?: localizedGenericNames["pt"].sanitized(),
            slovenian = localizedNames["sl"].sanitized() ?: localizedGenericNames["sl"].sanitized(),
            turkish = localizedNames["tr"].sanitized() ?: localizedGenericNames["tr"].sanitized(),
            russian = localizedNames["ru"].sanitized() ?: localizedGenericNames["ru"].sanitized(),
            ukrainian = localizedNames["uk"].sanitized() ?: localizedGenericNames["uk"].sanitized(),
            arabic = localizedNames["ar"].sanitized() ?: localizedGenericNames["ar"].sanitized(),
            chineseSimplified =
                localizedNames["zh"].sanitized() ?: localizedGenericNames["zh"].sanitized(),
            fallback =
                localizedNames[""].sanitized()
                    ?: localizedGenericNames[""].sanitized()
                    ?: localizedNames.values.firstOrNull { it.isNotBlank() }?.sanitizeHtml()
                    ?: code,
        )

    val thumbnail = imageThumbUrl ?: imageFrontThumbUrl
    val fullSize = imageUrl ?: imageFrontUrl

    val servingQuantity = findQuantity(servingSize)
    val packageQuantity = findQuantity(quantity)

    return OpenFoodFactsProduct(
        id = OpenFoodFactsProductId(code),
        name = name,
        brand = brands?.takeIf { it.isNotEmpty() }?.filterNot { it.isBlank() }?.joinToString(),
        nutritionFacts = nutriments?.toNutritionFacts() ?: NutritionFacts(),
        servingQuantity = servingQuantity,
        packageQuantity = packageQuantity,
        thumbnail = thumbnail?.let(::FileUri),
        image = fullSize?.let(::FileUri),
        source = "https://world.openfoodfacts.org/product/$code",
    )
}

private fun Nutriments.toNutritionFacts() =
    NutritionFacts.requireAll(
        proteins = proteins100g.toNormalizedNutrientValue { it.grams },
        carbohydrates = carbohydrates100g.toNormalizedNutrientValue { it.grams },
        energy = energyKcal100g.toNormalizedNutrientValue { it.kilocalories },
        fats = fat100g.toNormalizedNutrientValue { it.grams },
        saturatedFats = saturatedFat100g.toNormalizedNutrientValue { it.grams },
        transFats = transFat100g.toNormalizedNutrientValue { it.grams },
        monounsaturatedFats = monounsaturatedFat100g.toNormalizedNutrientValue { it.grams },
        polyunsaturatedFats = polyunsaturatedFat100g.toNormalizedNutrientValue { it.grams },
        omega3 = omega3Fat100g.toNormalizedNutrientValue { it.grams },
        omega6 = omega6Fat100g.toNormalizedNutrientValue { it.grams },
        sugars = sugars100g.toNormalizedNutrientValue { it.grams },
        addedSugars = addedSugars100g.toNormalizedNutrientValue { it.grams },
        dietaryFiber = fiber100g.toNormalizedNutrientValue { it.grams },
        solubleFiber = solubleFiber100g.toNormalizedNutrientValue { it.grams },
        insolubleFiber = insolubleFiber100g.toNormalizedNutrientValue { it.grams },
        salt = salt100g.toNormalizedNutrientValue { it.grams },
        cholesterol = cholesterol100g.toNormalizedNutrientValue { it.grams },
        caffeine = caffeine100g.toNormalizedNutrientValue { it.grams },
        vitaminA = vitaminA100g.toNormalizedNutrientValue { it.grams },
        vitaminB1 = vitaminB1100g.toNormalizedNutrientValue { it.grams },
        vitaminB2 = vitaminB2100g.toNormalizedNutrientValue { it.grams },
        vitaminB3 = vitaminB3100g.toNormalizedNutrientValue { it.grams },
        vitaminB5 = vitaminB5100g.toNormalizedNutrientValue { it.grams },
        vitaminB6 = vitaminB6100g.toNormalizedNutrientValue { it.grams },
        vitaminB7 = vitaminB7100g.toNormalizedNutrientValue { it.grams },
        vitaminB9 = vitaminB9100g.toNormalizedNutrientValue { it.grams },
        vitaminB12 = vitaminB12100g.toNormalizedNutrientValue { it.grams },
        vitaminC = vitaminC100g.toNormalizedNutrientValue { it.grams },
        vitaminD = vitaminD100g.toNormalizedNutrientValue { it.grams },
        vitaminE = vitaminE100g.toNormalizedNutrientValue { it.grams },
        vitaminK = vitaminK100g.toNormalizedNutrientValue { it.grams },
        manganese = manganese100g.toNormalizedNutrientValue { it.grams },
        magnesium = magnesium100g.toNormalizedNutrientValue { it.grams },
        potassium = potassium100g.toNormalizedNutrientValue { it.grams },
        calcium = calcium100g.toNormalizedNutrientValue { it.grams },
        copper = copper100g.toNormalizedNutrientValue { it.grams },
        zinc = zinc100g.toNormalizedNutrientValue { it.grams },
        sodium = sodium100g.toNormalizedNutrientValue { it.grams },
        iron = iron100g.toNormalizedNutrientValue { it.grams },
        phosphorus = phosphorus100g.toNormalizedNutrientValue { it.grams },
        selenium = selenium100g.toNormalizedNutrientValue { it.grams },
        iodine = iodine100g.toNormalizedNutrientValue { it.grams },
        chromium = chromium100g.toNormalizedNutrientValue { it.grams },
    )

private fun findQuantity(value: String?): AbsoluteQuantity? {
    if (value == null) return null
    return AbsoluteQuantity.parseOrNull(value)
}

private fun String.sanitizeHtml() =
    replace("&amp;", "&")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
        .trim()
