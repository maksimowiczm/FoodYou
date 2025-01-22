package com.maksimowiczm.foodyou.feature.diary.data.model

import com.maksimowiczm.foodyou.feature.addfood.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.addfood.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.feature.diary.database.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.feature.product.data.model.Product
import com.maksimowiczm.foodyou.feature.product.data.model.toDomain

data class Portion(
    val product: Product,
    val weightMeasurement: WeightMeasurement
) {
    val weight: Float
        get() = weightMeasurement.weight

    /**
     * Total calories for the portion.
     */
    val totalCalories: Float get() = product.calories * weight / 100
}

/**
 * Converts [ProductWithWeightMeasurement] to [Portion].
 */
fun ProductWithWeightMeasurement.toPortion(): Portion? {
    val weightMeasurement = when (this.weightMeasurement.measurement) {
        WeightMeasurementEnum.WeightUnit -> WeightMeasurement.WeightUnit(
            weight = this.weightMeasurement.quantity
        )

        WeightMeasurementEnum.Package -> WeightMeasurement.Package(
            quantity = this.weightMeasurement.quantity,
            packageWeight = this.product.packageWeight ?: return null
        )

        WeightMeasurementEnum.Serving -> WeightMeasurement.Serving(
            quantity = this.weightMeasurement.quantity,
            servingWeight = this.product.servingWeight ?: return null
        )
    }

    return Portion(
        product = this.product.toDomain(),
        weightMeasurement = weightMeasurement
    )
}
