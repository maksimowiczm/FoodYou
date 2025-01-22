package com.maksimowiczm.foodyou.feature.diary.database

import androidx.room.Embedded
import com.maksimowiczm.foodyou.feature.addfood.database.WeightMeasurementEntity
import com.maksimowiczm.foodyou.feature.product.database.ProductEntity

const val ProductWithWeightMeasurementSqlFields = """
    wm.id as wm_id,
    wm.mealId as wm_mealId,
    wm.diaryEpochDay as wm_diaryEpochDay,
    wm.productId as wm_productId,
    wm.measurement as wm_measurement,
    wm.quantity as wm_quantity,
    wm.createdAt as wm_createdAt,
    wm.isDeleted as wm_isDeleted,
    p.id as p_id,
    p.name as p_name,
    p.brand as p_brand,
    p.barcode as p_barcode,
    p.calories as p_calories,
    p.proteins as p_proteins,
    p.carbohydrates as p_carbohydrates,
    p.sugars as p_sugars,
    p.fats as p_fats,
    p.saturatedFats as p_saturatedFats,
    p.salt as p_salt,
    p.sodium as p_sodium,
    p.fiber as p_fiber,
    p.packageWeight as p_packageWeight,
    p.servingWeight as p_servingWeight,
    p.weightUnit as p_weightUnit,
    p.productSource as p_productSource
"""

data class ProductWithWeightMeasurement(
    @Embedded(prefix = "p_")
    val product: ProductEntity,

    @Embedded(prefix = "wm_")
    val weightMeasurement: WeightMeasurementEntity
)
