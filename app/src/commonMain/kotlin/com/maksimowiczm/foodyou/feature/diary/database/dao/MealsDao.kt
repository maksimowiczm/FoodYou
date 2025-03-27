package com.maksimowiczm.foodyou.feature.diary.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.maksimowiczm.foodyou.feature.diary.database.entity.MealEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface MealsDao {
    @Query(
        """
        SELECT *
        FROM MealEntity
        ORDER BY rank
        """
    )
    fun observeMeals(): Flow<List<MealEntity>>

    @Transaction
    suspend fun insertWithLastRank(meal: MealEntity) {
        val lastRank = observeMeals().first().maxOfOrNull(MealEntity::rank) ?: -1
        insertMeal(meal.copy(rank = lastRank + 1))
    }

    @Insert
    suspend fun insertMeal(meal: MealEntity)

    @Update
    suspend fun updateMeal(meal: MealEntity)

    @Query(
        """
        UPDATE MealEntity
        SET rank = :rank
        WHERE id = :mealId
        """
    )
    suspend fun updateMealRank(mealId: Long, rank: Int)

    @Transaction
    suspend fun updateMealsRanks(map: Map<Long, Int>) {
        map.forEach { (mealId, rank) ->
            updateMealRank(mealId, rank)
        }
    }

    @Delete
    suspend fun deleteMeal(meal: MealEntity)
}
