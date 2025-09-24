package com.maksimowiczm.foodyou.fooddiary.domain.repository

import com.maksimowiczm.foodyou.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.fooddiary.domain.entity.DiaryFood
import com.maksimowiczm.foodyou.fooddiary.domain.entity.FoodDiaryEntry
import com.maksimowiczm.foodyou.fooddiary.domain.entity.FoodDiaryEntryId
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

interface FoodDiaryEntryRepository {
    fun observe(id: FoodDiaryEntryId): Flow<FoodDiaryEntry?>

    fun observeAll(mealId: Long, date: LocalDate): Flow<List<FoodDiaryEntry>>

    suspend fun insert(
        measurement: Measurement,
        mealId: Long,
        date: LocalDate,
        food: DiaryFood,
        createdAt: LocalDateTime,
    ): FoodDiaryEntryId

    suspend fun update(entry: FoodDiaryEntry)

    suspend fun delete(id: FoodDiaryEntryId)
}
