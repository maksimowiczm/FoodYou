package com.maksimowiczm.foodyou.fooddiary.infrastructure.room

import androidx.room3.*
import kotlin.uuid.Uuid

@Entity(tableName = "FoodDiaryEntryMeal", indices = [Index("mealId")])
data class FoodDiaryEntryMealEntity(
    @PrimaryKey val entryId: Uuid,
    val mealId: Uuid,
)
