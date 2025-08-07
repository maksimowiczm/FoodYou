package com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.room

import com.maksimowiczm.foodyou.business.fooddiary.domain.Meal
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalMealDataSource
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.MealDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.MealEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalTime

internal class RoomMealDataSource(private val mealDao: MealDao) : LocalMealDataSource {

    override fun observeAllMeals(): Flow<List<Meal>> =
        mealDao.observeMeals().map { meals -> meals.map { it.toModel() } }

    override fun observeMealById(mealId: Long): Flow<Meal?> =
        mealDao.observeMealById(mealId).map { it?.toModel() }

    override suspend fun insertWithLastRank(meal: Meal) {
        mealDao.insertWithLastRank(meal.toEntity())
    }

    override suspend fun update(meal: Meal) {
        mealDao.updateMeal(meal.toEntity())
    }

    override suspend fun delete(meal: Meal) {
        mealDao.deleteMeal(meal.toEntity())
    }

    override suspend fun reorder(order: List<Long>) {
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
