package com.maksimowiczm.foodyou.userrecipe.infrastructure.room

import androidx.room.Entity
import androidx.room.Index
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import kotlin.uuid.Uuid

@Entity(
    tableName = "UserRecipeCompositionReference",
    primaryKeys = ["recipeId", "componentIdentity"],
    indices = [Index(value = ["componentIdentity"])],
)
data class UserRecipeCompositionReferenceEntity(
    val recipeId: Uuid,
    val componentIdentity: FoodCompositionComponentIdentity.Identified,
)
