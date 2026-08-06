package com.maksimowiczm.foodyou.fooddiary.infrastructure.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlin.uuid.Uuid

@Dao
interface FoodDiaryMealDao {
    @Query("SELECT entryId FROM FoodDiaryEntryMeal WHERE mealId = :mealId")
    suspend fun findEntriesByMeal(mealId: Uuid): List<Uuid>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMealReference(reference: FoodDiaryEntryMealEntity)

    @Query("DELETE FROM FoodDiaryEntryMeal WHERE entryId = :entryId")
    suspend fun deleteByEntryId(entryId: Uuid)
}
