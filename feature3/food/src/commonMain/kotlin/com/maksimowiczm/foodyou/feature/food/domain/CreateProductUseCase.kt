package com.maksimowiczm.foodyou.feature.food.domain

import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import com.maksimowiczm.foodyou.feature.food.data.database.food.Product

interface CreateProductUseCase {

    /**
     * Creates a product in the database and returns its ID.
     *
     * @param event The event associated with the product creation.
     * @return The ID of the created product.
     */
    suspend fun create(
        name: String,
        brand: String?,
        nutritionFacts: NutritionFacts,
        barcode: String?,
        packageWeight: Float?,
        servingWeight: Float?,
        note: String?,
        source: FoodSource,
        isLiquid: Boolean,
        event: FoodEvent.FoodCreationEvent
    ): FoodId.Product

    /**
     * Creates a product in the database, ensuring that it is unique.
     * If the product already exists, it returns null.
     *
     * @param event The event associated with the product creation.
     * @return The ID of the created product, or null if the product already exists.
     */
    suspend fun createUnique(
        name: String,
        brand: String?,
        nutritionFacts: NutritionFacts,
        barcode: String?,
        packageWeight: Float?,
        servingWeight: Float?,
        note: String?,
        source: FoodSource,
        isLiquid: Boolean,
        event: FoodEvent.FoodCreationEvent
    ): FoodId.Product?
}

internal class CreateProductUseCaseImpl(
    foodDatabase: FoodDatabase,
    private val productMapper: ProductMapper,
    private val foodEventMapper: FoodEventMapper
) : CreateProductUseCase {

    private val foodEventDao = foodDatabase.foodEventDao
    private val productDao = foodDatabase.productDao

    // Possible database inconsistency, because it's not run in a transaction.
    // It's possible that the product is created, but the event is not.
    // Very unlikely, but still. Not sure how to handle it properly here.

    override suspend fun create(
        name: String,
        brand: String?,
        nutritionFacts: NutritionFacts,
        barcode: String?,
        packageWeight: Float?,
        servingWeight: Float?,
        note: String?,
        source: FoodSource,
        isLiquid: Boolean,
        event: FoodEvent.FoodCreationEvent
    ): FoodId.Product {
        val (nutrients, vitamins, minerals) = productMapper.toEntityNutrients(nutritionFacts)

        val entity = Product(
            name = name,
            brand = brand,
            barcode = barcode,
            nutrients = nutrients,
            vitamins = vitamins,
            minerals = minerals,
            packageWeight = packageWeight,
            servingWeight = servingWeight,
            note = note,
            sourceType = source.type,
            sourceUrl = source.url,
            isLiquid = isLiquid
        )

        val id = productDao.insert(entity)

        val eventEntity = foodEventMapper.toEntity(event, FoodId.Product(id))
        foodEventDao.insert(eventEntity)

        return FoodId.Product(id)
    }

    override suspend fun createUnique(
        name: String,
        brand: String?,
        nutritionFacts: NutritionFacts,
        barcode: String?,
        packageWeight: Float?,
        servingWeight: Float?,
        note: String?,
        source: FoodSource,
        isLiquid: Boolean,
        event: FoodEvent.FoodCreationEvent
    ): FoodId.Product? {
        val (nutrients, vitamins, minerals) = productMapper.toEntityNutrients(nutritionFacts)
        val entity = Product(
            name = name,
            brand = brand,
            barcode = barcode,
            nutrients = nutrients,
            vitamins = vitamins,
            minerals = minerals,
            packageWeight = packageWeight,
            servingWeight = servingWeight,
            note = note,
            sourceType = source.type,
            sourceUrl = source.url,
            isLiquid = isLiquid
        )

        val id = productDao.insertUniqueProduct(entity)

        return if (id != null) {
            val eventEntity = foodEventMapper.toEntity(event, FoodId.Product(id))
            foodEventDao.insert(eventEntity)
            FoodId.Product(id)
        } else {
            null
        }
    }
}
