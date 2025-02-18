package com.maksimowiczm.foodyou.core.feature.addfood.data.model

import com.maksimowiczm.foodyou.core.feature.addfood.database.ProductWithWeightMeasurementEntity
import com.maksimowiczm.foodyou.core.feature.product.data.model.Product
import com.maksimowiczm.foodyou.core.feature.product.data.model.toDomain
import kotlin.math.roundToInt

data class ProductWithWeightMeasurement(
    val product: Product,
    val measurement: WeightMeasurement
) {
    val weight: Float
        get() = measurement.weight

    val calories: Int
        get() = product.nutrients.calories(weight).roundToInt()

    val proteins: Int
        get() = product.nutrients.proteins(weight).roundToInt()

    val carbohydrates: Int
        get() = product.nutrients.carbohydrates(weight).roundToInt()

    val fats: Int
        get() = product.nutrients.fats(weight).roundToInt()
}

fun ProductWithWeightMeasurementEntity.toDomain(): ProductWithWeightMeasurement {
    val weightMeasurement = when (this.weightMeasurement.measurement) {
        WeightMeasurementEnum.WeightUnit -> WeightMeasurement.WeightUnit(
            weight = this.weightMeasurement.quantity
        )

        WeightMeasurementEnum.Package -> WeightMeasurement.Package(
            packageWeight = this.product.packageWeight!!,
            quantity = this.weightMeasurement.quantity
        )

        WeightMeasurementEnum.Serving -> WeightMeasurement.Serving(
            servingWeight = this.product.servingWeight!!,
            quantity = this.weightMeasurement.quantity
        )
    }

    return ProductWithWeightMeasurement(
        product = this.product.toDomain(),
        measurement = weightMeasurement
    )
}
