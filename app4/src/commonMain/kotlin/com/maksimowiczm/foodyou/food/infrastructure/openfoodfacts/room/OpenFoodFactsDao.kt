package com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface OpenFoodFactsDao {
    @Query(
        """
        SELECT p.*
        FROM OpenFoodFactsPagingKey pk
        LEFT JOIN OpenFoodFactsProduct p ON p.barcode = pk.productBarcode
        WHERE pk.queryString = :query
        ORDER BY pk.id ASC
        """
    )
    fun getPagingSourceByQuery(query: String): PagingSource<Int, OpenFoodFactsProductEntity>

    @Query(
        """
        SELECT COUNT(*)
        FROM OpenFoodFactsPagingKey
        WHERE queryString = :query
        """
    )
    suspend fun getCountByQuery(query: String): Int

    @Query(
        """
        SELECT p.*
        FROM OpenFoodFactsProduct p
        """
    )
    fun getPagingSource(): PagingSource<Int, OpenFoodFactsProductEntity>

    @Query(
        """
        SELECT *
        FROM OpenFoodFactsProduct
        WHERE barcode = :barcode
        """
    )
    fun getPagingSourceByBarcode(barcode: String): PagingSource<Int, OpenFoodFactsProductEntity>

    @Upsert suspend fun upsertProducts(products: List<OpenFoodFactsProductEntity>)

    @Upsert suspend fun upsertProduct(product: OpenFoodFactsProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPagingKeys(keys: List<OpenFoodFactsPagingKeyEntity>)
}
