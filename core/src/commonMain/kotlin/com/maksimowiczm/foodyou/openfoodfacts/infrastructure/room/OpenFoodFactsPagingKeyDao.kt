package com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room

import androidx.paging.PagingSource
import androidx.room3.*
import kotlinx.coroutines.flow.Flow

@Dao
internal abstract class OpenFoodFactsPagingKeyDao<K> {
    abstract fun getPagingSourceByQuery(
        query: String
    ): PagingSource<Int, OpenFoodFactsProductEntity>

    abstract fun observeCountByQuery(query: String): Flow<Int>

    abstract suspend fun getPagingKeyCountByQuery(query: String): Int

    @Upsert protected abstract suspend fun upsertProduct(product: OpenFoodFactsProductEntity)

    @Query(
        """
        SELECT *
        FROM OpenFoodFactsProduct
        WHERE barcode = :barcode
        """
    )
    protected abstract suspend fun getProduct(barcode: String): OpenFoodFactsProductEntity?

    @Transaction
    protected open suspend fun upsertProductAndGet(
        product: OpenFoodFactsProductEntity
    ): OpenFoodFactsProductEntity? {
        val existing = getProduct(product.barcode)
        if (existing == null || existing != product) {
            upsertProduct(product)
            return product
        }
        return null
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertPagingKeys(keys: List<K>)

    protected abstract fun createPagingKey(queryString: String, productBarcode: String): K

    @Transaction
    open suspend fun insertProductsWithPagingKeys(
        products: List<OpenFoodFactsProductEntity>,
        queryString: String,
    ): List<OpenFoodFactsProductEntity> {
        val changed = mutableListOf<OpenFoodFactsProductEntity>()
        for (product in products) upsertProductAndGet(product)?.let(changed::add)
        insertPagingKeys(
            products.map { createPagingKey(queryString = queryString, productBarcode = it.barcode) }
        )
        return changed
    }
}
