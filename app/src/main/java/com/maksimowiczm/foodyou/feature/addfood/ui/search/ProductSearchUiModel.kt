package com.maksimowiczm.foodyou.feature.addfood.ui.search

import com.maksimowiczm.foodyou.feature.addfood.data.model.ProductSearchModel

data class ProductSearchUiModel(
    val model: ProductSearchModel,
    val isLoading: Boolean,
    val isChecked: Boolean
)
