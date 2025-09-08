package com.maksimowiczm.foodyou.fooddiary.domain.entity

import com.maksimowiczm.foodyou.shared.domain.food.NutritionFacts
import kotlin.jvm.JvmInline
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

@JvmInline value class ManualDiaryEntryId(val value: Long)

/**
 * Represents a manually added diary entry. When a user adds a entry without referring to any food
 * item.
 *
 * @param id The unique identifier of the manual diary entry.
 * @param mealId The identifier of the meal to which this entry belongs.
 * @param date The date of the diary entry.
 * @param nutritionFacts The nutrition facts for the food item based on the weight.
 * @param createdAt The timestamp when the entry was created.
 * @param updatedAt The timestamp when the entry was last updated.
 */
data class ManualDiaryEntry(
    val id: ManualDiaryEntryId,
    override val mealId: Long,
    override val date: LocalDate,
    override val name: String,
    override val nutritionFacts: NutritionFacts,
    override val createdAt: LocalDateTime,
    override val updatedAt: LocalDateTime,
) : DiaryEntry
