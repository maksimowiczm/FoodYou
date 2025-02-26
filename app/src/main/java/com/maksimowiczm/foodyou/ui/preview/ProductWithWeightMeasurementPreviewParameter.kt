package com.maksimowiczm.foodyou.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.maksimowiczm.foodyou.data.model.ProductWithWeightMeasurement

class ProductWithWeightMeasurementPreviewParameter :
    PreviewParameterProvider<ProductWithWeightMeasurement> {
    private val products = ProductPreviewParameterProvider().values
    private val measurements = WeightMeasurementPreviewParameter().values

    override val values = products.zip(measurements) { product, measurement ->
        ProductWithWeightMeasurement(
            product = product,
            measurementId = null,
            measurement = measurement
        )
    }.mapIndexed { index, productWithWeightMeasurement ->
        productWithWeightMeasurement.copy(
            measurementId = if (index % 3 == 0) index.toLong() else null
        )
    }
}
