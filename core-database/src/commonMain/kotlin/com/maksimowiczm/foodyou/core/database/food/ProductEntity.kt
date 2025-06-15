package com.maksimowiczm.foodyou.core.database.food

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val brand: String?,
    val barcode: String?,
    @Embedded
    val nutrients: Nutrients,
    @Embedded
    val vitamins: Vitamins,
    @Embedded
    val minerals: Minerals,
    val packageWeight: Float?,
    val servingWeight: Float?,
    val isLiquid: Boolean
)
