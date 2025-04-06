package com.maksimowiczm.foodyou.feature.calendar

import com.maksimowiczm.foodyou.feature.calendar.ui.CalendarViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val calendarModule = module {
    viewModelOf(::CalendarViewModel)
}
