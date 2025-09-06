package com.maksimowiczm.foodyou.app.infrastructure.room.fooddiary

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Meal")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val fromHour: Int,
    val fromMinute: Int,
    val toHour: Int,
    val toMinute: Int,
    val rank: Int = 0,
)
