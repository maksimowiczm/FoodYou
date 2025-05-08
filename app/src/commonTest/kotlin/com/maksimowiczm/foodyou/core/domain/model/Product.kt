package com.maksimowiczm.foodyou.core.domain.model

fun testProduct(
    id: FoodId.Product = FoodId.Product(1L),
    name: String = "Test Product",
    brand: String? = "Test Brand",
    nutrients: Nutrients = testNutrients(),
    barcode: String? = "1234567890123",
    packageWeight: PortionWeight.Package? = PortionWeight.Package(500f),
    servingWeight: PortionWeight.Serving? = PortionWeight.Serving(20f)
): Product = Product(
    id = id,
    name = name,
    brand = brand,
    nutrients = nutrients,
    barcode = barcode,
    packageWeight = packageWeight,
    servingWeight = servingWeight
)
