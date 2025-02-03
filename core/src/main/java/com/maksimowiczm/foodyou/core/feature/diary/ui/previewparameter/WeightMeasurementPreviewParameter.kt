package com.maksimowiczm.foodyou.core.feature.diary.ui.previewparameter

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.WeightMeasurement

class WeightMeasurementPreviewParameter : PreviewParameterProvider<WeightMeasurement> {
    override val values: Sequence<WeightMeasurement> = sequenceOf(
        WeightMeasurement.WeightUnit(100f),
        WeightMeasurement.Serving(1f, 150f),
        WeightMeasurement.Package(1f, 350f)
    )
}
