package com.maksimowiczm.foodyou.search.domain

import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import kotlin.time.Instant

sealed interface SearchHistoryCommand {
    data class RecordSearchQuery(val query: SearchQuery.Text, val timestamp: Instant) :
        SearchHistoryCommand
}
