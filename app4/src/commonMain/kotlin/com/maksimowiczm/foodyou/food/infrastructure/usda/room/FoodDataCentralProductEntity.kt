package com.maksimowiczm.foodyou.food.infrastructure.usda.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "FoodDataCentralProduct", indices = [Index("gtinUpc")])
data class FoodDataCentralProductEntity(
    @PrimaryKey val fdcId: Int,
    val brandOwner: String?,
    val brandName: String?,
    val gtinUpc: String?,
    val servingSize: Double?,
    val servingSizeUnit: String?,
    val description: String,
    val packageWeight: String?,
    @Embedded val nutrients: NutrientsEntity,
)
