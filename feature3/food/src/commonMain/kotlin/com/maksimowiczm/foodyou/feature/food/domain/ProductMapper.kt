package com.maksimowiczm.foodyou.feature.food.domain

import com.maksimowiczm.foodyou.feature.food.data.database.food.Minerals
import com.maksimowiczm.foodyou.feature.food.data.database.food.Nutrients
import com.maksimowiczm.foodyou.feature.food.data.database.food.Product as ProductEntity
import com.maksimowiczm.foodyou.feature.food.data.database.food.Vitamins
import com.maksimowiczm.foodyou.feature.food.domain.NutrientValue.Companion.toNutrientValue

interface ProductMapper {
    fun toModel(entity: ProductEntity): Product
}

internal class ProductMapperImpl : ProductMapper {
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
        packageWeight = entity.packageWeight,
        servingWeight = entity.servingWeight,
        note = entity.note,
        source = FoodSource(
            type = entity.sourceType,
            url = entity.sourceUrl
        )
    )

    private fun toNutritionFacts(nutrients: Nutrients, vitamins: Vitamins, minerals: Minerals) =
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
