package com.maksimowiczm.foodyou.core.feature.addfood.database

import com.maksimowiczm.foodyou.core.feature.addfood.data.model.WeightMeasurementEnum

data class QuantitySuggestionEntity(
    val measurement: WeightMeasurementEnum,
    val quantity: Float
)
