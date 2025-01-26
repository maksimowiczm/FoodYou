package com.maksimowiczm.foodyou.feature.addfood.ui.previewparameter

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.maksimowiczm.foodyou.feature.addfood.ui.search.ProductSearchUiModel

class ProductSearchUiModelPreviewParameter : PreviewParameterProvider<ProductSearchUiModel> {
    override val values: Sequence<ProductSearchUiModel> =
        ProductWithWeightMeasurementPreviewParameter().values.mapIndexed { index, it ->
            ProductSearchUiModel(
                model = it,
                isLoading = index % 3 == 1,
                isChecked = it.measurementId != null
            )
        }
}
