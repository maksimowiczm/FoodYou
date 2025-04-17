package com.maksimowiczm.foodyou.feature.productredesign.data

import com.maksimowiczm.foodyou.core.data.model.Nutrients
import com.maksimowiczm.foodyou.core.data.model.product.ProductEntity
import com.maksimowiczm.foodyou.core.data.model.product.ProductSource
import com.maksimowiczm.foodyou.core.domain.source.ProductLocalDataSource

internal class ProductRepository(private val localProductDataSource: ProductLocalDataSource) {

    /**
     * Creates a new user product in the database.
     *
     * @return The ID of the newly created product.
     */
    suspend fun createProduct(
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

        return localProductDataSource.upsertProduct(entity)
    }
}
