package com.maksimowiczm.foodyou.core.data.database.product

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.maksimowiczm.foodyou.core.data.model.product.ProductEntity
import com.maksimowiczm.foodyou.core.data.model.product.ProductSource
import com.maksimowiczm.foodyou.core.domain.source.ProductLocalDataSource
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductDao : ProductLocalDataSource {

    @Upsert
    protected abstract suspend fun upsertProducts(products: List<ProductEntity>)

    @Upsert
    abstract override suspend fun upsertProduct(product: ProductEntity): Long

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
    override suspend fun insertOpenFoodFactsProducts(products: List<ProductEntity>) {
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

    @Query(
        """
        SELECT *
        FROM productentity 
        WHERE id = :id
        """
    )
    abstract override fun observeProduct(id: Long): Flow<ProductEntity?>

    @Query(
        """
        DELETE FROM productentity  
        WHERE id = :id
        """
    )
    abstract override suspend fun deleteProduct(id: Long)

    @Query(
        """
        WITH UsedInRecipes AS (
            SELECT DISTINCT productId 
            FROM RecipeIngredientEntity i
        ),
        UsedInMeals AS (
            SELECT DISTINCT productId 
            FROM ProductMeasurementEntity m
        ),
        UsedProducts AS (
            SELECT DISTINCT productId 
            FROM UsedInRecipes
            UNION
            SELECT DISTINCT productId 
            FROM UsedInMeals
        )
        DELETE FROM ProductEntity 
        WHERE id IN (
            SELECT id 
            FROM ProductEntity 
            WHERE productSource = :source
            AND id NOT IN (SELECT productId FROM UsedProducts)
        )
        """
    )
    abstract override suspend fun deleteUnusedProducts(source: ProductSource)
}
