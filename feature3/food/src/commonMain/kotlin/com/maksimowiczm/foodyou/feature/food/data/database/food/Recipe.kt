package com.maksimowiczm.foodyou.feature.food.data.database.food

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val servings: Int,
    val note: String?,
    val isLiquid: Boolean
)
