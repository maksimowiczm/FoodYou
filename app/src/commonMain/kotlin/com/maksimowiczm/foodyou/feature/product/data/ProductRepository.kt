package com.maksimowiczm.foodyou.feature.product.data

import com.maksimowiczm.foodyou.core.database.FoodYouDatabase
import com.maksimowiczm.foodyou.core.database.dao.ProductDao
import com.maksimowiczm.foodyou.core.database.embedded.NutrientsEmbedded
import com.maksimowiczm.foodyou.core.database.entity.ProductEntity
import com.maksimowiczm.foodyou.core.database.entity.ProductSource
import com.maksimowiczm.foodyou.core.mapper.ProductMapper
import com.maksimowiczm.foodyou.core.model.Product
import kotlinx.coroutines.flow.first

internal class ProductRepository(database: FoodYouDatabase) {
    private val productDao: ProductDao = database.productDao

    suspend fun getProductById(id: Long): Product? = with(ProductMapper) {
        return productDao.observeProduct(id).first()?.toModel()
    }

    /**
     * Creates a new user product in the database.
     *
     * @return The ID of the newly created product.
     */
    suspend fun createUserProduct(
        name: String,
        brand: String?,
        barcode: String?,
        calories: Float,
        proteins: Float,
        carbohydrates: Float,
        sugars: Float?,
        fats: Float,
        saturatedFats: Float?,
        salt: Float?,
        sodium: Float?,
        fiber: Float?,
        packageWeight: Float?,
        servingWeight: Float?
    ): Long {
        val nutrients = NutrientsEmbedded(
            calories = calories,
            proteins = proteins,
            carbohydrates = carbohydrates,
            sugars = sugars,
            fats = fats,
            saturatedFats = saturatedFats,
            salt = salt,
            sodium = sodium,
            fiber = fiber
        )

        val entity = ProductEntity(
            name = name,
            brand = brand,
            barcode = barcode,
            nutrients = nutrients,
            packageWeight = packageWeight,
            servingWeight = servingWeight,
            productSource = ProductSource.User
        )

        return productDao.upsertProduct(entity)
    }

    suspend fun updateProduct(
        id: Long,
        name: String,
        brand: String?,
        barcode: String?,
        calories: Float,
        proteins: Float,
        carbohydrates: Float,
        sugars: Float?,
        fats: Float,
        saturatedFats: Float?,
        salt: Float?,
        sodium: Float?,
        fiber: Float?,
        packageWeight: Float?,
        servingWeight: Float?
    ) {
        val nutrients = NutrientsEmbedded(
            calories = calories,
            proteins = proteins,
            carbohydrates = carbohydrates,
            sugars = sugars,
            fats = fats,
            saturatedFats = saturatedFats,
            salt = salt,
            sodium = sodium,
            fiber = fiber
        )

        val entity = ProductEntity(
            id = id,
            name = name,
            brand = brand,
            barcode = barcode,
            nutrients = nutrients,
            packageWeight = packageWeight,
            servingWeight = servingWeight,
            productSource = ProductSource.User
        )

        productDao.upsertProduct(entity)
    }
}
