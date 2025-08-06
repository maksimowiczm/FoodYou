package com.maksimowiczm.foodyou.business.food.infrastructure.persistence.room

import com.maksimowiczm.foodyou.business.food.domain.FoodId
import com.maksimowiczm.foodyou.business.food.domain.FoodSource
import com.maksimowiczm.foodyou.business.food.domain.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.business.food.domain.NutritionFacts
import com.maksimowiczm.foodyou.business.food.domain.Product
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalProductDataSource
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.FoodSourceType
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.Minerals
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.Nutrients
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.ProductDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.ProductEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.Vitamins
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class RoomProductDataSource(private val productDao: ProductDao) : LocalProductDataSource {
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
        source = FoodSource(type = this.sourceType.toModel(), url = this.sourceUrl),
        nutritionFacts = this.toNutritionFacts(),
    )

private fun FoodSourceType.toModel(): FoodSource.Type =
    when (this) {
        FoodSourceType.User -> FoodSource.Type.User
        FoodSourceType.OpenFoodFacts -> FoodSource.Type.OpenFoodFacts
        FoodSourceType.USDA -> FoodSource.Type.USDA
        FoodSourceType.SwissFoodCompositionDatabase -> FoodSource.Type.SwissFoodCompositionDatabase
    }

private fun ProductEntity.toNutritionFacts(): NutritionFacts =
    NutritionFacts(
        proteins = nutrients.proteins.toNutrientValue(),
        carbohydrates = nutrients.carbohydrates.toNutrientValue(),
        energy = nutrients.energy.toNutrientValue(),
        fats = nutrients.fats.toNutrientValue(),
        saturatedFats = nutrients.saturatedFats.toNutrientValue(),
        transFats = nutrients.transFats.toNutrientValue(),
        monounsaturatedFats = nutrients.monounsaturatedFats.toNutrientValue(),
        polyunsaturatedFats = nutrients.polyunsaturatedFats.toNutrientValue(),
        omega3 = nutrients.omega3.toNutrientValue(),
        omega6 = nutrients.omega6.toNutrientValue(),
        sugars = nutrients.sugars.toNutrientValue(),
        addedSugars = nutrients.addedSugars.toNutrientValue(),
        dietaryFiber = nutrients.dietaryFiber.toNutrientValue(),
        solubleFiber = nutrients.solubleFiber.toNutrientValue(),
        insolubleFiber = nutrients.insolubleFiber.toNutrientValue(),
        salt = nutrients.salt.toNutrientValue(),
        cholesterol = nutrients.cholesterolMilli.toNutrientValue() / 1_000.0,
        caffeine = nutrients.caffeineMilli.toNutrientValue() / 1_000.0,
        vitaminA = vitamins.vitaminAMicro.toNutrientValue() / 1_000_000.0,
        vitaminB1 = vitamins.vitaminB1Milli.toNutrientValue() / 1_000.0,
        vitaminB2 = vitamins.vitaminB2Milli.toNutrientValue() / 1_000.0,
        vitaminB3 = vitamins.vitaminB3Milli.toNutrientValue() / 1_000.0,
        vitaminB5 = vitamins.vitaminB5Milli.toNutrientValue() / 1_000.0,
        vitaminB6 = vitamins.vitaminB6Milli.toNutrientValue() / 1_000.0,
        vitaminB7 = vitamins.vitaminB7Micro.toNutrientValue() / 1_000_000.0,
        vitaminB9 = vitamins.vitaminB9Micro.toNutrientValue() / 1_000_000.0,
        vitaminB12 = vitamins.vitaminB12Micro.toNutrientValue() / 1_000_000.0,
        vitaminC = vitamins.vitaminCMilli.toNutrientValue() / 1_000.0,
        vitaminD = vitamins.vitaminDMicro.toNutrientValue() / 1_000_000.0,
        vitaminE = vitamins.vitaminEMilli.toNutrientValue() / 1_000.0,
        vitaminK = vitamins.vitaminKMicro.toNutrientValue() / 1_000_000.0,
        manganese = minerals.manganeseMilli.toNutrientValue() / 1_000.0,
        magnesium = minerals.magnesiumMilli.toNutrientValue() / 1_000.0,
        potassium = minerals.potassiumMilli.toNutrientValue() / 1_000.0,
        calcium = minerals.calciumMilli.toNutrientValue() / 1_000.0,
        copper = minerals.copperMilli.toNutrientValue() / 1_000.0,
        zinc = minerals.zincMilli.toNutrientValue() / 1_000.0,
        sodium = minerals.sodiumMilli.toNutrientValue() / 1_000.0,
        iron = minerals.ironMilli.toNutrientValue() / 1_000.0,
        phosphorus = minerals.phosphorusMilli.toNutrientValue() / 1_000.0,
        selenium = minerals.seleniumMicro.toNutrientValue() / 1_000_000.0,
        iodine = minerals.iodineMicro.toNutrientValue() / 1_000_000.0,
        chromium = minerals.chromiumMicro.toNutrientValue() / 1_000_000.0,
    )

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

