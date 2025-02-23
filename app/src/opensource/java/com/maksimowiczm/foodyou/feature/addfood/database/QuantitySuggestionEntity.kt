package com.maksimowiczm.foodyou.feature.addfood.database

import com.maksimowiczm.foodyou.feature.addfood.data.model.WeightMeasurementEnum

data class QuantitySuggestionEntity(val measurement: WeightMeasurementEnum, val quantity: Float)
