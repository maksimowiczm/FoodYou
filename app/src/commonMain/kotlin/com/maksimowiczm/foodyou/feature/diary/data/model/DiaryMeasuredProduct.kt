package com.maksimowiczm.foodyou.feature.diary.data.model

import com.maksimowiczm.foodyou.ext.sumOf

data class DiaryMeasuredProduct(
    val product: Product,
    val measurement: WeightMeasurement,
    val measurementId: MeasurementId.Product
) {
    val weight: Float
        get() = when (measurement) {
            is WeightMeasurement.Package -> product.packageWeight!! * measurement.quantity
            is WeightMeasurement.Serving -> product.servingWeight!! * measurement.quantity
            is WeightMeasurement.WeightUnit -> measurement.weight
        }

    val calories: Float
        get() = product.nutrients.calories * weight / 100f

    val proteins: Float
        get() = product.nutrients.proteins * weight / 100f

    val carbohydrates: Float
        get() = product.nutrients.carbohydrates * weight / 100f

    val fats: Float
        get() = product.nutrients.fats * weight / 100f
}

fun List<DiaryMeasuredProduct>.nutrientValues(): Nutrients {
    val totalCalories = sumOf { it.calories }
    val totalProteins = sumOf { it.proteins }
    val totalCarbohydrates = sumOf { it.carbohydrates }
    val totalFats = sumOf { it.fats }

    return Nutrients(
        calories = totalCalories,
        proteins = totalProteins,
        carbohydrates = totalCarbohydrates,
        sugars = map { it.product.nutrients.sugars }.sumOf(),
        fats = totalFats,
        saturatedFats = map { it.product.nutrients.saturatedFats }.sumOf(),
        salt = map { it.product.nutrients.salt }.sumOf(),
        sodium = map { it.product.nutrients.sodium }.sumOf(),
        fiber = map { it.product.nutrients.fiber }.sumOf()
    )
}
