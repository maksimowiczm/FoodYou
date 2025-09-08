package com.maksimowiczm.foodyou.food.domain.entity

import com.maksimowiczm.foodyou.shared.domain.search.SearchQuery
import kotlinx.datetime.LocalDateTime

data class FoodSearchHistory(val date: LocalDateTime, val query: SearchQuery.Text)
