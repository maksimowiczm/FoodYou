package com.maksimowiczm.foodyou.food.search.domain

import com.maksimowiczm.foodyou.common.domain.event.IntegrationEvent
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import kotlin.time.Instant

data class FoodSearchEvent(val query: SearchQuery.Text, val timestamp: Instant) : IntegrationEvent
