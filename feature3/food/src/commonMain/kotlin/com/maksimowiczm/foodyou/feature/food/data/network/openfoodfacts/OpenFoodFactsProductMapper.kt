package com.maksimowiczm.foodyou.feature.food.data.network.openfoodfacts

import com.maksimowiczm.foodyou.feature.food.data.network.UnitType
import com.maksimowiczm.foodyou.feature.food.data.network.UnitType.MICROGRAMS
import com.maksimowiczm.foodyou.feature.food.data.network.UnitType.MILLIGRAMS
import com.maksimowiczm.foodyou.feature.food.data.network.multiplier
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource
import com.maksimowiczm.foodyou.feature.food.domain.RemoteNutritionFacts
import com.maksimowiczm.foodyou.feature.food.domain.RemoteProduct
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.model.OpenFoodFactsProduct

internal class OpenFoodFactsProductMapper {

    fun toRemoteProduct(product: OpenFoodFactsProduct): RemoteProduct {
        val name = product.name?.takeIf { it.isNotBlank() }
        val brand = product.brand?.takeIf { it.isNotBlank() }
        val barcode = product.barcode?.takeIf { it.isNotBlank() }

        val packageWeight = product.packageWeight?.takeIf { it > 0 }
        val servingWeight = product.servingWeight?.takeIf { it > 0 }
        val source = FoodSource(
            type = FoodSource.Type.OpenFoodFacts,
            url = product.url
        )

        val nutritionFacts = product.nutritionFacts?.let {
            RemoteNutritionFacts(
                energy = it.energy?.toFloat(),
                carbohydrates = it.carbohydrates?.toFloat(),
                sugars = it.sugars?.toFloat(),
                addedSugars = it.addedSugars?.toFloat(),
                fats = it.fats?.toFloat(),
                saturatedFats = it.saturatedFats?.toFloat(),
                transFats = it.transFats?.toFloat(),
                monounsaturatedFats = it.monounsaturatedFats?.toFloat(),
                polyunsaturatedFats = it.polyunsaturatedFats?.toFloat(),
                omega3 = it.omega3Fats?.toFloat(),
                omega6 = it.omega6Fats?.toFloat(),
                proteins = it.proteins?.toFloat(),
                salt = it.salt?.toFloat(),
                fiber = it.fiber?.toFloat(),
                solubleFiber = it.solubleFiber?.toFloat(),
                insolubleFiber = it.insolubleFiber?.toFloat(),
                cholesterolMilli = it.cholesterol?.normalize(MILLIGRAMS),
                caffeineMilli = it.caffeine?.normalize(MILLIGRAMS),
                vitaminAMicro = it.vitaminA?.normalize(MICROGRAMS),
                vitaminB1Milli = it.vitaminB1?.normalize(MILLIGRAMS),
                vitaminB2Milli = it.vitaminB2?.normalize(MILLIGRAMS),
                vitaminB3Milli = it.vitaminB3?.normalize(MILLIGRAMS),
                vitaminB5Milli = it.vitaminB5?.normalize(MILLIGRAMS),
                vitaminB6Milli = it.vitaminB6?.normalize(MILLIGRAMS),
                vitaminB7Micro = it.vitaminB7?.normalize(MICROGRAMS),
                vitaminB9Micro = it.vitaminB9?.normalize(MICROGRAMS),
                vitaminB12Micro = it.vitaminB12?.normalize(MICROGRAMS),
                vitaminCMilli = it.vitaminC?.normalize(MILLIGRAMS),
                vitaminDMicro = it.vitaminD?.normalize(MICROGRAMS),
                vitaminEMilli = it.vitaminE?.normalize(MILLIGRAMS),
                vitaminKMicro = it.vitaminK?.normalize(MICROGRAMS),
                manganeseMilli = it.manganese?.normalize(MILLIGRAMS),
                magnesiumMilli = it.magnesium?.normalize(MILLIGRAMS),
                potassiumMilli = it.potassium?.normalize(MILLIGRAMS),
                calciumMilli = it.calcium?.normalize(MILLIGRAMS),
                copperMilli = it.copper?.normalize(MILLIGRAMS),
                zincMilli = it.zinc?.normalize(MILLIGRAMS),
                sodiumMilli = it.sodium?.normalize(MILLIGRAMS),
                ironMilli = it.iron?.normalize(MILLIGRAMS),
                phosphorusMilli = it.phosphorus?.normalize(MILLIGRAMS),
                seleniumMicro = it.selenium?.normalize(MICROGRAMS),
                iodineMicro = it.iodine?.normalize(MICROGRAMS),
                chromiumMicro = it.chromium?.normalize(MICROGRAMS)
            )
        }

        return RemoteProduct(
            name = name,
            brand = brand,
            barcode = barcode,
            nutritionFacts = nutritionFacts,
            packageWeight = packageWeight,
            servingWeight = servingWeight,
            source = source
        )
    }
}

private fun Double?.normalize(targetUnit: UnitType) = this?.times(multiplier(targetUnit))?.toFloat()
