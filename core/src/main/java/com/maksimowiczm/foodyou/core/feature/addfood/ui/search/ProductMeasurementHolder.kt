package com.maksimowiczm.foodyou.core.feature.addfood.ui.search

import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductWithWeightMeasurement
import kotlinx.coroutines.flow.StateFlow

interface ProductMeasurementHolder {
    val measurementId: Long?
    val model: StateFlow<ProductWithWeightMeasurement?>
}
