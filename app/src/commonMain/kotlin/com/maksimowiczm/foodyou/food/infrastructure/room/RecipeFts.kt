package com.maksimowiczm.foodyou.food.infrastructure.room

import androidx.room.Entity
import androidx.room.Fts4

@Fts4(contentEntity = RecipeEntity::class)
@Entity(tableName = "RecipeFts")
data class RecipeFts(val name: String, val note: String?)
