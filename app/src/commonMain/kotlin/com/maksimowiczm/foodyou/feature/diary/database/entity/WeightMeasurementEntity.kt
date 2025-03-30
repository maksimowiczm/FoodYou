package com.maksimowiczm.foodyou.feature.diary.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurementEnum

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MealEntity::class,
            parentColumns = ["id"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["productId"]),
        Index(value = ["isDeleted"]),
        Index(value = ["mealId"])
    ]
)
data class WeightMeasurementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val mealId: Long,
    val diaryEpochDay: Int,
    val productId: Long,

    val createdAt: Long,
    val measurement: WeightMeasurementEnum,
    val quantity: Float,
    val isDeleted: Boolean = false
)
