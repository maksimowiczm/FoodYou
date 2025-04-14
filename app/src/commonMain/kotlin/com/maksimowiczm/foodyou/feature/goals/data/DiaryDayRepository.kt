package com.maksimowiczm.foodyou.feature.goals.data

import com.maksimowiczm.foodyou.core.data.model.diaryday.DiaryDay as DbDiaryDay
import com.maksimowiczm.foodyou.core.data.source.DiaryDayLocalDataSource
import com.maksimowiczm.foodyou.core.mapper.MeasurementMapper
import com.maksimowiczm.foodyou.core.mapper.NutrientsMapper
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.PortionWeight
import com.maksimowiczm.foodyou.core.repository.GoalsRepository
import com.maksimowiczm.foodyou.feature.goals.model.DiaryDay
import com.maksimowiczm.foodyou.feature.goals.model.Food
import com.maksimowiczm.foodyou.feature.goals.model.Meal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

internal class DiaryDayRepository(
    private val goalsRepository: GoalsRepository,
    private val diaryDayDao: DiaryDayLocalDataSource
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeDiaryDay(date: LocalDate) = diaryDayDao
        .observeDiaryDay(epochDay = date.toEpochDays())
        .flatMapLatest { diaryDayView ->
            val foods = diaryDayView.toFoods()

            goalsRepository.observeDailyGoals().map {
                DiaryDay(
                    date = date,
                    foods = foods,
                    dailyGoals = it
                )
            }
        }
}

private fun List<DbDiaryDay>.toFoods(): Map<Meal, List<Food>> = groupBy {
    Meal(
        id = it.mealId,
        name = it.mealName
    )
}.mapValues { (_, list) ->
    list.map {
        val productId = it.productId
        val recipeId = it.recipeId

        val id = when {
            productId != null -> FoodId.Product(productId)
            recipeId != null -> FoodId.Recipe(recipeId)
            else -> error("Product ID and Recipe ID are both null")
        }

        Food(
            foodId = id,
            name = it.foodName,
            packageWeight = it.packageWeight?.let { PortionWeight.Package(it) },
            servingWeight = it.servingWeight?.let { PortionWeight.Serving(it) },
            nutrients = with(NutrientsMapper) { it.nutrients.toModel() },
            measurement = with(MeasurementMapper) { it.toMeasurement() }
        )
    }
}
