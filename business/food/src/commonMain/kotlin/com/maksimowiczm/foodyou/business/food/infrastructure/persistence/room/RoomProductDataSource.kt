package com.maksimowiczm.foodyou.business.food.infrastructure.persistence.room

import com.maksimowiczm.foodyou.business.food.domain.Product
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalProductDataSource
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodSource
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.ProductDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.ProductEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.toDomain
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.toEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.toEntityNutrients
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.toNutritionFacts
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapValues

internal class RoomProductDataSource(private val productDao: ProductDao) : LocalProductDataSource {
    override fun observeProducts(limit: Int, offset: Int): Flow<List<Product>> =
        productDao.observeProducts(limit, offset).mapValues(ProductEntity::toModel)

    override fun observeProduct(id: FoodId.Product): Flow<Product?> =
        productDao.observeProduct(id.id).map { it?.toModel() }

    override suspend fun deleteProduct(product: Product) {
        val entity = product.toEntity()
        productDao.deleteProduct(entity)
    }

    override suspend fun insertProduct(product: Product): FoodId.Product {
        val entity = product.toEntity()
        val id = productDao.insertProduct(entity)
        return FoodId.Product(id)
    }

    override suspend fun insertUniqueProduct(product: Product): FoodId.Product? =
        productDao.insertUniqueProduct(product.toEntity())?.let(FoodId::Product)

    override suspend fun updateProduct(product: Product) {
        productDao.updateProduct(product.toEntity())
    }
}

private fun ProductEntity.toModel(): Product =
    Product(
        id = FoodId.Product(this.id),
        name = this.name,
        brand = this.brand,
        barcode = this.barcode,
        note = this.note,
        isLiquid = this.isLiquid,
        packageWeight = this.packageWeight,
        servingWeight = this.servingWeight,
        source = FoodSource(type = this.sourceType.toDomain(), url = this.sourceUrl),
        nutritionFacts = this.toNutritionFacts(),
    )

private fun ProductEntity.toNutritionFacts(): NutritionFacts =
    toNutritionFacts(nutrients, vitamins, minerals)

private fun Product.toEntity(): ProductEntity {
    val (nutrients, vitamins, minerals) = toEntityNutrients(nutritionFacts)

    return ProductEntity(
        id = id.id,
        name = name,
        brand = brand,
        barcode = barcode,
        nutrients = nutrients,
        vitamins = vitamins,
        minerals = minerals,
        packageWeight = packageWeight,
        servingWeight = servingWeight,
        note = note,
        sourceType = source.type.toEntity(),
        sourceUrl = source.url,
        isLiquid = isLiquid,
    )
}
