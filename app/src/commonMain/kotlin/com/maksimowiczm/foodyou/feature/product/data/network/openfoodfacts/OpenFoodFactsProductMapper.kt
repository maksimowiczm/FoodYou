package com.maksimowiczm.foodyou.feature.product.data.network.openfoodfacts

import com.maksimowiczm.foodyou.feature.product.data.network.openfoodfacts.model.v2.OpenFoodFactsProductV2
import com.maksimowiczm.foodyou.feature.product.domain.RemoteNutritionFacts
import com.maksimowiczm.foodyou.feature.product.domain.RemoteProduct

internal object OpenFoodFactsProductMapper {

    fun toRemoteProduct(product: OpenFoodFactsProductV2): RemoteProduct = RemoteProduct(
        name = product.name,
        brand = product.brand,
        barcode = product.barcode,
        nutritionFacts = RemoteNutritionFacts(
            proteins = product.nutritionFacts?.proteins?.toFloat(),
            carbohydrates = product.nutritionFacts?.carbohydrates?.toFloat(),
            fats = product.nutritionFacts?.fats?.toFloat(),
            calories = product.nutritionFacts?.calories?.toFloat(),
            saturatedFats = product.nutritionFacts?.saturatedFats?.toFloat(),
            monounsaturatedFats = null,
            polyunsaturatedFats = null,
            omega3 = null,
            omega6 = null,
            sugars = product.nutritionFacts?.sugars?.toFloat(),
            salt = product.nutritionFacts?.salt?.toFloat(),
            fiber = product.nutritionFacts?.fiber?.toFloat(),
            cholesterolMilli = null,
            caffeineMilli = null,
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
            iodineMicro = product.nutritionFacts?.iodine.toMicrograms()
        ),
        packageWeight = product.packageWeight,
        servingWeight = product.servingWeight
    )

    private fun Double?.toMilligrams() = this?.let { it * 1_000 }?.toFloat()
    private fun Double?.toMicrograms() = this?.let { it * 1_000_000 }?.toFloat()
}
