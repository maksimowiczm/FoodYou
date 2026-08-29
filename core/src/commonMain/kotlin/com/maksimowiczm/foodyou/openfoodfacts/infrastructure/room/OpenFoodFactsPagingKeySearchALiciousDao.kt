package com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room

import androidx.paging.PagingSource
import androidx.room3.*
import androidx.room3.paging.PagingSourceDaoReturnTypeConverter
import kotlinx.coroutines.flow.Flow

@Dao
@DaoReturnTypeConverters(PagingSourceDaoReturnTypeConverter::class)
internal abstract class OpenFoodFactsPagingKeySearchALiciousDao :
    OpenFoodFactsPagingKeyDao<OpenFoodFactsPagingKeySearchALiciousEntity>() {
    @Query(
        """
        SELECT p.*
        FROM OpenFoodFactsPagingKeySearchALicious pk
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
        FROM OpenFoodFactsPagingKeySearchALicious pk
        LEFT JOIN OpenFoodFactsProduct p ON p.barcode = pk.productBarcode
        WHERE pk.queryString = :query
        """
    )
    abstract override fun observeCountByQuery(query: String): Flow<Int>

    @Query(
        """
        SELECT COUNT(*)
        FROM OpenFoodFactsPagingKeySearchALicious
        WHERE queryString = :query
        """
    )
    abstract override suspend fun getPagingKeyCountByQuery(query: String): Int

    override fun createPagingKey(queryString: String, productBarcode: String) =
        OpenFoodFactsPagingKeySearchALiciousEntity(
            queryString = queryString,
            productBarcode = productBarcode,
        )
}
