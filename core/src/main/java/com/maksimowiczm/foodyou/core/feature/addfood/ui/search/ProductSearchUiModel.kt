package com.maksimowiczm.foodyou.core.feature.addfood.ui.search

import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductWithWeightMeasurement

data class ProductSearchUiModel(
    val model: ProductWithWeightMeasurement,
    val isLoading: Boolean,
    val isChecked: Boolean
)
