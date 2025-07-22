package com.maksimowiczm.foodyou.feature.food.data.database.food

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductDao {

    @Query("SELECT * FROM Product WHERE id = :productId")
    abstract fun observe(productId: Long): Flow<Product?>

    @Insert
    abstract suspend fun insert(product: Product): Long

    @Update
    abstract suspend fun update(product: Product)

    @Delete
    abstract suspend fun delete(product: Product)

    @Insert
    protected abstract suspend fun insert(products: List<Product>)

    @Query(
        """
        SELECT EXISTS (
            SELECT 1
            FROM Product
            WHERE name = :name AND
                  (:brand IS NULL OR brand = :brand) AND
                  (:barcode IS NULL OR barcode = :barcode) AND
                  :source = sourceType
        )
        """
    )
    protected abstract suspend fun existsProductByNameAndBrand(
        name: String,
        brand: String?,
        barcode: String?,
        source: FoodSource.Type
    ): Boolean

    /**
     * Inserts a list of products into the database, ensuring that only unique products are added.
     * This method filters out products that already exist based on their name, brand, barcode, and
     * source type.
     *
     * @param products The list of products to be inserted.
     * @return The number of products inserted into the database.
     */
    @Transaction
    open suspend fun insertUniqueProducts(products: List<Product>): Int {
        val uniqueProducts = products.filterNot { product ->
            existsProductByNameAndBrand(
                name = product.name,
                brand = product.brand,
                barcode = product.barcode,
                source = product.sourceType
            )
        }

        if (uniqueProducts.isNotEmpty()) {
            insert(uniqueProducts)
        }

        return uniqueProducts.size
    }

    @Transaction
    open suspend fun insertUniqueProduct(product: Product) {
        if (!existsProductByNameAndBrand(
                name = product.name,
                brand = product.brand,
                barcode = product.barcode,
                source = product.sourceType
            )
        ) {
            insert(product)
        }
    }
}
