package com.maksimowiczm.foodyou.core.data.database.goals

import androidx.room.Dao
import androidx.room.Query
import com.maksimowiczm.foodyou.core.domain.source.DiaryDayLocalDataSource
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DiaryDayDao : DiaryDayLocalDataSource {
    @Query(
        """
        SELECT *
        FROM DiaryDayView d
        WHERE d.epochDay = :epochDay
        """
    )
    abstract override fun observeDiaryDay(epochDay: Int): Flow<List<DiaryDayView>>
}
