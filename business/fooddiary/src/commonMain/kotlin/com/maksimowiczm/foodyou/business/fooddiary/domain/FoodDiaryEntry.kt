package com.maksimowiczm.foodyou.business.fooddiary.domain

import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import kotlin.jvm.JvmInline
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

@JvmInline value class FoodDiaryEntryId(val value: Long)

/**
 * Represents a food diary entry.
 *
 * @param id The unique identifier of the food diary entry.
 * @param mealId The identifier of the meal to which this entry belongs.
 * @param date The date of the diary entry.
 * @param measurement The measurement details for the food item.
 * @param food The food item associated with this diary entry.
 * @param createdAt The timestamp when the entry was created.
 * @param updatedAt The timestamp when the entry was last updated.
 */
data class FoodDiaryEntry(
    val id: FoodDiaryEntryId,
    override val mealId: Long,
    override val date: LocalDate,
    val measurement: Measurement,
    val food: DiaryFood,
    override val createdAt: LocalDateTime,
    override val updatedAt: LocalDateTime,
) : DiaryEntry {
    override val name: String = food.name

    val weight: Double
        get() = food.weight(measurement)

    /**
     * Total nutrition facts for the entry based on the food's nutrition facts and the weight of the
     * measurement.
     */
    override val nutritionFacts: NutritionFacts by lazy { food.nutritionFacts * (weight / 100) }
}
