package com.maksimowiczm.foodyou.userrecipe.infrastructure.room

import androidx.room.Entity
import androidx.room.Index
import kotlin.uuid.Uuid

@Entity(
    tableName = "UserRecipeCompositionReference",
    primaryKeys = ["recipeId", "componentType", "componentValue"],
    indices = [Index(value = ["componentType", "componentValue"])],
)
data class UserRecipeCompositionReferenceEntity(
    val recipeId: Uuid,
    val componentType: String,
    val componentValue: String,
)
