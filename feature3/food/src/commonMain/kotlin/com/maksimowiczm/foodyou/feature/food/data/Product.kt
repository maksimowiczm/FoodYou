package com.maksimowiczm.foodyou.feature.food.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
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
    val note: String?
)
