package com.maksimowiczm.foodyou.core.database.food

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val servings: Int,
    val isLiquid: Boolean,
    val note: String?
)
