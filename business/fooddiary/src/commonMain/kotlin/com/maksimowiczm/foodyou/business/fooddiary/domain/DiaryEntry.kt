package com.maksimowiczm.foodyou.business.fooddiary.domain

import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

sealed interface DiaryEntry {
    val mealId: Long
    val date: LocalDate
    val name: String
    val nutritionFacts: NutritionFacts
    val createdAt: LocalDateTime
    val updatedAt: LocalDateTime
}
