package com.maksimowiczm.foodyou.core.mapper

import com.maksimowiczm.foodyou.core.database.product.ProductEntity
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.PortionWeight
import com.maksimowiczm.foodyou.core.model.Product

object ProductMapper {
    fun ProductEntity.toModel() = Product(
        id = FoodId.Product(id),
        name = name,
        brand = brand,
        barcode = null,
        nutrients = with(NutrientsMapper) { nutrients.toModel() },
        packageWeight = packageWeight?.let { PortionWeight.Package(it) },
        servingWeight = servingWeight?.let { PortionWeight.Serving(it) }
    )
}
