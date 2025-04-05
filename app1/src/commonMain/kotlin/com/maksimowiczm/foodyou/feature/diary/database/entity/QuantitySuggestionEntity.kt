package com.maksimowiczm.foodyou.feature.diary.database.entity

import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurementEnum

data class QuantitySuggestionEntity(val measurement: WeightMeasurementEnum, val quantity: Float)
