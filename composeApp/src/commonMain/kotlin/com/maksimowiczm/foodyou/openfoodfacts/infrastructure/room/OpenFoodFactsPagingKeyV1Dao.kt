package com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal abstract class OpenFoodFactsPagingKeyV1Dao :
    OpenFoodFactsPagingKeyDao<OpenFoodFactsPagingKeyV1Entity>() {
    @Query(
        """
        SELECT p.*
        FROM OpenFoodFactsPagingKeyV1 pk
        LEFT JOIN OpenFoodFactsProduct p ON p.barcode = pk.productBarcode
        WHERE pk.queryString = :query
        GROUP BY p.barcode
        ORDER BY MIN(pk.id) ASC
        """
    )
    abstract override fun getPagingSourceByQuery(
        query: String
    ): PagingSource<Int, OpenFoodFactsProductEntity>

    @Query(
        """
        SELECT COUNT(DISTINCT pk.productBarcode)
        FROM OpenFoodFactsPagingKeyV1 pk
        LEFT JOIN OpenFoodFactsProduct p ON p.barcode = pk.productBarcode
        WHERE pk.queryString = :query
        """
    )
    abstract override fun observeCountByQuery(query: String): Flow<Int>

    @Query(
        """
        SELECT COUNT(*)
        FROM OpenFoodFactsPagingKeyV1
        WHERE queryString = :query
        """
    )
    abstract override suspend fun getPagingKeyCountByQuery(query: String): Int

    override fun createPagingKey(queryString: String, productBarcode: String) =
        OpenFoodFactsPagingKeyV1Entity(queryString = queryString, productBarcode = productBarcode)
}
