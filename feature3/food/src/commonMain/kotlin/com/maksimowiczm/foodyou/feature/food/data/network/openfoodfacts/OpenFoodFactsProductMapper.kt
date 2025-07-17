package com.maksimowiczm.foodyou.feature.food.data.network.openfoodfacts

import com.maksimowiczm.foodyou.feature.food.data.network.multiplierForUnit
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
                cholesterolMilli = it.cholesterol?.times(
                    multiplierForUnit(it.cholesterolUnit, "mg")
                )?.toFloat(),
                caffeineMilli = it.caffeine?.times(multiplierForUnit(it.caffeineUnit, "mg"))
                    ?.toFloat(),
                vitaminAMicro = it.vitaminA?.times(multiplierForUnit(it.vitaminAUnit, "mcg"))
                    ?.toFloat(),
                vitaminB1Milli = it.vitaminB1?.times(multiplierForUnit(it.vitaminB1Unit, "mg"))
                    ?.toFloat(),
                vitaminB2Milli = it.vitaminB2?.times(multiplierForUnit(it.vitaminB2Unit, "mg"))
                    ?.toFloat(),
                vitaminB3Milli = it.vitaminB3?.times(multiplierForUnit(it.vitaminB3Unit, "mg"))
                    ?.toFloat(),
                vitaminB5Milli = it.vitaminB5?.times(multiplierForUnit(it.vitaminB5Unit, "mg"))
                    ?.toFloat(),
                vitaminB6Milli = it.vitaminB6?.times(multiplierForUnit(it.vitaminB6Unit, "mg"))
                    ?.toFloat(),
                vitaminB7Micro = it.vitaminB7?.times(multiplierForUnit(it.vitaminB7Unit, "mcg"))
                    ?.toFloat(),
                vitaminB9Micro = it.vitaminB9?.times(multiplierForUnit(it.vitaminB9Unit, "mcg"))
                    ?.toFloat(),
                vitaminB12Micro = it.vitaminB12?.times(multiplierForUnit(it.vitaminB12Unit, "mcg"))
                    ?.toFloat(),
                vitaminCMilli = it.vitaminC?.times(multiplierForUnit(it.vitaminCUnit, "mg"))
                    ?.toFloat(),
                vitaminDMicro = it.vitaminD?.times(multiplierForUnit(it.vitaminDUnit, "mcg"))
                    ?.toFloat(),
                vitaminEMilli = it.vitaminE?.times(multiplierForUnit(it.vitaminEUnit, "mg"))
                    ?.toFloat(),
                vitaminKMicro = it.vitaminK?.times(multiplierForUnit(it.vitaminKUnit, "mcg"))
                    ?.toFloat(),
                manganeseMilli = it.manganese?.times(multiplierForUnit(it.manganeseUnit, "mg"))
                    ?.toFloat(),
                magnesiumMilli = it.magnesium?.times(multiplierForUnit(it.magnesiumUnit, "mg"))
                    ?.toFloat(),
                potassiumMilli = it.potassium?.times(multiplierForUnit(it.potassiumUnit, "mg"))
                    ?.toFloat(),
                calciumMilli = it.calcium?.times(multiplierForUnit(it.calciumUnit, "mg"))
                    ?.toFloat(),
                copperMilli = it.copper?.times(multiplierForUnit(it.copperUnit, "mg"))?.toFloat(),
                zincMilli = it.zinc?.times(multiplierForUnit(it.zincUnit, "mg"))?.toFloat(),
                sodiumMilli = it.sodium?.times(multiplierForUnit(it.sodiumUnit, "mg"))?.toFloat(),
                ironMilli = it.iron?.times(multiplierForUnit(it.ironUnit, "mg"))?.toFloat(),
                phosphorusMilli = it.phosphorus?.times(multiplierForUnit(it.phosphorusUnit, "mg"))
                    ?.toFloat(),
                seleniumMicro = it.selenium?.times(multiplierForUnit(it.seleniumUnit, "mcg"))
                    ?.toFloat(),
                iodineMicro = it.iodine?.times(multiplierForUnit(it.iodineUnit, "mcg"))?.toFloat(),
                chromiumMicro = it.chromium?.times(multiplierForUnit(it.chromiumUnit, "mcg"))
                    ?.toFloat()
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
