package com.maksimowiczm.foodyou.swissfoodcomposition.domain

import com.maksimowiczm.foodyou.common.Div
import com.maksimowiczm.foodyou.common.Plus
import com.maksimowiczm.foodyou.common.Times
import com.maksimowiczm.foodyou.common.domain.food.NutrientValue

@JvmInline
value class SwissFoodNutrientComponent<T>(val value: NutrientValue<T>)
    where T : Plus<T>, T : Times<T>, T : Div<T>
