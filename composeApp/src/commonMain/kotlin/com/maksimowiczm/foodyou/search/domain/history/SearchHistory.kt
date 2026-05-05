package com.maksimowiczm.foodyou.search.domain.history

import com.maksimowiczm.foodyou.search.domain.SearchQuery
import kotlin.time.Instant

data class SearchHistory(val query: SearchQuery.NotBlank, val timestamp: Instant)
