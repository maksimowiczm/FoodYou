package com.maksimowiczm.foodyou.feature.addfood.data.model

import com.maksimowiczm.foodyou.feature.addfood.database.ProductSearchEntity
import com.maksimowiczm.foodyou.feature.addfood.database.ProductWithWeightMeasurementEntity
import com.maksimowiczm.foodyou.feature.product.data.model.Product
import com.maksimowiczm.foodyou.feature.product.data.model.toDomain
import kotlin.math.roundToInt

data class ProductWithWeightMeasurement(
    val product: Product,
    val measurementId: Long?,
    val measurement: WeightMeasurement
) {
    val weight: Float
        get() = measurement.weight

    val calories: Int
        get() = product.calories(weight).roundToInt()
}

fun ProductSearchEntity.toDomain(): ProductWithWeightMeasurement {
    val weightMeasurement = when (this.weightMeasurement?.measurement) {
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

        null -> if (product.servingWeight != null) {
            WeightMeasurement.Serving(
                servingWeight = this.product.servingWeight,
                quantity = 1f
            )
        } else if (product.packageWeight != null) {
            WeightMeasurement.Package(
                packageWeight = this.product.packageWeight,
                quantity = 1f
            )
        } else {
            WeightMeasurement.WeightUnit(
                weight = 100f
            )
        }
    }

    return ProductWithWeightMeasurement(
        product = product.toDomain(),
        measurementId = if (todaysMeasurement) this.weightMeasurement?.id else null,
        measurement = weightMeasurement
    )
}

// TODO might want to improve mapping logic
fun ProductWithWeightMeasurementEntity.toDomain(): ProductWithWeightMeasurement {
    return ProductSearchEntity(
        product = this.product,
        weightMeasurement = this.weightMeasurement,
        todaysMeasurement = true
    ).toDomain()
}
