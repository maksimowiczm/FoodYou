package com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.data

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
abstract class OpenFoodFactsDao {

    @Insert
    protected abstract suspend fun insertProduct(product: OpenFoodFactsProduct)

    @Insert
    protected abstract suspend fun insertProducts(products: List<OpenFoodFactsProduct>)

    @Query(
        """
        SELECT EXISTS (
            SELECT 1
            FROM OpenFoodFactsProduct
            WHERE name = :name AND
                  (:brand IS NULL OR brand = :brand) AND
                  (:barcode IS NULL OR barcode = :barcode)
        )
        """
    )
    protected abstract suspend fun existsProductByNameAndBrand(
        name: String,
        brand: String?,
        barcode: String?
    ): Boolean

    open suspend fun insertUniqueProducts(products: List<OpenFoodFactsProduct>) {
        val uniqueProducts = products.filterNot { product ->
            existsProductByNameAndBrand(
                name = product.name,
                brand = product.brand,
                barcode = product.barcode
            )
        }

        if (uniqueProducts.isNotEmpty()) {
            insertProducts(uniqueProducts)
        }
    }

    suspend fun insertUniqueProduct(product: OpenFoodFactsProduct) {
        if (!existsProductByNameAndBrand(
                name = product.name,
                brand = product.brand,
                barcode = product.barcode
            )
        ) {
            insertProduct(product)
        }
    }

    @Query(
        """
        SELECT *
        FROM OpenFoodFactsProduct
        WHERE (:query1 IS NULL OR name LIKE '%' || :query1 || '%') AND
              (:query2 IS NULL OR brand LIKE '%' || :query2 || '%') AND
              (:query3 IS NULL OR barcode LIKE '%' || :query3 || '%')
        """
    )
    abstract fun observeProducts(
        query1: String? = null,
        query2: String? = null,
        query3: String? = null
    ): PagingSource<Int, OpenFoodFactsProduct>

    @Query(
        """
        SELECT COUNT(*)
        FROM OpenFoodFactsProduct
        WHERE (:query1 IS NULL OR name LIKE '%' || :query1 || '%') AND
              (:query2 IS NULL OR brand LIKE '%' || :query2 || '%') AND
              (:query3 IS NULL OR barcode LIKE '%' || :query3 || '%')
        """
    )
    abstract fun observeProductsCount(
        query1: String? = null,
        query2: String? = null,
        query3: String? = null
    ): Flow<Int>

    @Query(
        """
        SELECT COUNT(*)
        FROM OpenFoodFactsProduct
        WHERE barcode = :barcode
        """
    )
    abstract fun observeProductsCountByBarcode(barcode: String): Flow<Int>

    @Query(
        """
        SELECT *
        FROM OpenFoodFactsProduct
        WHERE barcode = :barcode
        """
    )
    abstract fun observeProductsByBarcode(barcode: String): PagingSource<Int, OpenFoodFactsProduct>

    @Query(
        """
        SELECT *
        FROM OpenFoodFactsPagingKey
        WHERE queryString = :query AND country = :country
        """
    )
    abstract suspend fun getPagingKey(query: String, country: String): OpenFoodFactsPagingKey?

    @Upsert
    abstract suspend fun upsertPagingKey(pagingKey: OpenFoodFactsPagingKey)

    @Query(
        """
        SELECT *
        FROM OpenFoodFactsProduct
        WHERE id = :id
        """
    )
    abstract fun observeProductById(id: Long): Flow<OpenFoodFactsProduct?>
}
