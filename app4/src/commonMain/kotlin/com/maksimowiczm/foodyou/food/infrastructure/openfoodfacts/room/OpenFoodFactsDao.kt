package com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface OpenFoodFactsDao {
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
    fun getPagingSourceByQuery(query: String): PagingSource<Int, OpenFoodFactsProductEntity>

    @Query(
        """
        SELECT COUNT(DISTINCT pk.productBarcode)
        FROM OpenFoodFactsPagingKey pk
        LEFT JOIN OpenFoodFactsProduct p ON p.barcode = pk.productBarcode
        WHERE pk.queryString = :query
        """
    )
    fun observeCountByQuery(query: String): Flow<Int>

    @Query(
        """
        SELECT COUNT(*)
        FROM OpenFoodFactsPagingKey
        WHERE queryString = :query
        """
    )
    suspend fun getPagingKeyCountByQuery(query: String): Int

    @Query(
        """
        SELECT p.*
        FROM OpenFoodFactsProduct p
        GROUP BY p.barcode
        """
    )
    fun getPagingSource(): PagingSource<Int, OpenFoodFactsProductEntity>

    @Query(
        """
        SELECT COUNT(*)
        FROM OpenFoodFactsProduct
        """
    )
    fun observeCount(): Flow<Int>

    @Query(
        """
        SELECT *
        FROM OpenFoodFactsProduct
        WHERE barcode = :barcode
        LIMIT 1
        """
    )
    fun getPagingSourceByBarcode(barcode: String): PagingSource<Int, OpenFoodFactsProductEntity>

    @Query(
        """
        SELECT 1 
        FROM OpenFoodFactsProduct
        WHERE barcode = :barcode
        LIMIT 1
        """
    )
    fun observeCountByBarcode(barcode: String): Flow<Int>

    @Upsert suspend fun upsertProducts(products: List<OpenFoodFactsProductEntity>)

    @Upsert suspend fun upsertProduct(product: OpenFoodFactsProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPagingKeys(keys: List<OpenFoodFactsPagingKeyEntity>)

    @Query(
        """
        SELECT *
        FROM OpenFoodFactsProduct
        WHERE barcode = :barcode
        LIMIT 1
        """
    )
    fun observe(barcode: String): Flow<OpenFoodFactsProductEntity?>
}
