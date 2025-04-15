package com.maksimowiczm.foodyou.feature.measurement

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.core.domain.model.Food
import com.maksimowiczm.foodyou.core.domain.model.Measurement

@Immutable
data class MeasurableFood(
    val food: Food,
    val suggestions: List<Measurement>,
    val selected: Measurement? = null
)
