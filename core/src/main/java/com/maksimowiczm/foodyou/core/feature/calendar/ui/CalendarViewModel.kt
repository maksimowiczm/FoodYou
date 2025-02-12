package com.maksimowiczm.foodyou.core.feature.calendar.ui

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.core.feature.system.data.AndroidStringFormatRepository
import com.maksimowiczm.foodyou.core.feature.system.data.DateProvider
import kotlinx.datetime.LocalDate

class CalendarViewModel(
    private val stringFormatRepository: AndroidStringFormatRepository,
    dateProvider: DateProvider
) : ViewModel() {
    val today = dateProvider.observe()

    val weekDayNamesShort: List<String>
        get() = stringFormatRepository.weekDayNamesShort.toList()

    fun formatMonthYear(date: LocalDate): String = stringFormatRepository.formatMonthYear(date)
}
