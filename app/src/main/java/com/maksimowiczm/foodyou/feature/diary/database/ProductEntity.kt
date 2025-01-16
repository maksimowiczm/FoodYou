package com.maksimowiczm.foodyou.feature.diary.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.feature.diary.data.model.Barcode
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightUnit

@Entity
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val brand: String?,
    val barcode: Barcode?,
    val calories: Float,
    val proteins: Float,
    val carbohydrates: Float,
    val sugars: Float?,
    val fats: Float,
    val saturatedFats: Float?,
    val salt: Float?,
    val sodium: Float?,
    val fiber: Float?,
    val packageQuantity: Float?,
    val servingQuantity: Float?,
    val weightUnit: WeightUnit
)
