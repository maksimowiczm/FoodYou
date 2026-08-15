package com.maksimowiczm.foodyou.features.home.integration

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponent
import kotlin.uuid.Uuid
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@Entity(tableName = "HomeEntry")
data class HomeEntryEntity(
    @PrimaryKey val entryId: Uuid,
    val profileId: Uuid,
    val mealId: Uuid?,
    val date: LocalDate,
    val time: LocalTime,
    val composition: FoodCompositionComponent,
)
