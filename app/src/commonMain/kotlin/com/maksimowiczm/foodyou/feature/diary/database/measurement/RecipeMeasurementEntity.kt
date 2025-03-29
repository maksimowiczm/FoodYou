package com.maksimowiczm.foodyou.feature.diary.database.measurement

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.feature.diary.database.entity.RecipeEntity

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["recipeId"]),
        Index(value = ["mealId"]),
        Index(value = ["diaryEpochDay"])
    ]
)
data class RecipeMeasurementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val mealId: Long,
    val diaryEpochDay: Int,
    val recipeId: Long,

    val createdAt: Long,
    val measurement: WeightMeasurementEnum,
    val quantity: Float,
    val isDeleted: Boolean = false
)
