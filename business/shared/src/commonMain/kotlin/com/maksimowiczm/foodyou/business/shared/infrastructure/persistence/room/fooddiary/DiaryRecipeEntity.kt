package com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.FoodSourceType

@Entity(tableName = "DiaryRecipe")
data class DiaryRecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val servings: Int,
    val isLiquid: Boolean,
    val sourceType: FoodSourceType?,
    val sourceUrl: String?,
    val note: String?,
)
