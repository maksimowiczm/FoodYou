package com.maksimowiczm.foodyou.feature.diary.core.database.product

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.feature.diary.core.database.core.Nutrients

@Entity
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val brand: String?,
    val barcode: String?,
    @Embedded
    val nutrients: Nutrients,
    val packageWeight: Float?,
    val servingWeight: Float?,
    // val weightUnit: WeightUnit,
    val productSource: ProductSource
)
