package com.maksimowiczm.foodyou.feature.addfood.data.model

import com.maksimowiczm.foodyou.feature.addfood.database.ProductSearchEntity
import com.maksimowiczm.foodyou.feature.addfood.database.ProductSearchEntityProduct
import com.maksimowiczm.foodyou.feature.product.data.model.Product
import com.maksimowiczm.foodyou.feature.product.data.model.WeightUnit
import kotlin.math.roundToInt

data class ProductSearchModel(
    val product: Product,
    val measurementId: Long?,
    val measurement: WeightMeasurement
) {
    val weight: Float
        get() = measurement.weight

    val calories: Int
        get() = product.calories(weight).roundToInt()

    data class Product(
        val id: Long,
        val name: String,
        val brand: String?,
        val barcode: String?,
        val calories: Float,
        val packageWeight: Float?,
        val servingWeight: Float?,
        val weightUnit: WeightUnit
    ) {
        fun calories(weight: Float) = calories * weight / 100
    }
}

fun ProductSearchEntity.toDomain(): ProductSearchModel {
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

    return ProductSearchModel(
        product = this.product.toDomain(),
        measurementId = if (hasMeasurement) this.weightMeasurement?.id else null,
        measurement = weightMeasurement
    )
}

fun ProductSearchEntityProduct.toDomain() = ProductSearchModel.Product(
    id = id,
    name = name,
    brand = brand,
    barcode = barcode,
    calories = calories,
    packageWeight = packageWeight,
    servingWeight = servingWeight,
    weightUnit = weightUnit
)

fun Product.toSearchModel() = ProductSearchModel.Product(
    id = id,
    name = name,
    brand = brand,
    barcode = barcode,
    calories = calories,
    packageWeight = packageWeight,
    servingWeight = servingWeight,
    weightUnit = weightUnit
)
