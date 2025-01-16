package com.maksimowiczm.foodyou.feature.diary.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["mealId", "diaryEpochDay", "productId"],
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["productId"])
    ]
)
data class MealProductEntity(
    val mealId: MealId,
    val diaryEpochDay: Long,
    val productId: Long,

    val weight: Float
)
