package com.maksimowiczm.foodyou.feature.food.domain

import com.maksimowiczm.foodyou.feature.food.data.database.food.Minerals
import com.maksimowiczm.foodyou.feature.food.data.database.food.Nutrients
import com.maksimowiczm.foodyou.feature.food.data.database.food.Product as ProductEntity
import com.maksimowiczm.foodyou.feature.food.data.database.food.Vitamins
import com.maksimowiczm.foodyou.feature.food.domain.NutrientValue.Companion.toNutrientValue

interface ProductMapper {
    fun toModel(entity: ProductEntity): Product

    fun toEntity(model: Product): ProductEntity
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
        ),
        isLiquid = entity.isLiquid
    )

    override fun toEntity(model: Product): ProductEntity {
        val (nutrients, vitamins, minerals) = toEntityNutrients(model.nutritionFacts)

        return ProductEntity(
            id = model.id.id,
            name = model.name,
            brand = model.brand,
            barcode = model.barcode,
            nutrients = nutrients,
            vitamins = vitamins,
            minerals = minerals,
            packageWeight = model.packageWeight,
            servingWeight = model.servingWeight,
            note = model.note,
            sourceType = model.source.type,
            sourceUrl = model.source.url,
            isLiquid = model.isLiquid
        )
    }

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

    private fun toEntityNutrients(
        nutritionFacts: NutritionFacts
    ): Triple<Nutrients, Vitamins, Minerals> {
        val nutrients = Nutrients(
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
            iodineMicro = nutritionFacts.iodineMicro.value,
            chromiumMicro = nutritionFacts.chromiumMicro.value
        )
        return Triple(nutrients, vitamins, minerals)
    }
}
