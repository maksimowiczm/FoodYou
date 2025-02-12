package com.maksimowiczm.foodyou.core.feature.diary.ui.mealscard

import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.feature.diary.data.DiaryRepository
import com.maksimowiczm.foodyou.core.feature.diary.ui.DiaryViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class MealsCardViewModel(
    diaryRepository: DiaryRepository
) : DiaryViewModel(
    diaryRepository
) {
    /**
     * Flow that emits the current time every minute.
     */
    val time = flow {
        while (true) {
            emit(getCurrentTime())
            delay(60_000)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = getCurrentTime()
    )

    private fun getCurrentTime(): LocalTime {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
    }
}
