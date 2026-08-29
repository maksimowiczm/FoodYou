package com.maksimowiczm.foodyou.openfoodfacts.infrastructure

import com.maksimowiczm.foodyou.common.domain.FileUri
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.kilocalories
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
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
        identity = OpenFoodFactsProductIdentity(code),
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
        proteins = proteins100g?.grams.toNutrientValue(),
        carbohydrates = carbohydrates100g?.grams.toNutrientValue(),
        energy = energyKcal100g?.kilocalories.toNutrientValue(),
        fats = fat100g?.grams.toNutrientValue(),
        saturatedFats = saturatedFat100g?.grams.toNutrientValue(),
        transFats = transFat100g?.grams.toNutrientValue(),
        monounsaturatedFats = monounsaturatedFat100g?.grams.toNutrientValue(),
        polyunsaturatedFats = polyunsaturatedFat100g?.grams.toNutrientValue(),
        omega3 = omega3Fat100g?.grams.toNutrientValue(),
        omega6 = omega6Fat100g?.grams.toNutrientValue(),
        sugars = sugars100g?.grams.toNutrientValue(),
        addedSugars = addedSugars100g?.grams.toNutrientValue(),
        dietaryFiber = fiber100g?.grams.toNutrientValue(),
        solubleFiber = solubleFiber100g?.grams.toNutrientValue(),
        insolubleFiber = insolubleFiber100g?.grams.toNutrientValue(),
        salt = salt100g?.grams.toNutrientValue(),
        cholesterol = cholesterol100g?.grams.toNutrientValue(),
        caffeine = caffeine100g?.grams.toNutrientValue(),
        vitaminA = vitaminA100g?.grams.toNutrientValue(),
        vitaminB1 = vitaminB1100g?.grams.toNutrientValue(),
        vitaminB2 = vitaminB2100g?.grams.toNutrientValue(),
        vitaminB3 = vitaminB3100g?.grams.toNutrientValue(),
        vitaminB5 = vitaminB5100g?.grams.toNutrientValue(),
        vitaminB6 = vitaminB6100g?.grams.toNutrientValue(),
        vitaminB7 = vitaminB7100g?.grams.toNutrientValue(),
        vitaminB9 = vitaminB9100g?.grams.toNutrientValue(),
        vitaminB12 = vitaminB12100g?.grams.toNutrientValue(),
        vitaminC = vitaminC100g?.grams.toNutrientValue(),
        vitaminD = vitaminD100g?.grams.toNutrientValue(),
        vitaminE = vitaminE100g?.grams.toNutrientValue(),
        vitaminK = vitaminK100g?.grams.toNutrientValue(),
        manganese = manganese100g?.grams.toNutrientValue(),
        magnesium = magnesium100g?.grams.toNutrientValue(),
        potassium = potassium100g?.grams.toNutrientValue(),
        calcium = calcium100g?.grams.toNutrientValue(),
        copper = copper100g?.grams.toNutrientValue(),
        zinc = zinc100g?.grams.toNutrientValue(),
        sodium = sodium100g?.grams.toNutrientValue(),
        iron = iron100g?.grams.toNutrientValue(),
        phosphorus = phosphorus100g?.grams.toNutrientValue(),
        selenium = selenium100g?.grams.toNutrientValue(),
        iodine = iodine100g?.grams.toNutrientValue(),
        chromium = chromium100g?.grams.toNutrientValue(),
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
