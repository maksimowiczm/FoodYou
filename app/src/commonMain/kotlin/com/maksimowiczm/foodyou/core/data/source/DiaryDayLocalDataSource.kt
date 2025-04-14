package com.maksimowiczm.foodyou.core.data.source

import com.maksimowiczm.foodyou.core.data.model.diaryday.DiaryDay
import kotlinx.coroutines.flow.Flow

interface DiaryDayLocalDataSource {
    fun observeDiaryDay(epochDay: Int): Flow<List<DiaryDay>>
}
