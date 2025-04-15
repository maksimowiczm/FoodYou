package com.maksimowiczm.foodyou.core.domain.source

import com.maksimowiczm.foodyou.core.data.model.diaryday.DiaryDay
import kotlinx.coroutines.flow.Flow

interface DiaryDayLocalDataSource {
    fun observeDiaryDay(epochDay: Int): Flow<List<DiaryDay>>
}
