package com.maksimowiczm.foodyou.core.data.model.measurement

import androidx.room.Embedded
import androidx.room.Relation
import com.maksimowiczm.foodyou.core.data.model.product.ProductEntity

data class ProductWithMeasurement(
    @Relation(
        parentColumn = "productId",
        entityColumn = "id",
        entity = ProductEntity::class
    )
    val product: ProductEntity,

    @Embedded
    val measurement: ProductMeasurementEntity
)
