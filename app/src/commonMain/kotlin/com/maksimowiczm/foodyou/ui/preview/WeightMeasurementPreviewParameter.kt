package com.maksimowiczm.foodyou.ui.preview

import com.maksimowiczm.foodyou.data.model.WeightMeasurement
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

class WeightMeasurementPreviewParameter : PreviewParameterProvider<WeightMeasurement> {
    override val values: Sequence<WeightMeasurement> = sequenceOf(
        WeightMeasurement.WeightUnit(100f),
        WeightMeasurement.Serving(1f, 150f),
        WeightMeasurement.Package(1f, 350f)
    )
}
