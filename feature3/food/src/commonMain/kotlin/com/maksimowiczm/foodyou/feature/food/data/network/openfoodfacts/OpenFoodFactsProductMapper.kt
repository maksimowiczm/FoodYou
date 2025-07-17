package com.maksimowiczm.foodyou.feature.food.data.network.openfoodfacts

import com.maksimowiczm.foodyou.feature.food.domain.FoodSource
import com.maksimowiczm.foodyou.feature.food.domain.RemoteNutritionFacts
import com.maksimowiczm.foodyou.feature.food.domain.RemoteProduct
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.model.OpenFoodFactsProduct

internal class OpenFoodFactsProductMapper {

    fun toRemoteProduct(product: OpenFoodFactsProduct): RemoteProduct = RemoteProduct(
        name = product.name?.takeIf { it.isNotBlank() } ?: product.brand,
        brand = product.brand?.takeIf { it.isNotBlank() },
        barcode = product.barcode?.takeIf { it.isNotBlank() },
        nutritionFacts = RemoteNutritionFacts(
            proteins = product.nutritionFacts?.proteins?.toFloat(),
            carbohydrates = product.nutritionFacts?.carbohydrates?.toFloat(),
            fats = product.nutritionFacts?.fats?.toFloat(),
            energy = product.nutritionFacts?.energy?.toFloat(),
            saturatedFats = product.nutritionFacts?.saturatedFats?.toFloat(),
            monounsaturatedFats = product.nutritionFacts?.monounsaturatedFats?.toFloat(),
            polyunsaturatedFats = product.nutritionFacts?.polyunsaturatedFats?.toFloat(),
            omega3 = product.nutritionFacts?.omega3Fats?.toFloat(),
            omega6 = product.nutritionFacts?.omega6Fats?.toFloat(),
            sugars = product.nutritionFacts?.sugars?.toFloat(),
            salt = product.nutritionFacts?.salt?.toFloat(),
            fiber = product.nutritionFacts?.fiber?.toFloat(),
            cholesterolMilli = product.nutritionFacts?.cholesterol?.toMilligrams(),
            caffeineMilli = product.nutritionFacts?.caffeine?.toMilligrams(),
            vitaminAMicro = product.nutritionFacts?.vitaminA.toMicrograms(),
            vitaminB1Milli = product.nutritionFacts?.vitaminB1.toMilligrams(),
            vitaminB2Milli = product.nutritionFacts?.vitaminB2.toMilligrams(),
            vitaminB3Milli = product.nutritionFacts?.vitaminB3.toMilligrams(),
            vitaminB5Milli = product.nutritionFacts?.vitaminB5.toMilligrams(),
            vitaminB6Milli = product.nutritionFacts?.vitaminB6.toMilligrams(),
            vitaminB7Micro = product.nutritionFacts?.vitaminB7.toMicrograms(),
            vitaminB9Micro = product.nutritionFacts?.vitaminB9.toMicrograms(),
            vitaminB12Micro = product.nutritionFacts?.vitaminB12.toMicrograms(),
            vitaminCMilli = product.nutritionFacts?.vitaminC.toMilligrams(),
            vitaminDMicro = product.nutritionFacts?.vitaminD.toMicrograms(),
            vitaminEMilli = product.nutritionFacts?.vitaminE.toMilligrams(),
            vitaminKMicro = product.nutritionFacts?.vitaminK.toMicrograms(),
            manganeseMilli = product.nutritionFacts?.manganese.toMilligrams(),
            magnesiumMilli = product.nutritionFacts?.magnesium.toMilligrams(),
            potassiumMilli = product.nutritionFacts?.potassium.toMilligrams(),
            calciumMilli = product.nutritionFacts?.calcium.toMilligrams(),
            copperMilli = product.nutritionFacts?.copper.toMilligrams(),
            zincMilli = product.nutritionFacts?.zinc.toMilligrams(),
            sodiumMilli = product.nutritionFacts?.sodium.toMilligrams(),
            ironMilli = product.nutritionFacts?.iron.toMilligrams(),
            phosphorusMilli = product.nutritionFacts?.phosphorus.toMilligrams(),
            seleniumMicro = product.nutritionFacts?.selenium.toMicrograms(),
            iodineMicro = product.nutritionFacts?.iodine.toMicrograms(),
            chromiumMicro = product.nutritionFacts?.chromium.toMicrograms(),
            transFats = product.nutritionFacts?.transFats?.toFloat(),
            addedSugars = product.nutritionFacts?.addedSugars?.toFloat(),
            solubleFiber = product.nutritionFacts?.solubleFiber?.toFloat(),
            insolubleFiber = product.nutritionFacts?.insolubleFiber?.toFloat()
        ),
        packageWeight = product.packageWeight?.takeIf { it > 0 },
        servingWeight = product.servingWeight?.takeIf { it > 0 },
        source = FoodSource(
            type = FoodSource.Type.OpenFoodFacts,
            url = product.url
        )
    )

    private fun Double?.toMilligrams() = this?.let { it * 1_000 }?.toFloat()
    private fun Double?.toMicrograms() = this?.let { it * 1_000_000 }?.toFloat()
}
