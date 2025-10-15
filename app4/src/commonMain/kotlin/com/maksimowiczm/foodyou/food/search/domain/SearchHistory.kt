package com.maksimowiczm.foodyou.food.search.domain

import kotlin.time.Instant

data class SearchHistory(val query: SearchQuery.NotBlank, val timestamp: Instant)
