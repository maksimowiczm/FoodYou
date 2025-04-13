package com.maksimowiczm.foodyou.core.database.measurement

import androidx.room.Embedded
import androidx.room.Relation
import com.maksimowiczm.foodyou.core.database.product.ProductEntity

data class ProductMeasurementVirtualEntity(
    @Embedded
    val product: ProductEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "productId",
        entity = ProductMeasurementEntity::class
    )
    val measurement: ProductMeasurementEntity
)
