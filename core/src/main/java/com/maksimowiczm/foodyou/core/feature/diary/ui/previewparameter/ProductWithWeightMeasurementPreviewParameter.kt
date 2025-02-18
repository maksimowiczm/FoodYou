package com.maksimowiczm.foodyou.core.feature.diary.ui.previewparameter

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.core.feature.product.ui.previewparameter.ProductPreviewParameterProvider

class ProductWithWeightMeasurementPreviewParameter :
    PreviewParameterProvider<ProductWithWeightMeasurement> {
    private val products = ProductPreviewParameterProvider().values
    private val measurements = WeightMeasurementPreviewParameter().values

    override val values = products.zip(measurements) { product, measurement ->
        ProductWithWeightMeasurement(
            product = product,
            measurement = measurement
        )
    }
}
