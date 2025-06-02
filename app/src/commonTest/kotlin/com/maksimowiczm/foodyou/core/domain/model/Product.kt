package com.maksimowiczm.foodyou.core.domain.model

import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.PortionWeight
import com.maksimowiczm.foodyou.core.model.Product

fun testProduct(
    id: FoodId.Product = FoodId.Product(1L),
    name: String = "Test Product",
    brand: String? = "Test Brand",
    nutritionFacts: NutritionFacts = testNutritionFacts(),
    barcode: String? = "1234567890123",
    packageWeight: PortionWeight.Package? = PortionWeight.Package(500f),
    servingWeight: PortionWeight.Serving? = PortionWeight.Serving(20f)
): Product = Product(
    id = id,
    name = name,
    brand = brand,
    nutritionFacts = nutritionFacts,
    barcode = barcode,
    totalWeight = packageWeight,
    servingWeight = servingWeight
)
