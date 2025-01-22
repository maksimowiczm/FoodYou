package com.maksimowiczm.foodyou.feature.addfood.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.feature.addfood.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.feature.product.database.ProductEntity

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["productId"]),
        Index(value = ["isDeleted"])
    ]
)
data class WeightMeasurementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val mealId: MealId,
    val diaryEpochDay: Long,
    val productId: Long,

    val createdAt: Long,
    val measurement: WeightMeasurementEnum,
    val quantity: Float,
    val isDeleted: Boolean = false
)
