package com.maksimowiczm.foodyou.features.home.integration

import androidx.room3.*
import kotlin.uuid.Uuid
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@Entity(
    tableName = "HomeEntry",
    primaryKeys = ["entryId", "profileId"],
)
data class HomeEntryEntity(
    val entryId: Uuid,
    val profileId: Uuid,
    val mealId: Uuid?,
    val date: LocalDate,
    val time: LocalTime,
    val payload: HomeEntryPayload,
)