private fun toEntityNutrients(
    nutritionFacts: NutritionFacts
): Triple<Nutrients, Vitamins, Minerals> {
    val nutrients =
        Nutrients(
            proteins = nutritionFacts.proteins.value,
            carbohydrates = nutritionFacts.carbohydrates.value,
            energy = nutritionFacts.energy.value,
            fats = nutritionFacts.fats.value,
            saturatedFats = nutritionFacts.saturatedFats.value,
            transFats = nutritionFacts.transFats.value,
            monounsaturatedFats = nutritionFacts.monounsaturatedFats.value,
            polyunsaturatedFats = nutritionFacts.polyunsaturatedFats.value,
            omega3 = nutritionFacts.omega3.value,
            omega6 = nutritionFacts.omega6.value,
            sugars = nutritionFacts.sugars.value,
            addedSugars = nutritionFacts.addedSugars.value,
            dietaryFiber = nutritionFacts.dietaryFiber.value,
            solubleFiber = nutritionFacts.solubleFiber.value,
            insolubleFiber = nutritionFacts.insolubleFiber.value,
            salt = nutritionFacts.salt.value,
            cholesterolMilli = nutritionFacts.cholesterol.value?.times(1_000.0),
            caffeineMilli = nutritionFacts.caffeine.value?.times(1_000.0),
        )
    val vitamins =
        Vitamins(
            vitaminAMicro = nutritionFacts.vitaminA.value?.times(1_000_000.0),
            vitaminB1Milli = nutritionFacts.vitaminB1.value?.times(1_000.0),
            vitaminB2Milli = nutritionFacts.vitaminB2.value?.times(1_000.0),
            vitaminB3Milli = nutritionFacts.vitaminB3.value?.times(1_000.0),
            vitaminB5Milli = nutritionFacts.vitaminB5.value?.times(1_000.0),
            vitaminB6Milli = nutritionFacts.vitaminB6.value?.times(1_000.0),
            vitaminB7Micro = nutritionFacts.vitaminB7.value?.times(1_000_000.0),
            vitaminB9Micro = nutritionFacts.vitaminB9.value?.times(1_000_000.0),
            vitaminB12Micro = nutritionFacts.vitaminB12.value?.times(1_000_000.0),
            vitaminCMilli = nutritionFacts.vitaminC.value?.times(1_000.0),
            vitaminDMicro = nutritionFacts.vitaminD.value?.times(1_000_000.0),
            vitaminEMilli = nutritionFacts.vitaminE.value?.times(1_000.0),
            vitaminKMicro = nutritionFacts.vitaminK.value?.times(1_000_000.0),
        )
    val minerals =
        Minerals(
            manganeseMilli = nutritionFacts.manganese.value?.times(1_000.0),
            magnesiumMilli = nutritionFacts.magnesium.value?.times(1_000.0),
            potassiumMilli = nutritionFacts.potassium.value?.times(1_000.0),
            calciumMilli = nutritionFacts.calcium.value?.times(1_000.0),
            copperMilli = nutritionFacts.copper.value?.times(1_000.0),
            zincMilli = nutritionFacts.zinc.value?.times(1_000.0),
            sodiumMilli = nutritionFacts.sodium.value?.times(1_000.0),
            ironMilli = nutritionFacts.iron.value?.times(1_000.0),
            phosphorusMilli = nutritionFacts.phosphorus.value?.times(1_000.0),
            seleniumMicro = nutritionFacts.selenium.value?.times(1_000_000.0),
            iodineMicro = nutritionFacts.iodine.value?.times(1_000_000.0),
            chromiumMicro = nutritionFacts.chromium.value?.times(1_000_000.0),
        )
    return Triple(nutrients, vitamins, minerals)
}
