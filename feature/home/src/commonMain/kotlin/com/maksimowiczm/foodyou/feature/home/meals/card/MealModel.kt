package com.maksimowiczm.foodyou.feature.home.meals.card

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.core.fooddiary.domain.entity.FoodDiaryEntryId
import com.maksimowiczm.foodyou.core.fooddiary.domain.entity.ManualDiaryEntryId
import com.maksimowiczm.foodyou.core.shared.measurement.Measurement
import kotlinx.datetime.LocalTime

@Immutable
internal data class MealModel(
    val id: Long,
    val name: String,
    val from: LocalTime,
    val to: LocalTime,
    val isAllDay: Boolean,
    val foods: List<MealEntryModel>,
    val energy: Int,
    val proteins: Double,
    val carbohydrates: Double,
    val fats: Double,
)

@Immutable
internal sealed interface MealEntryModel {
    val name: String
    val energy: Int?
    val proteins: Double?
    val carbohydrates: Double?
    val fats: Double?
}

@Immutable
internal data class FoodMealEntryModel(
    val id: FoodDiaryEntryId,
    override val name: String,
    override val energy: Int?,
    override val proteins: Double?,
    override val carbohydrates: Double?,
    override val fats: Double?,
    val measurement: Measurement,
    val weight: Double?,
    val isLiquid: Boolean,
    val isRecipe: Boolean,
    val servingWeight: Double?,
    val totalWeight: Double?,
) : MealEntryModel

@Immutable
internal data class ManualMealEntryModel(
    val id: ManualDiaryEntryId,
    override val name: String,
    override val energy: Int?,
    override val proteins: Double?,
    override val carbohydrates: Double?,
    override val fats: Double?,
) : MealEntryModel
