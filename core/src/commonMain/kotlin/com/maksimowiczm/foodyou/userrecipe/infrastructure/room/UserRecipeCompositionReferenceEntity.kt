package com.maksimowiczm.foodyou.userrecipe.infrastructure.room

import androidx.room3.*
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import kotlin.uuid.Uuid

@Entity(
    tableName = "UserRecipeCompositionReference",
    primaryKeys = ["recipeId", "snapshotId"],
    indices = [Index(value = ["snapshotId"])],
)
data class UserRecipeCompositionReferenceEntity(
    val recipeId: Uuid,
    val snapshotId: FoodSnapshotId.Tracked,
)
