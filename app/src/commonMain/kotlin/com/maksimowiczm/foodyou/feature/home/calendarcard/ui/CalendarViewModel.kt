package com.maksimowiczm.foodyou.feature.home.calendarcard.ui

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.data.AndroidStringFormatRepository
import com.maksimowiczm.foodyou.data.DateProvider
import kotlinx.datetime.LocalDate

class CalendarViewModel(
    private val stringFormatRepository: AndroidStringFormatRepository,
    dateProvider: DateProvider
) : ViewModel() {
    val today = dateProvider.observeDate()

    val weekDayNamesShort: List<String>
        get() = stringFormatRepository.weekDayNamesShort.toList()

    fun formatMonthYear(date: LocalDate): String = stringFormatRepository.formatMonthYear(date)
}
