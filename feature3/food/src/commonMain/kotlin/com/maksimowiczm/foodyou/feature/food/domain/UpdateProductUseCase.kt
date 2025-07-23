package com.maksimowiczm.foodyou.feature.food.domain

import com.maksimowiczm.foodyou.core.ext.now
import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.LocalDateTime

interface UpdateProductUseCase {
    suspend fun update(product: Product)
}

internal class UpdateProductUseCaseImpl(
    foodDatabase: FoodDatabase,
    private val productMapper: ProductMapper,
    private val productEventMapper: ProductEventMapper
) : UpdateProductUseCase {
    private val productDao = foodDatabase.productDao
    private val productEventDao = foodDatabase.productEventDao

    override suspend fun update(product: Product) {
        val oldProduct = productDao
            .observe(product.id.id)
            .firstOrNull()
            ?.let(productMapper::toModel)

        if (oldProduct == null) {
            error("Product with id ${product.id.id} does not exist.")
        }

        val entity = productMapper.toEntity(product)
        productDao.update(entity)

        val eventEntity = productEventMapper.toEntity(
            model = ProductEvent.Edited(
                date = LocalDateTime.now(),
                oldProduct = oldProduct
            ),
            productId = product.id.id
        )

        productEventDao.insert(eventEntity)
    }
}
