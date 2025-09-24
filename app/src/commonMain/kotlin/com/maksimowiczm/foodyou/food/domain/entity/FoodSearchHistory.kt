package com.maksimowiczm.foodyou.food.domain.entity

import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import kotlin.time.Instant

data class FoodSearchHistory(val timestamp: Instant, val query: SearchQuery.Text)
