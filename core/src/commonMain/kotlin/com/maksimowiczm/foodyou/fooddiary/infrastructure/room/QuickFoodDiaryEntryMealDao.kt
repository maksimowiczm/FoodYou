package com.maksimowiczm.foodyou.fooddiary.infrastructure.room

import androidx.room3.*
import kotlin.uuid.Uuid

@Dao
interface QuickFoodDiaryEntryMealDao {
    @Query("SELECT entryId FROM QuickFoodDiaryEntryMeal WHERE mealId = :mealId")
    suspend fun findEntriesByMeal(mealId: Uuid): List<Uuid>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMealReference(reference: QuickFoodDiaryEntryMealEntity)

    @Query("DELETE FROM QuickFoodDiaryEntryMeal WHERE entryId = :entryId")
    suspend fun deleteByEntryId(entryId: Uuid)
}
