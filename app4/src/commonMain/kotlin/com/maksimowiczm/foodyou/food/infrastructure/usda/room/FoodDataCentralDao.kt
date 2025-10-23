package com.maksimowiczm.foodyou.food.infrastructure.usda.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDataCentralDao {
    @Query(
        """
        SELECT p.*
        FROM FoodDataCentralPagingKey pk
        LEFT JOIN FoodDataCentralProduct p ON p.fdcId = pk.fdcId
        WHERE pk.queryString = :query
        GROUP BY p.fdcId
        ORDER BY MIN(pk.id) ASC
        """
    )
    fun getPagingSourceByQuery(query: String): PagingSource<Int, FoodDataCentralProductEntity>

    @Query(
        """
        SELECT COUNT(DISTINCT pk.fdcId)
        FROM FoodDataCentralPagingKey pk
        LEFT JOIN FoodDataCentralProduct p ON p.fdcId = pk.fdcId
        WHERE pk.queryString = :query
        """
    )
    fun observeCountByQuery(query: String): Flow<Int>

    @Query(
        """
        SELECT COUNT(*)
        FROM FoodDataCentralPagingKey
        WHERE queryString = :query
        """
    )
    suspend fun getPagingKeyCountByQuery(query: String): Int

    @Query(
        """
        SELECT p.*
        FROM FoodDataCentralProduct p
        GROUP BY p.fdcId
        """
    )
    fun getPagingSource(): PagingSource<Int, FoodDataCentralProductEntity>

    @Query(
        """
        SELECT COUNT(*)
        FROM FoodDataCentralProduct
        """
    )
    fun observeCount(): Flow<Int>

    @Query(
        """
        SELECT *
        FROM FoodDataCentralProduct
        WHERE gtinUpc LIKE '%' || :barcode || '%'
        LIMIT 1
        """
    )
    fun getPagingSourceByBarcode(barcode: String): PagingSource<Int, FoodDataCentralProductEntity>

    @Query(
        """
        SELECT 1 
        FROM FoodDataCentralProduct
        WHERE gtinUpc LIKE '%' || :barcode || '%'
        LIMIT 1
        """
    )
    fun observeCountByBarcode(barcode: String): Flow<Int>

    @Query(
        """
        SELECT *
        FROM FoodDataCentralProduct
        WHERE fdcId = :fdcId
        """
    )
    fun getPagingSourceByFdcId(fdcId: Int): PagingSource<Int, FoodDataCentralProductEntity>

    @Query(
        """
        SELECT COUNT(*)
        FROM FoodDataCentralProduct
        WHERE fdcId = :fdcId
        """
    )
    fun observeCountByFdcId(fdcId: Int): Flow<Int>

    @Upsert suspend fun upsertProducts(products: List<FoodDataCentralProductEntity>)

    @Upsert suspend fun upsertProduct(product: FoodDataCentralProductEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPagingKeys(keys: List<FoodDataCentralPagingKeyEntity>)

    @Query(
        """
        SELECT *
        FROM FoodDataCentralProduct
        WHERE fdcId = :fdcId
        """
    )
    fun observe(fdcId: Int): Flow<FoodDataCentralProductEntity?>
}
