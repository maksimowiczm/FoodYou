package com.maksimowiczm.foodyou.core.model.testing

import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.NutritionFacts
import com.maksimowiczm.foodyou.core.model.Product

fun testProduct(
    id: FoodId.Product = FoodId.Product(1L),
    name: String = "Test Product",
    brand: String? = "Test Brand",
    nutritionFacts: NutritionFacts = testNutritionFacts(),
    barcode: String? = "1234567890123",
    totalWeight: Float? = 500f,
    servingWeight: Float? = 100f,
    isLiquid: Boolean = false,
    notes: String? = null
): Product = Product(
    id = id,
    name = name,
    brand = brand,
    nutritionFacts = nutritionFacts,
    barcode = barcode,
    totalWeight = totalWeight,
    servingWeight = servingWeight,
    isLiquid = isLiquid,
    notes = notes
)
