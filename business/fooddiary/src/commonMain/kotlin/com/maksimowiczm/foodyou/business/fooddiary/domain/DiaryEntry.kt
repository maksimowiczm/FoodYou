package com.maksimowiczm.foodyou.business.fooddiary.domain

import com.maksimowiczm.foodyou.business.shared.domain.measurement.Measurement
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class DiaryEntry(
    val id: Long,
    val mealId: Long,
    val date: LocalDate,
    val measurement: Measurement,
    val food: DiaryFood,
    val createdAt: LocalDateTime,
)
