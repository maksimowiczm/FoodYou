package com.maksimowiczm.foodyou.core.data.model.measurement

import androidx.room.Embedded
import androidx.room.Relation
import com.maksimowiczm.foodyou.core.data.model.product.ProductEntity

data class ProductWithMeasurement(
    @Embedded
    val product: ProductEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "productId",
        entity = ProductMeasurementEntity::class
    )
    val measurement: ProductMeasurementEntity
)
