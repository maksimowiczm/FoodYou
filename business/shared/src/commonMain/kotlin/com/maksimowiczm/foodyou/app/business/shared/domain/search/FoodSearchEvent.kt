package com.maksimowiczm.foodyou.app.business.shared.domain.search

import com.maksimowiczm.foodyou.shared.domain.event.DomainEvent
import com.maksimowiczm.foodyou.shared.domain.search.SearchQuery
import kotlinx.datetime.LocalDateTime

data class FoodSearchEvent(val query: SearchQuery.Text, val date: LocalDateTime) : DomainEvent
