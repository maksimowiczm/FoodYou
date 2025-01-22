package com.maksimowiczm.foodyou.feature.product.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.feature.product.data.model.ProductSource
import com.maksimowiczm.foodyou.feature.product.data.model.WeightUnit

@Entity
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val brand: String?,
    val barcode: String?,
    val calories: Float,
    val proteins: Float,
    val carbohydrates: Float,
    val sugars: Float?,
    val fats: Float,
    val saturatedFats: Float?,
    val salt: Float?,
    val sodium: Float?,
    val fiber: Float?,
    val packageWeight: Float?,
    val servingWeight: Float?,
    val weightUnit: WeightUnit,
    val productSource: ProductSource
)
