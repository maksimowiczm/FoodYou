package com.maksimowiczm.foodyou.fooddiary.infrastructure.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ManualDiaryEntryDao {

    @Query("SELECT * FROM ManualDiaryEntry WHERE id = :id")
    fun observe(id: Long): Flow<ManualDiaryEntryEntity?>

    @Query(
        """
            SELECT * 
            FROM ManualDiaryEntry 
            WHERE mealId = :mealId AND dateEpochDay = :dateEpochDay
        """
    )
    fun observeAll(mealId: Long, dateEpochDay: Long): Flow<List<ManualDiaryEntryEntity>>

    @Insert suspend fun insert(entry: ManualDiaryEntryEntity): Long

    @Update suspend fun update(entry: ManualDiaryEntryEntity)

    @Query("DELETE FROM ManualDiaryEntry WHERE id = :id") suspend fun delete(id: Long)
}
