package com.maksimowiczm.foodyou.feature.product.data

import com.maksimowiczm.foodyou.core.data.model.Nutrients
import com.maksimowiczm.foodyou.core.data.model.product.ProductEntity
import com.maksimowiczm.foodyou.core.data.model.product.ProductSource
import com.maksimowiczm.foodyou.core.domain.mapper.ProductMapper
import com.maksimowiczm.foodyou.core.domain.model.Product
import com.maksimowiczm.foodyou.core.domain.source.ProductLocalDataSource
import kotlinx.coroutines.flow.first

internal class ProductRepository(private val productDao: ProductLocalDataSource) {

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
        val nutrients = Nutrients(
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
            brand = brand?.takeIf { it.isNotBlank() },
            barcode = barcode?.takeIf { it.isNotBlank() },
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
        val nutrients = Nutrients(
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
            brand = brand?.takeIf { it.isNotBlank() },
            barcode = barcode?.takeIf { it.isNotBlank() },
            nutrients = nutrients,
            packageWeight = packageWeight,
            servingWeight = servingWeight,
            productSource = ProductSource.User
        )

        productDao.upsertProduct(entity)
    }
}
