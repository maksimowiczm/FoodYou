package com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts

import com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts.model.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.core.food.domain.entity.RemoteNutritionFacts
import com.maksimowiczm.foodyou.core.food.domain.entity.RemoteProduct
import com.maksimowiczm.foodyou.core.shared.food.FoodSource

internal class OpenFoodFactsProductMapper {
    fun toRemoteProduct(product: OpenFoodFactsProduct): RemoteProduct {
        val name = product.name?.takeIf { it.isNotBlank() }
        val brand = product.brand?.takeIf { it.isNotBlank() }
        val barcode = product.barcode?.takeIf { it.isNotBlank() }

        val packageWeight = product.packageWeight?.takeIf { it > 0 }
        val servingWeight = product.servingWeight?.takeIf { it > 0 }
        val source = FoodSource(type = FoodSource.Type.OpenFoodFacts, url = product.url)

        val nutritionFacts =
            product.nutritionFacts?.let {
                RemoteNutritionFacts(
                    energy = it.energy,
                    carbohydrates = it.carbohydrates,
                    sugars = it.sugars,
                    addedSugars = it.addedSugars,
                    fats = it.fats,
                    saturatedFats = it.saturatedFats,
                    transFats = it.transFats,
                    monounsaturatedFats = it.monounsaturatedFats,
                    polyunsaturatedFats = it.polyunsaturatedFats,
                    omega3 = it.omega3Fats,
                    omega6 = it.omega6Fats,
                    proteins = it.proteins,
                    salt = it.salt,
                    dietaryFiber = it.fiber,
                    solubleFiber = it.solubleFiber,
                    insolubleFiber = it.insolubleFiber,
                    cholesterol = it.cholesterol,
                    caffeine = it.caffeine,
                    vitaminA = it.vitaminA,
                    vitaminB1 = it.vitaminB1,
                    vitaminB2 = it.vitaminB2,
                    vitaminB3 = it.vitaminB3,
                    vitaminB5 = it.vitaminB5,
                    vitaminB6 = it.vitaminB6,
                    vitaminB7 = it.vitaminB7,
                    vitaminB9 = it.vitaminB9,
                    vitaminB12 = it.vitaminB12,
                    vitaminC = it.vitaminC,
                    vitaminD = it.vitaminD,
                    vitaminE = it.vitaminE,
                    vitaminK = it.vitaminK,
                    manganese = it.manganese,
                    magnesium = it.magnesium,
                    potassium = it.potassium,
                    calcium = it.calcium,
                    copper = it.copper,
                    zinc = it.zinc,
                    sodium = it.sodium,
                    iron = it.iron,
                    phosphorus = it.phosphorus,
                    selenium = it.selenium,
                    iodine = it.iodine,
                    chromium = it.chromium,
                )
            }

        val isLiquid =
            product.packageQuantityUnit?.equals("ml", ignoreCase = true) == true ||
                product.servingQuantityUnit?.equals("ml", ignoreCase = true) == true

        return RemoteProduct(
            name = name,
            brand = brand,
            barcode = barcode,
            nutritionFacts = nutritionFacts,
            packageWeight = packageWeight?.toDouble(),
            servingWeight = servingWeight?.toDouble(),
            source = source,
            isLiquid = isLiquid,
        )
    }
}
