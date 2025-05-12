package com.maksimowiczm.foodyou.core.domain.mapper

import com.maksimowiczm.foodyou.core.data.model.product.ProductEntity
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.PortionWeight
import com.maksimowiczm.foodyou.core.domain.model.Product

object ProductMapper {
    fun ProductEntity.toModel() = Product(
        id = FoodId.Product(id),
        name = name,
        brand = brand,
        barcode = barcode,
        nutritionFacts = NutritionFactsMapper.toNutritionFacts(nutrients, vitamins, minerals),
        packageWeight = packageWeight?.let { PortionWeight.Package(it) },
        servingWeight = servingWeight?.let { PortionWeight.Serving(it) }
    )
}
