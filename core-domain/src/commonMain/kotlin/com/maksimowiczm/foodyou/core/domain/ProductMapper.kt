package com.maksimowiczm.foodyou.core.domain

import com.maksimowiczm.foodyou.core.database.food.Minerals
import com.maksimowiczm.foodyou.core.database.food.Nutrients
import com.maksimowiczm.foodyou.core.database.food.ProductEntity
import com.maksimowiczm.foodyou.core.database.food.Vitamins
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.core.model.NutritionFacts
import com.maksimowiczm.foodyou.core.model.Product

interface ProductMapper {
    fun toModel(entity: ProductEntity): Product
}

internal object ProductMapperImpl : ProductMapper {
    override fun toModel(entity: ProductEntity): Product = Product(
        id = FoodId.Product(entity.id),
        name = entity.name,
        brand = entity.brand,
        barcode = entity.barcode,
        nutritionFacts = toNutritionFacts(
            nutrients = entity.nutrients,
            vitamins = entity.vitamins,
            minerals = entity.minerals
        ),
        totalWeight = entity.packageWeight,
        servingWeight = entity.servingWeight,
        isLiquid = entity.isLiquid
    )

    private fun toNutritionFacts(nutrients: Nutrients, vitamins: Vitamins, minerals: Minerals) =
        NutritionFacts(
            calories = nutrients.calories.toNutrientValue(),
            proteins = nutrients.proteins.toNutrientValue(),
            carbohydrates = nutrients.carbohydrates.toNutrientValue(),
            sugars = nutrients.sugars.toNutrientValue(),
            fats = nutrients.fats.toNutrientValue(),
            saturatedFats = nutrients.saturatedFats.toNutrientValue(),
            monounsaturatedFats = nutrients.monounsaturatedFats.toNutrientValue(),
            polyunsaturatedFats = nutrients.polyunsaturatedFats.toNutrientValue(),
            omega3 = nutrients.omega3.toNutrientValue(),
            omega6 = nutrients.omega6.toNutrientValue(),
            salt = nutrients.salt.toNutrientValue(),
            fiber = nutrients.fiber.toNutrientValue(),
            cholesterolMilli = nutrients.cholesterolMilli.toNutrientValue(),
            caffeineMilli = nutrients.caffeineMilli.toNutrientValue(),
            vitaminAMicro = vitamins.vitaminAMicro.toNutrientValue(),
            vitaminB1Milli = vitamins.vitaminB1Milli.toNutrientValue(),
            vitaminB2Milli = vitamins.vitaminB2Milli.toNutrientValue(),
            vitaminB3Milli = vitamins.vitaminB3Milli.toNutrientValue(),
            vitaminB5Milli = vitamins.vitaminB5Milli.toNutrientValue(),
            vitaminB6Milli = vitamins.vitaminB6Milli.toNutrientValue(),
            vitaminB7Micro = vitamins.vitaminB7Micro.toNutrientValue(),
            vitaminB9Micro = vitamins.vitaminB9Micro.toNutrientValue(),
            vitaminB12Micro = vitamins.vitaminB12Micro.toNutrientValue(),
            vitaminCMilli = vitamins.vitaminCMilli.toNutrientValue(),
            vitaminDMicro = vitamins.vitaminDMicro.toNutrientValue(),
            vitaminEMilli = vitamins.vitaminEMilli.toNutrientValue(),
            vitaminKMicro = vitamins.vitaminKMicro.toNutrientValue(),
            manganeseMilli = minerals.manganeseMilli.toNutrientValue(),
            magnesiumMilli = minerals.magnesiumMilli.toNutrientValue(),
            potassiumMilli = minerals.potassiumMilli.toNutrientValue(),
            calciumMilli = minerals.calciumMilli.toNutrientValue(),
            copperMilli = minerals.copperMilli.toNutrientValue(),
            zincMilli = minerals.zincMilli.toNutrientValue(),
            sodiumMilli = minerals.sodiumMilli.toNutrientValue(),
            ironMilli = minerals.ironMilli.toNutrientValue(),
            phosphorusMilli = minerals.phosphorusMilli.toNutrientValue(),
            seleniumMicro = minerals.seleniumMicro.toNutrientValue(),
            iodineMicro = minerals.iodineMicro.toNutrientValue(),
            chromiumMicro = minerals.chromiumMicro.toNutrientValue()
        )
}
