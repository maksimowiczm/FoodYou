package com.maksimowiczm.foodyou.feature.diary.database

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    @Query(
        """
        SELECT * 
        FROM MealProductEntity
        INNER JOIN ProductEntity p ON p.id = productId
        WHERE diaryEpochDay = :date
        """
    )
    fun observeMealProducts(date: Long): Flow<List<MealProductWithProduct>>
}
