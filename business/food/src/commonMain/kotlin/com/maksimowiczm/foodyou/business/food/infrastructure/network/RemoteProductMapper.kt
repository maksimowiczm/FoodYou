package com.maksimowiczm.foodyou.business.food.infrastructure.network

import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.food.domain.entity.Product
import com.maksimowiczm.foodyou.food.domain.entity.RemoteProduct
import com.maksimowiczm.foodyou.shared.food.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.shared.food.NutritionFacts

internal class RemoteProductMapper {
    fun toModel(remote: RemoteProduct, id: FoodId.Product = FoodId.Product(0)): Product {
        val name = remote.name
        if (name == null) {
            error("Product name cannot be null")
        }

        val nutritionFacts = remote.nutritionFacts
        if (nutritionFacts == null) {
            error("Nutrition facts cannot be null")
        }

        val facts =
            NutritionFacts.requireAll(
                proteins = nutritionFacts.proteins.toNutrientValue(),
                carbohydrates = nutritionFacts.carbohydrates.toNutrientValue(),
                energy = nutritionFacts.energy.toNutrientValue(),
                fats = nutritionFacts.fats.toNutrientValue(),
                saturatedFats = nutritionFacts.saturatedFats.toNutrientValue(),
                transFats = nutritionFacts.transFats.toNutrientValue(),
                monounsaturatedFats = nutritionFacts.monounsaturatedFats.toNutrientValue(),
                polyunsaturatedFats = nutritionFacts.polyunsaturatedFats.toNutrientValue(),
                omega3 = nutritionFacts.omega3.toNutrientValue(),
                omega6 = nutritionFacts.omega6.toNutrientValue(),
                sugars = nutritionFacts.sugars.toNutrientValue(),
                addedSugars = nutritionFacts.addedSugars.toNutrientValue(),
                dietaryFiber = nutritionFacts.dietaryFiber.toNutrientValue(),
                solubleFiber = nutritionFacts.solubleFiber.toNutrientValue(),
                insolubleFiber = nutritionFacts.insolubleFiber.toNutrientValue(),
                salt = nutritionFacts.salt.toNutrientValue(),
                cholesterol = nutritionFacts.cholesterol.toNutrientValue(),
                caffeine = nutritionFacts.caffeine.toNutrientValue(),
                vitaminA = nutritionFacts.vitaminA.toNutrientValue(),
                vitaminB1 = nutritionFacts.vitaminB1.toNutrientValue(),
                vitaminB2 = nutritionFacts.vitaminB2.toNutrientValue(),
                vitaminB3 = nutritionFacts.vitaminB3.toNutrientValue(),
                vitaminB5 = nutritionFacts.vitaminB5.toNutrientValue(),
                vitaminB6 = nutritionFacts.vitaminB6.toNutrientValue(),
                vitaminB7 = nutritionFacts.vitaminB7.toNutrientValue(),
                vitaminB9 = nutritionFacts.vitaminB9.toNutrientValue(),
                vitaminB12 = nutritionFacts.vitaminB12.toNutrientValue(),
                vitaminC = nutritionFacts.vitaminC.toNutrientValue(),
                vitaminD = nutritionFacts.vitaminD.toNutrientValue(),
                vitaminE = nutritionFacts.vitaminE.toNutrientValue(),
                vitaminK = nutritionFacts.vitaminK.toNutrientValue(),
                manganese = nutritionFacts.manganese.toNutrientValue(),
                magnesium = nutritionFacts.magnesium.toNutrientValue(),
                potassium = nutritionFacts.potassium.toNutrientValue(),
                calcium = nutritionFacts.calcium.toNutrientValue(),
                copper = nutritionFacts.copper.toNutrientValue(),
                zinc = nutritionFacts.zinc.toNutrientValue(),
                sodium = nutritionFacts.sodium.toNutrientValue(),
                iron = nutritionFacts.iron.toNutrientValue(),
                phosphorus = nutritionFacts.phosphorus.toNutrientValue(),
                selenium = nutritionFacts.selenium.toNutrientValue(),
                iodine = nutritionFacts.iodine.toNutrientValue(),
                chromium = nutritionFacts.chromium.toNutrientValue(),
            )

        return Product(
            id = id,
            name = name,
            brand = remote.brand?.takeIf { it.isNotBlank() },
            barcode = remote.barcode?.takeIf { it.isNotBlank() },
            packageWeight = remote.packageWeight,
            servingWeight = remote.servingWeight,
            nutritionFacts = facts,
            source = remote.source,
            note = null,
            isLiquid = remote.isLiquid,
        )
    }
}
