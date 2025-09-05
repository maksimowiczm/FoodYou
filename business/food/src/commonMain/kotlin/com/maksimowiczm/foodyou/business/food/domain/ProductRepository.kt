package com.maksimowiczm.foodyou.business.food.domain

import com.maksimowiczm.foodyou.business.shared.domain.food.FoodId
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodSource
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun observeProduct(id: FoodId.Product): Flow<Product?>

    fun observeProducts(limit: Int, offset: Int): Flow<List<Product>>

    /**
     * @param name Name of the product.
     * @param brand Brand of the product, if available.
     * @param barcode Barcode of the product, if available.
     * @param note Additional note about the product, if available.
     * @param isLiquid Indicates whether the product is liquid (e.g., juice, milk).
     * @param packageWeight Weight of the product package, if available.
     * @param source Source of the product.
     * @param servingWeight Weight of a single serving of the product, if available.
     * @param nutritionFacts Nutrition facts of the product per 100g or 100ml, depending on whether
     *   the product is solid or liquid.
     */
    suspend fun insertProduct(
        name: String,
        brand: String?,
        barcode: String?,
        note: String?,
        isLiquid: Boolean,
        packageWeight: Double?,
        servingWeight: Double?,
        source: FoodSource,
        nutritionFacts: NutritionFacts,
    ): FoodId.Product

    /**
     * Creates a new product only if a product with the same name, brand, and barcode does not
     * already exist.
     *
     * @return The ID of the newly created product, or null if a duplicate product exists.
     */
    suspend fun insertUniqueProduct(
        name: String,
        brand: String?,
        barcode: String?,
        note: String?,
        isLiquid: Boolean,
        packageWeight: Double?,
        servingWeight: Double?,
        source: FoodSource,
        nutritionFacts: NutritionFacts,
    ): FoodId.Product?

    suspend fun updateProduct(product: Product)

    suspend fun deleteProduct(product: Product)
}
