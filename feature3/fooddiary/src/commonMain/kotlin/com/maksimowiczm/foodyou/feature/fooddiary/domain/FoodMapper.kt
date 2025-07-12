package com.maksimowiczm.foodyou.feature.fooddiary.domain

import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.feature.food.domain.NutritionFacts
import com.maksimowiczm.foodyou.feature.food.domain.ProductMapper
import com.maksimowiczm.foodyou.feature.fooddiary.data.Food as FoodEntity
import com.maksimowiczm.foodyou.feature.fooddiary.data.FoodWithMeasurement as FoodWithMeasurementEntity
import com.maksimowiczm.foodyou.feature.fooddiary.data.Measurement
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface FoodMapper {
    fun toFoodMeasurement(entity: Measurement): FoodMeasurement

    fun toFood(entity: FoodEntity): Food

    fun toFoodWithMeasurement(entity: FoodWithMeasurementEntity): FoodWithMeasurement
}

internal class FoodMapperImpl(private val productMapper: ProductMapper) : FoodMapper {
    override fun toFoodMeasurement(entity: Measurement): FoodMeasurement = with(entity) {
        val foodId = when {
            productId != null -> FoodId.Product(productId)
            recipeId != null -> FoodId.Recipe(recipeId)
            else -> error("Measurement must have either productId or recipeId")
        }

        FoodMeasurement(
            foodId = foodId,
            measurementId = id,
            measurement = toMeasurement()
        )
    }

    override fun toFood(entity: FoodEntity): Food = with(entity) {
        val foodId = when {
            productId != null -> FoodId.Product(productId)
            recipeId != null -> FoodId.Recipe(recipeId)
            else -> error("Food must have either productId or recipeId")
        }

        val facts = NutritionFacts(
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

        Food(
            id = foodId,
            headline = entity.headline,
            nutritionFacts = facts,
            totalWeight = entity.totalWeight,
            servingWeight = entity.servingWeight
        )
    }

    @OptIn(ExperimentalTime::class)
    override fun toFoodWithMeasurement(entity: FoodWithMeasurementEntity): FoodWithMeasurement {
        val product = entity.product?.let(productMapper::toModel)

        val food = when {
            product != null -> product
            else -> error("FoodWithMeasurement must have either product or recipe")
        }

        val date = Instant
            .fromEpochSeconds(entity.measurement.createdAt)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date

        return FoodWithMeasurement(
            measurementId = entity.measurement.id,
            measurement = com.maksimowiczm.foodyou.feature.measurement.domain.Measurement.from(
                entity.measurement.measurement,
                entity.measurement.quantity
            ),
            measurementDate = date,
            mealId = entity.measurement.mealId,
            food = food
        )
    }
}
