package com.maksimowiczm.foodyou.app.infrastructure.room.food

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Recipe")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val servings: Int,
    val note: String?,
    val isLiquid: Boolean,
)
