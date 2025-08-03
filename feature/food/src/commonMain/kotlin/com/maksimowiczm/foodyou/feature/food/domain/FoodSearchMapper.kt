package com.maksimowiczm.foodyou.feature.food.domain

import com.maksimowiczm.foodyou.feature.food.data.database.search.FoodSearch as FoodSearchData
import com.maksimowiczm.foodyou.feature.food.domain.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import kotlinx.serialization.json.Json

interface FoodSearchMapper {
    fun toModel(data: FoodSearchData): FoodSearch
}

internal class FoodSearchMapperImpl : FoodSearchMapper {
    override fun toModel(data: FoodSearchData): FoodSearch = with(data) {
        val foodId = when {
            productId != null -> FoodId.Product(productId)
            recipeId != null -> FoodId.Recipe(recipeId)
            else -> error("Food must have either productId or recipeId")
        }

        val decodedMeasurement: Measurement? = data.measurementJson?.let(Json::decodeFromString)

        when (foodId) {
            is FoodId.Product -> FoodSearch.Product(
                id = foodId,
                headline = data.headline,
                isLiquid = data.isLiquid,
                nutritionFacts = NutritionFacts(
                    proteins = nutrients?.proteins.toNutrientValue(),
                    carbohydrates = nutrients?.carbohydrates.toNutrientValue(),
                    energy = nutrients?.energy.toNutrientValue(),
                    fats = nutrients?.fats.toNutrientValue(),
                    saturatedFats = nutrients?.saturatedFats.toNutrientValue(),
                    transFats = nutrients?.transFats.toNutrientValue(),
                    monounsaturatedFats = nutrients?.monounsaturatedFats.toNutrientValue(),
                    polyunsaturatedFats = nutrients?.polyunsaturatedFats.toNutrientValue(),
                    omega3 = nutrients?.omega3.toNutrientValue(),
                    omega6 = nutrients?.omega6.toNutrientValue(),
                    sugars = nutrients?.sugars.toNutrientValue(),
                    addedSugars = nutrients?.addedSugars.toNutrientValue(),
                    dietaryFiber = nutrients?.dietaryFiber.toNutrientValue(),
                    solubleFiber = nutrients?.solubleFiber.toNutrientValue(),
                    insolubleFiber = nutrients?.insolubleFiber.toNutrientValue(),
                    salt = nutrients?.salt.toNutrientValue(),
                    cholesterolMilli = nutrients?.cholesterolMilli.toNutrientValue(),
                    caffeineMilli = nutrients?.caffeineMilli.toNutrientValue(),
                    vitaminAMicro = vitamins?.vitaminAMicro.toNutrientValue(),
                    vitaminB1Milli = vitamins?.vitaminB1Milli.toNutrientValue(),
                    vitaminB2Milli = vitamins?.vitaminB2Milli.toNutrientValue(),
                    vitaminB3Milli = vitamins?.vitaminB3Milli.toNutrientValue(),
                    vitaminB5Milli = vitamins?.vitaminB5Milli.toNutrientValue(),
                    vitaminB6Milli = vitamins?.vitaminB6Milli.toNutrientValue(),
                    vitaminB7Micro = vitamins?.vitaminB7Micro.toNutrientValue(),
                    vitaminB9Micro = vitamins?.vitaminB9Micro.toNutrientValue(),
                    vitaminB12Micro = vitamins?.vitaminB12Micro.toNutrientValue(),
                    vitaminCMilli = vitamins?.vitaminCMilli.toNutrientValue(),
                    vitaminDMicro = vitamins?.vitaminDMicro.toNutrientValue(),
                    vitaminEMilli = vitamins?.vitaminEMilli.toNutrientValue(),
                    vitaminKMicro = vitamins?.vitaminKMicro.toNutrientValue(),
                    manganeseMilli = minerals?.manganeseMilli.toNutrientValue(),
                    magnesiumMilli = minerals?.magnesiumMilli.toNutrientValue(),
                    potassiumMilli = minerals?.potassiumMilli.toNutrientValue(),
                    calciumMilli = minerals?.calciumMilli.toNutrientValue(),
                    copperMilli = minerals?.copperMilli.toNutrientValue(),
                    zincMilli = minerals?.zincMilli.toNutrientValue(),
                    sodiumMilli = minerals?.sodiumMilli.toNutrientValue(),
                    ironMilli = minerals?.ironMilli.toNutrientValue(),
                    phosphorusMilli = minerals?.phosphorusMilli.toNutrientValue(),
                    seleniumMicro = minerals?.seleniumMicro.toNutrientValue(),
                    iodineMicro = minerals?.iodineMicro.toNutrientValue(),
                    chromiumMicro = minerals?.chromiumMicro.toNutrientValue()
                ),
                totalWeight = data.totalWeight,
                servingWeight = data.servingWeight,
                defaultMeasurement = decodedMeasurement ?: defaultMeasurement(data)
            )

            is FoodId.Recipe -> FoodSearch.Recipe(
                id = foodId,
                headline = data.headline,
                isLiquid = data.isLiquid,
                defaultMeasurement = decodedMeasurement ?: Measurement.Serving(1f)
            )
        }
    }
}

private fun defaultMeasurement(food: FoodSearchData) = when {
    food.servingWeight != null -> Measurement.Serving(1f)
    food.totalWeight != null -> Measurement.Package(1f)
    else -> Measurement.Gram(100f)
}
