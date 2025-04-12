package com.maksimowiczm.foodyou.feature.recipe.model

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.model.Product

@Immutable
internal data class Ingredient(val product: Product, val measurement: Measurement)
