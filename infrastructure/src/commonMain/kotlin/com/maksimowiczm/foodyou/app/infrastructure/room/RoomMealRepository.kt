package com.maksimowiczm.foodyou.app.infrastructure.room

import com.maksimowiczm.foodyou.app.infrastructure.room.fooddiary.MealDao
import com.maksimowiczm.foodyou.app.infrastructure.room.fooddiary.MealEntity
import com.maksimowiczm.foodyou.fooddiary.domain.entity.Meal
import com.maksimowiczm.foodyou.fooddiary.domain.repository.MealRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalTime

internal class RoomMealRepository(private val mealDao: MealDao) : MealRepository {
    override fun observeMeal(mealId: Long): Flow<Meal?> =
        mealDao.observeMealById(mealId).map { it?.toModel() }

    override fun observeMeals(): Flow<List<Meal>> =
        mealDao.observeMeals().map { meals -> meals.map { it.toModel() } }

    override suspend fun insertMealWithLastRank(name: String, from: LocalTime, to: LocalTime) {
        val meal = Meal(id = 0, name = name, from = from, to = to, rank = 0)
        mealDao.insertWithLastRank(meal.toEntity())
    }

    override suspend fun updateMeal(id: Long, name: String, from: LocalTime, to: LocalTime) {
        mealDao.updateMealIgnoreRank(
            id = id,
            name = name,
            fromHour = from.hour,
            fromMinute = from.minute,
            toHour = to.hour,
            toMinute = to.minute,
        )
    }

    override suspend fun deleteMeal(mealId: Long) {
        mealDao.delete(mealId)
    }

    override suspend fun reorderMeals(order: List<Long>) {
        val rankMap = order.withIndex().associate { (index, id) -> id to index }
        mealDao.updateMealsRanks(rankMap)
    }
}

private fun MealEntity.toModel(): Meal {
    val from = LocalTime(fromHour, fromMinute)
    val to = LocalTime(toHour, toMinute)

    return Meal(id = id, name = name, from = from, to = to, rank = rank)
}

private fun Meal.toEntity(): MealEntity =
    MealEntity(
        id = id,
        name = name,
        fromHour = from.hour,
        fromMinute = from.minute,
        toHour = to.hour,
        toMinute = to.minute,
        rank = rank,
    )
