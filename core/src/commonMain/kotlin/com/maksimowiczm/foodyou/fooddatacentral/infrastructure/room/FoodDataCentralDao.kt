package com.maksimowiczm.foodyou.fooddatacentral.infrastructure.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSearchParameters.DataType
import kotlinx.coroutines.flow.Flow

@Dao
internal abstract class FoodDataCentralDao {
    @Query(
        """
        SELECT p.*
        FROM FoodDataCentralPagingKey pk
        LEFT JOIN FoodDataCentralProduct p ON p.fdcId = pk.fdcId
        WHERE pk.queryString = :query AND pk.dataTypes IS :dataTypes
        GROUP BY p.fdcId
        ORDER BY MIN(pk.id) ASC
        """
    )
    abstract fun getPagingSourceByQuery(
        query: String,
        dataTypes: Set<DataType>?,
    ): PagingSource<Int, FoodDataCentralProductEntity>

    @Query(
        """
        SELECT COUNT(DISTINCT pk.fdcId)
        FROM FoodDataCentralPagingKey pk
        LEFT JOIN FoodDataCentralProduct p ON p.fdcId = pk.fdcId
        WHERE pk.queryString = :query AND pk.dataTypes IS :dataTypes
        """
    )
    abstract fun observeCountByQuery(query: String, dataTypes: Set<DataType>?): Flow<Int>

    @Query(
        """
        SELECT COUNT(*)
        FROM FoodDataCentralPagingKey
        WHERE queryString = :query AND dataTypes IS :dataTypes
        """
    )
    abstract suspend fun getPagingKeyCountByQuery(query: String, dataTypes: Set<DataType>?): Int

    @Query(
        """
        SELECT p.*
        FROM FoodDataCentralProduct p
        WHERE p.dataType IN (:dataTypeOrdinals)
        GROUP BY p.fdcId
        """
    )
    abstract fun getPagingSource(
        dataTypeOrdinals: Set<Int>
    ): PagingSource<Int, FoodDataCentralProductEntity>

    @Query(
        """
        SELECT COUNT(*)
        FROM FoodDataCentralProduct
        WHERE dataType IN (:dataTypeOrdinals)
        """
    )
    abstract fun observeCount(dataTypeOrdinals: Set<Int>): Flow<Int>

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

    @Transaction
    open suspend fun upsertProductAndGet(
        product: FoodDataCentralProductEntity
    ): FoodDataCentralProductEntity? {
        val existing = getProduct(product.fdcId)
        if (existing == null || existing != product) {
            upsertProduct(product)
            return product
        }
        return null
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertPagingKeys(keys: List<FoodDataCentralPagingKeyEntity>)

    @Transaction
    open suspend fun insertProductsWithPagingKeys(
        products: List<FoodDataCentralProductEntity>,
        keys: List<FoodDataCentralPagingKeyEntity>,
    ): List<FoodDataCentralProductEntity> {
        val changed = mutableListOf<FoodDataCentralProductEntity>()
        for (product in products) upsertProductAndGet(product)?.let(changed::add)
        insertPagingKeys(keys)
        return changed
    }

    @Query(
        """
        SELECT *
        FROM FoodDataCentralProduct
        WHERE fdcId = :fdcId
        """
    )
    protected abstract suspend fun getProduct(fdcId: Int): FoodDataCentralProductEntity?

    @Query(
        """
        SELECT *
        FROM FoodDataCentralProduct
        WHERE fdcId = :fdcId
        """
    )
    abstract fun observe(fdcId: Int): Flow<FoodDataCentralProductEntity?>
}
