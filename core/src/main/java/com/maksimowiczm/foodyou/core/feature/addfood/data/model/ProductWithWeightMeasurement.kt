package com.maksimowiczm.foodyou.core.feature.addfood.data.model

import com.maksimowiczm.foodyou.core.feature.addfood.database.ProductSearchEntity
import com.maksimowiczm.foodyou.core.feature.addfood.database.ProductWithWeightMeasurementEntity
import com.maksimowiczm.foodyou.core.feature.addfood.database.WeightMeasurementEntity
import com.maksimowiczm.foodyou.core.feature.product.data.model.Product
import com.maksimowiczm.foodyou.core.feature.product.data.model.toDomain
import kotlin.math.roundToInt

data class ProductWithWeightMeasurement(
    val product: Product,
    val measurement: WeightMeasurement,
    val measurementId: Long?,
    val rank: Float
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

fun ProductSearchEntity.toDomain(): ProductWithWeightMeasurement {
    val product = this.product.toDomain()
    val measurementId = if (todaysMeasurement) this.weightMeasurement?.id else null
    val rank = this.weightMeasurement?.rank ?: WeightMeasurementEntity.FIRST_RANK

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

        null -> WeightMeasurement.defaultForProduct(product)
    }

    return ProductWithWeightMeasurement(
        product = product,
        measurement = weightMeasurement,
        measurementId = measurementId,
        rank = rank
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
