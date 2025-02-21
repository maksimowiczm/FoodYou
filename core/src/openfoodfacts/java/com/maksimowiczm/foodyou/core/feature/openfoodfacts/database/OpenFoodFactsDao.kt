package com.maksimowiczm.foodyou.core.feature.openfoodfacts.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.maksimowiczm.foodyou.core.feature.product.data.model.ProductSource
import com.maksimowiczm.foodyou.core.feature.product.database.ProductEntity

@Dao
interface OpenFoodFactsDao {
    @Query(
        """
        SELECT * 
        FROM OpenFoodFactsPagingKey 
        WHERE queryString = :query
            AND country = :country
        """
    )
    suspend fun getPagingKey(query: String, country: String): OpenFoodFactsPagingKey?

    @Upsert
    suspend fun upsertPagingKey(pagingKey: OpenFoodFactsPagingKey)

    @Query(
        """
        DELETE FROM OpenFoodFactsPagingKey 
        """
    )
    suspend fun clearPagingKeys()

    @Query(
        """
        SELECT id
        FROM productentity 
        WHERE name = :name 
        AND (:brand IS NULL OR brand = :brand) 
        AND (:barcode IS NULL OR barcode = :barcode)
        AND productSource = :source
        """
    )
    suspend fun getProductId(
        name: String,
        brand: String?,
        barcode: String?,
        source: ProductSource
    ): Int?

    @Transaction
    suspend fun upsertOpenFoodFactsProducts(products: List<ProductEntity>) {
        val ids = products.map { product ->
            val id = getProductId(
                product.name,
                product.brand,
                product.barcode,
                ProductSource.OpenFoodFacts
            )
            id to product
        }

        val productsWithIds = ids.map { (id, product) ->
            if (id != null) {
                product.copy(id = id.toLong())
            } else {
                product
            }
        }

        upsertProducts(productsWithIds)
    }

    @Upsert
    suspend fun upsertProducts(products: List<ProductEntity>)
}
