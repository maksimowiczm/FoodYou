package com.maksimowiczm.foodyou.core.mapper

import com.maksimowiczm.foodyou.core.database.entity.ProductEntity
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.core.model.Nutrients
import com.maksimowiczm.foodyou.core.model.PortionWeight
import com.maksimowiczm.foodyou.core.model.Product

object ProductMapper {
    fun ProductEntity.toModel() = Product(
        id = FoodId.Product(id),
        name = name,
        brand = brand,
        barcode = null,
        nutrients = Nutrients(
            calories = nutrients.calories.toNutrientValue(),
            proteins = nutrients.proteins.toNutrientValue(),
            carbohydrates = nutrients.carbohydrates.toNutrientValue(),
            sugars = nutrients.sugars.toNutrientValue(),
            fats = nutrients.fats.toNutrientValue(),
            saturatedFats = nutrients.saturatedFats.toNutrientValue(),
            salt = nutrients.salt.toNutrientValue(),
            sodium = nutrients.sodium.toNutrientValue(),
            fiber = nutrients.fiber.toNutrientValue()
        ),
        packageWeight = packageWeight?.let { PortionWeight.Package(it) },
        servingWeight = servingWeight?.let { PortionWeight.Serving(it) }
    )
}
