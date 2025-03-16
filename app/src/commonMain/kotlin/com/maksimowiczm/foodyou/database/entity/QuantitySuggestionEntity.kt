package com.maksimowiczm.foodyou.database.entity

import com.maksimowiczm.foodyou.data.model.WeightMeasurementEnum

data class QuantitySuggestionEntity(val measurement: WeightMeasurementEnum, val quantity: Float)
