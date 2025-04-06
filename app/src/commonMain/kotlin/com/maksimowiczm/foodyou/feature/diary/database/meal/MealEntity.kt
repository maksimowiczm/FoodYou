package com.maksimowiczm.foodyou.feature.diary.database.meal

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val fromHour: Int,
    val fromMinute: Int,
    val toHour: Int,
    val toMinute: Int,
    val rank: Int
)
