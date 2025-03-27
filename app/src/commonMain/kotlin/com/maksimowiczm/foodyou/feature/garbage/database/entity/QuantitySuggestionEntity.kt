package com.maksimowiczm.foodyou.feature.garbage.database.entity

import com.maksimowiczm.foodyou.feature.garbage.data.model.WeightMeasurementEnum

data class QuantitySuggestionEntity(val measurement: WeightMeasurementEnum, val quantity: Float)
