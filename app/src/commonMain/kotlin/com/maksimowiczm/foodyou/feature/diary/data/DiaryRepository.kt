package com.maksimowiczm.foodyou.feature.diary.data

import com.maksimowiczm.foodyou.feature.diary.database.DiaryDatabase
import com.maksimowiczm.foodyou.feature.diary.database.entity.MealEntity
import com.maksimowiczm.foodyou.feature.diary.domain.ObserveMealsByDateUseCase
import com.maksimowiczm.foodyou.feature.diary.domain.ObserveMealsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class DiaryRepository(diaryDatabase: DiaryDatabase) :
    ObserveMealsByDateUseCase,
    ObserveMealsUseCase,
    MealRepository {
    val mealsDao = diaryDatabase.mealsDao

    override fun observeMealsByDate(date: LocalDate): Flow<List<ObserveMealsByDateUseCase.Meal>> =
        mealsDao.observeMeals().map {
            it.map { it.toMealByDate() }
        }

    override fun observeMeals(): Flow<List<ObserveMealsUseCase.Meal>> =
        mealsDao.observeMeals().map {
            it.map { it.toMeal() }
        }

    override suspend fun createMeal(name: String, from: LocalTime, to: LocalTime) {
        mealsDao.insertWithLastRank(
            MealEntity(
                name = name,
                fromHour = from.hour,
                fromMinute = from.minute,
                toHour = to.hour,
                toMinute = to.minute,
                rank = -1
            )
        )
    }

    override suspend fun updateMeal(meal: ObserveMealsUseCase.Meal) {
        mealsDao.updateMeal(meal.toEntity())
    }

    override suspend fun deleteMeal(meal: ObserveMealsUseCase.Meal) {
        mealsDao.deleteMeal(meal.toEntity())
    }

    override suspend fun updateMealsRanks(map: Map<Long, Int>) {
        mealsDao.updateMealsRanks(map)
    }
}

private fun MealEntity.toMealByDate() = ObserveMealsByDateUseCase.Meal(
    id = id,
    name = name,
    from = LocalTime(fromHour, fromMinute),
    to = LocalTime(toHour, toMinute),
    rank = rank,
    calories = 0,
    proteins = 0,
    carbohydrates = 0,
    fats = 0,
    isEmpty = true
)

private fun MealEntity.toMeal() = ObserveMealsUseCase.Meal(
    id = id,
    name = name,
    from = LocalTime(fromHour, fromMinute),
    to = LocalTime(toHour, toMinute),
    rank = rank
)

fun ObserveMealsUseCase.Meal.toEntity() = MealEntity(
    id = id,
    name = name,
    fromHour = from.hour,
    fromMinute = from.minute,
    toHour = to.hour,
    toMinute = to.minute,
    rank = rank
)
