package com.maksimowiczm.foodyou.feature.search.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val brand: String?,
    val barcode: String?,

    val packageWeight: Float?,
    val servingWeight: Float?,
    val foodForm: FoodForm,
    val productSource: ProductSource,

    @Embedded
    val nutrients: Nutrients
)
