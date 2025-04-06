package com.maksimowiczm.foodyou.feature.diary.database.product

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert

@Dao
abstract class ProductDao {

    @Upsert
    protected abstract suspend fun upsertProducts(products: List<ProductEntity>)

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
    protected abstract suspend fun getProductId(
        name: String,
        brand: String?,
        barcode: String?,
        source: ProductSource
    ): Int?

    @Transaction
    @Insert
    open suspend fun insertOpenFoodFactsProducts(products: List<ProductEntity>) {
        val ids = products
            .filter { it.productSource == ProductSource.OpenFoodFacts }
            .map { product ->
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
}
