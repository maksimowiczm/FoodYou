package com.maksimowiczm.foodyou.feature.food.domain

import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase

interface CreateProductUseCase {

    /**
     * Creates a product in the database and returns its ID.
     *
     * @param product The product to be created.
     * @param event The event associated with the product creation.
     * @return The ID of the created product.
     */
    suspend fun create(product: Product, event: ProductEvent.ProductCreationEvent): FoodId.Product

    /**
     * Creates a product in the database, ensuring that it is unique.
     * If the product already exists, it returns null.
     *
     * @param product The product to be created.
     * @param event The event associated with the product creation.
     * @return The ID of the created product, or null if the product already exists.
     */
    suspend fun createUnique(
        product: Product,
        event: ProductEvent.ProductCreationEvent
    ): FoodId.Product?
}

internal class CreateProductUseCaseImpl(
    foodDatabase: FoodDatabase,
    private val productMapper: ProductMapper,
    private val productEventMapper: ProductEventMapper
) : CreateProductUseCase {

    private val productEventDao = foodDatabase.productEventDao
    private val productDao = foodDatabase.productDao

    // Possible database inconsistency, because it's not run in a transaction.
    // It's possible that the product is created, but the event is not.
    // Very unlikely, but still. Not sure how to handle it properly here.

    override suspend fun create(
        product: Product,
        event: ProductEvent.ProductCreationEvent
    ): FoodId.Product {
        val entity = productMapper.toEntity(product)
        val id = productDao.insert(entity)
        val eventEntity = productEventMapper.toEntity(event, id)
        productEventDao.insert(eventEntity)
        return FoodId.Product(id)
    }

    override suspend fun createUnique(
        product: Product,
        event: ProductEvent.ProductCreationEvent
    ): FoodId.Product? {
        val entity = productMapper.toEntity(product)
        val id = productDao.insertUniqueProduct(entity)

        return if (id != null) {
            val eventEntity = productEventMapper.toEntity(event, id)
            productEventDao.insert(eventEntity)
            FoodId.Product(id)
        } else {
            null
        }
    }
}
