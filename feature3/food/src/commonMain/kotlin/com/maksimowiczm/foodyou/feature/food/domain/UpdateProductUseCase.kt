package com.maksimowiczm.foodyou.feature.food.domain

import com.maksimowiczm.foodyou.core.ext.now
import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.LocalDateTime

interface UpdateProductUseCase {
    suspend fun update(
        id: FoodId.Product,
        name: String,
        brand: String?,
        nutritionFacts: NutritionFacts,
        barcode: String?,
        packageWeight: Float?,
        servingWeight: Float?,
        note: String?,
        source: FoodSource,
        isLiquid: Boolean
    )
}

internal class UpdateProductUseCaseImpl(
    foodDatabase: FoodDatabase,
    private val productMapper: ProductMapper,
    private val foodEventMapper: FoodEventMapper
) : UpdateProductUseCase {
    private val productDao = foodDatabase.productDao
    private val foodEventDao = foodDatabase.foodEventDao

    override suspend fun update(
        id: FoodId.Product,
        name: String,
        brand: String?,
        nutritionFacts: NutritionFacts,
        barcode: String?,
        packageWeight: Float?,
        servingWeight: Float?,
        note: String?,
        source: FoodSource,
        isLiquid: Boolean
    ) {
        val oldProduct = productDao.observe(id.id).firstOrNull()

        if (oldProduct == null) {
            error("Product with id ${id.id} does not exist.")
        }

        val (nutrients, vitamins, minerals) = productMapper.toEntityNutrients(nutritionFacts)
        val updatedProduct = oldProduct.copy(
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

        productDao.update(updatedProduct)

        val eventEntity = foodEventMapper.toEntity(
            model = FoodEvent.Edited(
                date = LocalDateTime.now(),
                oldFood = productMapper.toModel(oldProduct)
            ),
            foodId = id
        )

        foodEventDao.insert(eventEntity)
    }
}
