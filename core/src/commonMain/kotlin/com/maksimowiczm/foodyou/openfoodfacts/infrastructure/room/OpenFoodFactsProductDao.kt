package com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room

import androidx.paging.PagingSource
import androidx.room3.*
import androidx.room3.paging.PagingSourceDaoReturnTypeConverter
import kotlinx.coroutines.flow.Flow

@Dao
@DaoReturnTypeConverters(PagingSourceDaoReturnTypeConverter::class)
internal abstract class OpenFoodFactsProductDao {
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

    @Upsert abstract suspend fun upsertProduct(product: OpenFoodFactsProductEntity)

    @Transaction
    open suspend fun upsertProductAndGet(
        product: OpenFoodFactsProductEntity
    ): OpenFoodFactsProductEntity? {
        val existing = getProduct(product.barcode)
        if (existing == null || existing != product) {
            upsertProduct(product)
            return product
        }
        return null
    }

    @Query(
        """
        SELECT *
        FROM OpenFoodFactsProduct
        WHERE barcode = :barcode
        """
    )
    protected abstract suspend fun getProduct(barcode: String): OpenFoodFactsProductEntity?

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
