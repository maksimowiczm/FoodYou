package com.maksimowiczm.foodyou.search.domain

import com.maksimowiczm.foodyou.common.domain.search.SearchQuery

sealed interface SearchHistoryCommand {
    data class RecordSearchQuery(val query: SearchQuery.Text) : SearchHistoryCommand
}
