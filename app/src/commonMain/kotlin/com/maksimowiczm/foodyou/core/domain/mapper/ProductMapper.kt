package com.maksimowiczm.foodyou.core.domain.mapper

import com.maksimowiczm.foodyou.core.data.model.Minerals
import com.maksimowiczm.foodyou.core.data.model.Nutrients
import com.maksimowiczm.foodyou.core.data.model.Vitamins
import com.maksimowiczm.foodyou.core.data.model.product.ProductEntity
import com.maksimowiczm.foodyou.core.data.model.product.ProductSource
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.PortionWeight
import com.maksimowiczm.foodyou.core.domain.model.Product

object ProductMapper {
    @JvmName("toModelFromEntity")
    fun toModel(entity: ProductEntity): Product = entity.toModel()

    fun ProductEntity.toModel() = Product(
        id = FoodId.Product(id),
        name = name,
        brand = brand,
        barcode = barcode,
        nutritionFacts = NutritionFactsMapper.toNutritionFacts(nutrients, vitamins, minerals),
        packageWeight = packageWeight?.let { PortionWeight.Package(it) },
        servingWeight = servingWeight?.let { PortionWeight.Serving(it) }
    )

    fun Product.toEntity(source: ProductSource = ProductSource.User): ProductEntity {
        val nutrients = Nutrients(
            proteins = nutritionFacts.proteins.value,
            carbohydrates = nutritionFacts.carbohydrates.value,
            fats = nutritionFacts.fats.value,
            calories = nutritionFacts.calories.value,
            saturatedFats = nutritionFacts.saturatedFats.value,
            monounsaturatedFats = nutritionFacts.monounsaturatedFats.value,
            polyunsaturatedFats = nutritionFacts.polyunsaturatedFats.value,
            omega3 = nutritionFacts.omega3.value,
            omega6 = nutritionFacts.omega6.value,
            sugars = nutritionFacts.sugars.value,
            salt = nutritionFacts.salt.value,
            fiber = nutritionFacts.fiber.value,
            cholesterolMilli = nutritionFacts.cholesterolMilli.value,
            caffeineMilli = nutritionFacts.caffeineMilli.value
        )

        val vitamins = Vitamins(
            vitaminAMicro = nutritionFacts.vitaminAMicro.value,
            vitaminB1Milli = nutritionFacts.vitaminB1Milli.value,
            vitaminB2Milli = nutritionFacts.vitaminB2Milli.value,
            vitaminB3Milli = nutritionFacts.vitaminB3Milli.value,
            vitaminB5Milli = nutritionFacts.vitaminB5Milli.value,
            vitaminB6Milli = nutritionFacts.vitaminB6Milli.value,
            vitaminB7Micro = nutritionFacts.vitaminB7Micro.value,
            vitaminB9Micro = nutritionFacts.vitaminB9Micro.value,
            vitaminB12Micro = nutritionFacts.vitaminB12Micro.value,
            vitaminCMilli = nutritionFacts.vitaminCMilli.value,
            vitaminDMicro = nutritionFacts.vitaminDMicro.value,
            vitaminEMilli = nutritionFacts.vitaminEMilli.value,
            vitaminKMicro = nutritionFacts.vitaminKMicro.value
        )

        val minerals = Minerals(
            manganeseMilli = nutritionFacts.manganeseMilli.value,
            magnesiumMilli = nutritionFacts.magnesiumMilli.value,
            potassiumMilli = nutritionFacts.potassiumMilli.value,
            calciumMilli = nutritionFacts.calciumMilli.value,
            copperMilli = nutritionFacts.copperMilli.value,
            zincMilli = nutritionFacts.zincMilli.value,
            sodiumMilli = nutritionFacts.sodiumMilli.value,
            ironMilli = nutritionFacts.ironMilli.value,
            phosphorusMilli = nutritionFacts.phosphorusMilli.value,
            seleniumMicro = nutritionFacts.seleniumMicro.value,
            iodineMicro = nutritionFacts.iodineMicro.value
        )

        return ProductEntity(
            id = id.id,
            name = name,
            brand = brand?.takeIf { it.isNotBlank() },
            barcode = barcode?.takeIf { it.isNotBlank() },
            nutrients = nutrients,
            vitamins = vitamins,
            minerals = minerals,
            packageWeight = packageWeight?.weight,
            servingWeight = servingWeight?.weight,
            productSource = source
        )
    }
}
