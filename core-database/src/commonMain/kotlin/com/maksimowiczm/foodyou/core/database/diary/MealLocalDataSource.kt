package com.maksimowiczm.foodyou.core.database.diary

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MealLocalDataSource {

    @Query("SELECT * FROM MealEntity")
    abstract fun observeMeals(): Flow<List<MealEntity>>

    @Query("SELECT * FROM MealEntity WHERE id = :id")
    abstract fun observeMealById(id: Long): Flow<MealEntity?>

    @Insert
    protected abstract suspend fun insertMeal(meal: MealEntity)

    @Query(
        """
        SELECT rank
        FROM MealEntity
        ORDER BY rank DESC
        LIMIT 1
        """
    )
    protected abstract suspend fun getLastRank(): Int

    @Transaction
    open suspend fun insertWithLastRank(entity: MealEntity) {
        val lastRank = getLastRank()

        insertMeal(
            entity.copy(
                rank = lastRank + 1
            )
        )
    }

    @Update
    abstract suspend fun update(meal: MealEntity)

    @Delete
    abstract suspend fun delete(meal: MealEntity)

    @Query(
        """
        UPDATE MealEntity
        SET rank = :rank
        WHERE id = :mealId
        """
    )
    protected abstract suspend fun updateMealRank(mealId: Long, rank: Int)

    @Transaction
    open suspend fun updateMealsRanks(map: Map<Long, Int>) {
        map.forEach { (mealId, rank) ->
            updateMealRank(mealId, rank)
        }
    }
}
