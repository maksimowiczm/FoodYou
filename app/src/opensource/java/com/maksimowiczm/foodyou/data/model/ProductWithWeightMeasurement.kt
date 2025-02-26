package com.maksimowiczm.foodyou.data.model

import com.maksimowiczm.foodyou.database.entity.ProductSearchEntity
import com.maksimowiczm.foodyou.database.entity.ProductWithWeightMeasurementEntity
import com.maksimowiczm.foodyou.database.entity.WeightMeasurementEntity

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
