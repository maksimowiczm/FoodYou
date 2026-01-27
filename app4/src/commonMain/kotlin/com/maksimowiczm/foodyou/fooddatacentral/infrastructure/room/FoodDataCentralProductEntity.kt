package com.maksimowiczm.foodyou.fooddatacentral.infrastructure.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.common.infrastructure.food.NutrientsEntity

@Entity(tableName = "FoodDataCentralProduct", indices = [Index("gtinUpc")])
internal data class FoodDataCentralProductEntity(
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
