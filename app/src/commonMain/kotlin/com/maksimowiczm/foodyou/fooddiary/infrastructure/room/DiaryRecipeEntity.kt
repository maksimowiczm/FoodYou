package com.maksimowiczm.foodyou.fooddiary.infrastructure.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DiaryRecipe")
data class DiaryRecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val servings: Int,
    val isLiquid: Boolean,
    val note: String?,
)
