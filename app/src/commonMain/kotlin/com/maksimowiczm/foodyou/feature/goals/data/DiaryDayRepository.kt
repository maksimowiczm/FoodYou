package com.maksimowiczm.foodyou.feature.goals.data

import com.maksimowiczm.foodyou.core.database.FoodYouDatabase
import com.maksimowiczm.foodyou.core.database.goals.DiaryDayDao
import com.maksimowiczm.foodyou.core.database.goals.DiaryDayView
import com.maksimowiczm.foodyou.core.database.measurement.Measurement as MeasurementEntity
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.model.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.core.model.Nutrients
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
    database: FoodYouDatabase,
    private val goalsRepository: GoalsRepository
) {
    val diaryDayDao: DiaryDayDao = database.diaryDayDao

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

private fun List<DiaryDayView>.toFoods(): Map<Meal, List<Food>> = groupBy {
    Meal(
        id = it.mealId,
        name = it.mealName
    )
}.mapValues { (_, list) ->
    list.map {
        val measurement = when (it.measurement) {
            MeasurementEntity.Gram -> Measurement.Gram(it.quantity)
            MeasurementEntity.Package -> Measurement.Package(it.quantity)
            MeasurementEntity.Serving -> Measurement.Serving(it.quantity)
        }

        val id = when {
            it.productId != null -> FoodId.Product(it.productId)
            it.recipeId != null -> FoodId.Recipe(it.recipeId)
            else -> error("Product ID and Recipe ID are both null")
        }

        Food(
            foodId = id,
            name = it.foodName,
            packageWeight = it.packageWeight?.let { PortionWeight.Package(it) },
            servingWeight = it.servingWeight?.let { PortionWeight.Serving(it) },
            nutrients = Nutrients(
                calories = it.nutrients.calories.toNutrientValue(),
                proteins = it.nutrients.proteins.toNutrientValue(),
                carbohydrates = it.nutrients.carbohydrates.toNutrientValue(),
                sugars = it.nutrients.sugars.toNutrientValue(),
                fats = it.nutrients.fats.toNutrientValue(),
                saturatedFats = it.nutrients.saturatedFats.toNutrientValue(),
                salt = it.nutrients.salt.toNutrientValue(),
                sodium = it.nutrients.sodium.toNutrientValue(),
                fiber = it.nutrients.fiber.toNutrientValue()
            ),
            measurement = measurement
        )
    }
}
