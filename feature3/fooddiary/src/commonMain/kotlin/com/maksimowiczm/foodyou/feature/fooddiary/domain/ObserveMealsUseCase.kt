package com.maksimowiczm.foodyou.feature.fooddiary.domain

import com.maksimowiczm.foodyou.feature.fooddiary.data.FoodDiaryDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

internal interface ObserveMealsUseCase {
    operator fun invoke(date: LocalDate): Flow<List<Meal>>
}

internal class ObserveMealsUseCaseImpl(
    foodDiaryDatabase: FoodDiaryDatabase,
    private val foodMapper: FoodMapper
) : ObserveMealsUseCase {
    private val mealDao = foodDiaryDatabase.mealDao
    private val measurementDao = foodDiaryDatabase.measurementDao

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(date: LocalDate): Flow<List<Meal>> {
        return mealDao.observeMeals().flatMapLatest { meals ->
            if (meals.isEmpty()) {
                return@flatMapLatest flowOf(emptyList())
            }

            meals.map { meal ->
                measurementDao.observeFoodWithMeasurement(
                    mealId = meal.id,
                    epochDay = date.toEpochDays()
                ).map { food ->
                    val from = LocalTime(meal.fromHour, meal.fromMinute)
                    val to = LocalTime(meal.toHour, meal.toMinute)

                    Meal(
                        id = meal.id,
                        name = meal.name,
                        from = from,
                        to = to,
                        rank = meal.rank,
                        food = food.map(foodMapper::toFoodWithMeasurement)
                    )
                }
            }.combine { it.toList() }
        }
    }
}
