package com.maksimowiczm.foodyou.feature.fooddiary.data

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
    abstract fun observeMealById(mealId: Long): Flow<Meal?>

    @Query("SELECT * FROM meal ORDER BY rank ASC")
    abstract fun observeMeals(): Flow<List<Meal>>

    @Delete
    abstract suspend fun deleteMeal(meal: Meal)

    @Update
    abstract suspend fun updateMeal(meal: Meal)

    @Transaction
    open suspend fun updateMealsRanks(map: Map<Long, Int>) {
        val meals = observeMeals().first()

        meals.map {
            val updated = it.copy(
                rank = map[it.id] ?: it.rank
            )

            updateMeal(updated)
        }
    }

    @Insert
    protected abstract fun insertMeal(meal: Meal)

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
    open suspend fun insertWithLastRank(meal: Meal) {
        val lastRank = getLastMealRank()
        val mealWithRank = meal.copy(rank = lastRank + 1)
        insertMeal(mealWithRank)
    }
}
