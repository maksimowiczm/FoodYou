package com.maksimowiczm.foodyou.fooddiary.infrastructure.room

import androidx.room3.*
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import kotlin.uuid.Uuid

@Entity(
    tableName = "FoodDiaryEntryReference",
    primaryKeys = ["entryId", "snapshotId"],
    indices = [Index(value = ["snapshotId"])],
)
data class FoodDiaryEntryReferenceEntity(
    val entryId: Uuid,
    val snapshotId: FoodSnapshotId.Tracked,
)
