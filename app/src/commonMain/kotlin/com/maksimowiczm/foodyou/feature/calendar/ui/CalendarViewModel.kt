package com.maksimowiczm.foodyou.feature.calendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.data.DateProvider
import com.maksimowiczm.foodyou.core.data.StringFormatRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

internal class CalendarViewModel(
    private val stringFormatRepository: StringFormatRepository,
    dateProvider: DateProvider
) : ViewModel() {
    val today = dateProvider.observeDate().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    )

    val weekDayNamesShort: List<String>
        get() = stringFormatRepository.weekDayNamesShort.toList()

    fun formatMonthYear(date: LocalDate): String = stringFormatRepository.formatMonthYear(date)
}
