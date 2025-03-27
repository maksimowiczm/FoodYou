package com.maksimowiczm.foodyou.feature.diary.data

import com.maksimowiczm.foodyou.feature.diary.database.DiaryDatabase
import com.maksimowiczm.foodyou.feature.diary.database.entity.MealEntity
import com.maksimowiczm.foodyou.feature.diary.domain.Meal
import com.maksimowiczm.foodyou.feature.diary.domain.ObserveMealsByDateUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class DiaryRepository(diaryDatabase: DiaryDatabase) : ObserveMealsByDateUseCase {
    val mealsDao = diaryDatabase.mealsDao

    override fun observeMealsByDate(date: LocalDate): Flow<List<Meal>> =
        mealsDao.observeMeals().map {
            it.map { it.toMeal() }
        }
}

private fun MealEntity.toMeal() = Meal(
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
