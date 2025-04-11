package com.maksimowiczm.foodyou.core.database.product

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.core.database.core.NutrientsEmbedded

@Entity
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val brand: String?,
    val barcode: String?,
    @Embedded
    val nutrients: NutrientsEmbedded,
    val packageWeight: Float?,
    val servingWeight: Float?,
    // val weightUnit: WeightUnit,
    val productSource: ProductSource
)
