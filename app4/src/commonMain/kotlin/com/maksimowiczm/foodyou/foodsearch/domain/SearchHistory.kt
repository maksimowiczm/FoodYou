package com.maksimowiczm.foodyou.foodsearch.domain

import kotlin.time.Instant

data class SearchHistory(val query: SearchQuery.NotBlank, val timestamp: Instant)
