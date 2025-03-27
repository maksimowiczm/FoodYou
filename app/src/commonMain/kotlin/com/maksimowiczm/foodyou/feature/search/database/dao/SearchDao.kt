package com.maksimowiczm.foodyou.feature.search.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.maksimowiczm.foodyou.feature.search.database.entity.ProductQueryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {
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
    fun observeProductQueries(limit: Int): Flow<List<ProductQueryEntity>>
}
