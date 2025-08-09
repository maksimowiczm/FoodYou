package com.maksimowiczm.foodyou.feature.food.diary.search.presentation

import com.maksimowiczm.foodyou.business.food.domain.FoodSearch
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement

internal fun Measurement.weight(product: FoodSearch.Product): Double? =
    when (this) {
        is Measurement.Gram -> value
        is Measurement.Milliliter -> value
        is Measurement.Package -> product.totalWeight?.let { it * quantity }
        is Measurement.Serving -> product.servingWeight?.let { it * quantity }
    }
