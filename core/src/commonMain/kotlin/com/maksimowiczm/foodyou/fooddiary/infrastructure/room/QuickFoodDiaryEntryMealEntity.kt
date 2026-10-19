package com.maksimowiczm.foodyou.fooddiary.infrastructure.room

import androidx.room3.*
import kotlin.uuid.Uuid

@Entity(tableName = "QuickFoodDiaryEntryMeal", indices = [Index("mealId")])
data class QuickFoodDiaryEntryMealEntity(
    @PrimaryKey val entryId: Uuid,
    val mealId: Uuid,
)
