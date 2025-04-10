package com.maksimowiczm.foodyou.feature.goals.database

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DiaryDayDao {
    @Query(
        """
        SELECT *
        FROM DiaryDayView d
        WHERE d.epochDay = :epochDay
        """
    )
    abstract fun observeDiaryDay(epochDay: Int): Flow<List<DiaryDayView>>
}
