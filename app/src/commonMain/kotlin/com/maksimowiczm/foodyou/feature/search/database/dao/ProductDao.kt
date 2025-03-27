package com.maksimowiczm.foodyou.feature.search.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.maksimowiczm.foodyou.feature.search.database.entity.ProductEntity
import com.maksimowiczm.foodyou.feature.search.database.entity.ProductSource

@Dao
interface ProductDao {
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

    /**
     * Updates or inserts the given products into the database. If a product with the same name,
     * brand, barcode and source already exists in the database, it will be updated with the new
     * values. If not, a new product will be inserted.
     */
    @Transaction
    suspend fun upsertUniqueProducts(products: List<ProductEntity>) {
        val ids = products.map { product ->
            val id = getProductId(
                product.name,
                product.brand,
                product.barcode,
                product.productSource
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
