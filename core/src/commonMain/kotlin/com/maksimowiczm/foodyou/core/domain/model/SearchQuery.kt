package com.maksimowiczm.foodyou.core.domain.model

import kotlinx.datetime.LocalDateTime

data class SearchQuery(val query: String, val date: LocalDateTime)
