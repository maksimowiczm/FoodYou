package com.maksimowiczm.foodyou.core.feature.addfood.ui.previewparameter

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.ProductSearchUiModel
import com.maksimowiczm.foodyou.core.feature.diary.ui.previewparameter.ProductWithWeightMeasurementPreviewParameter

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
