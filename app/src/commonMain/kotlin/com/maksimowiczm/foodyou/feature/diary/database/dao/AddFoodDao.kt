package com.maksimowiczm.foodyou.feature.diary.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.maksimowiczm.foodyou.feature.diary.database.entity.MealEntity
import com.maksimowiczm.foodyou.feature.diary.database.entity.ProductQueryEntity
import com.maksimowiczm.foodyou.feature.diary.database.measurement.ProductWithWeightMeasurementEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface AddFoodDao {

    @Upsert
    suspend fun upsertProductQuery(productQueryEntity: ProductQueryEntity)

    @Query(
        """
        SELECT *
        FROM ProductQueryEntity
        ORDER BY date DESC
        LIMIT :limit
        """
    )
    fun observeLatestQueries(limit: Int): Flow<List<ProductQueryEntity>>

    @Transaction
    @Query(
        """
        SELECT wm.*
        FROM WeightMeasurementEntity wm
        INNER JOIN ProductEntity p ON wm.productId = p.id
        WHERE wm.diaryEpochDay = :epochDay
        AND (:mealId IS NULL OR wm.mealId = :mealId)
        AND wm.isDeleted = 0
        """
    )
    fun observeMeasuredProducts(
        mealId: Long?,
        epochDay: Int
    ): Flow<List<ProductWithWeightMeasurementEntity>>

    @Query(
        """
        SELECT *
        FROM MealEntity
        """
    )
    fun observeMeals(): Flow<List<MealEntity>>

    @Query(
        """
        SELECT *
        FROM MealEntity
        WHERE id = :id
        """
    )
    fun observeMealById(id: Long): Flow<MealEntity?>

    @Insert
    suspend fun insertMeal(meal: MealEntity)

    @Transaction
    suspend fun insertWithLastRank(meal: MealEntity) {
        val lastRank = observeMeals().first().maxOfOrNull(MealEntity::rank) ?: -1
        insertMeal(meal.copy(rank = lastRank + 1))
    }

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
