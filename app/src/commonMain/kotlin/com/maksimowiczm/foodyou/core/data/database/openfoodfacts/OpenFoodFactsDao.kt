package com.maksimowiczm.foodyou.core.data.database.openfoodfacts

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.maksimowiczm.foodyou.core.data.model.openfoodfacts.OpenFoodFactsPagingKeyEntity
import com.maksimowiczm.foodyou.core.data.source.OpenFoodFactsLocalDataSource

@Dao
abstract class OpenFoodFactsDao : OpenFoodFactsLocalDataSource {
    @Query(
        """
        SELECT * 
        FROM OpenFoodFactsPagingKeyEntity 
        WHERE queryString = :query
            AND country = :country
        """
    )
    abstract override suspend fun getPagingKey(
        query: String,
        country: String
    ): OpenFoodFactsPagingKeyEntity?

    @Upsert
    abstract override suspend fun upsertPagingKey(pagingKey: OpenFoodFactsPagingKeyEntity)

    @Query(
        """
        DELETE FROM OpenFoodFactsPagingKeyEntity 
        """
    )
    abstract override suspend fun clearPagingKeys()
}
