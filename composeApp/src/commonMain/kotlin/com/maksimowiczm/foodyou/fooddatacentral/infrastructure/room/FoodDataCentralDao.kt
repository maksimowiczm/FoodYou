package com.maksimowiczm.foodyou.fooddatacentral.infrastructure.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
internal abstract class FoodDataCentralDao {
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
    abstract fun getPagingSourceByQuery(
        query: String
    ): PagingSource<Int, FoodDataCentralProductEntity>

    @Query(
        """
        SELECT COUNT(DISTINCT pk.fdcId)
        FROM FoodDataCentralPagingKey pk
        LEFT JOIN FoodDataCentralProduct p ON p.fdcId = pk.fdcId
        WHERE pk.queryString = :query
        """
    )
    abstract fun observeCountByQuery(query: String): Flow<Int>

    @Query(
        """
        SELECT COUNT(*)
        FROM FoodDataCentralPagingKey
        WHERE queryString = :query
        """
    )
    abstract suspend fun getPagingKeyCountByQuery(query: String): Int

    @Query(
        """
        SELECT p.*
        FROM FoodDataCentralProduct p
        GROUP BY p.fdcId
        """
    )
    abstract fun getPagingSource(): PagingSource<Int, FoodDataCentralProductEntity>

    @Query(
        """
        SELECT COUNT(*)
        FROM FoodDataCentralProduct
        """
    )
    abstract fun observeCount(): Flow<Int>

    @Query(
        """
        SELECT *
        FROM FoodDataCentralProduct
        WHERE gtinUpc LIKE '%' || :barcode || '%'
        LIMIT 1
        """
    )
    abstract fun getPagingSourceByBarcode(
        barcode: String
    ): PagingSource<Int, FoodDataCentralProductEntity>

    @Query(
        """
        SELECT 1 
        FROM FoodDataCentralProduct
        WHERE gtinUpc LIKE '%' || :barcode || '%'
        LIMIT 1
        """
    )
    abstract fun observeCountByBarcode(barcode: String): Flow<Int>

    @Query(
        """
        SELECT *
        FROM FoodDataCentralProduct
        WHERE fdcId = :fdcId
        """
    )
    abstract fun getPagingSourceByFdcId(fdcId: Int): PagingSource<Int, FoodDataCentralProductEntity>

    @Query(
        """
        SELECT COUNT(*)
        FROM FoodDataCentralProduct
        WHERE fdcId = :fdcId
        """
    )
    abstract fun observeCountByFdcId(fdcId: Int): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract suspend fun insertProducts(products: List<FoodDataCentralProductEntity>)

    @Upsert abstract suspend fun upsertProduct(product: FoodDataCentralProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertPagingKeys(keys: List<FoodDataCentralPagingKeyEntity>)

    @Transaction
    open suspend fun insertProductsWithPagingKeys(
        products: List<FoodDataCentralProductEntity>,
        keys: List<FoodDataCentralPagingKeyEntity>,
    ) {
        insertProducts(products)
        insertPagingKeys(keys)
    }

    @Query(
        """
        SELECT *
        FROM FoodDataCentralProduct
        WHERE fdcId = :fdcId
        """
    )
    abstract fun observe(fdcId: Int): Flow<FoodDataCentralProductEntity?>
}
