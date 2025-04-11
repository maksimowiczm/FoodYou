package com.maksimowiczm.foodyou.core.repository

import com.maksimowiczm.foodyou.core.database.FoodYouDatabase
import com.maksimowiczm.foodyou.core.database.product.ProductDao
import com.maksimowiczm.foodyou.core.database.product.ProductEntity
import com.maksimowiczm.foodyou.core.model.Food
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.core.model.Nutrients
import com.maksimowiczm.foodyou.core.model.PortionWeight
import com.maksimowiczm.foodyou.core.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface FoodRepository {
    fun observeFood(id: FoodId): Flow<Food?>

    suspend fun deleteFood(id: FoodId)
}

internal class FoodRepositoryImpl(database: FoodYouDatabase) : FoodRepository {
    private val productDao: ProductDao = database.productDao

    override fun observeFood(id: FoodId): Flow<Food?> = when (id) {
        is FoodId.Product -> productDao.observeProduct(id.id).map { it?.toFood() }
    }

    override suspend fun deleteFood(id: FoodId) {
        when (id) {
            is FoodId.Product -> productDao.deleteProduct(id.id)
        }
    }
}

private fun ProductEntity.toFood(): Food = Product(
    id = FoodId.Product(id),
    name = name,
    brand = brand,
    barcode = barcode,
    nutrients = Nutrients(
        calories = nutrients.calories.toNutrientValue(),
        proteins = nutrients.proteins.toNutrientValue(),
        carbohydrates = nutrients.carbohydrates.toNutrientValue(),
        sugars = nutrients.sugars.toNutrientValue(),
        fats = nutrients.fats.toNutrientValue(),
        saturatedFats = nutrients.saturatedFats.toNutrientValue(),
        salt = nutrients.salt.toNutrientValue(),
        sodium = nutrients.sodium.toNutrientValue(),
        fiber = nutrients.fiber.toNutrientValue()
    ),
    packageWeight = packageWeight?.let { PortionWeight.Package(it) },
    servingWeight = servingWeight?.let { PortionWeight.Serving(it) }
)
