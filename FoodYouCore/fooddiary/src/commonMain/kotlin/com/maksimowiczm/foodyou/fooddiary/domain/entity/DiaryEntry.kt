package com.maksimowiczm.foodyou.fooddiary.domain.entity

import com.maksimowiczm.foodyou.shared.domain.food.NutritionFacts
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
