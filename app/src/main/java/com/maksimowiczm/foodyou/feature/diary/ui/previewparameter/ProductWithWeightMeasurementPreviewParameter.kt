package com.maksimowiczm.foodyou.feature.diary.ui.previewparameter

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.maksimowiczm.foodyou.feature.addfood.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.feature.product.ui.previewparameter.ProductPreviewParameterProvider

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
