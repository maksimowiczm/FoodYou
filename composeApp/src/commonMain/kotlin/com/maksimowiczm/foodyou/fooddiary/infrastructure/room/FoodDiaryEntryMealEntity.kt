package com.maksimowiczm.foodyou.fooddiary.infrastructure.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.uuid.Uuid

@Entity(tableName = "FoodDiaryEntryMeal", indices = [Index("mealId")])
data class FoodDiaryEntryMealEntity(
    @PrimaryKey val entryId: Uuid,
    val mealId: Uuid,
)
