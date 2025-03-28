package com.maksimowiczm.foodyou.feature.diary.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RecipeMeasurementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val mealId: Long,
    val diaryEpochDay: Int,
    val recipeId: Long,

    val createdAt: Long,
    val measurement: String,
    val quantity: Float,
    val isDeleted: Boolean = false
)
