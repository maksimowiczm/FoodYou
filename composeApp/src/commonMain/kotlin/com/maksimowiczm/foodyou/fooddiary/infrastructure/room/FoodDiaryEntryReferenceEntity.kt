package com.maksimowiczm.foodyou.fooddiary.infrastructure.room

import androidx.room.Entity
import androidx.room.Index
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import kotlin.uuid.Uuid

@Entity(
    tableName = "FoodDiaryEntryReference",
    primaryKeys = ["entryId", "componentIdentity"],
    indices = [Index(value = ["componentIdentity"])],
)
data class FoodDiaryEntryReferenceEntity(
    val entryId: Uuid,
    val componentIdentity: FoodCompositionComponentIdentity.Identified,
)
