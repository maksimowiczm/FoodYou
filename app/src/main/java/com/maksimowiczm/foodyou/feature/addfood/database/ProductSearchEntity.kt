package com.maksimowiczm.foodyou.feature.addfood.database

import androidx.room.Embedded
import com.maksimowiczm.foodyou.feature.addfood.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.feature.product.data.model.WeightUnit

data class ProductSearchEntity(
    @Embedded(prefix = "p_")
    val product: ProductSearchEntityProduct,

    @Embedded(prefix = "wm_")
    val weightMeasurement: ProductSearchEntityWeightMeasurement?,

    val hasMeasurement: Boolean
)

data class ProductSearchEntityProduct(
    val id: Long,
    val name: String,
    val brand: String?,
    val barcode: String?,
    val calories: Float,
    val packageWeight: Float?,
    val servingWeight: Float?,
    val weightUnit: WeightUnit
)

data class ProductSearchEntityWeightMeasurement(
    val id: Long,
    val quantity: Float,
    val measurement: WeightMeasurementEnum,
    val createdAt: Long
)
