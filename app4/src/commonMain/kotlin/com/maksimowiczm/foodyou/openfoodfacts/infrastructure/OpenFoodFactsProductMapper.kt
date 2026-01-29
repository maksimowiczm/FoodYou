package com.maksimowiczm.foodyou.openfoodfacts.infrastructure

import com.maksimowiczm.foodyou.common.domain.Image
import com.maksimowiczm.foodyou.common.domain.food.FoodBrand
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
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
        FoodName(
            english =
                localizedNames["en"].takeIfNotBlank()
                    ?: localizedGenericNames["en"].takeIfNotBlank(),
            catalan =
                localizedNames["ca"].takeIfNotBlank()
                    ?: localizedGenericNames["ca"].takeIfNotBlank(),
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
        brand =
            brands
                ?.takeIf { it.isNotEmpty() }
                ?.filterNot { it.isBlank() }
                ?.joinToString()
                ?.let(::FoodBrand),
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
        proteins = proteins100g.toNutrientValue(),
        carbohydrates = carbohydrates100g.toNutrientValue(),
        energy = energyKcal100g.toNutrientValue(),
        fats = fat100g.toNutrientValue(),
        saturatedFats = saturatedFat100g.toNutrientValue(),
        transFats = transFat100g.toNutrientValue(),
        monounsaturatedFats = monounsaturatedFat100g.toNutrientValue(),
        polyunsaturatedFats = polyunsaturatedFat100g.toNutrientValue(),
        omega3 = omega3Fat100g.toNutrientValue(),
        omega6 = omega6Fat100g.toNutrientValue(),
        sugars = sugars100g.toNutrientValue(),
        addedSugars = addedSugars100g.toNutrientValue(),
        dietaryFiber = fiber100g.toNutrientValue(),
        solubleFiber = solubleFiber100g.toNutrientValue(),
        insolubleFiber = insolubleFiber100g.toNutrientValue(),
        salt = salt100g.toNutrientValue(),
        cholesterol = cholesterol100g.toNutrientValue(),
        caffeine = caffeine100g.toNutrientValue(),
        vitaminA = vitaminA100g.toNutrientValue(),
        vitaminB1 = vitaminB1100g.toNutrientValue(),
        vitaminB2 = vitaminB2100g.toNutrientValue(),
        vitaminB3 = vitaminB3100g.toNutrientValue(),
        vitaminB5 = vitaminB5100g.toNutrientValue(),
        vitaminB6 = vitaminB6100g.toNutrientValue(),
        vitaminB7 = vitaminB7100g.toNutrientValue(),
        vitaminB9 = vitaminB9100g.toNutrientValue(),
        vitaminB12 = vitaminB12100g.toNutrientValue(),
        vitaminC = vitaminC100g.toNutrientValue(),
        vitaminD = vitaminD100g.toNutrientValue(),
        vitaminE = vitaminE100g.toNutrientValue(),
        vitaminK = vitaminK100g.toNutrientValue(),
        manganese = manganese100g.toNutrientValue(),
        magnesium = magnesium100g.toNutrientValue(),
        potassium = potassium100g.toNutrientValue(),
        calcium = calcium100g.toNutrientValue(),
        copper = copper100g.toNutrientValue(),
        zinc = zinc100g.toNutrientValue(),
        sodium = sodium100g.toNutrientValue(),
        iron = iron100g.toNutrientValue(),
        phosphorus = phosphorus100g.toNutrientValue(),
        selenium = selenium100g.toNutrientValue(),
        iodine = iodine100g.toNutrientValue(),
        chromium = chromium100g.toNutrientValue(),
    )
