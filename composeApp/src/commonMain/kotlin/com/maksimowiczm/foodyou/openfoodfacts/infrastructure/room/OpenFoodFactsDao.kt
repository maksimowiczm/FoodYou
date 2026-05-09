package com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
internal abstract class OpenFoodFactsDao {
    @Query(
        """
        SELECT p.*
        FROM OpenFoodFactsPagingKey pk
        LEFT JOIN OpenFoodFactsProduct p ON p.barcode = pk.productBarcode
        WHERE pk.queryString = :query
        GROUP BY p.barcode
        ORDER BY MIN(pk.id) ASC
        """
    )
    abstract fun getPagingSourceByQuery(
        query: String
    ): PagingSource<Int, OpenFoodFactsProductEntity>

    @Query(
        """
        SELECT COUNT(DISTINCT pk.productBarcode)
        FROM OpenFoodFactsPagingKey pk
        LEFT JOIN OpenFoodFactsProduct p ON p.barcode = pk.productBarcode
        WHERE pk.queryString = :query
        """
    )
    abstract fun observeCountByQuery(query: String): Flow<Int>

    @Query(
        """
        SELECT COUNT(*)
        FROM OpenFoodFactsPagingKey
        WHERE queryString = :query
        """
    )
    abstract suspend fun getPagingKeyCountByQuery(query: String): Int

    @Query(
        """
        SELECT p.*
        FROM OpenFoodFactsProduct p
        GROUP BY p.barcode
        """
    )
    abstract fun getPagingSource(): PagingSource<Int, OpenFoodFactsProductEntity>

    @Query(
        """
        SELECT COUNT(*)
        FROM OpenFoodFactsProduct
        """
    )
    abstract fun observeCount(): Flow<Int>

    @Query(
        """
        SELECT *
        FROM OpenFoodFactsProduct
        WHERE barcode LIKE '%' || :barcode || '%'
        LIMIT 1
        """
    )
    abstract fun getPagingSourceByBarcode(
        barcode: String
    ): PagingSource<Int, OpenFoodFactsProductEntity>

    @Query(
        """
        SELECT 1 
        FROM OpenFoodFactsProduct
        WHERE barcode LIKE '%' || :barcode || '%'
        LIMIT 1
        """
    )
    abstract fun observeCountByBarcode(barcode: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract suspend fun insertProducts(products: List<OpenFoodFactsProductEntity>)

    @Upsert abstract suspend fun upsertProduct(product: OpenFoodFactsProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertPagingKeys(keys: List<OpenFoodFactsPagingKeyEntity>)

    @Transaction
    open suspend fun insertProductsWithPagingKeys(
        products: List<OpenFoodFactsProductEntity>,
        keys: List<OpenFoodFactsPagingKeyEntity>,
    ) {
        insertProducts(products)
        insertPagingKeys(keys)
    }

    @Query(
        """
        SELECT *
        FROM OpenFoodFactsProduct
        WHERE barcode = :barcode
        LIMIT 1
        """
    )
    abstract fun observe(barcode: String): Flow<OpenFoodFactsProductEntity?>
}
