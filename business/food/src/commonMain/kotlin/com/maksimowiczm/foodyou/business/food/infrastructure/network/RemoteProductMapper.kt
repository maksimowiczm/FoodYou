package com.maksimowiczm.foodyou.business.food.infrastructure.network

import com.maksimowiczm.foodyou.business.food.domain.Product
import com.maksimowiczm.foodyou.business.food.domain.RemoteProduct
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId

internal class RemoteProductMapper {
    fun toModel(remote: RemoteProduct, id: FoodId.Product = FoodId.Product(0)): Product {
        if (remote.name == null) {
            error("Product name cannot be null")
        }

        if (remote.nutritionFacts == null) {
            error("Nutrition facts cannot be null")
        }

        val facts =
            NutritionFacts.requireAll(
                proteins = remote.nutritionFacts.proteins.toNutrientValue(),
                carbohydrates = remote.nutritionFacts.carbohydrates.toNutrientValue(),
                energy = remote.nutritionFacts.energy.toNutrientValue(),
                fats = remote.nutritionFacts.fats.toNutrientValue(),
                saturatedFats = remote.nutritionFacts.saturatedFats.toNutrientValue(),
                transFats = remote.nutritionFacts.transFats.toNutrientValue(),
                monounsaturatedFats = remote.nutritionFacts.monounsaturatedFats.toNutrientValue(),
                polyunsaturatedFats = remote.nutritionFacts.polyunsaturatedFats.toNutrientValue(),
                omega3 = remote.nutritionFacts.omega3.toNutrientValue(),
                omega6 = remote.nutritionFacts.omega6.toNutrientValue(),
                sugars = remote.nutritionFacts.sugars.toNutrientValue(),
                addedSugars = remote.nutritionFacts.addedSugars.toNutrientValue(),
                dietaryFiber = remote.nutritionFacts.dietaryFiber.toNutrientValue(),
                solubleFiber = remote.nutritionFacts.solubleFiber.toNutrientValue(),
                insolubleFiber = remote.nutritionFacts.insolubleFiber.toNutrientValue(),
                salt = remote.nutritionFacts.salt.toNutrientValue(),
                cholesterol = remote.nutritionFacts.cholesterol.toNutrientValue(),
                caffeine = remote.nutritionFacts.caffeine.toNutrientValue(),
                vitaminA = remote.nutritionFacts.vitaminA.toNutrientValue(),
                vitaminB1 = remote.nutritionFacts.vitaminB1.toNutrientValue(),
                vitaminB2 = remote.nutritionFacts.vitaminB2.toNutrientValue(),
                vitaminB3 = remote.nutritionFacts.vitaminB3.toNutrientValue(),
                vitaminB5 = remote.nutritionFacts.vitaminB5.toNutrientValue(),
                vitaminB6 = remote.nutritionFacts.vitaminB6.toNutrientValue(),
                vitaminB7 = remote.nutritionFacts.vitaminB7.toNutrientValue(),
                vitaminB9 = remote.nutritionFacts.vitaminB9.toNutrientValue(),
                vitaminB12 = remote.nutritionFacts.vitaminB12.toNutrientValue(),
                vitaminC = remote.nutritionFacts.vitaminC.toNutrientValue(),
                vitaminD = remote.nutritionFacts.vitaminD.toNutrientValue(),
                vitaminE = remote.nutritionFacts.vitaminE.toNutrientValue(),
                vitaminK = remote.nutritionFacts.vitaminK.toNutrientValue(),
                manganese = remote.nutritionFacts.manganese.toNutrientValue(),
                magnesium = remote.nutritionFacts.magnesium.toNutrientValue(),
                potassium = remote.nutritionFacts.potassium.toNutrientValue(),
                calcium = remote.nutritionFacts.calcium.toNutrientValue(),
                copper = remote.nutritionFacts.copper.toNutrientValue(),
                zinc = remote.nutritionFacts.zinc.toNutrientValue(),
                sodium = remote.nutritionFacts.sodium.toNutrientValue(),
                iron = remote.nutritionFacts.iron.toNutrientValue(),
                phosphorus = remote.nutritionFacts.phosphorus.toNutrientValue(),
                selenium = remote.nutritionFacts.selenium.toNutrientValue(),
                iodine = remote.nutritionFacts.iodine.toNutrientValue(),
                chromium = remote.nutritionFacts.chromium.toNutrientValue(),
            )

        return Product(
            id = id,
            name = remote.name,
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
