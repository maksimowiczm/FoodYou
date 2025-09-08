package com.maksimowiczm.foodyou.fooddiary.domain.repository

import com.maksimowiczm.foodyou.fooddiary.domain.entity.ManualDiaryEntry
import com.maksimowiczm.foodyou.fooddiary.domain.entity.ManualDiaryEntryId
import com.maksimowiczm.foodyou.shared.domain.food.NutritionFacts
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

interface ManualDiaryEntryRepository {
    fun observe(id: ManualDiaryEntryId): Flow<ManualDiaryEntry?>

    fun observeAll(mealId: Long, date: LocalDate): Flow<List<ManualDiaryEntry>>

    suspend fun insert(
        name: String,
        mealId: Long,
        date: LocalDate,
        nutritionFacts: NutritionFacts,
        createdAt: LocalDateTime,
    ): ManualDiaryEntryId

    suspend fun update(entry: ManualDiaryEntry)

    suspend fun delete(id: ManualDiaryEntryId)
}
