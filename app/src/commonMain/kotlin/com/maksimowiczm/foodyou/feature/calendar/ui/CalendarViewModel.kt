package com.maksimowiczm.foodyou.feature.calendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.util.DateFormatter
import com.maksimowiczm.foodyou.core.util.DateProvider
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

internal class CalendarViewModel(
    private val dateFormatter: DateFormatter,
    dateProvider: DateProvider
) : ViewModel() {
    val today = dateProvider.observeDate().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    )

    val weekDayNamesShort: List<String>
        get() = dateFormatter.weekDayNamesShort.toList()

    fun formatMonthYear(date: LocalDate): String = dateFormatter.formatMonthYear(date)
}
