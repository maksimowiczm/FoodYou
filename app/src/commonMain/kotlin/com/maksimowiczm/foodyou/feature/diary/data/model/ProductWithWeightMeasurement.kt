package com.maksimowiczm.foodyou.feature.diary.data.model

import com.maksimowiczm.foodyou.feature.diary.database.entity.ProductSearchEntity
import com.maksimowiczm.foodyou.feature.diary.database.entity.ProductWithWeightMeasurementEntity
import com.maksimowiczm.foodyou.feature.diary.database.entity.WeightMeasurementEntity

data class ProductWithWeightMeasurement(
    val product: Product,
    val measurementId: Long?,
    val measurement: WeightMeasurement
) {
    val weight: Float
        get() = measurement.weight

    val calories: Float
        get() = product.nutrients.calories(weight)

    val proteins: Float
        get() = product.nutrients.proteins(weight)

    val carbohydrates: Float
        get() = product.nutrients.carbohydrates(weight)

    val fats: Float
        get() = product.nutrients.fats(weight)
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
