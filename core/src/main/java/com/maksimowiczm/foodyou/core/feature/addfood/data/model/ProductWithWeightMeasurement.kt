package com.maksimowiczm.foodyou.core.feature.addfood.data.model

import com.maksimowiczm.foodyou.core.feature.addfood.database.ProductSearchEntity
import com.maksimowiczm.foodyou.core.feature.addfood.database.ProductWithWeightMeasurementEntity
import com.maksimowiczm.foodyou.core.feature.addfood.database.WeightMeasurementEntity
import com.maksimowiczm.foodyou.core.feature.product.data.model.Product
import com.maksimowiczm.foodyou.core.feature.product.data.model.toDomain
import kotlin.math.roundToInt

data class ProductWithWeightMeasurement(
    val product: Product,
    val measurementId: Long?,
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
    val product = this.product.toDomain()
    val measurementId = this.weightMeasurement.id
    val weightMeasurement = this.weightMeasurement.toDomain(product)

    return ProductWithWeightMeasurement(
        product = product,
        measurementId = measurementId,
        measurement = weightMeasurement
    )
}

fun ProductSearchEntity.toDomain(): ProductWithWeightMeasurement {
    val product = this.product.toDomain()
    val measurementId = this.weightMeasurement?.id

    val weightMeasurement = this.weightMeasurement?.toDomain(product)
        ?: WeightMeasurement.defaultForProduct(product)

    return ProductWithWeightMeasurement(
        product = product,
        measurementId = if (todaysMeasurement) measurementId else null,
        measurement = weightMeasurement
    )
}

private fun WeightMeasurementEntity.toDomain(product: Product) = when (this.measurement) {
    WeightMeasurementEnum.WeightUnit -> WeightMeasurement.WeightUnit(
        weight = quantity
    )

    WeightMeasurementEnum.Package -> WeightMeasurement.Package(
        quantity = quantity,
        packageWeight = product.packageWeight!!
    )

    WeightMeasurementEnum.Serving -> WeightMeasurement.Serving(
        quantity = quantity,
        servingWeight = product.servingWeight!!
    )
}
