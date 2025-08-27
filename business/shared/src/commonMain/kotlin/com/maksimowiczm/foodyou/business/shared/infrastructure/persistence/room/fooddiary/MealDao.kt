package com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
abstract class MealDao {

    @Query("SELECT * FROM meal WHERE id = :mealId")
    abstract fun observeMealById(mealId: Long): Flow<MealEntity?>

    @Query("SELECT * FROM meal ORDER BY rank ASC")
    abstract fun observeMeals(): Flow<List<MealEntity>>

    @Delete protected abstract suspend fun deleteMeal(meal: MealEntity)

    @Transaction
    open suspend fun delete(mealId: Long) {
        val meal = observeMealById(mealId).first() ?: return
        deleteMeal(meal)
    }

    @Update abstract suspend fun updateMeal(meal: MealEntity)

    @Transaction
    open suspend fun updateMealsRanks(map: Map<Long, Int>) {
        val meals = observeMeals().first()

        meals.map {
            val updated = it.copy(rank = map[it.id] ?: it.rank)

            updateMeal(updated)
        }
    }

    @Insert protected abstract fun insertMeal(meal: MealEntity)

    @Query(
        """
        SELECT COALESCE(MAX(rank), -1) + 1 
        FROM meal
        ORDER BY rank DESC
        LIMIT 1
        """
    )
    protected abstract suspend fun getLastMealRank(): Int

    @Transaction
    open suspend fun insertWithLastRank(meal: MealEntity) {
        val lastRank = getLastMealRank()
        val mealWithRank = meal.copy(rank = lastRank + 1)
        insertMeal(mealWithRank)
    }
}
