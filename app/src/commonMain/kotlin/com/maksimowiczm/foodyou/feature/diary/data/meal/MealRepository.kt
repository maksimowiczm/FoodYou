package com.maksimowiczm.foodyou.feature.diary.data.meal

import com.maksimowiczm.foodyou.feature.diary.database.DiaryDatabase
import com.maksimowiczm.foodyou.feature.diary.database.meal.MealDao
import com.maksimowiczm.foodyou.feature.diary.database.meal.MealEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalTime

internal interface MealRepository {
    fun observeMeals(): Flow<List<Meal>>

    suspend fun createMeal(name: String, from: LocalTime, to: LocalTime)

    suspend fun updateMeal(id: Long, name: String, from: LocalTime, to: LocalTime)

    suspend fun deleteMeal(id: Long)

    /**
     * Orders meals by their rank.
     *
     * @param map A map where the key is the meal ID and the value is the new rank.
     */
    suspend fun updateMealsRanks(map: Map<Long, Int>)
}

internal class MealRepositoryImpl(database: DiaryDatabase) : MealRepository {
    private val mealDao: MealDao = database.mealDao

    override fun observeMeals(): Flow<List<Meal>> = mealDao.observeMeals().map { list ->
        list.map { entity ->
            Meal(
                id = entity.id,
                name = entity.name,
                from = LocalTime(entity.fromHour, entity.fromMinute),
                to = LocalTime(entity.toHour, entity.toMinute),
                rank = entity.rank
            )
        }
    }

    override suspend fun createMeal(name: String, from: LocalTime, to: LocalTime) {
        val entity = MealEntity(
            name = name,
            fromHour = from.hour,
            fromMinute = from.minute,
            toHour = to.hour,
            toMinute = to.minute,
            rank = 0
        )

        mealDao.insertWithLastRank(entity)
    }

    override suspend fun updateMeal(id: Long, name: String, from: LocalTime, to: LocalTime) {
        val entity = mealDao.getMealById(id) ?: return

        val updatedEntity = entity.copy(
            name = name,
            fromHour = from.hour,
            fromMinute = from.minute,
            toHour = to.hour,
            toMinute = to.minute
        )

        mealDao.update(updatedEntity)
    }

    override suspend fun deleteMeal(id: Long) {
        val entity = mealDao.getMealById(id) ?: return
        mealDao.delete(entity)
    }

    override suspend fun updateMealsRanks(map: Map<Long, Int>) {
        mealDao.updateMealsRanks(map)
    }
}
