package com.maksimowiczm.foodyou.business.food.domain

import com.maksimowiczm.foodyou.shared.event.DomainEvent
import com.maksimowiczm.foodyou.shared.search.SearchQuery
import kotlinx.datetime.LocalDateTime

data class FoodSearchEvent(val query: SearchQuery.Text, val date: LocalDateTime) : DomainEvent
