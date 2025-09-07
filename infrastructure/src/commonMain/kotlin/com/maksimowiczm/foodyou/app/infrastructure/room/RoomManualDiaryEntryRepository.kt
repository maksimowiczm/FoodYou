package com.maksimowiczm.foodyou.app.infrastructure.room

import com.maksimowiczm.foodyou.app.infrastructure.room.fooddiary.ManualDiaryEntryDao
import com.maksimowiczm.foodyou.app.infrastructure.room.fooddiary.ManualDiaryEntryEntity
import com.maksimowiczm.foodyou.app.infrastructure.room.shared.toEntityNutrients
import com.maksimowiczm.foodyou.app.infrastructure.room.shared.toNutritionFacts
import com.maksimowiczm.foodyou.fooddiary.domain.entity.ManualDiaryEntry
import com.maksimowiczm.foodyou.fooddiary.domain.entity.ManualDiaryEntryId
import com.maksimowiczm.foodyou.fooddiary.domain.repository.ManualDiaryEntryRepository
import com.maksimowiczm.foodyou.shared.domain.food.NutritionFacts
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapIfNotNull
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

internal class RoomManualDiaryEntryRepository(private val dao: ManualDiaryEntryDao) :
    ManualDiaryEntryRepository {
    override fun observe(id: ManualDiaryEntryId): Flow<ManualDiaryEntry?> =
        dao.observe(id.value).mapIfNotNull(ManualDiaryEntryEntity::toModel)

    override fun observeAll(mealId: Long, date: LocalDate): Flow<List<ManualDiaryEntry>> =
        dao.observeAll(mealId, date.toEpochDays()).map { list ->
            list.map(ManualDiaryEntryEntity::toModel)
        }

    override suspend fun insert(
        name: String,
        mealId: Long,
        date: LocalDate,
        nutritionFacts: NutritionFacts,
        createdAt: LocalDateTime,
    ): ManualDiaryEntryId {
        val entry =
            ManualDiaryEntry(
                id = ManualDiaryEntryId(0),
                mealId = mealId,
                date = date,
                name = name,
                nutritionFacts = nutritionFacts,
                createdAt = createdAt,
                updatedAt = createdAt,
            )
        val id = dao.insert(entry.toEntity())
        return ManualDiaryEntryId(id)
    }

    override suspend fun update(entry: ManualDiaryEntry) = dao.update(entry.toEntity())

    override suspend fun delete(id: ManualDiaryEntryId) = dao.delete(id.value)
}

@OptIn(ExperimentalTime::class)
private fun ManualDiaryEntryEntity.toModel(): ManualDiaryEntry =
    ManualDiaryEntry(
        id = ManualDiaryEntryId(id),
        mealId = mealId,
        date = LocalDate.fromEpochDays(dateEpochDay.toInt()),
        name = name,
        nutritionFacts =
            toNutritionFacts(nutrients = nutrients, vitamins = vitamins, minerals = minerals),
        createdAt =
            Instant.fromEpochSeconds(createdEpochSeconds)
                .toLocalDateTime(TimeZone.currentSystemDefault()),
        updatedAt =
            Instant.fromEpochSeconds(updatedEpochSeconds)
                .toLocalDateTime(TimeZone.currentSystemDefault()),
    )

@OptIn(ExperimentalTime::class)
private fun ManualDiaryEntry.toEntity(): ManualDiaryEntryEntity {
    val (nutrients, vitamins, minerals) = toEntityNutrients(nutritionFacts)

    return ManualDiaryEntryEntity(
        id = id.value,
        mealId = mealId,
        dateEpochDay = date.toEpochDays(),
        name = name,
        nutrients = nutrients,
        vitamins = vitamins,
        minerals = minerals,
        createdEpochSeconds = createdAt.toInstant(TimeZone.currentSystemDefault()).epochSeconds,
        updatedEpochSeconds = updatedAt.toInstant(TimeZone.currentSystemDefault()).epochSeconds,
    )
}
