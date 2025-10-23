package com.maksimowiczm.foodyou.food.infrastructure.usda.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "FoodDataCentralPagingKey",
    indices = [Index(value = ["queryString"]), Index(value = ["fdcId"])],
    foreignKeys =
        [
            ForeignKey(
                entity = FoodDataCentralProductEntity::class,
                parentColumns = ["fdcId"],
                childColumns = ["fdcId"],
                onDelete = ForeignKey.CASCADE,
            )
        ],
)
data class FoodDataCentralPagingKeyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val queryString: String,
    val fdcId: Int,
)
