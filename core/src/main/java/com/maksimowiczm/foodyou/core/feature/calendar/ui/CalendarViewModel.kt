package com.maksimowiczm.foodyou.core.feature.calendar.ui

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.core.feature.system.data.DateProvider
import com.maksimowiczm.foodyou.core.feature.system.data.SystemInfoRepository
import kotlinx.datetime.LocalDate

class CalendarViewModel(
    private val systemInfoRepository: SystemInfoRepository,
    dateProvider: DateProvider
) : ViewModel() {
    val today = dateProvider.observe()

    val weekDayNamesShort: List<String>
        get() = systemInfoRepository.weekDayNamesShort.toList()

    fun formatMonthYear(date: LocalDate): String = systemInfoRepository.formatMonthYear(date)
}
