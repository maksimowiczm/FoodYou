package com.maksimowiczm.foodyou.foodyou3.infrastructure

import com.maksimowiczm.foodyou.foodyou3.domain.FoodYou3MeasurementType

internal object FoodYou3MeasurementTypeConverter {
    fun toWeightMeasurementType(weightMeasurementType: Int) =
        when (weightMeasurementType) {
            FoodYou3MeasurementTypeSQLConstants.GRAM -> FoodYou3MeasurementType.Gram
            FoodYou3MeasurementTypeSQLConstants.MILLILITER -> FoodYou3MeasurementType.Milliliter
            FoodYou3MeasurementTypeSQLConstants.PACKAGE -> FoodYou3MeasurementType.Package
            FoodYou3MeasurementTypeSQLConstants.SERVING -> FoodYou3MeasurementType.Serving
            FoodYou3MeasurementTypeSQLConstants.OUNCE -> FoodYou3MeasurementType.Ounce
            FoodYou3MeasurementTypeSQLConstants.FLUID_OUNCE -> FoodYou3MeasurementType.FluidOunce
            else -> error("WeightMeasurementType not found")
        }
}

internal object FoodYou3MeasurementTypeSQLConstants {
    const val GRAM = 0
    const val PACKAGE = 1
    const val SERVING = 2
    const val MILLILITER = 3
    const val OUNCE = 4
    const val FLUID_OUNCE = 5
}
