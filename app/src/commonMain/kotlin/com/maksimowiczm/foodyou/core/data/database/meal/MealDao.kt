package com.maksimowiczm.foodyou.core.data.database.meal

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.maksimowiczm.foodyou.core.data.model.meal.MealEntity
import com.maksimowiczm.foodyou.core.data.source.MealLocalDataSource
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MealDao : MealLocalDataSource {
    @Query("SELECT * FROM MealEntity")
    abstract override fun observeMeals(): Flow<List<MealEntity>>

    @Query("SELECT * FROM MealEntity WHERE id = :id")
    abstract override fun observeMeal(id: Long): Flow<MealEntity?>

    @Query("SELECT * FROM MealEntity WHERE id = :id")
    abstract override suspend fun getMealById(id: Long): MealEntity?

    @Insert
    protected abstract fun insertMeal(meal: MealEntity)

    @Query(
        """
        SELECT * 
        FROM MealEntity
        ORDER BY rank DESC
        LIMIT 1
        """
    )
    protected abstract fun getLastRank(): Int

    @Transaction
    override suspend fun insertWithLastRank(meal: MealEntity) {
        val lastRank = getLastRank()

        insertMeal(
            meal.copy(
                rank = lastRank + 1
            )
        )
    }

    @Update
    abstract override suspend fun update(meal: MealEntity)

    @Delete
    abstract override suspend fun delete(meal: MealEntity)

    @Query(
        """
        UPDATE MealEntity
        SET rank = :rank
        WHERE id = :mealId
        """
    )
    protected abstract suspend fun updateMealRank(mealId: Long, rank: Int)

    @Transaction
    override suspend fun updateMealsRanks(map: Map<Long, Int>) {
        map.forEach { (mealId, rank) ->
            updateMealRank(mealId, rank)
        }
    }
}
