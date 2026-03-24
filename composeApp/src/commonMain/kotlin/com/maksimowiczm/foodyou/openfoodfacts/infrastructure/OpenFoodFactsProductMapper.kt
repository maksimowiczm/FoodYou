package com.maksimowiczm.foodyou.openfoodfacts.infrastructure

import com.maksimowiczm.foodyou.common.domain.Image
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.kilocalories
import com.maksimowiczm.foodyou.common.extension.takeIfNotBlank
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.model.Nutriments
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.model.OpenFoodFactsProductNetwork
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room.OpenFoodFactsProductEntity
import kotlinx.serialization.json.Json

internal class OpenFoodFactsProductMapper {
    fun toEntity(model: OpenFoodFactsProductNetwork) =
        OpenFoodFactsProductEntity(barcode = model.code, rawJson = Json.encodeToString(model))

    fun toModel(entity: OpenFoodFactsProductEntity) =
        toModel(Json.decodeFromString<OpenFoodFactsProductNetwork>(entity.rawJson))

    fun toModel(network: OpenFoodFactsProductNetwork): OpenFoodFactsProduct = network.toModel()
}

private fun OpenFoodFactsProductNetwork.toModel(): OpenFoodFactsProduct {
    val name =
        FoodName.requireAll(
            english =
                localizedNames["en"].takeIfNotBlank()
                    ?: localizedGenericNames["en"].takeIfNotBlank(),
            catalan =
                localizedNames["ca"].takeIfNotBlank()
                    ?: localizedGenericNames["ca"].takeIfNotBlank(),
            czech =
                localizedNames["cs"].takeIfNotBlank()
                    ?: localizedGenericNames["cs"].takeIfNotBlank(),
            danish =
                localizedNames["da"].takeIfNotBlank()
                    ?: localizedGenericNames["da"].takeIfNotBlank(),
            german =
                localizedNames["de"].takeIfNotBlank()
                    ?: localizedGenericNames["de"].takeIfNotBlank(),
            spanish =
                localizedNames["es"].takeIfNotBlank()
                    ?: localizedGenericNames["es"].takeIfNotBlank(),
            french =
                localizedNames["fr"].takeIfNotBlank()
                    ?: localizedGenericNames["fr"].takeIfNotBlank(),
            indonesian =
                localizedNames["id"].takeIfNotBlank()
                    ?: localizedNames["in"].takeIfNotBlank()
                    ?: localizedGenericNames["id"].takeIfNotBlank()
                    ?: localizedGenericNames["in"].takeIfNotBlank(),
            italian =
                localizedNames["it"].takeIfNotBlank()
                    ?: localizedGenericNames["it"].takeIfNotBlank(),
            hungarian =
                localizedNames["hu"].takeIfNotBlank()
                    ?: localizedGenericNames["hu"].takeIfNotBlank(),
            dutch =
                localizedNames["nl"].takeIfNotBlank()
                    ?: localizedGenericNames["nl"].takeIfNotBlank(),
            polish =
                localizedNames["pl"].takeIfNotBlank()
                    ?: localizedGenericNames["pl"].takeIfNotBlank(),
            portugueseBrazil =
                localizedNames["pt_br"].takeIfNotBlank()
                    ?: localizedNames["pt"].takeIfNotBlank()
                    ?: localizedGenericNames["pt_br"].takeIfNotBlank()
                    ?: localizedGenericNames["pt"].takeIfNotBlank(),
            slovenian =
                localizedNames["sl"].takeIfNotBlank()
                    ?: localizedGenericNames["sl"].takeIfNotBlank(),
            turkish =
                localizedNames["tr"].takeIfNotBlank()
                    ?: localizedGenericNames["tr"].takeIfNotBlank(),
            russian =
                localizedNames["ru"].takeIfNotBlank()
                    ?: localizedGenericNames["ru"].takeIfNotBlank(),
            ukrainian =
                localizedNames["uk"].takeIfNotBlank()
                    ?: localizedGenericNames["uk"].takeIfNotBlank(),
            arabic =
                localizedNames["ar"].takeIfNotBlank()
                    ?: localizedGenericNames["ar"].takeIfNotBlank(),
            chineseSimplified =
                localizedNames["zh"].takeIfNotBlank()
                    ?: localizedGenericNames["zh"].takeIfNotBlank(),
            fallback =
                localizedNames[""].takeIfNotBlank()
                    ?: localizedGenericNames[""].takeIfNotBlank()
                    ?: localizedNames.values.firstOrNull { it.isNotBlank() }
                    ?: code,
        )

    val thumbnail = imageThumbUrl ?: imageFrontThumbUrl
    val fullSize = imageUrl ?: imageFrontUrl

    return OpenFoodFactsProduct(
        identity = OpenFoodFactsProductIdentity(code),
        name = name,
        brand = brands?.takeIf { it.isNotEmpty() }?.filterNot { it.isBlank() }?.joinToString(),
        nutritionFacts = nutriments?.toNutritionFacts() ?: NutritionFacts(),
        // TODO
        servingQuantity = null,
        packageQuantity = null,
        thumbnail = thumbnail?.let(Image::Remote),
        image = fullSize?.let(Image::Remote),
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
