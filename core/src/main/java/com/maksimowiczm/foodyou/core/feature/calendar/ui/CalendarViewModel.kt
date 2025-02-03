package com.maksimowiczm.foodyou.core.feature.calendar.ui

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.core.feature.system.data.SystemInfoRepository
import com.maksimowiczm.foodyou.core.feature.system.data.TodayDateProvider
import java.time.LocalDate

class CalendarViewModel(
    private val systemInfoRepository: SystemInfoRepository,
    todayDateProvider: TodayDateProvider
) : ViewModel() {
    val today = todayDateProvider.observe()

    val weekDayNamesShort: List<String>
        get() = systemInfoRepository.weekDayNamesShort.toList()

    fun formatMonthYear(date: LocalDate): String = systemInfoRepository.formatMonthYear(date)
}
